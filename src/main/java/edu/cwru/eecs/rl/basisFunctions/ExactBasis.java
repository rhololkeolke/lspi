package edu.cwru.eecs.rl.basisFunctions;

import Jama.Matrix;
import edu.cwru.eecs.rl.types.BasisFunctions;

import java.io.Serializable;

public class ExactBasis implements BasisFunctions, Serializable{

    private int[] numStates;
    private int[] offsets;
    private int numActions;

    public ExactBasis(int[] numStates, int numActions)
    {
        this.numStates = new int[numStates.length];
        System.arraycopy(numStates, 0, this.numStates, 0, numStates.length);
        this.offsets = new int[numStates.length];
        this.offsets[0] = 1;
        for(int i=1; i<offsets.length; i++) {
            offsets[i] = offsets[i - 1] * numStates[i - 1];
        }
        this.numActions = numActions;
    }

    public int getStateActionIndex(Matrix state, int action)
    {
        int base = action*(this.size()/numActions);

        int offset = 0;
        for(int i=0; i<state.getRowDimension(); i++)
        {
            offset += offsets[i]*state.get(i, 0);
        }
        return base+offset;
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
        for(int i : numStates)
        {
            totalStates *= i;
        }
        return totalStates*numActions;
    }
}