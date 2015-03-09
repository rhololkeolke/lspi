package edu.cwru.eecs.rl.basisfunctions;

import static org.junit.Assert.assertEquals;

import Jama.Matrix;
import org.junit.Test;

import edu.cwru.eecs.rl.types.BasisFunctions;

public class GaussianRBFTests {

	@Test
	public void testNumBasisCalculation() {
		BasisFunctions gaussRBF = new GaussianRbf(3, 3, 3);
		
		assertEquals(30, gaussRBF.size());
	}
	
	@Test
	public void test0AngleAnd0Velocity() {
		BasisFunctions gaussRBF = new GaussianRbf(3, 3, 3);
		
		double[] state = {0,0};
		Matrix phi = gaussRBF.evaluate(new Matrix(state, state.length), 0);
		
		// test for action 0
		assertEquals(1.000, phi.get(0, 0), .001);
		assertEquals(0.446, phi.get(1, 0), .001);
		assertEquals(0.735, phi.get(2, 0), .001);
		assertEquals(0.446, phi.get(3, 0), .001);
		assertEquals(0.607, phi.get(4, 0), .001);
		assertEquals(1.000, phi.get(5, 0), .001);
		assertEquals(0.607, phi.get(6, 0), .001);
		assertEquals(0.446, phi.get(7, 0), .001);
		assertEquals(0.735, phi.get(8, 0), .001);
		assertEquals(0.446, phi.get(9, 0), .001);
		assertEquals(0.000, phi.get(10, 0), .001);
		assertEquals(0.000, phi.get(11, 0), .001);
		assertEquals(0.000, phi.get(12, 0), .001);
		assertEquals(0.000, phi.get(13, 0), .001);
		assertEquals(0.000, phi.get(14, 0), .001);
		assertEquals(0.000, phi.get(15, 0), .001);
		assertEquals(0.000, phi.get(16, 0), .001);
		assertEquals(0.000, phi.get(17, 0), .001);
		assertEquals(0.000, phi.get(18, 0), .001);
		assertEquals(0.000, phi.get(19, 0), .001);
		assertEquals(0.000, phi.get(20, 0), .001);
		assertEquals(0.000, phi.get(21, 0), .001);
		assertEquals(0.000, phi.get(22, 0), .001);
		assertEquals(0.000, phi.get(23, 0), .001);
		assertEquals(0.000, phi.get(24, 0), .001);
		assertEquals(0.000, phi.get(25, 0), .001);
		assertEquals(0.000, phi.get(26, 0), .001);
		assertEquals(0.000, phi.get(27, 0), .001);
		assertEquals(0.000, phi.get(28, 0), .001);
		assertEquals(0.000, phi.get(29, 0), .001);
		
		// test for action 1
		phi = gaussRBF.evaluate(new Matrix(state, state.length), 1);
		assertEquals(0.000, phi.get(0, 0), .001);
		assertEquals(0.000, phi.get(1, 0), .001);
		assertEquals(0.000, phi.get(2, 0), .001);
		assertEquals(0.000, phi.get(3, 0), .001);
		assertEquals(0.000, phi.get(4, 0), .001);
		assertEquals(0.000, phi.get(5, 0), .001);
		assertEquals(0.000, phi.get(6, 0), .001);
		assertEquals(0.000, phi.get(7, 0), .001);
		assertEquals(0.000, phi.get(8, 0), .001);
		assertEquals(0.000, phi.get(9, 0), .001);
		assertEquals(1.000, phi.get(10, 0), .001);
		assertEquals(0.446, phi.get(11, 0), .001);
		assertEquals(0.735, phi.get(12, 0), .001);
		assertEquals(0.446, phi.get(13, 0), .001);
		assertEquals(0.607, phi.get(14, 0), .001);
		assertEquals(1.000, phi.get(15, 0), .001);
		assertEquals(0.607, phi.get(16, 0), .001);
		assertEquals(0.446, phi.get(17, 0), .001);
		assertEquals(0.735, phi.get(18, 0), .001);
		assertEquals(0.446, phi.get(19, 0), .001);
		assertEquals(0.000, phi.get(20, 0), .001);
		assertEquals(0.000, phi.get(21, 0), .001);
		assertEquals(0.000, phi.get(22, 0), .001);
		assertEquals(0.000, phi.get(23, 0), .001);
		assertEquals(0.000, phi.get(24, 0), .001);
		assertEquals(0.000, phi.get(25, 0), .001);
		assertEquals(0.000, phi.get(26, 0), .001);
		assertEquals(0.000, phi.get(27, 0), .001);
		assertEquals(0.000, phi.get(28, 0), .001);
		assertEquals(0.000, phi.get(29, 0), .001);
		
		// test for action 2
		phi = gaussRBF.evaluate(new Matrix(state, state.length), 2);
		assertEquals(0.000, phi.get(0, 0), .001);
		assertEquals(0.000, phi.get(1, 0), .001);
		assertEquals(0.000, phi.get(2, 0), .001);
		assertEquals(0.000, phi.get(3, 0), .001);
		assertEquals(0.000, phi.get(4, 0), .001);
		assertEquals(0.000, phi.get(5, 0), .001);
		assertEquals(0.000, phi.get(6, 0), .001);
		assertEquals(0.000, phi.get(7, 0), .001);
		assertEquals(0.000, phi.get(8, 0), .001);
		assertEquals(0.000, phi.get(9, 0), .001);
		assertEquals(0.000, phi.get(10, 0), .001);
		assertEquals(0.000, phi.get(11, 0), .001);
		assertEquals(0.000, phi.get(12, 0), .001);
		assertEquals(0.000, phi.get(13, 0), .001);
		assertEquals(0.000, phi.get(14, 0), .001);
		assertEquals(0.000, phi.get(15, 0), .001);
		assertEquals(0.000, phi.get(16, 0), .001);
		assertEquals(0.000, phi.get(17, 0), .001);
		assertEquals(0.000, phi.get(18, 0), .001);
		assertEquals(0.000, phi.get(19, 0), .001);
		assertEquals(1.000, phi.get(20, 0), .001);
		assertEquals(0.446, phi.get(21, 0), .001);
		assertEquals(0.735, phi.get(22, 0), .001);
		assertEquals(0.446, phi.get(23, 0), .001);
		assertEquals(0.607, phi.get(24, 0), .001);
		assertEquals(1.000, phi.get(25, 0), .001);
		assertEquals(0.607, phi.get(26, 0), .001);
		assertEquals(0.446, phi.get(27, 0), .001);
		assertEquals(0.735, phi.get(28, 0), .001);
		assertEquals(0.446, phi.get(29, 0), .001);
	}

}