package edu.cwru.eecs.rl;

import Jama.Matrix;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by rhol on 8/4/14.
 */
public class ChainLearnTests {

    Simulator simulator;
    Policy randomPolicy;
    List<Sample> samples;

    @Before
    public void setupLearner() {
        simulator = new Chain(10, .9, 0);
        BasisFunctions fake_basis = new FakeBasis();
        randomPolicy = new Policy(1,
                simulator.numActions(),
                fake_basis,
                Matrix.random(fake_basis.size(), 1));

        samples = PolicySampler.sample(simulator, 10, 500, randomPolicy);
    }

    @Test
    public void testChainLearnWithPolyBasis() {

        BasisFunctions poly_basis = new PolynomialBasis(3, simulator.numActions());
        Policy learnedPolicy = new Policy(0,
                simulator.numActions(),
                poly_basis,
                Matrix.random(poly_basis.size(), 1));


        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        // not the greatest test but it should work
        Assert.assertTrue(avgLearnedRewards > avgRandomRewards);
    }

    @Test
    public void testChainLearnWithExactBasisAndLSTDQ() {
        BasisFunctions exact_basis = new ExactBasis(new int[]{simulator.numStates()}, simulator.numActions());
        Policy learnedPolicy = new Policy(0,
                simulator.numActions(),
                exact_basis,
                Matrix.random(exact_basis.size(), 1));

        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10, Lspi.PolicyImprover.LSTDQ);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        Assert.assertTrue(avgLearnedRewards > avgRandomRewards);
    }

    @Test
    public void testChainLearnWithExactBasisAndLSTDQExact() {
        BasisFunctions exact_basis = new ExactBasis(new int[]{simulator.numStates()}, simulator.numActions());
        Policy learnedPolicy = new Policy(0,
                simulator.numActions(),
                exact_basis,
                Matrix.random(exact_basis.size(), 1));

        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10, Lspi.PolicyImprover.LSTDQ_EXACT);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 10, 500, learnedPolicy);

        Assert.assertTrue(avgLearnedRewards > avgRandomRewards);
    }
}
