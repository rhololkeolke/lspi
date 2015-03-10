package edu.cwru.eecs.rl.types;

import Jama.Matrix;

import java.io.Serializable;

public class Policy implements Serializable {

    public double explore;
    public int actions;
    public BasisFunctions basis;
    public Matrix weights;

    /**
     * Constructs a policy.
     *
     * @param explore    Probability of choosing an action randomly. (0 means follow the policy
     *                   exactly)
     * @param numActions Number of actions in the policy
     * @param basis      Basis function to use when calculating best action
     * @param weights    Weights for the basis function features
     */
    public Policy(double explore, int numActions, BasisFunctions basis, Matrix weights) {
        this.explore = explore;
        this.actions = numActions;
        this.basis = basis;
        this.weights = weights.copy();
    }

    /**
     * Construct a copy of the old policy. The weights are a deep copy, but the basis function is a
     * shallow copy.
     *
     * @param oldPolicy Policy to copy
     */
    public Policy(Policy oldPolicy) {
        this.explore = oldPolicy.explore;
        this.actions = oldPolicy.actions;
        this.basis = oldPolicy.basis;
        this.weights = oldPolicy.weights.copy();
    }

    /**
     * Given a state return the best action according to the policy.
     *
     * @param state Current state of the environment
     * @return The index of the best action
     * @throws Exception If state dimensions do not match weight dimensions
     */
    public int evaluate(double[] state) throws Exception {
        return evaluate(new Matrix(state, state.length));
    }

    /**
     * Given a state return the best action according to the policy.
     *
     * @param state Current state of the environment
     * @return The index of the best action
     * @throws Exception If state dimensions do not match weight dimensions
     */
    public int evaluate(Matrix state) throws Exception {
        int bestAction = 0;

        if (Math.random() < this.explore) {
            bestAction = (int) (Math.random() * actions);
        } else {
            double bestQ = Double.NEGATIVE_INFINITY;
            for (int action = 0; action < actions; action++) {
                double currQ = this.stateActionValue(state, action);
                if (currQ > bestQ) {
                    bestQ = currQ;
                    bestAction = action;
                }
            }
        }
        return bestAction;
    }

    /**
     * Returns the state-action value for the given state action pair.
     *
     * @param state  State to calculate Q function for
     * @param action Action to calculate Q function for
     * @return State-action function value for specified state-action pair
     * @throws Exception If state dimensions do not match weight dimensions
     */
    public double stateActionValue(Matrix state, int action) throws Exception {

        Matrix phi = getPhi(state, action);
        if (phi.getRowDimension() != this.weights.getRowDimension()) {
            throw new Exception("Phi matrix dimension does not match policy weight dimensions");
        }

        double total = 0;
        for (int i = 0; i < phi.getRowDimension(); i++) {
            total += phi.get(i, 0) * this.weights.get(i, 0);
        }
        return total;
    }

    /**
     * Returns the phi matrix (i.e. the basis function evaluated for a given state action pair).
     *
     * @param state  State to evaluate basis for
     * @param action Action to evaluate basis for.
     * @return Vector of basis function features.
     */
    public Matrix getPhi(Matrix state, int action) {
        return basis.evaluate(state, action);
    }
}
