/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jivesoftware.smack.debugger;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.*;

import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Very simple debugger that prints to the console (stdout) the sent and received stanzas. Use
 * this debugger with caution since printing to the console is an expensive operation that may
 * even block the thread since only one thread may print at a time.<p>
 * <p/>
 * It is possible to not only print the raw sent and received stanzas but also the interpreted
 * packets by Smack. By default interpreted packets won't be printed. To enable this feature
 * just change the <tt>printInterpreted</tt> static variable to <tt>true</tt>.
 *
 * @author Gaston Dombiak
 */
public class ConsoleDebugger implements SmackDebugger {

    public static boolean printInterpreted = false;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm:ss aaa");

    private XMPPConnection connection = null;

    private PacketListener listener = null;
    private ConnectionListener connListener = null;

    private Writer writer;
    private Reader reader;
    private ReaderListener readerListener;
    private WriterListener writerListener;

    public ConsoleDebugger(XMPPConnection connection, Writer writer, Reader reader) {
        this.connection = connection;
        this.writer = writer;
        this.reader = reader;
        createDebug();
    }

    /**
     * Creates the listeners that will print in the console when new activity is detected.
     */
    private void createDebug() {
        // Create a special Reader that wraps the main Reader and logs data to the GUI.
        ObservableReader debugReader = new ObservableReader(reader);
        readerListener = new ReaderListener() {
            public void read(String str) {
                System.out.println(
                        dateFormatter.format(new Date()) + " RCV  (" + connection.hashCode() +
                        "): " +
                        str);
            }
        };
        debugReader.addReaderListener(readerListener);

        // Create a special Writer that wraps the main Writer and logs data to the GUI.
        ObservableWriter debugWriter = new ObservableWriter(writer);
        writerListener = new WriterListener() {
            public void write(String str) {
                System.out.println(
                        dateFormatter.format(new Date()) + " SENT (" + connection.hashCode() +
                        "): " +
                        str);
            }
        };
        debugWriter.addWriterListener(writerListener);

        // Assign the reader/writer objects to use the debug versions. The packet reader
        // and writer will use the debug versions when they are created.
        reader = debugReader;
        writer = debugWriter;

        // Create a thread that will listen for all incoming packets and write them to
        // the GUI. This is what we call "interpreted" packet data, since it's the packet
        // data as Smack sees it and not as it's coming in as raw XML.
        listener = new PacketListener() {
            public void processPacket(Packet packet) {
                if (printInterpreted) {
                    System.out.println(
                            dateFormatter.format(new Date()) + " RCV PKT (" +
                            connection.hashCode() +
                            "): " +
                            packet.toXML());
                }
            }
        };

        connListener = new ConnectionListener() {
            public void connectionClosed() {
                System.out.println(
                        dateFormatter.format(new Date()) + " Connection closed (" +
                        connection.hashCode() +
                        ")");
            }

            public void connectionClosedOnError(Exception e) {
                System.out.println(
                        dateFormatter.format(new Date()) +
                        " Connection closed due to an exception (" +
                        connection.hashCode() +
                        ")");
                e.printStackTrace();
            }
        };
    }

    public Reader newConnectionReader(Reader newReader) {
        ((ObservableReader)reader).removeReaderListener(readerListener);
        ObservableReader debugReader = new ObservableReader(newReader);
        debugReader.addReaderListener(readerListener);
        reader = debugReader;
        return reader;
    }

    public Writer newConnectionWriter(Writer newWriter) {
        ((ObservableWriter)writer).removeWriterListener(writerListener);
        ObservableWriter debugWriter = new ObservableWriter(newWriter);
        debugWriter.addWriterListener(writerListener);
        writer = debugWriter;
        return writer;
    }

    public void userHasLogged(String user) {
        boolean isAnonymous = "".equals(StringUtils.parseName(user));
        String title =
                "User logged (" + connection.hashCode() + "): "
                + (isAnonymous ? "" : StringUtils.parseBareAddress(user))
                + "@"
                + connection.getServiceName()
                + ":"
                + connection.getPort();
        title += "/" + StringUtils.parseResource(user);
        System.out.println(title);
        // Add the connection listener to the connection so that the debugger can be notified
        // whenever the connection is closed.
        connection.addConnectionListener(connListener);
    }

    public Reader getReader() {
        return reader;
    }

    public Writer getWriter() {
        return writer;
    }

    public PacketListener getReaderListener() {
        return listener;
    }

    public PacketListener getWriterListener() {
        return null;
    }
}
