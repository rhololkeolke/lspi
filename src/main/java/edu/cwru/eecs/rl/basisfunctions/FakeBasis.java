package edu.cwru.eecs.rl.basisfunctions;

import edu.cwru.eecs.rl.types.BasisFunctions;
import Jama.Matrix;

import java.io.Serializable;

public class FakeBasis implements BasisFunctions, Serializable {

    @Override
    public Matrix evaluate(Matrix state, int action) {
        return new Matrix(new double[]{1}, 1);
    }

    @Override
    public int size() {
        return 1;
    }

}
