package edu.cwru.eecs.rl.basisfunctions;

import org.junit.Test;

import edu.cwru.eecs.rl.types.BasisFunctions;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import static org.junit.Assert.assertEquals;

public class GaussianRbfTests {

    @Test
    public void testNumBasisCalculation() {
        BasisFunctions gaussRbf = new GaussianRbf(3, 3, 3);

        assertEquals(30, gaussRbf.size());
    }

    @Test
    public void test0AngleAnd0Velocity() {
        BasisFunctions gaussRbf = new GaussianRbf(3, 3, 3);

        double[] state = {0, 0};
        Vector phi = gaussRbf.evaluate(new DenseVector(state), 0);

        // test for action 0
        assertEquals(1.000, phi.get(0), .001);
        assertEquals(0.446, phi.get(1), .001);
        assertEquals(0.735, phi.get(2), .001);
        assertEquals(0.446, phi.get(3), .001);
        assertEquals(0.607, phi.get(4), .001);
        assertEquals(1.000, phi.get(5), .001);
        assertEquals(0.607, phi.get(6), .001);
        assertEquals(0.446, phi.get(7), .001);
        assertEquals(0.735, phi.get(8), .001);
        assertEquals(0.446, phi.get(9), .001);
        assertEquals(0.000, phi.get(10), .001);
        assertEquals(0.000, phi.get(11), .001);
        assertEquals(0.000, phi.get(12), .001);
        assertEquals(0.000, phi.get(13), .001);
        assertEquals(0.000, phi.get(14), .001);
        assertEquals(0.000, phi.get(15), .001);
        assertEquals(0.000, phi.get(16), .001);
        assertEquals(0.000, phi.get(17), .001);
        assertEquals(0.000, phi.get(18), .001);
        assertEquals(0.000, phi.get(19), .001);
        assertEquals(0.000, phi.get(20), .001);
        assertEquals(0.000, phi.get(21), .001);
        assertEquals(0.000, phi.get(22), .001);
        assertEquals(0.000, phi.get(23), .001);
        assertEquals(0.000, phi.get(24), .001);
        assertEquals(0.000, phi.get(25), .001);
        assertEquals(0.000, phi.get(26), .001);
        assertEquals(0.000, phi.get(27), .001);
        assertEquals(0.000, phi.get(28), .001);
        assertEquals(0.000, phi.get(29), .001);

        // test for action 1
        phi = gaussRbf.evaluate(new DenseVector(state), 1);
        assertEquals(0.000, phi.get(0), .001);
        assertEquals(0.000, phi.get(1), .001);
        assertEquals(0.000, phi.get(2), .001);
        assertEquals(0.000, phi.get(3), .001);
        assertEquals(0.000, phi.get(4), .001);
        assertEquals(0.000, phi.get(5), .001);
        assertEquals(0.000, phi.get(6), .001);
        assertEquals(0.000, phi.get(7), .001);
        assertEquals(0.000, phi.get(8), .001);
        assertEquals(0.000, phi.get(9), .001);
        assertEquals(1.000, phi.get(10), .001);
        assertEquals(0.446, phi.get(11), .001);
        assertEquals(0.735, phi.get(12), .001);
        assertEquals(0.446, phi.get(13), .001);
        assertEquals(0.607, phi.get(14), .001);
        assertEquals(1.000, phi.get(15), .001);
        assertEquals(0.607, phi.get(16), .001);
        assertEquals(0.446, phi.get(17), .001);
        assertEquals(0.735, phi.get(18), .001);
        assertEquals(0.446, phi.get(19), .001);
        assertEquals(0.000, phi.get(20), .001);
        assertEquals(0.000, phi.get(21), .001);
        assertEquals(0.000, phi.get(22), .001);
        assertEquals(0.000, phi.get(23), .001);
        assertEquals(0.000, phi.get(24), .001);
        assertEquals(0.000, phi.get(25), .001);
        assertEquals(0.000, phi.get(26), .001);
        assertEquals(0.000, phi.get(27), .001);
        assertEquals(0.000, phi.get(28), .001);
        assertEquals(0.000, phi.get(29), .001);

        // test for action 2
        phi = gaussRbf.evaluate(new DenseVector(state), 2);
        assertEquals(0.000, phi.get(0), .001);
        assertEquals(0.000, phi.get(1), .001);
        assertEquals(0.000, phi.get(2), .001);
        assertEquals(0.000, phi.get(3), .001);
        assertEquals(0.000, phi.get(4), .001);
        assertEquals(0.000, phi.get(5), .001);
        assertEquals(0.000, phi.get(6), .001);
        assertEquals(0.000, phi.get(7), .001);
        assertEquals(0.000, phi.get(8), .001);
        assertEquals(0.000, phi.get(9), .001);
        assertEquals(0.000, phi.get(10), .001);
        assertEquals(0.000, phi.get(11), .001);
        assertEquals(0.000, phi.get(12), .001);
        assertEquals(0.000, phi.get(13), .001);
        assertEquals(0.000, phi.get(14), .001);
        assertEquals(0.000, phi.get(15), .001);
        assertEquals(0.000, phi.get(16), .001);
        assertEquals(0.000, phi.get(17), .001);
        assertEquals(0.000, phi.get(18), .001);
        assertEquals(0.000, phi.get(19), .001);
        assertEquals(1.000, phi.get(20), .001);
        assertEquals(0.446, phi.get(21), .001);
        assertEquals(0.735, phi.get(22), .001);
        assertEquals(0.446, phi.get(23), .001);
        assertEquals(0.607, phi.get(24), .001);
        assertEquals(1.000, phi.get(25), .001);
        assertEquals(0.607, phi.get(26), .001);
        assertEquals(0.446, phi.get(27), .001);
        assertEquals(0.735, phi.get(28), .001);
        assertEquals(0.446, phi.get(29), .001);
    }

}