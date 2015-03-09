package edu.cwru.eecs.rl.domains;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import Jama.Matrix;
import org.junit.Before;
import org.junit.Test;

import edu.cwru.eecs.rl.types.Sample;

public class DeterministicChainTests {

    private Chain simulator = null;

    @Before
    public void createSimulator()
    {
        simulator = new Chain(10, 1, 0);
    }

    @Test
    public void testStateSetterAndGetter() {
        Matrix inputState = new Matrix(new double[]{1}, 1);
        simulator.setState(inputState);
        assertTrue(inputState.equals(simulator.getState()));

        inputState = new Matrix(new double[]{0}, 1);
        simulator.setState(inputState);
        assertTrue(inputState.equals(simulator.getState()));

        inputState = new Matrix(new double[]{9}, 1);
        simulator.setState(inputState);
        assertTrue(inputState.equals(simulator.getState()));
    }

    @Test
    public void testSuccessfulStepLeft() {
        simulator.setState(new Matrix(new double[]{4}, 1));
        Sample testSample = simulator.step(0);

        assertEquals(4, (int)testSample.currState.get(0, 0));
        assertEquals(0, (int)testSample.action);
        assertEquals(3, (int)testSample.nextState.get(0, 0));
        assertEquals(0, (int)testSample.reward);

    }

    @Test
    public void testSuccessfulStepRight() {
        simulator.setState(new Matrix(new double[]{4}, 1));
        Sample testSample = simulator.step(1);

        assertEquals(4, (int)testSample.currState.get(0, 0));
        assertEquals(1, (int)testSample.action);
        assertEquals(5, (int)testSample.nextState.get(0, 0));
        assertEquals(0, (int)testSample.reward);
    }

    @Test
    public void testLeftEndStep() {
        simulator.setState(new Matrix(new double[]{0}, 1));
        Sample testSample = simulator.step(0);

        assertEquals(0, (int)testSample.currState.get(0, 0));
        assertEquals(0, (int)testSample.action);
        assertEquals(0, (int)testSample.nextState.get(0, 0));
        assertEquals(1, (int)testSample.reward);
    }

    @Test
    public void testRightEndStep() {
        simulator.setState(new Matrix(new double[]{9}, 1));
        Sample testSample = simulator.step(1);

        assertEquals(9, (int)testSample.currState.get(0, 0));
        assertEquals(1, (int)testSample.action);
        assertEquals(9, (int)testSample.nextState.get(0, 0));
        assertEquals(1, (int)testSample.reward);
    }

    @Test
    public void testFailedStepLeft() {
        simulator = new Chain(10, 0, 0);
        simulator.setState(new Matrix(new double[]{4}, 1));

        Sample testSample = simulator.step(0);

        assertEquals(4, (int)testSample.currState.get(0, 0));
        assertEquals(0, (int)testSample.action);
        assertEquals(5, (int)testSample.nextState.get(0, 0));
        assertEquals(0, (int)testSample.reward);
    }

    @Test
    public void testFailedStepRight() {
        simulator = new Chain(10, 0, 0);
        simulator.setState(new Matrix(new double[]{4}, 1));

        Sample testSample = simulator.step(1);

        assertEquals(4, (int)testSample.currState.get(0, 0));
        assertEquals(1, (int)testSample.action);
        assertEquals(3, (int)testSample.nextState.get(0, 0));
        assertEquals(0, (int)testSample.reward);
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
