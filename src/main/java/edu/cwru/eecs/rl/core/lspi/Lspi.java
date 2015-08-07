package edu.cwru.eecs.rl.core.lspi;

import edu.cwru.eecs.linalg.SparseMatrix;
import edu.cwru.eecs.rl.basisfunctions.ExactBasis;
import edu.cwru.eecs.rl.types.Model;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import Jama.Matrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lspi implements Serializable {

    public enum PolicyImprover {LSTDQ_MTJ, LSTDQ_EXACT_MTJ}

    /**
     * Learn the policy given the samples and initial policy. Uses the lstdq Policy Improver.
     *
     * @param samples        List of samples from the enviroment that the policy applies to
     * @param initialPolicy  Starting policy. Can be random weights.
     * @param gamma          Discount factor
     * @param epsilon        lstdq policy improvement tolerance. Stops when policy changes by less
     *                       than epsilon.
     * @param maxIterations  If tolerance is not achieved by maxIterations then stop.
     * @param policyImprover Specifies the lstdq strategy
     * @return The learned policy
     */
    public static Policy learn(List<Sample> samples,
                               Policy initialPolicy,
                               double gamma,
                               double epsilon,
                               int maxIterations,
                               PolicyImprover policyImprover) {
        Policy oldPolicy;
        Policy newPolicy = initialPolicy;
        int iteration = 0;

        do {
            System.out.println("Starting iteration " + iteration);
            oldPolicy = new Policy(newPolicy);
            switch (policyImprover) {
                case LSTDQ_EXACT_MTJ:
                    newPolicy.weights = lstdqExactMtj(samples, oldPolicy, gamma);
                    break;
                case LSTDQ_MTJ:
                    // fall through
                default:
                    newPolicy.weights = lstdqMtj(samples, oldPolicy, gamma);
            }
            iteration++;
            System.out.println("epsilon: " + epsilon);
            System.out.println("normF: " + newPolicy.weights.minus(oldPolicy.weights).normF());
            System.out.println("normInf: " + newPolicy.weights.minus(oldPolicy.weights).normInf());
        } while (newPolicy.weights.minus(oldPolicy.weights).normInf() > epsilon
                 && iteration <= maxIterations);

        if (iteration >= maxIterations) {
            System.out.println("Lspi failed to converge within " + maxIterations);
            System.out.println("Epsilon: " + epsilon
                               + " normF: " + newPolicy.weights.minus(oldPolicy.weights)
                .normF());
        }

        return newPolicy;
    }

    /**
     * Performs an policy improvement iteration. This method assumes nothing about the basis
     * functions and should work for all domains.
     *
     * @param samples List of samples to learn from
     * @param policy  Current policy to improve
     * @param gamma   Discount factor
     * @return New policy weights
     */
    public static Matrix lstdq(List<Sample> samples, Policy policy, double gamma) {
        int basisSize = policy.basis.size();
        Matrix matA = Matrix.identity(basisSize, basisSize).times(.01);
        Matrix vecB = new Matrix(basisSize, 1);

        for (Sample sample : samples) {
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

            // update matA
            matA.plusEquals(phi1.times(phi2.minusEquals(phi3.timesEquals(gamma)).transpose()));

            // update vecB
            vecB.plusEquals(phi1.timesEquals(sample.reward));
        }

        Matrix weightVec = matA.solve(vecB);

        return weightVec;
    }

    public static Matrix lstdqMtj(List<Sample> samples,
                                  Policy policy,
                                  double gamma) {
        int basisSize = policy.basis.size();
        Matrix matA = Matrix.identity(basisSize, basisSize).times(.01);
        Matrix vecB = new Matrix(basisSize, 1);

        System.out.println("Evaluating the samples");
        for (Sample sample : samples) {
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

            // update matA
            matA.plusEquals(phi1.times(phi2.minusEquals(phi3.timesEquals(gamma)).transpose()));

            // update vecB
            vecB.plusEquals(phi1.timesEquals(sample.reward));
        }

        System.out.println("Converting to MTJ matrices");
        no.uib.cipr.matrix.Matrix sparseMatA = new LinkedSparseMatrix(basisSize, basisSize);
        for (int i = 0; i < basisSize; i++) {
            for (int j = 0; j < basisSize; j++) {
                if (matA.get(i, j) != 0) {
                    sparseMatA.set(i, j, matA.get(i, j));
                }
            }
        }

        Vector denseVecB = new DenseVector(basisSize);
        for (int i = 0; i < basisSize; i++) {
            denseVecB.set(i, vecB.get(i, 0));
        }

        System.out.println("Solving matrix equations");
        IterativeSolver solver = new GMRES(denseVecB);

        Vector vecX = new DenseVector(basisSize);
        try {
            vecX = solver.solve(sparseMatA, denseVecB, vecX);
        } catch (IterativeSolverNotConvergedException e) {
            System.err.println("IterativeSolverNotConvergedException: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        System.out.println("Copying solution back to old matrix type");
        Matrix weightVec = Matrix.random(vecX.size(), 1);

        for (int i = 0; i < vecX.size(); i++) {
            weightVec.set(i, 0, vecX.get(i));
        }

        return weightVec;
    }

    public static Matrix lstdqExactMtj(List<Sample> samples,
                                       Policy policy,
                                       double gamma) {
        ExactBasis basis = null;
        if (policy.basis instanceof ExactBasis) {
            basis = (ExactBasis) policy.basis;
        } else {
            System.err.println("LSTDQExact requires a policy with a "
                    + "basis function of class ExactBasis.class. "
                    + "Running normal LSTDQ instead.");
            return lstdq(samples, policy, gamma);
        }

        int basisSize = policy.basis.size();
        no.uib.cipr.matrix.Matrix matA = new LinkedSparseMatrix(basisSize, basisSize);
        System.out.println("Preconditioning matrix");
        for (int i=0; i<basisSize; i++) {
            matA.set(i, i, .01);
        }
        Vector vecB = new DenseVector(basisSize);

        System.out.println("Evaluating samples");
        for (Sample sample : samples) {
            int bestAction = 0;
            try {
                bestAction = policy.sparseEvaluate(sample.nextState);
            } catch (Exception e) {
                System.err.println("Failed to evaluate the policy");
                e.printStackTrace();
            }

            int currStateIndex = basis.getStateActionIndex(sample.currState, sample.action);
            int nextStateIndex = basis.getStateActionIndex(sample.nextState, bestAction);

            if (currStateIndex == nextStateIndex) {
                matA.set(currStateIndex, currStateIndex, matA.get(currStateIndex, currStateIndex) + 1 - gamma);
            } else {
                matA.set(currStateIndex, currStateIndex, matA.get(currStateIndex, currStateIndex) + 1);
                matA.set(currStateIndex, nextStateIndex, matA.get(currStateIndex, nextStateIndex) - gamma);
            }

            vecB.set(currStateIndex, vecB.get(currStateIndex) + sample.reward);
        }

        System.out.println("Solving matrix equations");
        IterativeSolver solver = new GMRES(vecB);
        int maxGmresIterations = 1000000;
        solver.setIterationMonitor(new DefaultIterationMonitor(maxGmresIterations, 1e-5, 1e-50, 1e+5));

        Vector vecX = new DenseVector(basisSize);

        while (maxGmresIterations <= 1000000000) {
            try {
                vecX = solver.solve(matA, vecB, vecX);
                break;
            } catch (IterativeSolverNotConvergedException e) {
                System.err.println("IterativeSolverNotConvergedException: " + e.getMessage());
                System.err.println("Ran for " + solver.getIterationMonitor().iterations() + " iterations");
                e.printStackTrace();
            }
            maxGmresIterations *= 10;
            solver.setIterationMonitor(new DefaultIterationMonitor(maxGmresIterations, 1e-5, 1e-50, 1e+5));
            System.out.println("Trying again with " + maxGmresIterations + " iterations");
        }

        System.out.println("Copying solution back to old matrix type");
        Matrix weightVec = new Matrix(vecX.size(), 1);

        for (int i = 0; i < basisSize; i++) {
            weightVec.set(i, 0, vecX.get(i));
        }

        return weightVec;
    }
}