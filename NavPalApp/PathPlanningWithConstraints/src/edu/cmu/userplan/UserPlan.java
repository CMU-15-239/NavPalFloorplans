package edu.cmu.userplan;


public class UserPlan {
	protected UserPlanNode rootNode;
	private StringPrinterVisitor printer = new StringPrinterVisitor();
	
	public UserPlan(UserPlanNode rootNode) {
		this.rootNode = rootNode;
	}
	
	/**
	 * Returns the root node from the planning tree.
	 * @return
	 */
	public UserPlanNode getRootNode() {
		return this.rootNode;
	}
	
	@Override
	public String toString() {
		return printer.toString(rootNode);
	}
	
	public UserPlanNode getRoot(){ return rootNode;}

}
