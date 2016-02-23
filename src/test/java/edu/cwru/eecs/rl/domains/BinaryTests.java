package edu.cwru.eecs.rl.domains;

import edu.cwru.eecs.rl.types.Sample;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.SparseVector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Devin on 2/22/16.
 */
public class BinaryTests {

    private Binary simulator = null;
    private int numBits = 20;

    @Before
    public void createSimulator() { simulator = new Binary(numBits); }

    @Test
    public void testStateSetterAndGetter() {
        Vector inputState = new SparseVector(numBits);
        inputState.set(numBits-1, 1.0);
        simulator.setState(inputState);
        Vector simState = simulator.getState();
        assertEquals(inputState.size(), simState.size());
        for (int i=0; i<inputState.size(); i++) {
            assertEquals(inputState.get(i), simState.get(i), 1e-12);
        }
    }

    @Test
    public void testSetBit() {
        Sample testSample = simulator.step(0);

        Vector expectedCurrState = new SparseVector(numBits);
        Vector expectedNextState = new SparseVector(numBits);
        expectedNextState.set(0, 1.0);

        assertEquals(expectedCurrState.size(), testSample.currState.size());
        for (int i=0; i<expectedCurrState.size(); i++) {
            assertEquals(expectedCurrState.get(i), testSample.currState.get(i), 1e-12);
        }
        assertEquals(0, testSample.action);
        assertEquals(expectedNextState.size(), testSample.nextState.size());
        for (int i=0; i<expectedNextState.size(); i++) {
            assertEquals(expectedNextState.get(i), testSample.nextState.get(i), 1e-12);
        }
        assertEquals(-1, testSample.reward, 1e-12);
        assertFalse(testSample.absorb);

    }

    @Test
    public void testSetAlreadySetBit() {
        Vector expectedCurrState = new SparseVector(numBits);
        expectedCurrState.set(0, 1.0);
        simulator.setState(expectedCurrState);

        Sample testSample = simulator.step(0);

        Vector expectedNextState = expectedCurrState;

        assertEquals(expectedCurrState.size(), testSample.currState.size());
        for (int i=0; i<expectedCurrState.size(); i++) {
            assertEquals(expectedCurrState.get(i), testSample.currState.get(i), 1e-12);
        }
        assertEquals(0, testSample.action);
        assertEquals(expectedNextState.size(), testSample.nextState.size());
        for (int i=0; i<expectedNextState.size(); i++) {
            assertEquals(expectedNextState.get(i), testSample.nextState.get(i), 1e-12);
        }
        assertEquals(-1, testSample.reward, 1e-12);
        assertFalse(testSample.absorb);
    }

    @Test
    public void testSetAllBits() {
        Vector expectedCurrState = new SparseVector(numBits);
        for(int i=1; i<numBits; i++) {
            expectedCurrState.set(i, 1.0);
        }
        simulator.setState(expectedCurrState);

        Sample testSample = simulator.step(0);

        Vector expectedNextState = expectedCurrState.copy();
        expectedNextState.set(0, 1.0);

        assertEquals(expectedCurrState.size(), testSample.currState.size());
        for (int i=0; i<expectedCurrState.size(); i++) {
            assertEquals(expectedCurrState.get(i), testSample.currState.get(i), 1e-12);
        }        assertEquals(0, testSample.action);
        assertEquals(0, testSample.action);
        assertEquals(expectedNextState.size(), testSample.nextState.size());
        for (int i=0; i<expectedNextState.size(); i++) {
            assertEquals(expectedNextState.get(i), testSample.nextState.get(i), 1e-12);
        }        assertEquals(100, testSample.reward, 1e-12);
        assertTrue(testSample.absorb);
    }

    @Test
    public void testNumStates() {
        assertEquals(1048576, simulator.numStates());
    }

    @Test
    public void testNumActions() {
        assertEquals(numBits, simulator.numActions());
    }
}
