package edu.cwru.eecs.rl.basisFunctions;

import java.io.Serializable;

import Jama.Matrix;

import edu.cwru.eecs.rl.types.BasisFunctions;

public class PolynomialBasis implements BasisFunctions, Serializable {

	private static final long serialVersionUID = -5924385805310240525L;
	
	private int polynomialDegree;
	private int numActions;
	
	public PolynomialBasis(int polynomialDegree, int numActions)
	{
		this.polynomialDegree = polynomialDegree;
		this.numActions = numActions;
	}

	@Override
	public Matrix evaluate(Matrix state, int action) {
		Matrix results = new Matrix(numActions*polynomialDegree, 1);
		
		for(int i=0; i<polynomialDegree; i++)
		{
			results.set(action*polynomialDegree+i, 0, Math.pow(state.get(0, 0), i));
		}
		
		return results;
	}

	@Override
	public int size() {
		return polynomialDegree*numActions;
	}

}
