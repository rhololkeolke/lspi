package edu.cwru.eecs.rl.core.lspi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

import edu.cwru.eecs.rl.basisfunctions.ExactBasis;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.DefaultIterationMonitor;
import no.uib.cipr.matrix.sparse.GMRES;
import no.uib.cipr.matrix.sparse.IterativeSolver;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;

public class Lspi implements Serializable {

    public static final Logger logger = LoggerFactory.getLogger(Lspi.class);

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
        double normInf;
        do {
            logger.info("Starting iteration {}", iteration);
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
            assert newPolicy.weights != null;
            Vector newWeights = newPolicy.weights.copy();
            normInf = newWeights.add(-1, oldPolicy.weights).norm(Vector.Norm.Infinity);
            logger.info("normInf: {}", normInf);
        } while (normInf > epsilon && iteration <= maxIterations);

        if (iteration >= maxIterations) {
            logger.info("Lspi failed to converge within {}", maxIterations);
            logger.info("Epsilon: {} normInf: {}", epsilon, normInf);
        }

        return newPolicy;
    }

    public static Vector lstdqMtj(List<Sample> samples,
                                  Policy policy,
                                  double gamma) {
        int basisSize = policy.basis.size();
        Matrix matA = Matrices.identity(basisSize).scale(.01);

        Vector vecB = new DenseVector(basisSize);

        logger.info("Evaluating the samples");
        for (Sample sample : samples) {
            // Find the value of pi(s')
            int bestAction = 0;
            try {
                bestAction = policy.evaluate(sample.nextState);
            } catch (Exception e) {
                logger.error("Failed to evaluate the policy. Reason: {}", e.getMessage(), e);
            }

            // phi(s,a)
            Vector phi1 = policy.getPhi(sample.currState, sample.action);
            // phi(s,a)
            Vector phi2 = phi1.copy();
            // phi(s', pi(s'))
            Vector phi3 = policy.getPhi(sample.nextState, bestAction);

            // update matA
            Matrix y = new DenseMatrix(phi2.add(phi3.scale(-gamma)));
            Matrix x = new DenseMatrix(phi1);
            x.transBmultAdd(y, matA);

            // update vecB
            vecB.add(sample.reward, phi1);
        }

        logger.info("Solving matrix equations");
        IterativeSolver solver = new GMRES(vecB);

        Vector vecX = new DenseVector(basisSize);
        try {
            vecX = solver.solve(matA, vecB, vecX);
        } catch (IterativeSolverNotConvergedException e) {
            logger.error("IterativeSolverNotConvergedException: {}", e.getMessage(), e);
            return null;
        }

        return vecX;
    }

    public static Vector lstdqExactMtj(List<Sample> samples,
                                       Policy policy,
                                       double gamma) {
        ExactBasis basis;
        if (policy.basis instanceof ExactBasis) {
            basis = (ExactBasis) policy.basis;
        } else {
            logger.error("LSTDQExact requires a policy with a "
                    + "basis function of class ExactBasis.class. "
                    + "Running normal LSTDQ instead.");
            return lstdqMtj(samples, policy, gamma);
        }

        int basisSize = policy.basis.size();
        Matrix matA = new LinkedSparseMatrix(basisSize, basisSize);
        logger.info("Preconditioning matrix");
        for (int i = 0; i < basisSize; i++) {
            matA.set(i, i, .01);
        }
        Vector vecB = new DenseVector(basisSize);

        logger.info("Evaluating samples");
        for (Sample sample : samples) {
            int bestAction = 0;
            try {
                bestAction = policy.sparseEvaluate(sample.nextState);
            } catch (Exception e) {
                logger.error("Failed to evaluate the policy. Reason: {}", e.getMessage(), e);
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

        logger.info("Solving matrix equations");
        IterativeSolver solver = new GMRES(vecB);
        int maxGmresIterations = 1000000;
        solver.setIterationMonitor(new DefaultIterationMonitor(maxGmresIterations, 1e-5, 1e-50, 1e+5));

        Vector vecX = new DenseVector(basisSize);

        while (maxGmresIterations <= 1000000000) {
            try {
                vecX = solver.solve(matA, vecB, vecX);
                break;
            } catch (IterativeSolverNotConvergedException e) {
                logger.error("IterativeSolverNotConvergedException: {}", e.getMessage(), e);
                logger.error("Ran for {} iterations", solver.getIterationMonitor().iterations());
            }
            maxGmresIterations *= 10;
            solver.setIterationMonitor(new DefaultIterationMonitor(maxGmresIterations,
                    1e-5,
                    1e-50,
                    1e+5));
            logger.info("Trying again with {} iterations", maxGmresIterations);
        }
        return vecX;
    }
}