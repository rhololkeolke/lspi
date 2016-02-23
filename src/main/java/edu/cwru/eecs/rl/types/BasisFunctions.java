package edu.cwru.eecs.rl.types;

import no.uib.cipr.matrix.Vector;

public interface BasisFunctions {

    Vector evaluate(Vector state, int action);

    int size();
}
