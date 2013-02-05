package edu.cmu.mdp;

import java.util.List;
import java.util.Map;

import edu.cmu.Action;
import edu.cmu.recognizer.State;

public interface MDPProblem {
	
	/**
	 * Returns the discount factory for this problem.
	 * @return
	 */
	public double getDiscountFactor();
	
	/**
	 * Returns a list of the states in this MDP problem, the index of
	 * a state in this list corresponds to their index in the 
	 * {@link TransitionMatrix} objects returned from this problem
	 * @return
	 */
	public List<State> getStates();
	
	/**
	 * Returns a list of the action names in this problem
	 * @return
	 */
	public List<Action> getActions();
	
	/**
	 * Returns a map of the {@link TransitionMatrix} objects in this problem.
	 * @return
	 */
	public Map<Action, TransitionMatrix> getTransitionMatrices();
	
	/**
	 * Returns the probability of transitioning from state <em>s</em> to state
	 * <em>sp</em> using action <em>act</em>.
	 *  
	 * @param s
	 * @param act
	 * @param sp
	 * @return
	 */
	public double getTransitionProbability(int s, Action act, int sp);

    /** 
     * Returns the state given an index.
     * @author jeanoh
     */
    public State getState(int index);

    /**
     * reward function: S x A x S' --> R
     * @author jeanoh
     */
    public double getReward(int s, Action act, int sp);

    /**
     * Returns Action with the given index
     * @param index
     * @return
     * @author jeanoh
     */
    public Action getAction(int index);

}
