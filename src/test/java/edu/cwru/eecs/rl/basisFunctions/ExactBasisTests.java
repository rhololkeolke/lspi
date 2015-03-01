package edu.cwru.eecs.rl.basisFunctions;

import Jama.Matrix;
import edu.cwru.eecs.rl.types.BasisFunctions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ExactBasisTests {

    @Test
    public void testNumBasisCalculation() {
        BasisFunctions basis = new ExactBasis(new int[]{3}, 2);
        assertEquals(6, basis.size());

        basis = new ExactBasis(new int[]{3, 3}, 2);
        assertEquals(18, basis.size());
    }

    @Test
    public void testStateActionIndex() {
        ExactBasis basis = new ExactBasis(new int[]{3}, 2);

        Matrix state = new Matrix(1,1);
        state.set(0, 0, 0);

        assertEquals(0, basis.getStateActionIndex(state, 0));
        assertEquals(3, basis.getStateActionIndex(state, 1));

        state.set(0, 0, 1);
        assertEquals(1, basis.getStateActionIndex(state, 0));
        assertEquals(4, basis.getStateActionIndex(state, 1));

        basis = new ExactBasis(new int[]{3,3}, 2);

        state = new Matrix(2, 1);
        state.set(0, 0, 0);
        state.set(1, 0, 0);

        assertEquals(0, basis.getStateActionIndex(state, 0));
        assertEquals(9, basis.getStateActionIndex(state, 1));

        state.set(0, 0, 1);

        assertEquals(1, basis.getStateActionIndex(state, 0));
        assertEquals(10, basis.getStateActionIndex(state, 1));

        state.set(1, 0, 1);

        assertEquals(4, basis.getStateActionIndex(state, 0));
        assertEquals(13, basis.getStateActionIndex(state, 1));

    }

    @Test
    public void testEvaluate() {
        BasisFunctions basis = new ExactBasis(new int[]{3}, 2);

        Matrix state = new Matrix(1, 1);
        state.set(0, 0, 0);

        Matrix phi = basis.evaluate(state, 0);
        assertEquals(6, phi.getRowDimension());
        assertEquals(1, phi.getColumnDimension());
        assertEquals(1, (int)phi.get(0, 0));
        assertEquals(0, (int)phi.get(1, 0));
        assertEquals(0, (int)phi.get(2, 0));
        assertEquals(0, (int)phi.get(3, 0));
        assertEquals(0, (int)phi.get(4, 0));
        assertEquals(0, (int)phi.get(5, 0));
    }
}
