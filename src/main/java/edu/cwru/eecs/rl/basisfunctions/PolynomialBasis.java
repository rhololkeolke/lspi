package edu.cwru.eecs.rl.basisfunctions;

import edu.cwru.eecs.rl.types.BasisFunctions;
import Jama.Matrix;

import java.io.Serializable;

public class PolynomialBasis implements BasisFunctions, Serializable {

    private int polynomialDegree;
    private int numActions;

    public PolynomialBasis(int polynomialDegree, int numActions) {
        this.polynomialDegree = polynomialDegree;
        this.numActions = numActions;
    }

    @Override
    public Matrix evaluate(Matrix state, int action) {
        Matrix results = new Matrix(numActions * polynomialDegree, 1);

        for (int i = 0; i < polynomialDegree; i++) {
            results.set(action * polynomialDegree + i, 0, Math.pow(state.get(0, 0), i));
        }

        return results;
    }

    @Override
    public int size() {
        return polynomialDegree * numActions;
    }

}
