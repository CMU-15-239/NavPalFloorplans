package edu.cmu.userplan;

/**
 * A visitor pattern to walk through a user plan
 * @author meneguzzi
 *
 */
public interface UserPlanNodeVisitor {
	/**
	 * Visits a {@link UserPlanNode} within a {@link UserPlan}
	 * @param node
	 * @return
	 */
	public boolean visit(UserPlanNode node);
}
