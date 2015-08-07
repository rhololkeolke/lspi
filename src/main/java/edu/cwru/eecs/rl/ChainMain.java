package edu.cwru.eecs.rl;

import edu.cwru.eecs.rl.agent.PolicySampler;
import edu.cwru.eecs.rl.basisfunctions.FakeBasis;
import edu.cwru.eecs.rl.basisfunctions.PolynomialBasis;
import edu.cwru.eecs.rl.core.lspi.Lspi;
import edu.cwru.eecs.rl.domains.Chain;
import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.BasisFunctions;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import Jama.Matrix;

import java.util.List;

public class ChainMain {

    /**
     * Runs the chain domain with Lspi with a set of parameters that are known to work. These
     * parameters are from the paper and original Matlab implementation of Lspi.
     *
     * @param args Now arguments are used
     */
    public static void main(String[] args) {
        System.out.println("Hello Lspi");

        System.out.println("Initializing chain domain with 10 states and 90% success probability");

        Simulator simulator = new Chain(10, .9, 0);
        BasisFunctions fakeBasis = new FakeBasis();
        BasisFunctions polyBasis = new PolynomialBasis(3, simulator.numActions());
        Policy randomPolicy = new Policy(1,
                                         simulator.numActions(),
                                         fakeBasis,
                                         Matrix.random(fakeBasis.size(), 1));
        Policy learnedPolicy = new Policy(0,
                                          simulator.numActions(),
                                          polyBasis,
                                          Matrix.random(polyBasis.size(), 1));

        System.out.println("Sampling 10 episodes with 500 steps using random policy");
        List<Sample> samples = PolicySampler.sample(simulator, 10, 500, randomPolicy);

        System.out.println("Running Lspi");
        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10, Lspi.PolicyImprover.LSTDQ_MTJ);

        System.out.println("Evaluating random and learned policy");
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        System.out.println("Random Policy Average Rewards: " + avgRandomRewards);
        System.out.println("Learned Policy Average Rewards: " + avgLearnedRewards);
    }
}
