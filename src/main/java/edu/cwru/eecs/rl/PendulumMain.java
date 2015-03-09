package edu.cwru.eecs.rl;

import edu.cwru.eecs.rl.agent.PolicySampler;
import edu.cwru.eecs.rl.basisfunctions.FakeBasis;
import edu.cwru.eecs.rl.basisfunctions.GaussianRbf;
import edu.cwru.eecs.rl.core.lspi.Lspi;
import edu.cwru.eecs.rl.domains.Pendulum;
import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.BasisFunctions;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import Jama.Matrix;

import java.util.List;

public class PendulumMain {

    /**
     * Main method. Runs the Pendulum domain with known working settings.
     * These settings are taken from the Lspi paper and original Matlab code.
     *
     * @param args Not used
     */
    public static void main(String[] args) {
        System.out.println("Hello Lspi");
        
        System.out.println("Initializing pendulum domain");
        
        Simulator simulator = new Pendulum();
        BasisFunctions fakeBasis = new FakeBasis();
        BasisFunctions rbfBasis = new GaussianRbf(3, 3, 3);
        Policy randomPolicy = new Policy(1, 
                simulator.numActions(), 
                fakeBasis,
                Matrix.random(fakeBasis.size(), 1));
        Policy learnedPolicy = new Policy(0,
                simulator.numActions(), 
                rbfBasis,
                Matrix.random(rbfBasis.size(), 1));

        System.out.println("Sampling 1000 episodes with 50 steps using random policy");
        List<Sample> samples = PolicySampler.sample(simulator, 1000, 50, randomPolicy);
        
        System.out.println("Running Lspi");
        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 20);
        
        System.out.println("Evaluating random and learned policy");
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 1000, 50, randomPolicy);
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 1000, 50, learnedPolicy);
        
        System.out.println("Random Policy Average Rewards: " + avgRandomRewards);
        System.out.println("Learned Policy Average Rewards: " + avgLearnedRewards);

    }

}
