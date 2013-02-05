/**
 * @author jeanoh@cs.cmu.edu
 * @date Nov. 16, 2009
 */
package edu.cmu.future;

import edu.cmu.recognizer.BeliefState;
import edu.cmu.userplan.UserPlan;

/**
 * @opt shape component
 * @author meneguzzi
 * @modified jeanoh (interface --> abstract class)
 */
public abstract class AbstractFuturePlanRecognizer {

	/**
	 * current belief state 
	 */
	BeliefState beliefState;	
		
    /** 
     * returns a collection of information needs that is likely to be needed by the user
     * "in the future" based on the user's current state. 
     * <note>: avoid returning the actual plan segment, since the data structure for user state (or plan-step)
     * may be specific to each plan recognition algorithm. e.g., mdp.State for the Markov state-based approach. 
     * @param state TODO
     */
    public abstract UserPlan predictFutureSteps();

	public synchronized void setBeliefState(BeliefState b) {
		beliefState = b;
	}

	// If predictor doesn't define a model refinement, then don't force it. 
	public void refineModel() {}
}
