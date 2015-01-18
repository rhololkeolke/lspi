package edu.cwru.eecs.rl;

import java.util.List;
import java.util.Random;

import Jama.Matrix;

import edu.cwru.eecs.rl.agent.PolicySampler;
import edu.cwru.eecs.rl.basisFunctions.FakeBasis;
import edu.cwru.eecs.rl.basisFunctions.GaussianRBF;
import edu.cwru.eecs.rl.core.lspi.LSPI;
import edu.cwru.eecs.rl.domains.Pendulum;
import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.BasisFunctions;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;

public class PendulumMain {

	public static void main(String[] args) {
		System.out.println("Hello LSPI");
		
		System.out.println("Initializing pendulum domain");
		
		Simulator simulator = new Pendulum();
		BasisFunctions fake_basis = new FakeBasis();
		BasisFunctions rbf_basis = new GaussianRBF(3, 3, 3);
		Policy randomPolicy = new Policy(1, 
				simulator.numActions(), 
				fake_basis,
                Matrix.random(fake_basis.size(), 1));
		Policy learnedPolicy = new Policy(0,
				simulator.numActions(), 
				rbf_basis,
                Matrix.random(rbf_basis.size(), 1));

		System.out.println("Sampling 1000 episodes with 50 steps using random policy");
		List<Sample> samples = PolicySampler.sample(simulator, 1000, 50, randomPolicy);
		
		System.out.println("Running LSPI");
		learnedPolicy = LSPI.learn(samples, learnedPolicy, .9, 1e-5, 20);
		
		System.out.println("Evaluating random and learned policy");
		double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 1000, 50, randomPolicy);
		double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 1000, 50, learnedPolicy);
		
		System.out.println("Random Policy Average Rewards: " + avgRandomRewards);
		System.out.println("Learned Policy Average Rewards: " + avgLearnedRewards);

	}

}
