package edu.cwru.eecs.rl.types;

import java.io.Serializable;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

public class Sample implements Serializable {

    private static final long serialVersionUID = -3257281634992483010L;

    public enum Type {NORMAL, NEGATIVE, ABSTRACT};

    public Vector currState;
    public int action;
    public double reward;
    public Vector nextState;
    public boolean absorb;
    public Type type = Type.NORMAL;

    /**
     * Constructs a sample class. Absorb is set to false.
     *
     * @param currState s
     * @param action    a
     * @param nextState s'
     * @param reward    r
     */
    public Sample(Vector currState, int action,
                  Vector nextState, double reward) {
        this.currState = currState.copy();
        this.action = action;
        this.nextState = nextState.copy();
        this.reward = reward;
        this.absorb = false;
        this.type = Type.NORMAL;
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
        this.currState = new DenseVector(currState);
        this.action = action;
        this.nextState = new DenseVector(nextState);
        this.reward = reward;
        this.absorb = false;
        this.type = Type.NORMAL;
    }

    public Sample(double[] currState, int action,
                  double[] nextState, double reward, Type type) {
        this.currState = new DenseVector(currState);
        this.action = action;
        this.nextState = new DenseVector(nextState);
        this.reward = reward;
        this.absorb = false;
        this.type = type;
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
    public Sample(Vector currState, int action,
                  Vector nextState, double reward, boolean absorb) {
        this.currState = currState.copy();
        this.action = action;
        this.nextState = nextState.copy();
        this.reward = reward;
        this.absorb = absorb;
        this.type = Type.NORMAL;
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
        this.currState = new DenseVector(currState);
        this.action = action;
        this.nextState = new DenseVector(nextState);
        this.reward = reward;
        this.absorb = false;
        this.type = Type.NORMAL;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) {
            return false;
        }
        Sample input = (Sample) other;
        boolean equal = true;
        equal = equal && (this.currState.size() == input.currState.size());
        if (!equal) {
            return false; // no sense checking the rest of its already not equal
        }
        for (int i = 0; i < this.currState.size(); i++) {
            // this might not work because of doubles
            equal = equal && (this.currState.get(i) == input.currState.get(i));
        }
        equal = equal && (this.action == input.action);
        equal = equal && (this.nextState.size() == input.nextState.size());
        if (!equal) {
            return false; // no sense checking the rest if its already not equal
        }
        for (int i = 0; i < this.nextState.size(); i++) {
            equal = equal && (this.nextState.get(i) == input.nextState.get(i));
        }
        equal = equal && (this.reward == input.reward);
        equal = equal && (this.type == input.type);

        return equal;
    }
}
