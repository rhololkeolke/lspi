package edu.cwru.eecs.rl.agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import edu.cwru.eecs.rl.basisfunctions.FakeBasis;
import edu.cwru.eecs.rl.domains.Chain;
import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.BasisFunctions;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import Jama.Matrix;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class PolicySamplerTests {

    private Simulator simulator;
    private Policy randomPolicy;

    /**
     * Construct a chain domain and a random policy for sampling.
     */
    @Before
    public void setUp() {
        simulator = new Chain(10, 1, 0);
        BasisFunctions basis = new FakeBasis();
        randomPolicy = new Policy(1, simulator.numActions(), basis,
                                  Matrix.random(basis.size(), 1));
    }

    @Test
    public void testRandomPolicySample() {
        List<Sample> samples = PolicySampler.sample(simulator, 10, 10, randomPolicy);

        assertEquals(100, samples.size());

        // verify that the samples are all the same
        // this is a really weak test of randomness
        // (with uniform sampling expect at least one sample to differ)
        boolean samplesDiffer = false;
        Sample testSample = samples.get(0);
        for (Sample sample : samples) {
            if (!sample.equals(testSample)) {
                samplesDiffer = true;
                break;
            }
        }

        assertTrue(samplesDiffer);
    }

    @Test
    public void testMaxEpisodes() {
        List<Sample> samples = PolicySampler.sample(simulator, 1, 10, randomPolicy);
        assertEquals(10, samples.size());
    }

    @Test
    public void testEpisodeLength() {
        List<Sample> samples = PolicySampler.sample(simulator, 10, 1, randomPolicy);
        assertEquals(10, samples.size());
    }

}
