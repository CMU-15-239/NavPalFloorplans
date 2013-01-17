package edu.cmu.recognizer;

import edu.cmu.mdp.Policy;

public interface ThreadCallBack {
	void callMDPSolver(Policy policy);

	void createMDP(Policy policy);

}
