package edu.cwru.eecs.rl.domains;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import edu.cwru.eecs.rl.types.Sample;
import Jama.Matrix;
import org.junit.Before;
import org.junit.Test;

public class DeterministicPendulumTests {

    Pendulum simulator;

    /**
     * Creates a new deterministic pendulum domain for testing.
     */
    @Before
    public void setup() {
        simulator = new Pendulum(20);
        double[] stateArray = {0.1571, 0.1397};
        simulator.setState(new Matrix(stateArray, stateArray.length));
    }

    @Test
    public void testStepFunction() {
        Sample sample = simulator.step(1);

        assertEquals(.1571, sample.currState.get(0, 0), .0001);
        assertEquals(.1397, sample.currState.get(1, 0), .0001);

        assertEquals(1, sample.action);
        double roundedVal = (double) Math.round(sample.nextState.get(0, 0) * 10000) / 10000;
        assertEquals(0.1813, roundedVal, .0001);
        roundedVal = (double) Math.round(sample.nextState.get(1, 0) * 10000) / 10000;
        assertEquals(0.3502, roundedVal, .0001);
        assertEquals(0, (int) sample.reward);
    }

    @Test
    public void testSimEnd() {
        int iter = 0;
        final int maxIter = 1000;

        while (!simulator.isTerminal(simulator.getState())) {
            iter++;
            if (iter >= maxIter) {
                fail("Pendulum did not enter terminal state before max iterations");
            }
            simulator.step(1);
        }
        assertTrue(simulator.isNonGoalTerminal(simulator.getState()));
        assertFalse(simulator.isGoal(simulator.getState()));
    }

}
