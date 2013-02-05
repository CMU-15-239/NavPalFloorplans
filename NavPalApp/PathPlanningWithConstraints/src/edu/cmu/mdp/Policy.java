package edu.cmu.mdp;

import java.util.List;
import java.util.logging.Logger;

import edu.cmu.recognizer.State;

public class Policy {

	/**
	 * double[nStates][nActions]
	 */
	double[][] probability;
	public final static double EPSILON = 0.0001; // allow small errors
	Logger logger = Logger.getLogger(getClass().getName());
	
	int nStates;
	int nActions;

	public Policy(int nStates, int nActions) {
		probability = new double[nStates][nActions];
		this.nStates = nStates;
		this.nActions = nActions;
	}

	public double[] get(int state) {
		return probability[state];
	}

	public double get(int state, int action) {
		return probability[state][action];
	}

	public void set(int state, int action, double p) {
		probability[state][action] = p;
	}

	/**
	 * check the values for each state sum to 1 modulo small error<EPSILON
	 * 
	 * @return true if sum is close to 1; false otherwise
	 */
	public boolean validate() {

		for (int s = 0; s < probability.length; ++s) {
			double sum = 0;
			for (int a = 0; a < probability[0].length; ++a) {
				sum += get(s, a);
			}
			if (Math.abs(sum - 1) > EPSILON) {
				logger.warning("Policy validation failed at state [" + s + "].");
				print();
				return false;
			}
		}
		return true;
	}

	public void print() {
		System.out.println(toString());
	}

	public void print(List<State> states) {
		String ret = "\n--- Policy (s x a)---\n \t\n";
		for (int s = 0; s < probability.length; s++) {
			ret += ("s" + states.get(s).getLabel() + "\t");
			for (int a = 0; a < probability[a].length - 1; ++a) {
				ret += (probability[s][a] + ", ");
			}
			ret += ("\n");
		}
		System.out.println(ret);
	}

	@Override
	public String toString() {
		String ret = "\n--- Policy (s x a)---\n \t";

		ret += "\n";
		for (int s = 0; s < probability.length; ++s) {
			ret += ("s" + s + "\t");
			for (int a = 0; a < probability[a].length; ++a) {
				ret += (probability[s][a] + ", ");
			}
			ret += ("\n");
		}
		return ret;
	}
	
	public void reset(){
		for(int i = 0; i < nStates; i++)
			for(int j = 0; j < nActions; j++)
				probability[i][j] = 0;
	}
}