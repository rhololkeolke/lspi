package edu.cwru.eecs.rl.core.lspi;

import edu.cwru.eecs.linalg.SparseMatrix;
import edu.cwru.eecs.rl.basisfunctions.ExactBasis;
import edu.cwru.eecs.rl.types.Model;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import Jama.Matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lspi implements Serializable {

    public enum PolicyImprover {LSTDQ, LSTDQ_EXACT, LSTDQ_EXACT_WITH_WEIGHTING, LSTDQ_OPT_EXACT}

    /**
     * Learn the policy given the samples and initial policy. Uses the lstdq Policy Improver.
     *
     * @param samples       List of samples from the enviroment that the policy applies to
     * @param initialPolicy Starting policy. Can be random weights.
     * @param gamma         Discount factor
     * @param epsilon       lstdq policy improvement tolerance. Stops when policy changes by less
     *                      than epsilon.
     * @param maxIterations If tolerance is not achieved by maxIterations then stop.
     * @return The learned policy
     */
    public static Policy learn(List<Sample> samples,
                               Policy initialPolicy,
                               double gamma,
                               double epsilon,
                               int maxIterations) {
        return Lspi.learn(samples,
                          initialPolicy,
                          gamma,
                          epsilon,
                          maxIterations,
                          PolicyImprover.LSTDQ,
                          0,
                          0);
    }

    public static Policy learn(List<Sample> samples,
                               Policy initialPolicy,
                               double gamma,
                               double epsilon,
                               int maxIterations,
                               double tolerance,
                               int maxSolverIterations) {
        return Lspi.learn(samples, initialPolicy, gamma, epsilon, maxIterations, PolicyImprover.LSTDQ_EXACT, tolerance, maxSolverIterations);
    }

    public static Policy learn(Model model,
                               Policy initialPolicy,
                               double gamma,
                               double epsilon,
                               int maxIterations,
                               double tolerance,
                               int maxSolverIterations) {
        Policy oldPolicy;
        Policy newPolicy = initialPolicy;
        int iteration = 0;

        do {
            oldPolicy = new Policy(newPolicy);
            newPolicy.weights = lstdqModelExact(model, oldPolicy, gamma, tolerance, maxSolverIterations);
            iteration++;
            System.out.println("epsilon: " + epsilon);
            System.out.println("iteration: " + iteration);
            System.out.println("maxiteration: " + maxIterations);
            System.out.println("distance: " + newPolicy.weights.minus(oldPolicy.weights).normF());
            System.out.println("normInf: " + newPolicy.weights.minus(oldPolicy.weights).normInf());
        } while (newPolicy.weights.minus(oldPolicy.weights).normInf() > epsilon && iteration <= maxIterations);

        if (iteration >= maxIterations) {
            System.out.println("Lspi failed to converge within " + maxIterations);
            System.out.println("Epsilon: " + epsilon
                    + " Distance: " + newPolicy.weights.minus(oldPolicy.weights)
                    .normF());
            System.out.println("normInf: " + newPolicy.weights.minus(oldPolicy.weights).normInf());
        }

        return newPolicy;
    }


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
                               PolicyImprover policyImprover,
                               double tolerance,
                               int maxSolverIterations) {
        Policy oldPolicy;
        Policy newPolicy = initialPolicy;
        int iteration = 0;

        do {
            oldPolicy = new Policy(newPolicy);
            switch (policyImprover) {
                case LSTDQ:
                    newPolicy.weights = lstdq(samples, oldPolicy, gamma);
                    break;
                case LSTDQ_EXACT_WITH_WEIGHTING:
                    newPolicy.weights = lstdqExactWithWeighting(samples, oldPolicy, gamma);
                    break;
                case LSTDQ_OPT_EXACT:
                    newPolicy.weights = lstdqOptExact(samples, oldPolicy, gamma);
                    break;
                default:
                    newPolicy.weights = lstdqExact(samples, oldPolicy, gamma, tolerance, maxSolverIterations);
            }
            iteration++;
            System.out.println("distance: " + newPolicy.weights.minus(oldPolicy.weights).normF());
            System.out.println("normInf: " + newPolicy.weights.minus(oldPolicy.weights).normInf());
        } while (newPolicy.weights.minus(oldPolicy.weights).normF() > epsilon
                 && iteration <= maxIterations);

        if (iteration >= maxIterations) {
            System.out.println("Lspi failed to converge within " + maxIterations);
            System.out.println("Epsilon: " + epsilon
                               + " Distance: " + newPolicy.weights.minus(oldPolicy.weights)
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

    public static Matrix lstdqModelExact(Model model,
                                         Policy policy,
                                         double gamma,
                                         double tolerance,
                                         int maxSolverIterations) {
        ExactBasis basis = null;
        if (policy.basis instanceof ExactBasis) {
            basis = (ExactBasis) policy.basis;
        } else {
            throw new IllegalArgumentException("Policy must use ExactBasis class or one of its children");
        }

        int basisSize = policy.basis.size();
        SparseMatrix matA = SparseMatrix.diagonal(basisSize, .01);
        Matrix vecB = new Matrix(basisSize, 1);

        for (Model.StateActionTuple saTuple : model.getAllStateActions()) {
            int currStateIndex = basis.getStateActionIndex(saTuple.s, saTuple.a);
            matA.update(currStateIndex, currStateIndex, 1);
            for (Map.Entry<Model.MatrixWrapper, Double> transitionProbs : model.getTransitionProbabilities(saTuple).entrySet()) {
                int bestAction = 0;
                try {
                    bestAction = policy.evaluate(transitionProbs.getKey().m);
                } catch (Exception e) {
                    System.err.println("Failed to evaluate the policy");
                    e.printStackTrace();
                }
                int nextStateIndex = basis.getStateActionIndex(transitionProbs.getKey().m, bestAction);
                matA.update(currStateIndex, nextStateIndex, -gamma*transitionProbs.getValue());
            }

            vecB.set(currStateIndex, 0, vecB.get(currStateIndex, 0) + model.getReward(saTuple));
        }

        return steepestDescent(matA, vecB, tolerance, maxSolverIterations);
    }

    /**
     * Performs and LSTDQ iteration. This method only works when the basis function is of type
     * ExactBasis. These types of basis functions return a vector that is all zeros except for one
     * element that is equal to 1. This allows for optimizations which skip calculating the
     * intermediate matrices.
     *
     * @param samples List of samples to learn from
     * @param policy  Current policy to improve
     * @param gamma   Discount factor
     * @return Updated policy weights
     */
    public static Matrix lstdqExact(List<Sample> samples,
                                    Policy policy,
                                    double gamma,
                                    double tolerance,
                                    int maxSolverIterations) {
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
        SparseMatrix matA = SparseMatrix.diagonal(basisSize, .01);
        Matrix vecB = new Matrix(basisSize, 1);

        System.out.println("Evaluating samples");
        for (Sample sample : samples) {
            // Find the value of pi(s')
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
                matA.update(currStateIndex, currStateIndex, 1 - gamma);
            } else {
                matA.update(currStateIndex, currStateIndex, 1);
                matA.update(currStateIndex, nextStateIndex, -gamma);
            }

            vecB.set(currStateIndex, 0, vecB.get(currStateIndex, 0) + sample.reward);
        }

        System.out.println("Solving matrix equations");
        return steepestDescent(matA, vecB, tolerance, maxSolverIterations);
    }

    public static Matrix steepestDescent(SparseMatrix matA, Matrix vecB, double tolerance, int maxSolverIterations) {
        // TODO: check if matrix A is singular before attempting to solve the matrix

        // initial guess for weightVec is random
        // TODO: change this to the last policy
        Matrix weightVec = Matrix.random(vecB.getRowDimension(), 1);
        boolean converged = false;
        double normInf = Double.MAX_VALUE;
        for (int iter = 0; iter < maxSolverIterations; iter++) {
            // calculate the residual
            Matrix residual = vecB.minus(matA.times(weightVec));

            // calculate the learning rate
            // TODO: Could be more efficient if dotProduct realizes that the two are the same
            double alphaNumer = dotProduct(residual, residual);
            Matrix alphaVecDenom = matA.times(residual);
            double alphaDenom = dotProduct(residual, alphaVecDenom);

            Matrix deltaX = residual.times(alphaNumer / alphaDenom);

            normInf = deltaX.normInf();
            if (normInf < tolerance) {
                System.out.println("Steepest gradient converged at iteration "
                        + iter + " with deltaX.inf(): " + normInf);
                converged = true;
                break;
            }

            // update weightVec
            weightVec.plusEquals(deltaX);
        }

        if (!converged) {
            System.err.println("Steepest gradient failed to converge within "
                    + maxSolverIterations + " iterations with error: " + normInf);
        } else {
            System.out.println("Steepest gradient converged within "
                    + maxSolverIterations + " iterations");
        }
        return weightVec;
    }

    /**
     * Performs and LSTDQ iteration. This method only works when the basis function is of type
     * ExactBasis. These types of basis functions return a vector that is all zeros except for one
     * element that is equal to 1. This allows for optimizations which skip calculating the
     * intermediate matrices.
     *
     * <p>
     * Tries to correct for biased sampling by reweighting samples.
     *
     * @param samples List of samples to learn from
     * @param policy  Current policy to improve
     * @param gamma   Discount factor
     * @return Updated policy weights
     */
    @Deprecated
    public static Matrix lstdqExactWithWeighting(List<Sample> samples,
                                                 Policy policy,
                                                 double gamma) {
        ExactBasis basis = null;
        if (policy.basis instanceof ExactBasis) {
            basis = (ExactBasis) policy.basis;
        } else {
            System.err.println("lstdqExact requires a policy with a basis "
                               + "function of class ExactBasis.class. "
                               + "Running normal lstdq instead.");
            return lstdq(samples, policy, gamma);
        }

        Map<String, List<Double>> sampleWeights = new HashMap<>();
        for (Sample sample : samples) {
            String key = stateToString(sample.currState);
            List<Double> counts = sampleWeights.containsKey(key)
                                  ? sampleWeights.get(key) : new ArrayList<Double>();
            for (int i = counts.size(); i < policy.actions; i++) {
                counts.add(0.0);
            }
            counts.set(sample.action, counts.get(sample.action) + 1);
            sampleWeights.put(key, counts);
        }

        // normalize
        for (String key : sampleWeights.keySet()) {
            double total = 0;
            List<Double> counts = sampleWeights.get(key);
            for (Double count : counts) {
                total += count;
            }
            for (int i = 0; i < counts.size(); i++) {
                if (counts.get(i) != 0) {
                    counts.set(i, total / counts.get(i));
                }
            }
            total = 0;
            for (Double count : counts) {
                total += count;
            }
            for (int i = 0; i < counts.size(); i++) {
                counts.set(i, counts.get(i) / total);
            }
        }

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

            int currStateIndex = basis.getStateActionIndex(sample.currState, sample.action);
            int nextStateIndex = basis.getStateActionIndex(sample.nextState, bestAction);

            String key = stateToString(sample.currState);
            List<Double> weights = sampleWeights.get(key);
            double weight = weights.size() > sample.action ? weights.get(sample.action) : 0;
            if (currStateIndex == nextStateIndex) {
                matA.set(currStateIndex, currStateIndex, matA.get(currStateIndex, currStateIndex)
                                                         + weight * (1 - gamma));
            } else {
                matA.set(currStateIndex, currStateIndex, matA.get(currStateIndex, currStateIndex)
                                                         + weight);
                matA.set(currStateIndex, nextStateIndex, matA.get(currStateIndex, nextStateIndex)
                                                         - weight * gamma);
            }

            vecB.set(currStateIndex, 0, vecB.get(currStateIndex, 0) + weight * sample.reward);
        }

        return matA.solve(vecB);
    }

    /**
     * Performs and LSTDQ iteration. This method only works when the basis function is of type
     * ExactBasis. These types of basis functions return a vector that is all zeros except for one
     * element that is equal to 1. This allows for optimizations which skip calculating the
     * intermediate matrices.
     *
     * <p>
     * Utilizes the iterative construction of the inverse matrix B from the LSPI paper.
     *
     * @param samples List of samples to learn from
     * @param policy  Current policy to improve
     * @param gamma   Discount factor
     * @return Updated policy weights
     */
    @Deprecated
    public static Matrix lstdqOptExact(List<Sample> samples, Policy policy, double gamma) {
        ExactBasis basis = null;
        if (policy.basis instanceof ExactBasis) {
            basis = (ExactBasis) policy.basis;
        } else {
            System.err.println("lstdqOptExact requires a policy with a "
                               + "basis function of class ExactBasis.class. "
                               + "Running normal lstdq instead.");
            return lstdq(samples, policy, gamma);
        }

        int basisSize = policy.basis.size();
        // iteratively constructed inverse of matrix A. See LSPI paper
        Matrix matB = Matrix.identity(basisSize, basisSize).times(1.0 / .01);
        Matrix vecB = new Matrix(basisSize, 1);

        for (Sample sample : samples) {
            int bestAction = 0;
            try {
                bestAction = policy.evaluate(sample.nextState);
            } catch (Exception e) {
                System.err.println("Failed to evaluate policy");
                e.printStackTrace();
            }

            int currStateIndex = basis.getStateActionIndex(sample.currState, sample.action);
            int nextStateIndex = basis.getStateActionIndex(sample.nextState, bestAction);

            Matrix nextMatB = new Matrix(basisSize, basisSize);
            if (currStateIndex == nextStateIndex) {
                double gammaP = 1 - gamma; // gamma' =  1 - gamma
                double denom = 1 + gammaP * matB.get(currStateIndex, currStateIndex);

                for (int x = 0; x < basisSize; x++) {
                    for (int y = 0; y < basisSize; y++) {
                        nextMatB.set(x, y, matB.get(x, y)
                                           - (gammaP * matB.get(x, currStateIndex)
                                              * matB.get(currStateIndex, y)) / denom);
                    }
                }
            } else {
                double denom = 1 + matB.get(currStateIndex, currStateIndex)
                               - gamma * matB.get(nextStateIndex, currStateIndex);

                for (int x = 0; x < basisSize; x++) {
                    for (int y = 0; y < basisSize; y++) {
                        nextMatB.set(x, y,
                                     matB.get(x, y) - (-gamma * matB.get(x, currStateIndex)
                                                       * matB.get(nextStateIndex, y)
                                                       + matB.get(x, currStateIndex)
                                                         * matB.get(currStateIndex, y)) / denom);
                    }
                }
            }
            vecB.set(currStateIndex, 0, vecB.get(currStateIndex, 0) + sample.reward);
            matB = nextMatB;
        }

        return matB.times(vecB);
    }

    private static String stateToString(Matrix state) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < state.getRowDimension(); i++) {
            for (int j = 0; j < state.getColumnDimension(); j++) {
                sb.append(state.get(i, j));
                sb.append(",");
            }
            if (i < state.getRowDimension() - 1) {
                sb.append(";");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static double dotProduct(Matrix vecX, Matrix vecY) {
        if (vecX.getRowDimension() != vecY.getRowDimension()) {
            throw new IllegalArgumentException("Vector dimensions do not match. "
                                               + vecX.getRowDimension() + " != " + vecY
                .getRowDimension());
        }
        if (vecX.getColumnDimension() != 1 || vecY.getColumnDimension() != 1) {
            throw new IllegalArgumentException("Inputs are not vectors");
        }

        double result = 0;
        for (int i = 0; i < vecX.getRowDimension(); i++) {
            result += vecX.get(i, 0) * vecY.get(i, 0);
        }
        return result;
    }
}