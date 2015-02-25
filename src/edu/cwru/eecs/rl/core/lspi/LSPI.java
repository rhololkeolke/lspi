package edu.cwru.eecs.rl.core.lspi;

import Jama.Matrix;
import edu.cwru.eecs.rl.basisFunctions.ExactBasis;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import linalg.SparseMatrix;

import java.io.Serializable;
import java.util.*;

public class LSPI implements Serializable {
	
	private static final long serialVersionUID = 5103792662788904957L;
	
	public enum PolicyImprover { LSTDQ, LSTDQ_EXACT, LSTDQ_EXACT_WITH_WEIGHTING, LSTDQ_OPT_EXACT}

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
            else if(policyImprover == PolicyImprover.LSTDQ_EXACT_WITH_WEIGHTING)
                new_policy.weights = LSTDQExactWithWeighting(samples, old_policy, gamma);
            else if(policyImprover == PolicyImprover.LSTDQ_OPT_EXACT)
                new_policy.weights = LSTDQOptExact(samples, old_policy, gamma);
			else
				new_policy.weights = LSTDQExact(samples, old_policy, gamma);
			iteration++;
            System.out.println("distance: " + new_policy.weights.minus(old_policy.weights).normF());
		} while(new_policy.weights.minus(old_policy.weights).normF() > epsilon && iteration <= maxIterations);

		
		if(iteration >= maxIterations)
		{
			System.out.println("LSPI failed to converge within " + maxIterations);
            System.out.println("Epsilon: " + epsilon + " Distance: " + new_policy.weights.minus(old_policy.weights).normF());
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
			Matrix phi2 = phi1.copy();
			// phi(s', pi(s'))
			Matrix phi3 = policy.getPhi(sample.nextState, bestAction);
			
			// update A
			A.plusEquals(phi1.times(phi2.minusEquals(phi3.timesEquals(gamma)).transpose()));
			
			// update b
			b.plusEquals(phi1.timesEquals(sample.reward));
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
        SparseMatrix spA = SparseMatrix.diagonal(k, .01);
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
                spA.update(i, i, 1-gamma);
            }
            else
            {
                spA.update(i, i, 1);
                spA.update(i, j, -gamma);
            }

            b.set(i, 0, b.get(i, 0) + sample.reward);
        }

        // TODO: check if matrix A is singular before attempting to solve the matrix

        int maxIterations = 1000;
        double tolerance = .001;

        // initial guess for x is random
        // TODO: change this to the last policy
        Matrix x = Matrix.random(k, 1);
        boolean converged = false;
        double normInf = Double.MAX_VALUE;
        for(int iter=0; iter<maxIterations; iter++)
        {
            // calculate the residual
            Matrix r = b.minus(spA.times(x));

            // calculate the learning rate
            double alpha_numer = dotProduct(r, r); // TODO: Could be more efficient if dotProduct realizes that the two are the same
            Matrix alpha_vec_denom = spA.times(r);
            double alpha_denom = dotProduct(r, alpha_vec_denom);

            Matrix deltaX = r.times(alpha_numer/alpha_denom);

            normInf = deltaX.normInf();
            if(normInf < tolerance) {
                System.out.println("Steepest gradient converged at iteration " + iter + " with deltaX.inf(): " + normInf);
                converged = true;
                break;
            }

            // update x
            x.plusEquals(deltaX);
        }

        if(!converged)
        {
            System.err.println("Steepest gradient failed to converge within " + maxIterations + " iterations with error: " + normInf);
        }
        return x;
    }

    public static Matrix LSTDQExactWithWeighting(List<Sample> samples, Policy policy, double gamma) {
        ExactBasis basis = null;
        if (policy.basis instanceof ExactBasis)
            basis = (ExactBasis) policy.basis;
        else {
            System.err.println("LSTDQExact requires a policy with a basis function of class ExactBasis.class. Running normal LSTDQ instead.");
            return LSTDQ(samples, policy, gamma);
        }

        Map<String, List<Double>> sampleWeights = new HashMap<String, List<Double>>();
        for (Sample sample : samples) {
            String key = stateToString(sample.currState);
            List<Double> counts = sampleWeights.containsKey(key) ? sampleWeights.get(key) : new ArrayList<Double>();
            for(int i=counts.size(); i<policy.actions; i++)
            {
                counts.add(0.0);
            }
            counts.set(sample.action, counts.get(sample.action)+1);
            sampleWeights.put(key, counts);
        }

        // normalize
        for (String key : sampleWeights.keySet()) {
            double total = 0;
            List<Double> counts = sampleWeights.get(key);
            for(Double count : counts) {
                total += count;
            }
            for(int i=0; i<counts.size(); i++) {
                if(counts.get(i) != 0)
                    counts.set(i, total/counts.get(i));
            }
            total = 0;
            for(Double count : counts) {
                total += count;
            }
            for(int i=0; i<counts.size(); i++) {
                counts.set(i, counts.get(i)/total);
            }

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

            String key = stateToString(sample.currState);
            List<Double> weights = sampleWeights.get(key);
            double weight = weights.size() > sample.action ? weights.get(sample.action) : 0;
            if(i == j)
            {

                A.set(i, i, A.get(i, i) + weight*(1-gamma));
            }
            else
            {
                A.set(i, i, A.get(i, i) + weight);
                A.set(i, j, A.get(i, j) - weight*gamma);
            }

            b.set(i, 0, b.get(i, 0) + weight*sample.reward);
        }

        return A.solve(b);
    }

    public static Matrix LSTDQOptExact(List<Sample> samples, Policy policy, double gamma)
    {
        ExactBasis basis = null;
        if(policy.basis instanceof ExactBasis)
            basis = (ExactBasis)policy.basis;
        else
        {
            System.err.println("LSTDQOptExact requires a policy with a basis function of class ExactBasis.class. Running normal LSTDQ instead.");
            return LSTDQ(samples, policy, gamma);
        }

        int k = policy.basis.size();
        Matrix B = Matrix.identity(k,k).times(1./.01);
        Matrix b = new Matrix(k, 1);

        for(Sample sample : samples)
        {
            int bestAction = 0;
            try {
                bestAction = policy.evaluate(sample.nextState);
            } catch(Exception e) {
                System.err.println("Failed to evaluate policy");
                e.printStackTrace();
            }

            int i = basis.getStateActionIndex(sample.currState, sample.action);
            int j = basis.getStateActionIndex(sample.nextState, bestAction);

            Matrix H = new Matrix(k,k);
            if(i == j)
            {
                double gammaP = 1 - gamma; // gamma' =  1 - gamma
                double denom = 1 + gammaP*B.get(i,i);

                for(int x=0; x<k; x++)
                {
                    for(int y=0; y<k; y++)
                    {
                        H.set(x,y, B.get(x,y) - (gammaP*B.get(x,i)*B.get(i,y))/denom);
                    }
                }
            }
            else
            {
                double denom = 1 + B.get(i,i) - gamma*B.get(j, i);

                for(int x=0; x<k; x++)
                {
                    for(int y=0; y<k; y++)
                    {
                        H.set(x,y, B.get(x,y) - (-gamma*B.get(x,i)*B.get(j,y) + B.get(x,i)*B.get(i,y))/denom);
                    }
                }
            }
            b.set(i, 0, b.get(i, 0) + sample.reward);
            B = H;
        }

        return B.times(b);
    }

    private static String stateToString(Matrix state)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0; i<state.getRowDimension(); i++) {
            for(int j=0; j<state.getColumnDimension(); j++) {
                sb.append(state.get(i, j));
                sb.append(",");
            }
            if(i < state.getRowDimension()-1) {
                sb.append(";");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static double dotProduct(Matrix x, Matrix y)
    {
        if(x.getRowDimension() != y.getRowDimension())
            throw new IllegalArgumentException("Vector dimensions do not match. " + x.getRowDimension() + " != " + y.getRowDimension());
        if(x.getColumnDimension() != 1 || y.getColumnDimension() != 1)
            throw new IllegalArgumentException("Inputs are not vectors");

        double result = 0;
        for(int i=0; i<x.getRowDimension(); i++)
        {
            result += x.get(i, 0) * y.get(i, 0);
        }
        return result;
    }
}