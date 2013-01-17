package edu.cmu.recognizer;

import android.os.Looper;
import edu.cmu.mdp.MDPSolver;

/**
 * This thread will run the solver to generate a new policy
 * 
 * @author Piotr Yordanov
 *
 */
public class SolverThread implements Runnable {
	private final PlanRecognizer planRecognizer;
	Boolean firstRun = true;
	PlanRecognizer callback;

	public SolverThread(PlanRecognizer planRecognizer, PlanRecognizer callback) {
		this.planRecognizer = planRecognizer;
		this.callback = callback;
	}
	public void run() {
		Looper.prepare();
		if (firstRun) {
			System.out.println("Thread: Started Solver");
			this.planRecognizer.solver = new MDPSolver(this.planRecognizer.mdp);
			//TODO Fix this to use some variable way of determining how many iterations one should do before giving up
			//callback.solver.solve(100);
			callback.solver.readXML();
			callback.createMDP(this.planRecognizer.solver.computeStochasticPolicy());
			firstRun = false;
		}
		else {
			//TODO Fix this to use some variable way of determining how many iterations one should do before giving up
			this.planRecognizer.solver.solve(100);
			callback.callMDPSolver(this.planRecognizer.solver.computeStochasticPolicy());
		}
	}
}