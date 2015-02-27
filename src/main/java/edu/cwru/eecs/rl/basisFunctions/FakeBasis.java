package edu.cwru.eecs.rl.basisFunctions;

import java.io.Serializable;

import Jama.Matrix;
import edu.cwru.eecs.rl.types.BasisFunctions;

public class FakeBasis implements BasisFunctions, Serializable {

	private static final long serialVersionUID = -1136393952534461567L;

	@Override
	public Matrix evaluate(Matrix state, int action) {
		return new Matrix(new double[]{1}, 1);
	}

	@Override
	public int size() {
		return 1;
	}

}
