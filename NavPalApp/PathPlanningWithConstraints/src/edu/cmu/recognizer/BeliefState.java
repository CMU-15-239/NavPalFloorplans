package edu.cmu.recognizer;

import java.util.HashMap;
import java.util.Iterator;

import edu.cmu.userplan.UserPlanNode;

/**
 * A class that models the current user state, i.e. the current step the user
 * has just executed in its plan Library.
 * 
 * @author meneguzzi
 * 
 */
public class BeliefState extends HashMap<UserPlanNode, Double> {
	/**
	 * this object is requires by the HashMap
	 */
	private static final long serialVersionUID = 1L;

	public BeliefState(int size) {
		super(size);
	}

	/**
	 * Returns whether or not the supplied state matches this
	 * {@link BeliefState}
	 * 
	 * @param state
	 * @return
	 */
	@Override
	public boolean equals(Object state) {
		if (!(state instanceof BeliefState))
			return false;
		return this.values().equals(((BeliefState) state).values());
	}

	// reset belief probability to 0.
	public void reset() {
		for (Iterator<UserPlanNode> i = this.keySet().iterator(); i.hasNext();) {
			UserPlanNode node = i.next();
			this.put(node, new Double(0));
		}
	}

	/**
	 * Returns the probability of the user currently being in given plan-node
	 * 
	 * @param node
	 * @return
	 */
	public double getProbability(UserPlanNode node) {
		Double d = get(node);
		if (d == null)
			return 0;
		return d.doubleValue();
	}

	/**
	 * Find the belief probability of the state whose ID=nodeID
	 * 
	 * @param nodeId
	 * @return
	 */
	public double getProbability(String nodeId) {
		for (UserPlanNode node : this.keySet()) {
			if (node.getLabel().equals(nodeId)) {
				Double d = get(node);
				if (d == null)
					return 0;
				return d.doubleValue();
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	// jean
	public String toStringShort() {
		StringBuffer sb = new StringBuffer("");
		for (UserPlanNode node : this.keySet()) {
			sb.append(node.getLabel() + ":" + (get(node)).doubleValue() + "\n");
		}
		return sb.toString();
	}

	public boolean moreLiklyThan(String stateId, double threshold) {

		double probability = getProbability(stateId);
		return (probability > threshold) ? true : false;
	}
}
