package edu.cwru.eecs.rl.basisfunctions;

import edu.cwru.eecs.linalg.SparseMatrix;
import edu.cwru.eecs.rl.types.BasisFunctions;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import java.io.Serializable;

public class FakeBasis implements BasisFunctions, Serializable {

    @Override
    public Vector evaluate(Vector state, int action) {
        return new DenseVector(new double[]{1});
    }

    @Override
    public SparseMatrix sparseEvaluate(Vector State, int action) {
        SparseMatrix result = new SparseMatrix(1, 1);
        result.set(0, 0, 1);
        return result;
    }

    @Override
    public int size() {
        return 1;
    }

}
