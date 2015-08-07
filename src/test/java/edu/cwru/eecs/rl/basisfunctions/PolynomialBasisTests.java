package edu.cwru.eecs.rl.basisfunctions;

import org.junit.Test;

import edu.cwru.eecs.rl.types.BasisFunctions;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import static org.junit.Assert.assertEquals;

public class PolynomialBasisTests {

    @Test
    public void test3DegreePolynomial() {
        BasisFunctions polynomial = new PolynomialBasis(3, 2);

        assertEquals(6, polynomial.size());

        Vector phi = polynomial.evaluate(new DenseVector(new double[]{2}), 0);
        assertEquals(6, phi.size());
        assertEquals(1, (int) phi.get(0));
        assertEquals(2, (int) phi.get(1));
        assertEquals(4, (int) phi.get(2));
        assertEquals(0, (int) phi.get(3));
        assertEquals(0, (int) phi.get(4));
        assertEquals(0, (int) phi.get(5));

        phi = polynomial.evaluate(new DenseVector(new double[]{2}), 1);
        assertEquals(6, phi.size());
        assertEquals(0, (int) phi.get(0));
        assertEquals(0, (int) phi.get(1));
        assertEquals(0, (int) phi.get(2));
        assertEquals(1, (int) phi.get(3));
        assertEquals(2, (int) phi.get(4));
        assertEquals(4, (int) phi.get(5));
    }

}
