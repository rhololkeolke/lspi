package edu.cwru.eecs.rl.domains;

import org.junit.Before;
import org.junit.Test;

import edu.cwru.eecs.rl.types.Sample;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeterministicChainTests {

    private Chain simulator = null;

    @Before
    public void createSimulator() {
        simulator = new Chain(10, 1, 0);
    }

    @Test
    public void testStateSetterAndGetter() {
        Vector inputState = new DenseVector(new double[]{1});
        simulator.setState(inputState);
        assertTrue(inputState.equals(simulator.getState()));

        inputState = new DenseVector(new double[]{0});
        simulator.setState(inputState);
        assertTrue(inputState.equals(simulator.getState()));

        inputState = new DenseVector(new double[]{9});
        simulator.setState(inputState);
        assertTrue(inputState.equals(simulator.getState()));
    }

    @Test
    public void testSuccessfulStepLeft() {
        simulator.setState(new DenseVector(new double[]{4}));
        Sample testSample = simulator.step(0);

        assertEquals(4, (int) testSample.currState.get(0));
        assertEquals(0, testSample.action);
        assertEquals(3, (int) testSample.nextState.get(0));
        assertEquals(0, (int) testSample.reward);

    }

    @Test
    public void testSuccessfulStepRight() {
        simulator.setState(new DenseVector(new double[]{4}));
        Sample testSample = simulator.step(1);

        assertEquals(4, (int) testSample.currState.get(0));
        assertEquals(1, testSample.action);
        assertEquals(5, (int) testSample.nextState.get(0));
        assertEquals(0, (int) testSample.reward);
    }

    @Test
    public void testLeftEndStep() {
        simulator.setState(new DenseVector(new double[]{0}));
        Sample testSample = simulator.step(0);

        assertEquals(0, (int) testSample.currState.get(0));
        assertEquals(0, testSample.action);
        assertEquals(0, (int) testSample.nextState.get(0));
        assertEquals(1, (int) testSample.reward);
    }

    @Test
    public void testRightEndStep() {
        simulator.setState(new DenseVector(new double[]{9}));
        Sample testSample = simulator.step(1);

        assertEquals(9, (int) testSample.currState.get(0));
        assertEquals(1, testSample.action);
        assertEquals(9, (int) testSample.nextState.get(0));
        assertEquals(1, (int) testSample.reward);
    }

    @Test
    public void testFailedStepLeft() {
        simulator = new Chain(10, 0, 0);
        simulator.setState(new DenseVector(new double[]{4}));

        Sample testSample = simulator.step(0);

        assertEquals(4, (int) testSample.currState.get(0));
        assertEquals(0, testSample.action);
        assertEquals(5, (int) testSample.nextState.get(0));
        assertEquals(0, (int) testSample.reward);
    }

    @Test
    public void testFailedStepRight() {
        simulator = new Chain(10, 0, 0);
        simulator.setState(new DenseVector(new double[]{4}));

        Sample testSample = simulator.step(1);

        assertEquals(4, (int) testSample.currState.get(0));
        assertEquals(1, testSample.action);
        assertEquals(3, (int) testSample.nextState.get(0));
        assertEquals(0, (int) testSample.reward);
    }

    @Test
    public void testNumStates() {
        assertEquals(10, simulator.numStates());
    }

    @Test
    public void testNumActions() {
        assertEquals(2, simulator.numActions());
    }
}
