package edu.cwru.eecs.rl.domains;

import org.junit.Test;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProbabilisticPendulumTests {

    @Test
    public void testInitialState() {
        Pendulum simulator = new Pendulum(0);
        Vector initialState = simulator.getState();
        assertNotEquals(0, initialState.get(0));
        assertNotEquals(0, initialState.get(1));

        Vector totalInitState = new DenseVector(2);
        for (int i = 1; i <= 20000; i++) {
            simulator = new Pendulum(i);
            totalInitState.add(simulator.getState());
        }

        totalInitState.scale(1.0 / 20000.0);

        // check the mean
        assertEquals(0, totalInitState.get(0), .1);
        assertEquals(0, totalInitState.get(1), .1);
    }

}
