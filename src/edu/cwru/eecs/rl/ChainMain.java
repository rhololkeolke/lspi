package edu.cwru.eecs.rl;

import java.util.List;
import java.util.Random;

import Jama.Matrix;
import org.apache.commons.cli.CommandLine;

import edu.cwru.eecs.rl.agent.PolicySampler;
import edu.cwru.eecs.rl.basisFunctions.FakeBasis;
import edu.cwru.eecs.rl.basisFunctions.PolynomialBasis;
import edu.cwru.eecs.rl.core.lspi.LSPI;
import edu.cwru.eecs.rl.domains.Chain;
import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.BasisFunctions;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;

public class ChainMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello LSPI");
		
		System.out.println("Initializing chain domain with 10 states and 90% success probability");
		
		Simulator simulator = new Chain(10, .9, 0);
		BasisFunctions fake_basis = new FakeBasis();
		BasisFunctions poly_basis = new PolynomialBasis(3, simulator.numActions());
		Policy randomPolicy = new Policy(1, 
				simulator.numActions(), 
				fake_basis,
                Matrix.random(fake_basis.size(), 1));
		Policy learnedPolicy = new Policy(0,
				simulator.numActions(), 
				poly_basis,
                Matrix.random(poly_basis.size(), 1));

		System.out.println("Sampling 10 episodes with 500 steps using random policy");
		List<Sample> samples = PolicySampler.sample(simulator, 10, 500, randomPolicy);
		
		System.out.println("Running LSPI");
		learnedPolicy = LSPI.learn(samples, learnedPolicy, .9, 1e-5, 10);
		
		System.out.println("Evaluating random and learned policy");
		double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
		double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);
		
		System.out.println("Random Policy Average Rewards: " + avgRandomRewards);
		System.out.println("Learned Policy Average Rewards: " + avgLearnedRewards);
	}
	
	/**
	 * Takes in an array of strings and parses them as command line arguments
	 * 
	 * @param args
	 * @return
	 */
	public static CommandLine parse(String[] args) {
		return null;
	}

}
