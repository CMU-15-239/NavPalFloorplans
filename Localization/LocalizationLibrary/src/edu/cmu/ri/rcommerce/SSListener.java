package edu.cmu.ri.rcommerce;

/**
 * Callback interface for interacting with a StateServer, which keeps track of what is known about the
 * environment and your position in it.
 * @author Nisarg
 *
 */
public interface SSListener 
{
	/**
	 * Called whenever there is a change in the environment, like a new annotation.
	 * @param s The StateServer the change is associated with. 
	 */
	public void SSUpdate(StateServer s);
	/**
	 * Called whenever a new task has been assigned to this agent
	 * @param task An annotation describing the task.
	 */
	public void newTaskUpdate(Annotation task);
}
