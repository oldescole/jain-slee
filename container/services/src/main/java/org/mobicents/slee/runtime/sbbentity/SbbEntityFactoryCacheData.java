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

package org.mobicents.slee.runtime.sbbentity;

import org.apache.log4j.Logger;
import org.restcomm.cache.tree.Fqn;
import org.restcomm.cache.tree.Node;
import org.mobicents.slee.container.sbbentity.SbbEntityID;
import org.restcomm.cache.CacheData;
import org.restcomm.cluster.MobicentsCluster;

import javax.slee.ServiceID;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * Proxy object for activity context factory data management through JBoss Cache
 * 
 * @author martins
 * 
 */

public class SbbEntityFactoryCacheData extends CacheData {

	private static final Logger logger = Logger.getLogger(SbbEntityFactoryCacheData.class);

	public static final String SBB_ENTITY_FACTORY_FQN_NAME = "sbbe";
	
	/**
	 * the fqn of the node that holds all activity context cache child nodes
	 */
	protected static final Fqn SBB_ENTITY_FACTORY_FQN = Fqn.fromElements(SBB_ENTITY_FACTORY_FQN_NAME);

	/**
	 * 
	 * @param cluster
	 */
	public SbbEntityFactoryCacheData(MobicentsCluster cluster) {
		super(SBB_ENTITY_FACTORY_FQN, cluster.getMobicentsCache());
	}

	/**
	 * Retrieves a set containing sbb entity ids in the factory
	 * cache data
	 * 
	 * @return
	 */
	public Set<SbbEntityID> getSbbEntities() {
		final Node node = getNode();
		if (node == null) {
			return Collections.emptySet();
		}
		HashSet<SbbEntityID> result = new HashSet<SbbEntityID>();
		ServiceID serviceID = null;

		// FIXME: getChildValues
		for (Object obj : node.getChildValues()) {
			serviceID = (ServiceID) obj;
			for (SbbEntityID sbbEntityID : getRootSbbEntityIDs(serviceID)) {
				result.add(sbbEntityID);
				collectSbbEntities(sbbEntityID,result);
			}
		}		
		return result;
	}

	public Set<SbbEntityID> getRootSbbEntityIDs(ServiceID serviceID) {
		logger.debug("#### TEST [getRootSbbEntityIDs]");
		final Node node = getNode();
		logger.debug("#### TEST [getRootSbbEntityIDs]: node: "+node);

		if (node == null) {
			return Collections.emptySet();
		}

		// FIXME: getChild
		final Node serviceNode = node.getChild(serviceID);
		logger.debug("#### TEST [getRootSbbEntityIDs]: serviceID: "+serviceID);
		logger.debug("#### TEST [getRootSbbEntityIDs]: serviceNode: "+serviceNode);

		if (serviceNode == null) {
			return Collections.emptySet();
		}
		HashSet<SbbEntityID> result = new HashSet<SbbEntityID>();
		RootSbbEntityID rootSbbEntityID = null;

		// FIXME: getChildNames
		for (Object obj : serviceNode.getChildNames()) {
			logger.debug("#### TEST [getRootSbbEntityIDs]: Object: "+(String)obj);
			rootSbbEntityID = new RootSbbEntityID(serviceID, (String)obj);
			result.add(rootSbbEntityID);
		}

		logger.debug("#### TEST [getRootSbbEntityIDs]: result: "+result);
		return result;
	}

	public void WAremove() {
		final Node node = getNode();
		logger.debug("$$$$ node: "+node);
		if (node != null) {
			if (!node.getChildren().isEmpty()) {
				logger.debug("$$$$ in not empty: "+node);
				node.removeChildren();
			}

			if (!node.getChildren().isEmpty()) {
				logger.debug("$$$$ in not empty: "+node);
			}
		}
	}
	
	private void collectSbbEntities(SbbEntityID sbbEntityID, Set<SbbEntityID> result) {
		final SbbEntityCacheData sbbEntityCacheData = new SbbEntityCacheData(sbbEntityID,super.getMobicentsCache());
		for (SbbEntityID childSbbEntityID : sbbEntityCacheData.getAllChildSbbEntities()) {
			result.add(childSbbEntityID);
			collectSbbEntities(childSbbEntityID,result);
		}
	}

}