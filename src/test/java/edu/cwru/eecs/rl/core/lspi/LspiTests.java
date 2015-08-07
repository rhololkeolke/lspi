package edu.cwru.eecs.rl.core.lspi;

import edu.cwru.eecs.rl.agent.PolicySampler;
import edu.cwru.eecs.rl.basisfunctions.ExactBasis;
import edu.cwru.eecs.rl.basisfunctions.FakeBasis;
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
public class LspiTests {

    Simulator simulator;
    Policy randomPolicy;
    List<Sample> samples;

    /**
     * Construct a chain domain and a collect a bunch of random samples.
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
    public void testLstdqExactVsLstdq() {
        BasisFunctions
            exactBasis =
            new ExactBasis(new int[]{simulator.numStates()}, simulator.numActions());
        Policy exactBasisPolicy = new Policy(0,
                                             simulator.numActions(),
                                             exactBasis,
                                             Matrix.random(exactBasis.size(), 1));

        Matrix lstdqWeights = Lspi.lstdq(samples, exactBasisPolicy, .9);
        Matrix lstdqExactWeights = Lspi.lstdqExactMtj(samples, exactBasisPolicy, .9);

        Assert.assertEquals(lstdqExactWeights.getRowDimension(), lstdqWeights.getRowDimension());
        Assert.assertEquals(1, lstdqExactWeights.getColumnDimension());
        Assert.assertEquals(1, lstdqWeights.getColumnDimension());

        // verify that the two matrices are approximately equal
        for (int i = 0; i < lstdqExactWeights.getRowDimension(); i++) {
            Assert.assertEquals(lstdqExactWeights.get(i, 0), lstdqWeights.get(i, 0), .1);
        }
    }
}
