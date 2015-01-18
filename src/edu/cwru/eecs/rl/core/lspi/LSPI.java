package edu.cwru.eecs.rl.core.lspi;

import Jama.Matrix;
import edu.cwru.eecs.rl.basisFunctions.ExactBasis;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class LSPI implements Serializable {
	
	private static final long serialVersionUID = 5103792662788904957L;
	
	public enum PolicyImprover { LSTDQ, LSTDQ_EXACT}

	public static Policy learn(List<Sample> samples, Policy initial_policy, double gamma, double epsilon, int maxIterations)
	{
		return LSPI.learn(samples, initial_policy, gamma, epsilon, maxIterations, PolicyImprover.LSTDQ);
	}

	public static Policy learn(List<Sample> samples, Policy initial_policy, double gamma, double epsilon, int maxIterations, PolicyImprover policyImprover)
	{
		Policy old_policy;
		Policy new_policy = initial_policy;
		int iteration = 0;
		
		do {
			old_policy = new Policy(new_policy);
			if(policyImprover == PolicyImprover.LSTDQ)
				new_policy.weights = LSTDQ(samples, old_policy, gamma);
			else
				new_policy.weights = LSTDQExact(samples, old_policy, gamma);
			iteration++;
		} while(new_policy.weights.minus(old_policy.weights).normF() > epsilon && iteration < maxIterations);
		
		
		if(iteration >= maxIterations)
		{
			System.out.println("LSPI failed to converge within " + maxIterations);
		}
		
		return new_policy;
	}
	
	public static Matrix LSTDQ(List<Sample> samples, Policy policy, double gamma)
	{
		int k = policy.basis.size();
		Matrix A = Matrix.identity(k, k).times(.01);
		Matrix b = new Matrix(k, 1);
		
		for(Sample sample : samples)
		{
			// Find the value of pi(s')
            int bestAction = 0;
            try {
                bestAction = policy.evaluate(sample.nextState);
            } catch (Exception e) {
                System.err.println("Failed to evaluate the policy");
                e.printStackTrace();
            }

            // phi(s,a)
			Matrix phi1 = policy.getPhi(sample.currState, sample.action);
			// phi(s,a)
			Matrix phi2 = phi1;
			// phi(s', pi(s'))
			Matrix phi3 = policy.getPhi(sample.nextState, bestAction);
			
			// update A
			A.plusEquals(phi1.times(phi2.minus(phi3.times(gamma)).transpose()));
			
			// update b
			b.plusEquals(phi1.times(sample.reward));
		}

        Matrix x = A.solve(b);

		return x;
	}

    public static Matrix LSTDQExact(List<Sample> samples, Policy policy, double gamma)
    {
        ExactBasis basis = null;
        if(policy.basis instanceof ExactBasis)
            basis = (ExactBasis)policy.basis;
        else {
            System.err.println("LSTDQExact requires a policy with a basis function of class ExactBasis.class. Running normal LSTDQ instead.");
            return LSTDQ(samples, policy, gamma);
        }

        int k = policy.basis.size();
        Matrix A = Matrix.identity(k, k).times(.01);
        Matrix b = new Matrix(k, 1);

        for(Sample sample : samples)
        {
            // Find the value of pi(s')
            int bestAction = 0;
            try {
                bestAction = policy.evaluate(sample.nextState);
            } catch (Exception e) {
                System.err.println("Failed to evaluate the policy");
                e.printStackTrace();
            }

            int i = basis.getStateActionIndex(sample.currState, sample.action);
            int j = basis.getStateActionIndex(sample.nextState, bestAction);

            if(i == j)
            {
                A.set(i, i, A.get(i, i) + (1-gamma));
            }
            else
            {
                A.set(i, i, A.get(i, i) + 1);
                A.set(i, j, A.get(i, j) - gamma);
            }

            b.set(i, 0, b.get(i, 0) + sample.reward);
        }

        return A.solve(b);
    }
}