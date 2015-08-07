package edu.cwru.eecs.rl.core.lspi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import edu.cwru.eecs.rl.agent.PolicySampler;
import edu.cwru.eecs.rl.basisfunctions.ExactBasis;
import edu.cwru.eecs.rl.basisfunctions.FakeBasis;
import edu.cwru.eecs.rl.domains.Chain;
import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.BasisFunctions;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import no.uib.cipr.matrix.Vector;

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
                fakeBasis);

        samples = PolicySampler.sample(simulator, 10, 500, randomPolicy);
    }

    @Test
    public void testLstdqExactVsLstdq() {
        BasisFunctions
                exactBasis =
                new ExactBasis(new int[]{simulator.numStates()}, simulator.numActions());
        Policy exactBasisPolicy = new Policy(0,
                simulator.numActions(),
                exactBasis);

        Vector lstdqWeights = Lspi.lstdqMtj(samples, exactBasisPolicy, .9);
        Vector lstdqExactWeights = Lspi.lstdqExactMtj(samples, exactBasisPolicy, .9);

        Assert.assertEquals(lstdqExactWeights.size(), lstdqWeights.size());

        // verify that the two matrices are approximately equal
        for (int i = 0; i < lstdqExactWeights.size(); i++) {
            Assert.assertEquals(lstdqExactWeights.get(i), lstdqWeights.get(i), .1);
        }
    }
}
