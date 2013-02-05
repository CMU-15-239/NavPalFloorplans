package edu.cmu.userplan;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.Action;
import edu.cmu.recognizer.State;

/**
 * A node in a {@link UserPlan}, these nodes comprise the tree of potential
 * actions a user may select in the future. A node is composed of:
 * <ul>
 * <li>an {@code id} representing the action executed by the agent</li>
 * <li>whether or not this node {@code isInfoAction}</li>
 * <li>the {@code probability} of this action being selected given that the user
 * has just selected {@code parentNode}</li>
 * <li>the {@code deadline} for selecting this action
 * <li>a link to a {@code parentNode}</li>
 * <li>links to {@code nextNodes}</li>
 * </ul>
 * 
 * @author meneguzzi @
 * 
 * @modified jeanoh IndexedObject: index should match with corresponding state
 *           index added setIndex(); equals(); hashCode()
 */
public class UserPlanNode {

	// TODO ID is stateIndex for BeliefState; a query for UserPlan
	protected String label; 

	protected double probability;

	protected int deadline;

	protected UserPlanNode parentNode;

	protected List<UserPlanNode> nextNodes;
	
	protected Action action;
	
	int id;
	
	protected int reward;

	public UserPlanNode(State s, double probability, Action action) {
		super();
		this.label = s.getLabel();
		this.probability = probability;
		this.nextNodes = new ArrayList<UserPlanNode>();
		this.action = action;
		this.reward = s.getReward();
	}

	public UserPlanNode(State s, double probability, UserPlanNode parentNode, Action action) {
		this(s, probability, action);
		this.parentNode = parentNode;
	}
	
	public UserPlanNode(State s, double probability) {
		super();
		this.label = s.getLabel();
		this.probability = probability;
		this.nextNodes = new ArrayList<UserPlanNode>();
		id = s.getId();
	}

	public int getId() {
		return id;
	}

	/**
	 * Adds a new possible node after the current one to the potential future
	 * plans.
	 * 
	 * @param node
	 */
	public void addNextNode(UserPlanNode node) {
		this.nextNodes.add(node);
		node.parentNode = this;
	}

	public List<UserPlanNode> getNextNodes() {
		return nextNodes;
	}

	public UserPlanNode getParentNode() {
		return parentNode;
	}

	public String getLabel() {
		return label;
	}
	
	public int getReward() {
		return reward;
	}


	/**
	 * Calculates the probability that this node in the user plan will ever be
	 * executed. This probability is the result of multiplying the probabilities
	 * of all nodes in the path to the root.
	 * 
	 * @return
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Returns the deadline for the execution of this node.
	 * 
	 * @return
	 */
	public int getDeadline() {
		return deadline;
	}

	
	public String toOldString() {
		return "UserPlanNode: '" + label + "' deadline=" + deadline + " prob="	+ probability + "  " + action.getName();
	}
	
	@Override
	public String toString() {
		return "UserPlanNode: " + label + " prob="	+ probability + " " + nextNodes.toString();
	}

	/**
	 * Method to accept a visitor
	 * 
	 * @param visitor
	 * @return
	 */
	public boolean accept(UserPlanNodeVisitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Returns true if the given query is relevant to this plan node i.e.,
	 * matches with the ID (query) Note: this implementation became very
	 * specific to grid/door problem :-( need something more generic here.
	 * 
	 * @param query
	 * @return
	 * @author jeanoh
	 */
	public boolean isRelevant(String stateId) {
		if(stateId == null) return false;
		// id is either stateID (beliefState) or a query (userPlan)
		int indexDash = this.label.indexOf("-");
		if (indexDash < 0) { // state ID only
			return stateId.equals(this.label);
		} else // id contains a query "[]-[]"
			return label.substring(0, label.indexOf("-")).equals(stateId);
	}
	
	/**
	 * returns true if the plan contains a node for given query
	 * @param query
	 * @return
	 */
	public boolean hasNodeFor(String query) {
		if(this.label.equals(query)) return true;
		for(UserPlanNode childNode: this.nextNodes) {
			if(childNode.hasNodeFor(query)) return true;			
		}
		return false;
	}
	
	public Action getAction() {
		return this.action;
	}
}
