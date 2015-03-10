package edu.cwru.eecs.rl.domains;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import Jama.Matrix;
import org.junit.Test;

public class ProbabilisticPendulumTests {

    @Test
    public void testInitialState() {
        Pendulum simulator = new Pendulum(0);
        Matrix initialState = simulator.getState();
        assertNotEquals(0, initialState.get(0, 0));
        assertNotEquals(0, initialState.get(1, 0));

        Matrix totalInitState = new Matrix(2, 1);
        for (int i = 1; i <= 2000; i++) {
            simulator = new Pendulum(i);
            totalInitState.plus(simulator.getState());
        }

        totalInitState.times(1.0 / 2000.0);

        // check the mean
        assertEquals(0, totalInitState.get(0, 0), .01);
        assertEquals(0, totalInitState.get(1, 0), .01);
    }

}
