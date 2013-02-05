/**
 * @author jeanoh@cs.cmu.edu
 * @date Nov. 10, 2009
 */
package edu.cmu.mdp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.cmu.Action;
import edu.cmu.recognizer.State;

public class DynamicMDPProblem implements MDPProblem {

	protected List<State> states;
	HashMap<String, State> _stateMap; // label -> state

	protected List<Action> actions;

	protected HashMap<Action, TransitionMatrix> transitionMatrices;

	protected double discount;
	
	/** number of goals */
	private int nGoalSize = 0;
	
	/**
	 * The reward is defined as a weighted sum of reward from resulting state
	 * and the cost of taking action. Parameter gamma defines the weight of reward 
	 * from a resulting state; thus the weight of action cost is 1 - gamma.
	 */
	//public static double weightOfReward = 0.7; 

	Logger logger = Logger.getLogger(getClass().getName());
	
	public DynamicMDPProblem(List<State> states, List<Action> actions,
			List<TransitionMatrix> matrices, double discount, HashMap<String, State> stateHash) {
		this.states = states;
		this.actions = actions;
		this.transitionMatrices = new HashMap<Action, TransitionMatrix>(
				matrices.size());
		for (TransitionMatrix matrix : matrices) {
			transitionMatrices.put(matrix.action, matrix);
		}
		this.discount = discount;		
		_stateMap = stateHash;
	}

	public void setDiscountFactor(double d) {
		discount = d;
	}
	
	@Override
	public State getState(int index) {
		return states.get(index);
	}

	public State getStateByLabel(String label) {
		return _stateMap.get(label);
	}

	/**
	 * Returns reward of taking action act from state s and reaching state sp as a result.
	 * Note that the reward is a weighted sum of the reward from resulting state 
	 * and the cost of taking action. Field weightOfReward defines the weight of reward, 
	 * and the weight of cost is (1-weightOfReward) accordingly.    
	 * @param s -- current state
	 * @param act -- action to take
	 * @param sp -- resulting state after taking action act from state s
	 */
	@Override
	public double getReward(int s, Action act, int sp) {
		// reward from the resulting state
		double reward = ((states.get(sp))).getReward();
		// cost of taking this action
		double cost = act.getCost();
		//return (weightOfReward * reward) + ((1 - weightOfReward) * cost);		
		return reward + cost;
	}

	@Override
	public double getDiscountFactor() {
		return discount;
	};

	@Override
	public List<Action> getActions() {
		return actions;
	}

	@Override
	public List<State> getStates() {
		return states;
	}

	@Override
	public Map<Action, TransitionMatrix> getTransitionMatrices() {
		return transitionMatrices;
	}

	@Override
	public double getTransitionProbability(int s, Action act, int sp) {
		TransitionMatrix tm = transitionMatrices.get(act);
		if(tm == null) {
			logger.info("act=" + act.getId() + "," + act.hashCode());// + "; m=" + this.transitionMatrices);
			for(Iterator<Action> i=this.transitionMatrices.keySet().iterator(); i.hasNext();) {
				Action action = i.next();
				System.out.println("  a=" + act + "; b=" + action );
			}
			System.exit(0);
		}		
		return tm.get(s, sp);
	}
	
	@Override
	public Action getAction(int index) {
		return actions.get(index);
	}

	public Action getAction(int stateIndex, int actionIndex) {
		State state = getState(stateIndex);
		return state.getActions().get(actionIndex);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("discount: " + this.discount
				+ System.getProperty("line.separator"));
		StringBuilder sbStates = new StringBuilder();
		StringBuilder sbRewards = new StringBuilder();
		for (State state : states) {
			sbStates.append(" " + state.getId());
			sbRewards.append(" " + state.getValue());
		}

		sb.append("states:" + sbStates.toString()
				+ System.getProperty("line.separator"));
//		sb.append("rewards:" + sbRewards.toString()
//				+ System.getProperty("line.separator"));

		sb.append("actions:");
		//sb.append(System.getProperty("line.separator"));
		for (Action action : actions) {
			sb.append(" " + action);
			//sb.append(System.getProperty("line.separator"));
		}
		sb.append(System.getProperty("line.separator"));

		for (TransitionMatrix matrix : transitionMatrices.values()) {
			sb.append(System.getProperty("line.separator") + matrix.toString());
		}
		sb.append(System.getProperty("line.separator"));

		sb.append("rewards+cost:");
		sb.append(System.getProperty("line.separator"));
		for (State state: states) {
			sb.append(state.getId() + "\n");			
			for (Action action: state.getActions()) {
				sb.append(" " + action.getName() + "=" + getReward(state.getId(), action, state.getId()));
				sb.append(System.getProperty("line.separator"));
			}
			sb.append(System.getProperty("line.separator"));
		}	
		
		return sb.toString();
	}

	public int getGoalSize() {
		return nGoalSize;
	}
	
}
