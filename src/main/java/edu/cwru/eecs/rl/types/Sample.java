package edu.cwru.eecs.rl.types;

import Jama.Matrix;

import java.io.Serializable;

public class Sample implements Serializable {

    private static final long serialVersionUID = -3257281634992483010L;

    public Matrix currState;
    public int action;
    public double reward;
    public Matrix nextState;
    public boolean absorb;

    /**
     * Constructs a sample class. Absorb is set to false.
     *
     * @param currState s
     * @param action    a
     * @param nextState s'
     * @param reward    r
     */
    public Sample(Matrix currState, int action,
                  Matrix nextState, double reward) {
        this.currState = currState.copy();
        this.action = action;
        this.nextState = nextState.copy();
        this.reward = reward;
        this.absorb = false;
    }

    /**
     * Constructs a sample class. Absorb is set to false.
     *
     * @param currState s
     * @param action    a
     * @param nextState s'
     * @param reward    r
     */
    public Sample(double[] currState, int action,
                  double[] nextState, double reward) {
        this.currState = new Matrix(currState, currState.length);
        this.action = action;
        this.nextState = new Matrix(nextState, nextState.length);
        this.reward = reward;
        this.absorb = false;
    }

    /**
     * Constructs a sample class.
     *
     * @param currState s
     * @param action    a
     * @param nextState s'
     * @param reward    r
     * @param absorb    True if this action ended the episode
     */
    public Sample(Matrix currState, int action,
                  Matrix nextState, double reward, boolean absorb) {
        this.currState = currState.copy();
        this.action = action;
        this.nextState = nextState.copy();
        this.reward = reward;
        this.absorb = absorb;
    }

    /**
     * Constructs a sample class.
     *
     * @param currState s
     * @param action    a
     * @param nextState s'
     * @param reward    r
     * @param absorb    True if this action ended the episode
     */
    public Sample(double[] currState, int action,
                  double[] nextState, double reward, boolean absorb) {
        this.currState = new Matrix(currState, currState.length);
        this.action = action;
        this.nextState = new Matrix(nextState, nextState.length);
        this.reward = reward;
        this.absorb = false;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) {
            return false;
        }
        Sample input = (Sample) other;
        boolean equal = true;
        equal = equal && (this.currState.getRowDimension() == input.currState.getRowDimension());
        equal = equal
                && (this.currState.getColumnDimension() == input.currState.getColumnDimension());
        if (!equal) {
            return false; // no sense checking the rest of its already not equal
        }
        for (int i = 0; i < this.currState.getRowDimension(); i++) {
            // this might not work because of doubles
            equal = equal && (this.currState.get(i, 0) == input.currState.get(i, 0));
        }
        equal = equal && (this.action == input.action);
        equal = equal && (this.nextState.getRowDimension() == input.nextState.getRowDimension());
        equal = equal
                && (this.nextState.getColumnDimension() == input.nextState.getColumnDimension());
        if (!equal) {
            return false; // no sense checking the rest if its already not equal
        }
        for (int i = 0; i < this.nextState.getRowDimension(); i++) {
            equal = equal && (this.nextState.get(i, 0) == input.nextState.get(i, 0));
        }
        equal = equal && (this.reward == input.reward);

        return equal;
    }
}
