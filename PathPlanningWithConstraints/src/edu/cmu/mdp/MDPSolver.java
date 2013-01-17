//jeanoh@cs.cmu.edu
package edu.cmu.mdp;

import java.io.File;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Environment;
import edu.cmu.Action;
import edu.cmu.recognizer.State;

/**
 * Solver for MDP (S,A,Tr,R): states, actions, transition probabilities, rewards
 * jeanoh@cs.cmu.edu
 */
public class MDPSolver {

	DynamicMDPProblem _mdp;

	public static int _MOD = 50;
	static double _EPSILON = 0.001;

	public static final int VI = -9; // value iteration
	int algorithm = VI;
	private static Logger logger = Logger.getLogger(MDPSolver.class.getName());

	public MDPSolver(DynamicMDPProblem mdp) {
		_mdp = mdp;
	}

	public void solve(int nIterations) {
		switch (algorithm) {
		case VI:
		default:
			// reset state values
			for (State s : _mdp.getStates())
				s.setValue(0);
			valueIteration(nIterations);
			break;
		}
	}

	/**
	 * @author Piotr Method to write an xml file that will contain a saved state
	 *         of the current policy
	 */
	private void writeXML() {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("States");
			doc.appendChild(rootElement);

			for (State state : _mdp.getStates()) {
				Element stateElement = doc.createElement(state.getLabel());
				stateElement.appendChild(doc.createTextNode(String
						.valueOf(state.getValue())));
				rootElement.appendChild(stateElement);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			// Create file
			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()) {
				StreamResult result = new StreamResult(new File(root,
						"Recognizer/savedPolicy.xml"));

				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
				transformer.transform(source, result);

				System.out.println("File saved!");
			}

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}

	String currentStateName;

	/**
	 * @author Piotr Method to read an xml that contains a saved policy
	 */
	public void readXML() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					currentStateName = qName;
				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {
				}

				public void characters(char ch[], int start, int length)
						throws SAXException {
					_mdp.getStateByLabel(currentStateName).setValue(
							Double.parseDouble(new String(ch, start, length)));
				}
			};

			File root = Environment.getExternalStorageDirectory();
			File outFile = new File(new File(root, ""), "Recognizer/savedPolicy.xml");
			saxParser.parse(outFile, handler);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Value Iteration (Sutton&Barto 1998, p102) returns policy in HashMap
	 * (state -> action)
	 */
	public void valueIteration(int nIterations) {
		double delta = 99999;
		// int convcnt = 0;

		// while(true){ //|S| x |A| x |S|
		for (int iter = 0; iter < nIterations; ++iter) {
			// boolean cv = true;
			int convcnt = 0;
			for (State s : _mdp.getStates()) {
				double v = s.getValue();

				// DEBUG
				// if(s.getId() == 0) System.out.println("State"+ s.getLabel());
				Vector<Double> acts = new Vector<Double>(s.getActions().size());
				// Vector acts = new Vector(_mdp.getActions().size()); //
				// expected
				// value of
				// each
				// action
				for (Action action : s.getActions()) {
					// System.out.print(action.getName()+ "  ");
					double vA = 0;
					for (State state2 : _mdp.getStates()) { // for each next
															// state

						// DEBUG
						// if(s.getId() == 0
						// && _mdp.getTransitionProbability(s.getId(),action,
						// state2.getId()) > 0
						// //&& s.getId() == state2.getId()
						// ) {
						// System.out.println("\t" + action.getName() + "->[" +
						// state2.getLabel() + "] "
						// + "T" +
						// _mdp.getTransitionProbability(s.getId(),action,
						// state2.getId())
						// + " R=("+ _mdp.getReward(s.getId(), action,
						// state2.getId())
						// + "+" + (_mdp.getDiscountFactor() *
						// state2.getValue()) + ")");
						// }

						vA += (_mdp.getTransitionProbability(s.getId(), action,
								state2.getId()) * (_mdp.getReward(s.getId(),
								action, state2.getId()) + (_mdp
								.getDiscountFactor() * state2.getValue())));
					}
					acts.add(vA);
					// DEBUG
					// if(iter == 2) System.out.println("  action=" +
					// action.getName() + " rew=" + action.getCost());
				}
				// value of the best action
				double v2 = (Collections.max(acts)).doubleValue();
				s.setValue(v2);
				// DEBUG
				// if(s.getId() == 0) System.out.println(" value=" + v2);

				delta = Math.abs(v - v2);
				// if (delta > _EPSILON) cv = false;
				if (delta < _EPSILON)
					++convcnt;
			}
			// if (cv) ++convcnt;

			if (iter % _MOD == 0) {
				logger.info("iter=" + iter + ", delta=" + delta);
				// printStateValues();
			}
			++iter;
			if (convcnt == _mdp.getStates().size()) { // (convcnt > 10) {
				System.out.println("Converged! iter=" + iter);
				writeXML();
				// return computeDeterministicPolicy();
				return;
			}
		}
		System.out.println("Fail to converge after " + nIterations + " iter");
		// return computeDeterministicPolicy();
	}

	public Policy computeBoltzmannPolicy(double temp) {

		Policy policy = new Policy(_mdp.getStates().size(), _mdp.getActions()
				.size()); // deterministic policy
		for (State s : _mdp.getStates()) {

			int nActions = s.getActions().size();
			// normalized values for expected value of taking this action from
			// the current state
			double[] normValues = new double[nActions];
			// old range of values must be collected from the current values
			double sum = 0;

			for (int actionId = 0; actionId < nActions; ++actionId) {
				Action action = s.getAction(actionId);
				double vA = 0;
				for (State state2 : _mdp.getStates()) { // for each next state
					vA += (_mdp.getTransitionProbability(s.getId(), action,
							state2.getId()) * state2.getValue());
				}
				double v = Math.exp(vA / temp);
				if (Double.isInfinite(v) || Double.isNaN(v)) {
					System.out.println("boltzmann: NaN or Inf v=" + v);
				}
				normValues[actionId] = v;
				sum += v;
			}

			for (int actionId = 0; actionId < nActions; ++actionId) {
				normValues[actionId] /= sum;
				if (sum == 0)
					policy.set(s.getId(), actionId, 1.0 / nActions); // if sum
																		// is 0,
																		// uniform
																		// distribution
				else
					policy.set(s.getId(), actionId, normValues[actionId]);
			}
			// logger.info(s.getLabel() + " ==> " + act2prob);
		}
		return policy;
	}

	/**
	 * returns HashMap of stateIndex -> HashMap (action -> prob of taking the
	 * action)
	 */
	public Policy computeStochasticPolicy() {
		Policy policy = new Policy(_mdp.getStates().size(), _mdp.getActions()
				.size()); // deterministic policy
		for (State s : _mdp.getStates()) {
			int nActions = s.getActions().size();
			// normalized values for expected value of taking this action from
			// the current state
			double[] normValues = new double[nActions];
			// old range of values must be collected from the current values
			double maxReward = -1;
			double minReward = 0;

			for (int actionId = 0; actionId < nActions; ++actionId) {
				Action action = s.getAction(actionId);
				double vA = 0;
				for (State state2 : _mdp.getStates()) { // for each next state
					vA += (_mdp.getTransitionProbability(s.getId(), action,
							state2.getId()) * state2.getValue());
				}
				normValues[actionId] = vA;
				if (vA > maxReward)
					maxReward = vA;
				if (vA < minReward)
					minReward = vA;
			}
			// normalize state values that may be negative due to costs
			// into *positive* range [0,1]
			double sum = 0;
			for (int actionId = 0; actionId < nActions; ++actionId) {
				double p = normalize(minReward, maxReward, 0, 1,
						normValues[actionId]);
				sum += p;
				// logger.info("  norm[" + minReward + "," + maxReward + "]->v="
				// + normValues[actionId] + "=>" + p);
				normValues[actionId] = p;
			}
			// probability distribution: the values must sum to 1
			for (int actionId = 0; actionId < nActions; ++actionId) {
				double p = 0;
				if (sum == 0)
					p = 1.0 / nActions; // if sum is 0, uniform distribution
				else
					p = normValues[actionId] / sum;
				policy.set(s.getId(), actionId, p);
			}
			// logger.info(s.getLabel() + " ==> " + act2prob);
		}
		policy.validate();
		return policy;
	}

	/**
	 * Returns a normalized value for a given value in range [oldLow, oldHigh]
	 * into a new range [newLow, newHigh].
	 * 
	 * @param oldLow
	 * @param oldHigh
	 * @param newLow
	 * @param newHigh
	 * @param value
	 * @return
	 */
	public static double normalize(double oldLow, double oldHigh,
			double newLow, double newHigh, double value) {
		if (oldHigh == oldLow)
			return value;
		return (((value - oldLow) / (oldHigh - oldLow)) * (newHigh - newLow))
				+ newLow;
	}
}
