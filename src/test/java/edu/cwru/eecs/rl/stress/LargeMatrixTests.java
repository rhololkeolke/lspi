package edu.cwru.eecs.rl.stress;

import edu.cwru.eecs.rl.agent.PolicySampler;
import edu.cwru.eecs.rl.basisfunctions.ExactBasis;
import edu.cwru.eecs.rl.basisfunctions.FakeBasis;
import edu.cwru.eecs.rl.core.lspi.Lspi;
import edu.cwru.eecs.rl.domains.Binary;
import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.BasisFunctions;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by Devin on 2/22/16.
 */
public class LargeMatrixTests {

    private final int numBits = 10;
    private Simulator simulator;
    private Policy randomPolicy;
    private List<Sample> samples;

    @Before
    public void setupLearner() {
        simulator = new Binary(numBits);
        BasisFunctions fakeBasis = new FakeBasis();
        randomPolicy = new Policy(1, simulator.numActions(), fakeBasis);

        samples = PolicySampler.sample(simulator, 1000, 10000, randomPolicy);
    }

    public void _testLargeDenseMatrixWithExactBasisAndLstdq() {
        int[] numStates = new int[numBits];
        for (int i=0; i < numStates.length; i++) {
            numStates[i] = 2;
        }
        BasisFunctions exactBasis = new ExactBasis(numStates, simulator.numActions());
        Policy learnedPolicy = new Policy(0, simulator.numActions(), exactBasis);

        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 1, Lspi.PolicyImprover.LSTDQ_MTJ);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 100, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 100, 500, learnedPolicy);

        assertTrue(avgLearnedRewards > avgRandomRewards);
    }

    @Test
    public void testLargeDenseMatrixWithExactBasisAndLstdq() {
        CallMethod call = new CallMethod();
        Thread t = new Thread(call);
        t.start();
        try {
            Thread.sleep(10000); // wait
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(t.isAlive() || call.getEndTime() - call.getStartTime() >= 10000);
        t.stop();
    }

    @Test
    public void testLargeSparseMatrixWithExactBasisAndLstdq() {
        int[] numStates = new int[numBits];
        for (int i=0; i < numStates.length; i++) {
            numStates[i] = 2;
        }
        BasisFunctions exactBasis = new ExactBasis(numStates, simulator.numActions());
        Policy learnedPolicy = new Policy(0, simulator.numActions(), exactBasis);

        learnedPolicy = Lspi.learn(samples, learnedPolicy, .9, 1e-5, 10, Lspi.PolicyImprover.LSTDQ_EXACT_MTJ);

        simulator.reset();
        double avgRandomRewards = PolicySampler.evaluatePolicy(simulator, 100, 500, randomPolicy);
        simulator.reset();
        double avgLearnedRewards = PolicySampler.evaluatePolicy(simulator, 100, 500, learnedPolicy);

        assertThat("timestamp",
                avgLearnedRewards,
                greaterThan(avgRandomRewards));
    }

    public class CallMethod implements Runnable
    {
        private Instant startTime;
        private Instant endTime;

        //time in milli
        public long getStartTime()
        {
            return startTime.getEpochSecond();
        }

        //time in milli
        public long getEndTime()
        {
            return endTime.getEpochSecond();
        }

        public void run()
        {
            startTime = Instant.now();
            _testLargeDenseMatrixWithExactBasisAndLstdq();
            endTime = Instant.now();
        }
    }
}
