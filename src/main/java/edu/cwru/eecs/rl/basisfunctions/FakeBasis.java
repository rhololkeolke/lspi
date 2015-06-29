package edu.cwru.eecs.rl.basisfunctions;

import edu.cwru.eecs.linalg.SparseMatrix;
import edu.cwru.eecs.rl.types.BasisFunctions;
import Jama.Matrix;

import java.io.Serializable;

public class FakeBasis implements BasisFunctions, Serializable {

    @Override
    public Matrix evaluate(Matrix state, int action) {
        return new Matrix(new double[]{1}, 1);
    }

    @Override
    public SparseMatrix sparseEvaluate(Matrix State, int action) {
        SparseMatrix result = new SparseMatrix(1, 1);
        result.set(0, 0, 1);
        return result;
    }

    @Override
    public int size() {
        return 1;
    }

}
