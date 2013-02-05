package edu.cmu.mdp;

import java.text.DecimalFormat;

import edu.cmu.Action;

public class TransitionMatrix {

	protected double A[][];
	
	private int size;
	
	protected Action action;
	
	private double fp = 0.1;// failure prob
	private double tp = 0.9; // transition prob
	

	/**
	 * Creates a new Transition Matrix with the specified number of states and
	 * initializes the transition probabilities to zero.
	 * 
	 * @param n
	 *            The number of states in the transition matrix
	 */
	public TransitionMatrix(int n) {
		A = new double[n][n];	
		size = n;
		clear();
	}


	/**
	 * Creates a new TransitionMatrix with the specified number of states for 
	 * the specified action
	 * @param n
	 * @param actionName
	 */
	public TransitionMatrix(int n, Action action) {
		this(n);
		this.action = action;
		set(action.to,action.from,tp);
		set(action.from,action.to,tp);
		
		set(action.from,action.from,fp);
		set(action.to,action.to,fp);	
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				A[i][j] = 0;
			}
		}
	}
	/**
	 * Get a single element.
	 * 
	 * @param i Row index.
	 * @param j Column index.
	 * @return A(i,j)
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public double get(int i, int j) {
		return A[i][j];
	}

	/**
	 * Set a single element.
	 * 
	 * @param i Row index.
	 * @param j Column index.
	 * @param s A(i,j).
	 * @exception ArrayIndexOutOfBoundsException
	 */

	public void set(int i, int j, double s) {
		A[i][j] = s;
	}
	
	/**
	 * Returns the number of states in this transition matrix. This corresponds
	 * to both dimensions of this matrix, as it is square.
	 * @return
	 */
	public int getStateCount() {
		return A.length;
	}
	
	/**
	 * Normalizes the probabilities in each row so they sum up to one, with the
	 * specified probability of an error (and no state transition). The error
	 * probability specifies the chance of an action causing no state 
	 * transition at all. If the already matrix included the possibility of 
	 * remaining in the same state, we ignore the error probability. 
	 * @param errorProbability
	 */
	public void normalizeToOne(double errorProbability) {
		for(int i=0; i< A.length; i++) {
			float divisor = 0;
			for(int j=0; j< A[0].length; j++) {
				divisor += A[i][j];
			}
			//To avoid a divide by zero error
			//TODO check if this is actually the desired behavior
			//If the divisor is one, it means this state always transitions to 
			//itself when this action is executed
			if(divisor == 0) {
				A[i][i] = 1;
				continue;
			}
			//double discountError = errorProbability / divisor;
			for(int j=0; j< A[0].length; j++) {
				//Added the second clause in the test for the error
				//If we expect a transition into the same state (regardless of error)
				//Then we normalize that transition without the error probability
				if(i==j && A[i][j] == 0) {
					A[i][j] = errorProbability;
				} else {
					A[i][j] = (A[i][j] != 0) ? ((A[i][j] - errorProbability)/divisor) : 0;
					//A[i][j] = (A[i][j] != 0) ? A[i][j] - discountError : 0;
				}
			}
		}
	}
	
	/**
	 * Populates the transition matrix with data from a Plan Library
	 * @param library
	 
	public void populateTransitionMatrix(PlanLibrary library) {
		List<PlanTreeNode> states = library.getObservableNodes();
		//Adjust size if the number of states is larger than matrix order
		if(states.size() != A.length) {
			A = new double[states.size()][states.size()];
		}
		for(int i=1; i<states.size(); i++) {
			PlanTreeNode si = states.get(i);
			//PlanTreeNode sj = (PlanTreeNode) si.getParent();
			if(si.getLabel().equals(action.getName())) {
				List<PlanTreeNode> previous = si.getPreviousObservable();
				for(PlanTreeNode sj:previous) {
					int j = states.indexOf(sj);
					//The matrix is indexed with [j][i] because the table represents 
					//states BEFORE the action into states AFTER the action, thus
					//from sj to si
					A[j][i] = 1;
				}
			}
		}
		
	}
	*/
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		DecimalFormat df = new DecimalFormat("0.000");
		if(this.action != null) // check if action is non-null
			sb.append("T: "+this.action.getName()+System.getProperty("line.separator"));
		for(double[] line:A) {
			for(double column:line) {
				sb.append(df.format(column)+" ");
			}
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}
}
