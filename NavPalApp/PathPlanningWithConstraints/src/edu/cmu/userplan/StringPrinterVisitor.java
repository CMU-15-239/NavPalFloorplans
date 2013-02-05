package edu.cmu.userplan;

import java.util.ArrayList;
import java.util.List;

/**
 * A pretty printer for a user plan node, this method of printing is NOT thread-safe, so it is 
 * recommended that one uses a single object of this type for each {@link UserPlan} being printed.  
 * @author meneguzzi
 *
 */
public class StringPrinterVisitor implements UserPlanNodeVisitor {
	private StringBuilder builder = new StringBuilder();
	private int depth;
	private List<Integer> maxStringDepth = new ArrayList<Integer>();

	@Override
	public boolean visit(UserPlanNode node) {
		for(int i=0; i<depth; i++) {
			builder.append("|");
			for(int j=0; j<maxStringDepth.get(i); j++) {
				if(i==depth-1) {
					builder.append("-");
				} else {
					builder.append(" ");
				}
			}
		}
		builder.append(node.getLabel()+" "+System.getProperty("line.separator"));
		if(maxStringDepth.size()>depth) {
			if(maxStringDepth.get(depth) < node.getLabel().length()+1) {
				maxStringDepth.set(depth, node.getLabel().length()+1);
			}
		} else {
			maxStringDepth.add(node.getLabel().length()+1);
		}
		depth++;
		for(UserPlanNode n : node.getNextNodes()) {
			n.accept(this);
		}
		depth--;
		return true;
	}
	
	public String toString(UserPlanNode node) {
		builder.setLength(0);
		if(node != null) {
			depth = 0;
			maxStringDepth.add(0);
			maxStringDepth.clear();
			node.accept(this);
		}
		return builder.toString();
	}
}
