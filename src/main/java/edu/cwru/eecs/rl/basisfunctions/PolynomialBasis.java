package edu.cwru.eecs.rl.basisfunctions;

import edu.cwru.eecs.rl.types.BasisFunctions;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import java.io.Serializable;

public class PolynomialBasis implements BasisFunctions, Serializable {

    private int polynomialDegree;
    private int numActions;

    public PolynomialBasis(int polynomialDegree, int numActions) {
        this.polynomialDegree = polynomialDegree;
        this.numActions = numActions;
    }

    @Override
    public Vector evaluate(Vector state, int action) {
        Vector results = new DenseVector(numActions * polynomialDegree);

        for (int i = 0; i < polynomialDegree; i++) {
            results.set(action * polynomialDegree + i, Math.pow(state.get(0), i));
        }

        return results;
    }

    @Override
    public int size() {
        return polynomialDegree * numActions;
    }

}
