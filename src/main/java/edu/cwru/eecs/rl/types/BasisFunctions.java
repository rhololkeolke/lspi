package edu.cwru.eecs.rl.types;

import edu.cwru.eecs.linalg.SparseMatrix;
import no.uib.cipr.matrix.Vector;

public interface BasisFunctions {

    Vector evaluate(Vector state, int action);

    SparseMatrix sparseEvaluate(Vector state, int action);

    int size();
}
