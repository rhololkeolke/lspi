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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by Devin on 8/4/14.
 */
public class PendulumLearnTests {

    Simulator simulator;
    Policy randomPolicy;
    List<Sample> samples;

    /**
     * Construct a default Pendulum domain and collect a bunch of random samples.
     */
    @Before
    public void setupLearner() {
        simulator = new Pendulum();
        BasisFunctions fakeBasis = new FakeBasis();
        randomPolicy = new Policy(1,
                                  simulator.numActions(),
                                  fakeBasis,
                                  Matrix.random(fakeBasis.size(), 1));

        samples = PolicySampler.sample(simulator, 1000, 50, randomPolicy);
    }

    @Test
    public void testPendulumLearnWithRbfBasis() {
        BasisFunctions rbfBasis = new GaussianRbf(3, 3, 3);
        Policy learnedPolicy = new Policy(0,
                                          simulator.numActions(),
                                          rbfBasis,
                                          Matrix.random(rbfBasis.size(), 1));

        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        // not the greatest test but it should work
        Assert.assertTrue(avgLearnedRewards > avgRandomRewards);
        Assert.assertEquals(0, avgLearnedRewards, .001);
    }
}
