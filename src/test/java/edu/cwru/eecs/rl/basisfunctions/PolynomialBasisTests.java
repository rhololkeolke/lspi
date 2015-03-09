package edu.cwru.eecs.rl.basisfunctions;

import static org.junit.Assert.assertEquals;

import Jama.Matrix;
import org.junit.Test;

import edu.cwru.eecs.rl.types.BasisFunctions;

public class PolynomialBasisTests {

	@Test
	public void test3DegreePolynomial() {
		BasisFunctions polynomial = new PolynomialBasis(3, 2);
		
		assertEquals(6, polynomial.size());
		
		Matrix phi = polynomial.evaluate(new Matrix(new double[]{2}, 1), 0);
		assertEquals(6, phi.getRowDimension()*phi.getColumnDimension());
        assertEquals(6, phi.getRowDimension());
        assertEquals(1, phi.getColumnDimension());
		assertEquals(1, (int)phi.get(0, 0));
		assertEquals(2, (int)phi.get(1, 0));
		assertEquals(4, (int)phi.get(2, 0));
		assertEquals(0, (int)phi.get(3, 0));
		assertEquals(0, (int)phi.get(4, 0));
		assertEquals(0, (int)phi.get(5, 0));
		
		phi = polynomial.evaluate(new Matrix(new double[]{2}, 1), 1);
		assertEquals(6, phi.getRowDimension()*phi.getColumnDimension());
        assertEquals(6, phi.getRowDimension());
        assertEquals(1, phi.getColumnDimension());
		assertEquals(0, (int)phi.get(0, 0));
		assertEquals(0, (int)phi.get(1, 0));
		assertEquals(0, (int)phi.get(2, 0));
		assertEquals(1, (int)phi.get(3, 0));
		assertEquals(2, (int)phi.get(4, 0));
		assertEquals(4, (int)phi.get(5, 0));
	}

}
