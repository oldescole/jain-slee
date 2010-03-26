package org.mobicents.slee.runtime.eventrouter;

import org.mobicents.slee.runtime.activity.ActivityContext;
import org.mobicents.slee.runtime.activity.ActivityContextHandle;

/**
 * Interface for the container's Event Router
 * 
 * @author Eduardo Martins
 * 
 */
public interface EventRouter {

	/**
	 * Retrieves the event router activity object for the {@link ActivityContext} with the specified handle
	 * @param ach
	 * @param create indicates if the event router activity should be created, when it doesn't exist
	 * @return
	 */
	public EventRouterActivity getEventRouterActivity(ActivityContextHandle ach, boolean create);

	/**
	 * Requests the routing of a {@link DeferredEvent}
	 * 
	 * @param dE
	 */
	public void routeEvent(DeferredEvent dE);
	
	/**
	 * Requests the routing of a {@link DeferredEvent} to be resumed, after it's
	 * suspension finished. This operation will be run in the same thread, which
	 * means it must be invoked from the activity executor service
	 * 
	 * @param eventContextImpl
	 */
	public void resumeEventContext(EventContextImpl eventContextImpl);
	
	/**
	 * The activity has ended so the event router may close related runtime resources 
	 * @param ach
	 */
	public void activityEnded(ActivityContextHandle ach);

	/**
	 * Configures the event router, defining the number of event executors. This method will throw
	 * {@link IllegalStateException} if the container state is RUNNING.
	 * 
	 * @param eventRouterExecutors
	 */
	public void config(int eventRouterExecutors);

	/**
	 * Retrieves the object used to manage event references
	 * @return
	 */
	public DeferredEventReferencesManagement getDeferredEventReferencesManagement();

}
