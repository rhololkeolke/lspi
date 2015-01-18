package edu.cwru.eecs.rl;

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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by rhol on 8/4/14.
 */
public class PendulumLearnTests {
    Simulator simulator;
    Policy randomPolicy;
    List<Sample> samples;

    @Before
    public void setupLearner() {
        simulator = new Pendulum();
        BasisFunctions fake_basis = new FakeBasis();
        randomPolicy = new Policy(1,
                simulator.numActions(),
                fake_basis,
                Matrix.random(fake_basis.size(), 1));

        samples = PolicySampler.sample(simulator, 1000, 50, randomPolicy);
    }

    @Test
    public void testPendulumLearnWithRBFBasis() {
        BasisFunctions rbf_basis = new GaussianRBF(3, 3, 3);
        Policy learnedPolicy = new Policy(0,
                simulator.numActions(),
                rbf_basis,
                Matrix.random(rbf_basis.size(), 1));

        learnedPolicy = LSPI.learn(samples, learnedPolicy, .9, 1e-5, 10);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        // not the greatest test but it should work
        Assert.assertTrue(avgLearnedRewards > avgRandomRewards);
        Assert.assertEquals(0, avgLearnedRewards, .001);
    }
}
