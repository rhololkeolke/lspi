package edu.cwru.eecs.rl.types;

import Jama.Matrix;

public interface BasisFunctions {
	Matrix evaluate(Matrix state, int action);
	int size();
}
