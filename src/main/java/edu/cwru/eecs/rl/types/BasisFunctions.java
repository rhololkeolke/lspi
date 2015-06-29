package edu.cwru.eecs.rl.types;

import Jama.Matrix;
import edu.cwru.eecs.linalg.SparseMatrix;

public interface BasisFunctions {

    Matrix evaluate(Matrix state, int action);

    SparseMatrix sparseEvaluate(Matrix state, int action);

    int size();
}
