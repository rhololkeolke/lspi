package edu.cwru.eecs.rl.basisfunctions;

import edu.cwru.eecs.rl.types.BasisFunctions;
import Jama.Matrix;

import java.io.Serializable;

public class ExactBasis implements BasisFunctions, Serializable{

    private int[] numStates;
    private int[] offsets;
    private int numActions;

    /**
     * Constructs ExactBasis instance.
     *
     *<p>
     * numStates contains the number of possible values for each part
     * of the state vector. For example a state that consists of x and y
     * position in a 10x10 grid would have a numStates value of {10, 10}.
     *
     * @param numStates Number of possible values for each part of the state
     * @param numActions Number of possible actions
     */
    public ExactBasis(int[] numStates, int numActions) {
        this.numStates = new int[numStates.length];
        System.arraycopy(numStates, 0, this.numStates, 0, numStates.length);
        this.offsets = new int[numStates.length];
        this.offsets[0] = 1;
        for (int i = 1; i < offsets.length; i++) {
            offsets[i] = offsets[i - 1] * numStates[i - 1];
        }
        this.numActions = numActions;
    }

    /**
     * Given a world state and an action return the index in the sparse vector
     * that is equal to 1. There will only be one value that is non-zero and its
     * value will be 1.
     *
     * @param state Environment state
     * @param action Action being performed
     * @return Index in sparse vector equal to 1
     */
    public int getStateActionIndex(Matrix state, int action) {
        int base = action * (this.size() / numActions);

        int offset = 0;
        for (int i = 0; i < state.getRowDimension(); i++) {
            offset += offsets[i] * state.get(i, 0);
        }
        return base + offset;
    }

    @Override
    public Matrix evaluate(Matrix state, int action) {
        Matrix result = new Matrix(this.size(), 1);

        int index = getStateActionIndex(state, action);

        result.set(index, 0, 1);
        return result;
    }

    @Override
    public int size() {
        int totalStates = 1;
        for (int i : numStates) {
            totalStates *= i;
        }
        return totalStates * numActions;
    }
}