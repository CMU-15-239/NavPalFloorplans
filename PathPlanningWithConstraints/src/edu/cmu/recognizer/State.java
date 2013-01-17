package edu.cmu.recognizer;

/**
 * An MDP state, composed of an ID and a reward value;
 * @author meneguzz
 * @modified jeanoh
 */
import java.util.List;
import java.util.Vector;

import edu.cmu.Action;

public class State {
	protected int id;
	protected int reward;

	protected String label;
	double _value = 0;
	Vector<Action> actions; // action queries that are specific to each state
							// --jean

	Action _optAction;

	public State(int id, int reward) {
		super();
		this.id = id;
		this.reward = reward;
		this.actions = new Vector<Action>();
	}

	// This constructor only matters for when we create the root node of the
	// plan.
	// For that node, we only care about its label
	public State(String label) {
		this.label = label;
		reward = 0;
	}

	public State(int id, String label, int reward) {
		this(id, reward);
		this.label = label;
	}

	public State(State s) {
		this._optAction = null;
		this._value = 0;
		this.actions = s.getActions();
		this.id = s.id;
		this.label = s.label;
		this.reward = s.reward;
	}

	public void addAction(List<Action> actions2) {
		actions.add((Action) actions2);
	}

	public void addActions(Vector<Action> a) {
		actions = a;
	}

	public Vector<Action> getActions() {
		return actions;
	}

	public Action getAction(int actionId) {
		return actions.get(actionId);
	}

	public int getId() {
		return id;
	}

	public int getReward() {
		return reward;
	}

	@Override
	public String toString() {
		return "reward=" + reward + " label: " + label + " val: " + _value
				+ "\n";
	}

	// jeanoh
	public String getLabel() {
		return label;
	}

	// optimal policy
	public void setOptimalAction(Action id) {
		_optAction = id;
	}

	public Action getOptimalAction() {
		return _optAction;
	}

	// long-term expected reward of this state
	public void setValue(double v) {
		_value = v;
	}

	public double getValue() {
		return _value;
	}

	public boolean equals(State o) {
		return this.id == o.id;
	}

	public void setReward(int r) {
		this.reward = r;
	}

}
