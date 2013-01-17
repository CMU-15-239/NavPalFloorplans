/**
 * @author jeanoh@cs.cmu.edu
 * @date Nov. 16, 2009
 */
package edu.cmu.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import edu.cmu.Action;
import edu.cmu.mdp.DynamicMDPProblem;
import edu.cmu.mdp.Policy;
import edu.cmu.recognizer.BeliefState;
import edu.cmu.recognizer.State;
import edu.cmu.userplan.UserPlan;
import edu.cmu.userplan.UserPlanNode;

public class MarkovFuturePlanRecognizer extends AbstractFuturePlanRecognizer {

	DynamicMDPProblem _mdp;
	Policy _policy;
	boolean _policyUpdated;

	static Random _coin = new Random();


	Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * information likely to be needed with probability lower than _threshold
	 * will not be collected in advance.
	 */
	double _threshold;
	/**
	 * the max time steps to look ahead when predicting the future information
	 * needs
	 */
	int _maxDepth = 5;

	/**
	 * Constructor
	 * @param game
	 * @param mdp
	 * @param solver -- MDP solver
	 * @param thr -- probability threshold such that any path that has a lower probability 
	 * in the current policy (according to the solver) is ignored.
	 * @param maxDepth -- user's future plan is predicted only up to [maxDepth]
	 * steps away from the current state.   
	 */
	public MarkovFuturePlanRecognizer(DynamicMDPProblem mdp,
			Policy policy, double thr, int maxDepth) {
		_mdp = mdp;
		_threshold = thr;
		_maxDepth = maxDepth;
		int sz = _mdp.getStates().size();
		beliefState = new BeliefState(sz);
		_policy = policy;
	}

	/**
	 * returns a collection of information needs that is likely to be needed by
	 * the user "in the future" based on the user's current state. <note>: avoid
	 * returning the actual plan segment, since the data structure for user
	 * state (or plan-step) may be specific to each plan recognition algorithm.
	 * e.g., mdp.State for the Markov state-based approach.
	 */
	@Override
	public UserPlan predictFutureSteps() {		
		// dummy root node
		UserPlanNode root = new UserPlanNode(new State("root"), 1.0,new Action("none", -1));

		int depth = 1;
		List<State> wasSeenBefore = new ArrayList<State>();
		for (UserPlanNode node : beliefState.keySet()) {
			if (beliefState.getProbability(node) > _threshold)
				predictFutureStepsRecurOnHorizon(root, _mdp.getState(node.getId()), _threshold,
						depth, _maxDepth,wasSeenBefore);
		}
		UserPlan plan = new UserPlan(root);		
		return plan;
	}

	/**
	 * Recursivaly predict states
	 * @param parent 
	 * @param s
	 * @param thr
	 * @param depth
	 * @param maxDepth
	 * @param wasSeenBefore Temp list that contains all the nodes we visited so far to avoid cycles / redundancy
	 */
	private void predictFutureStepsRecurOnHorizon(UserPlanNode parent, State s, double thr, int depth,
			int maxDepth, List<State> wasSeenBefore ) {

		if (depth == maxDepth )
			return;

		// probability distribution over a set of actions available from state s
		double[] policyFromState = _policy.get(s.getId());

		for (int a = 0; a < policyFromState.length; a++) {
			// a set of actions is specific to a state			
			Action action = s.getAction(a);

			double p = policyFromState[action.getId()];

			UserPlanNode node = parent;

			node = new UserPlanNode(s, p,action);
			parent.addNextNode(node);

			wasSeenBefore.add(s);

			State nextState = nextState(s, action);
			// If nextState was already seen before, we won't bother looking into it!
			if (!wasSeenBefore.contains(nextState)) {
				predictFutureStepsRecurOnHorizon(node, nextState, thr, depth + 1, maxDepth, wasSeenBefore);
			}


		}
	}


	public void printPolicy() {		
		_policy.print(_mdp.getStates());
	}

	/**
	 * Whenever a new solver was created, set it and set the corresponding policy
	 * @param solver the new solver
	 */
	public void setPolicy(Policy policy) {
		this._policy = policy;
	}

	/**
	 * nextState(s, a): sample a next state using transition probability
	 * (s,a,s')
	 */
	public int nextState(int s, Action a) {
		int sz = _mdp.getStates().size();
		try {
			double flip = _coin.nextDouble();
			double sum = 0;
			for (int i = 0; i < sz; ++i) {
				double p = _mdp.getTransitionProbability(s, a, i);
				sum += p;
				if (flip <= sum)
					return i;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex + " s=" + s + " a=" + a);
			System.exit(0);
		}
		return sz - 1;
	}

	public State nextState(State s, Action a) {
		return _mdp.getState(nextState(s.getId(), a));
	}

	public void resetPolicy(){
		_policy.reset();
	}
}
