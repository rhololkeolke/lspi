package edu.cwru.eecs.rl.edu.cwru.eecs.rl.core.lspi;

import Jama.Matrix;
import edu.cwru.eecs.rl.agent.PolicySampler;
import edu.cwru.eecs.rl.basisFunctions.ExactBasis;
import edu.cwru.eecs.rl.basisFunctions.FakeBasis;
import edu.cwru.eecs.rl.core.lspi.LSPI;
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
public class LSPITests {

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
    public void testLSTDQExactVsLSTDQ() {
        BasisFunctions exact_basis = new ExactBasis(new int[]{simulator.numStates()}, simulator.numActions());
        Policy exactBasisPolicy = new Policy(0,
                simulator.numActions(),
                exact_basis,
                Matrix.random(exact_basis.size(), 1));

        Matrix LSTDQWeights = LSPI.LSTDQ(samples, exactBasisPolicy, .9);
        Matrix LSTDQExactWeights = LSPI.LSTDQExact(samples, exactBasisPolicy, .9);

        Assert.assertEquals(LSTDQExactWeights.getRowDimension(), LSTDQWeights.getRowDimension());
        Assert.assertEquals(1, LSTDQExactWeights.getColumnDimension());
        Assert.assertEquals(1, LSTDQWeights.getColumnDimension());

        // verify that the two matrices are approximately equal
        for(int i=0; i<LSTDQExactWeights.getRowDimension(); i++)
        {
            Assert.assertEquals(LSTDQExactWeights.get(i, 0), LSTDQWeights.get(i, 0), .001);
        }
    }

}
