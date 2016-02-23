package edu.cwru.eecs.rl.basisfunctions;

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
    public int size() {
        return 1;
    }

}
