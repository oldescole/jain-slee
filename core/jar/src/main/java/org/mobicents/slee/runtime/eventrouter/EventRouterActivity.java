package org.mobicents.slee.runtime.eventrouter;

import java.util.concurrent.ExecutorService;

import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.runtime.activity.ActivityContextHandle;

public class EventRouterActivity {

	/**
	 * 
	 */
	private final ActivityContextHandle ach;
	
	/**
	 * byte array form of the ac handle
	 */
	private byte[] activityHandleBytes;
	
	/**
	 * 
	 */
	private ExecutorService executorService;
	
	/**
	 * 
	 */
	private final ActivityEventQueueManager eventQueueManager;
	
	/**
	 * the event context for the event currently being routed
	 */
	private EventContextImpl currentEventContext;
	
	public EventRouterActivity(ActivityContextHandle ach, SleeContainer sleeContainer) {
		this.ach = ach;
		this.eventQueueManager = new ActivityEventQueueManager(ach,sleeContainer);
	}
	
	public ActivityEventQueueManager getEventQueueManager() {
		return eventQueueManager;
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}
	
	protected void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public ActivityContextHandle getActivityContextHandle() {
		return ach;
	}
	
	public byte[] getActivityHandleBytes() {
		return activityHandleBytes;
	}
	
	public void setActivityHandleBytes(byte[] activityHandleBytes) {
		this.activityHandleBytes = activityHandleBytes;
	}
	
	public EventContextImpl getCurrentEventContext() {
		return currentEventContext;
	}
	
	public void setCurrentEventContext(EventContextImpl currentEventContext) {
		this.currentEventContext = currentEventContext;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((EventRouterActivity) obj).ach
					.equals(this.ach);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return ach.hashCode();
	}
		
}
