/**
 * @author yordanov.piotr@gmail.com
 * @date Jul. 5, 2011
 */

package edu.cmu.recognizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import edu.cmu.Action;
import edu.cmu.MainInterface;
import edu.cmu.R;
import edu.cmu.Room;
import edu.cmu.future.MarkovFuturePlanRecognizer;
import edu.cmu.mdp.DynamicMDPProblem;
import edu.cmu.mdp.MDPSolver;
import edu.cmu.mdp.Policy;
import edu.cmu.mdp.TransitionMatrix;
import edu.cmu.userplan.UserPlan;
import edu.cmu.userplan.UserPlanNode;


/**
 *
 * @author Piotr Yordanov
 */

public class PlanRecognizer implements ThreadCallBack  {
	List<Action> actions;
	List<State> states;
	BeliefState beliefState;
	ArrayList<TransitionMatrix> transitionMatrix;

	MarkovFuturePlanRecognizer futur = null;
	DynamicMDPProblem mdp;
	MDPSolver solver;	

	MainInterface mainInterface;
	Properties properties = new Properties();

	List<UserPlanNode> bestPath = new ArrayList<UserPlanNode>();
	public HashMap<String, State> stateHash; // used to be able to rebuild the room path   stateHash<label, state>
	HashMap<State, Room> stateToRoomHash; // used to get a room out of a state

	boolean needToRecomputeMDP = true;

	static int graphCount = 0;
	static int logCount = 0;
	static int errorCount = 0;


	/**
	 * Instantiates a new plan recognizer.
	 */
	public PlanRecognizer(MainInterface mainInterface) {
		this.mainInterface = mainInterface;
	}

	/**
	 * once we have states and actions we can at least start the solver
	 */
	public void init(ArrayList<Room> rooms, Vector<Action> allActions) {
		setRooms(rooms);
		setActions(allActions);
		if(actions == null) {
			System.out.println("Recognizer: actions are null");
			return;
		}
		else if( states == null ){
			System.out.println("Recognizer: states are null");
			return;
		}
		transitionMatrix = createTransitionMatrix(actions,	states.size());

		mdp = new DynamicMDPProblem(states, actions, transitionMatrix, 0.9, stateHash);

		// Create the thread that will run the solver
		SolverThread mt = new SolverThread(this, this);
		Thread t = new Thread(mt);
		t.start();



		// Delete logs
		String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		String dirName = "/Recognizer/Logs/";
		File f = new File(baseDir + File.separator + dirName);
		f.delete();
		(new File(baseDir + File.separator + dirName)).mkdir();

		dirName = "/Recognizer/Graphs/";
		f = new File(baseDir + File.separator + dirName);
		f.delete();
		(new File(baseDir + File.separator + dirName)).mkdir();


	}

	private void loadResources() {
		Resources resources = mainInterface.getResources();
		try {
			InputStream rawResource = resources.openRawResource(R.raw.roomnames);
			properties.load(rawResource);
		} catch (NotFoundException e) {
			System.err.println("Did not find raw resource: "+e);
		} catch (IOException e) {
			System.err.println("Failed to open microlog property file");
		}
	}

	/**
		 __   ___ ___ ___  ___  __      ___            __  ___    __        __  
		/__` |__   |   |  |__  |__)    |__  |  | |\ | /  `  |  | /  \ |\ | /__` 
		.__/ |___  |   |  |___ |  \    |    \__/ | \| \__,  |  | \__/ | \| .__/ 

	 **/

	public synchronized void setBeliefState(BeliefState newBeliefState) {
		System.out.println("Recognizer: -----------------------");
		System.out.println("Recognizer: got new bs");
		if (futur != null) {
			beliefState = newBeliefState;
			futur.setBeliefState(beliefState);
			predict();
		}
		else 
			System.out.println("Futur is null");
		System.out.println("Recognizer: Finished working");
	}

	public void setRooms(ArrayList<Room> rooms) {
		loadResources();
		setStates(rooms);
	}

	public void setActions(Vector<Action> newActions) {
		int count = 0;
		for(Action action : newActions)
			action.setId(count++);

		for( State state : states) {
			state.addActions(newActions);
		}
		actions = newActions;

		System.out.println("Recognizer: succesfully created the Actions");
	}	

	/**
		 __   __              ___  ___     ___            __  ___    __        __  
		|__) |__) | \  /  /\   |  |__     |__  |  | |\ | /  `  |  | /  \ |\ | /__` 
		|    |  \ |  \/  /~~\  |  |___    |    \__/ | \| \__,  |  | \__/ | \| .__/ 

	 **/     	


	/**
	 * Given a list of Rooms, this method will generate the states
	 */
	private  void setStates(ArrayList<Room> rooms) {
		states = new ArrayList<State>();
		stateHash = new HashMap<String, State>(); 
		stateToRoomHash = new HashMap<State, Room>(); 	

		for( Room room : rooms) {
			State tempState = new State(room.getID(), room.getLabel(), 0);
			states.add(tempState);
			stateHash.put(tempState.getLabel(), tempState);
			stateToRoomHash.put(tempState, room);
		}

		stateHash.get("R1617").setReward(8); 	 // 1617
		stateHash.get("R1602C").setReward(10);	 // 1602C	 
		stateHash.get("R1502E").setReward(6);		 // 1502E
		System.out.println("Recognizer: succesfully created the states");

	}		


	/**
	 * Creates the transition matrix.
	 *
	 * @param actions the actions the user can take
	 * @param size the size of the state space ie number of states
	 * @return the transition matrix
	 */
	private  ArrayList<TransitionMatrix> createTransitionMatrix(
			List<Action> actions,int size) {
		ArrayList<TransitionMatrix> trans = new ArrayList<TransitionMatrix>(states.size());				

		for (int i = 0; i < actions.size(); i++) {			
			TransitionMatrix m0  = new TransitionMatrix(size, actions.get(i));			
			trans.add(m0);
		}

		return trans;
	}

	/**
	 * Predict will generate a UserPlan that contains a list of all possible predicted user plans.
	 * It will also ping the path planner add send it the new destination
	 *
	 * @param futur a futurePlanRecognizer object
	 */
	private synchronized void predict() {
		UserPlan plan = null;
		try{
			if(actions == null) {
				System.out.println("Recognizer: actions are null");
				return;
			}
			else if( states == null ){
				System.out.println("Recognizer: states are null");
				return;
			}
			else if( beliefState == null) {
				System.out.println("Recognizer: beliefState is null");
				return;
			}
			System.out.println("Recognizer: Predicting");	
			plan = futur.predictFutureSteps();
			System.out.println("Recognizer: Finished predicting");

			// Grab the list of generated paths
			System.out.println("Recognizer: Generating Possible plans that come from the tree");
			maxValue = 0;
			List<UserPlanNode> templist = new ArrayList<UserPlanNode>();
			buildLists(plan.getRoot(),templist);
			System.out.println("Recognizer: Finished generating Possible Plans");


			System.out.println("Recognizer: figuring out which room is the new destination");
			Room destinationRoom = getNewDestination();
			if(destinationRoom != null){
				generateLogFile(plan,"graph");
				System.out.println("Recognizer: predicted desination is " + destinationRoom.getLabel());
				mainInterface.placeDest(destinationRoom);
			}
			else {
				generateLogFile(plan,new String("error"));
				System.out.println("got an error:");
			}
		}
		catch(Exception e){
			e.printStackTrace();
			generateLogFile(plan,"error");
		}
	}


	/**
	 * This function will recursively find the paths under the root of the plan.
	 *
	 * @param node is current node
	 * @param tempList is a temp list that contains all the nodes we've seen this far
	 * @param reward is the cumulative reward
	 */
	double maxValue = 0;
	private synchronized void buildLists(UserPlanNode node, List<UserPlanNode> tempList) {
		if (node.getNextNodes().isEmpty()){
			double currentMaxVal = 0;
			// Go through all the elements and compute node.pro * node.reward
			for(UserPlanNode currentNode : tempList) {
				currentMaxVal += currentNode.getProbability() * currentNode.getReward();
			}
			if( currentMaxVal > maxValue ){
				bestPath = tempList;
				maxValue = currentMaxVal;
			}

			return;
		}

		for(UserPlanNode node1 : node.getNextNodes()) {
			List<UserPlanNode> newList = new ArrayList<UserPlanNode>(tempList);
			Collections.copy(newList, tempList);			

			newList.add(node1);

			buildLists(node1,newList);
		}
	}

	/**
	 * This function will return the new destination
	 * @return Room that is the final destination
	 */
	private Room getNewDestination() {
		if(bestPath == null){
			System.out.println("Recognizer: cannot generate a new destination. bestPath == null");
			return null;
		}
		if(bestPath.size() == 0){
			System.out.println("Recognizer: cannot generate a new destination. bestPath.size() == 0");
			return null;
		}
		String label = bestPath.get(bestPath.size()-1).getLabel();
		State tempState = stateHash.get(label);
		return stateToRoomHash.get(tempState);
	}




	/**
	 * @author Piotr	
	 * Method to generate a log file of everything that is running
	 */
	private void generateLogFile(UserPlan plan, String type){
		File root = Environment.getExternalStorageDirectory();
		if(type.equals("graph")){
			System.out.println("Recognizer: logCount " + graphCount );
			if (root.canWrite()){
				try{
					FileWriter fstream = new FileWriter(root.toString() + "/Recognizer/Graphs/graph" + graphCount++ + ".dot");
					BufferedWriter out = new BufferedWriter(fstream);
					out.write("digraph G {\n");


					if(bestPath.size() < 2) {
						String prevNode = (String) properties.get(bestPath.get(0).getLabel());
						System.out.println("Jim c'est: " +prevNode);
						out.write(prevNode + "\n}\n");	
					}
					else {
						//					String prevNode = (String) properties.get(bestPath.get(0).getLabel());
						//					String currNode = (String) properties.get(bestPath.get(1).getLabel());
						//					for(int i=1; i < bestPath.size() - 1; i++){
						//						out.write(prevNode + " -> " + currNode + "\n");	
						//						prevNode = (String) properties.get(bestPath.get(i).getLabel());
						//						currNode = (String) properties.get(bestPath.get(i+1).getLabel());
						//					}
						//					out.write("}\n");
						String prevNode = bestPath.get(0).getLabel();
						String currNode = bestPath.get(1).getLabel();
						System.out.println(prevNode);
						System.out.println(currNode);

						for(int i=1; i < bestPath.size() - 1; i++){
							out.write(prevNode + " -> " + currNode + "\n");	
							prevNode = bestPath.get(i).getLabel();
							currNode = bestPath.get(i+1).getLabel();
							System.out.println(prevNode);
							System.out.println(currNode);						
						}
						out.write("}\n");
					}
					out.close();
					generateLogFile(plan, "log");
				}catch (Exception e){
					e.printStackTrace();
					generateLogFile(null, "error");
				}
			}
		}
		else if(type.equals("error")){
			System.out.println("Recognizer: logging error");
			try{
				FileWriter fstream = new FileWriter(root.toString() + "/Recognizer/Logs/Error" + errorCount++);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("Recognizer: beliefState \n" + beliefState.toStringShort());
				out.write("\nRecognizer: plan " + plan);

				out.write("Recognizer: bestPath \n");
				for(UserPlanNode node : bestPath){
					out.write(node.toString());
				}

				out.write("Recognizer: states \n");
				for(State state : states){
					out.write(state.toString());
				}

				out.write("Recognizer: actions \n");
				for(Action action : actions){
					out.write(action.toString());
				}


				out.close();
			}catch (Exception et){
				System.err.println("Error: " + et.getMessage());
				et.printStackTrace();
			}
		}
		else if( type.equals("log")) {
			// Create the log file
			System.out.println("Recognizer: logging log");
			try{
				FileWriter fstream = new FileWriter(root.toString() + "/Recognizer/Logs/Log" + logCount++);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write("Recognizer: beliefState \n" + beliefState.toStringShort());
				out.write("\nRecognizer: plan " + plan);

				out.write("Recognizer: bestPath \n");
				for(UserPlanNode node : bestPath){
					out.write(node.toString());
				}

				out.write("Recognizer: states \n");
				for(State state : states){
					out.write(state.toString());
				}

				out.write("Recognizer: actions \n");
				for(Action action : actions){
					out.write(action.toString());
				}


				out.close();
			}catch (Exception e){
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}







	/**
	 * Callback function. Called when solver was solved for the first time. Now we can created the mdp and predict
	 */
	@Override
	public synchronized void createMDP(Policy policy){
		futur = new MarkovFuturePlanRecognizer(mdp, policy, 0.25, 5);
		futur.setBeliefState(beliefState); 
		predict();
	}

	/**
	 * Callback function. Method to be called whenever the Solver re-solved. Thus a new policy is created
	 */
	@Override
	public synchronized void callMDPSolver(Policy policy) {
		futur.setPolicy(policy);
		predict();
	}


	public void reset() {
		beliefState.clear();
		states.clear();
		actions.clear();
		bestPath.clear();
		stateHash.clear();
		stateToRoomHash.clear();

		transitionMatrix.clear();

		needToRecomputeMDP = true;
		properties.clear();

		graphCount = logCount = errorCount = 0;
		futur.resetPolicy();
	}
}