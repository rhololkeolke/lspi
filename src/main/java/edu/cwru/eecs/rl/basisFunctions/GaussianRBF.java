package edu.cwru.eecs.rl.basisFunctions;

import java.io.Serializable;

import Jama.Matrix;
import edu.cwru.eecs.rl.types.BasisFunctions;

public class GaussianRBF implements BasisFunctions, Serializable {
	
	private static final long serialVersionUID = -8563930031421254813L;
	
	private int numActions;
	private int numBasis;
	
	public GaussianRBF(int numFx, int numFy, int numActions)
	{
		this.numActions = numActions;
		this.numBasis = (numFx*numFy+1)*numActions;
	}

	@Override
	public Matrix evaluate(Matrix state, int action) {
		Matrix phi = new Matrix(numBasis, 1);
		
		if(action >= numActions || action < 0)
			return phi; // return 0's if action number is invalid
		
		if(Math.abs(state.get(0, 0)) > Math.PI/2.0)
			return phi;
		
		int base = (numBasis/numActions)*action;
		
		phi.set(base++, 0, 1.0);
		
		double sigma2 = 1.0;
		for(double x = -Math.PI/4.0; x <= Math.PI/4.0; x+= Math.PI/4.0)
		{
			for(double y = -1.0; y<=1.0; y+=1.0)
			{
				double dist = Math.pow(state.get(0, 0)-x, 2) + Math.pow(state.get(1, 0)-y, 2);
				phi.set(base++, 0, Math.exp(-dist/(2*sigma2)));
			}
		}
		
		return phi;
	}

	@Override
	public int size() {
		return numBasis;
	}

}
