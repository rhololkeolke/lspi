package edu.cwru.eecs.rl;

import edu.cwru.eecs.rl.agent.PolicySampler;
import edu.cwru.eecs.rl.basisfunctions.ExactBasis;
import edu.cwru.eecs.rl.basisfunctions.FakeBasis;
import edu.cwru.eecs.rl.basisfunctions.PolynomialBasis;
import edu.cwru.eecs.rl.core.lspi.Lspi;
import edu.cwru.eecs.rl.domains.Chain;
import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.BasisFunctions;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import Jama.Matrix;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by Devin on 8/4/14.
 */
public class ChainLearnTests {

    Simulator simulator;
    Policy randomPolicy;
    List<Sample> samples;

    /**
     * Construct a Chain domain and collect a bunch of random samples.
     */
    @Before
    public void setupLearner() {
        simulator = new Chain(10, .9, 0);
        BasisFunctions fakeBasis = new FakeBasis();
        randomPolicy = new Policy(1,
                                  simulator.numActions(),
                                  fakeBasis,
                                  Matrix.random(fakeBasis.size(), 1));

        samples = PolicySampler.sample(simulator, 10, 500, randomPolicy);
    }

    @Test
    public void testChainLearnWithPolyBasis() {

        BasisFunctions polyBasis = new PolynomialBasis(3, simulator.numActions());
        Policy learnedPolicy = new Policy(0,
                                          simulator.numActions(),
                                          polyBasis,
                                          Matrix.random(polyBasis.size(), 1));

        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        // not the greatest test but it should work
        Assert.assertTrue(avgLearnedRewards > avgRandomRewards);
    }

    @Test
    public void testChainLearnWithExactBasisAndLstdq() {
        BasisFunctions
            exactBasis =
            new ExactBasis(new int[]{simulator.numStates()}, simulator.numActions());
        Policy learnedPolicy = new Policy(0,
                                          simulator.numActions(),
                                          exactBasis,
                                          Matrix.random(exactBasis.size(), 1));

        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        Assert.assertTrue(avgLearnedRewards > avgRandomRewards);
    }

    @Test
    public void testChainLearnWithExactBasisAndLstdqExact() {
        BasisFunctions
            exactBasis =
            new ExactBasis(new int[]{simulator.numStates()}, simulator.numActions());
        Policy learnedPolicy = new Policy(0,
                                          simulator.numActions(),
                                          exactBasis,
                                          Matrix.random(exactBasis.size(), 1));

        learnedPolicy =
            Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10, Lspi.PolicyImprover.LSTDQ_EXACT, .001, 1000);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        Assert.assertTrue(avgLearnedRewards > avgRandomRewards);
    }
}
