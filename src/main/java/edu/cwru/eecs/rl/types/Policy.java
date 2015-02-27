package edu.cwru.eecs.rl.types;

import Jama.Matrix;

import java.io.Serializable;

public class Policy implements Serializable {
	
	private static final long serialVersionUID = 5504754409584472117L;
	
	public double explore;
	public int actions;
	public BasisFunctions basis;
	public Matrix weights;
	
	public Policy(double explore, int actions, BasisFunctions basis, Matrix weights)
	{
		this.explore = explore;
		this.actions = actions;
		this.basis = basis;
		this.weights = weights.copy();
	}
	
	public Policy(Policy old_policy)
	{
		this.explore = old_policy.explore;
		this.actions = old_policy.actions;
		this.basis = old_policy.basis;
		this.weights = old_policy.weights.copy();
	}
	
	public int evaluate(double[] state) throws Exception {
		return evaluate(new Matrix(state, state.length));
	}
	
	// return best action for given state
	public int evaluate(Matrix state) throws Exception {
		int bestAction = 0;
		
		if(Math.random() < this.explore)
		{
			bestAction = (int)(Math.random()*actions);	
		} 
		else 
		{
			double bestQ = Double.NEGATIVE_INFINITY;
			for(int action=0; action<actions; action++)
			{
				double Q = this.QValue(state, action);
				if(Q > bestQ)
				{
					bestQ = Q;
					bestAction = action;
				}
			}
		}
		return bestAction;
	}
	
	// return Q function value for state action pair
	public double QValue(Matrix state, int action) throws Exception {

        Matrix phi = getPhi(state, action);
        if(phi.getRowDimension() != this.weights.getRowDimension())
        {
            throw new Exception("Phi matrix dimension does not match policy weight dimensions");
        }

        double total = 0;
        for(int i=0; i<phi.getRowDimension(); i++)
        {
            total += phi.get(i, 0) * this.weights.get(i, 0);
        }
		return total;
	}
	
	public Matrix getPhi(Matrix state, int action)
	{
		return basis.evaluate(state, action);
	}
}
