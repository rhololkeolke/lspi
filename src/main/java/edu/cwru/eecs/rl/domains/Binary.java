package edu.cwru.eecs.rl.domains;

import edu.cwru.eecs.rl.types.Sample;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.SparseVector;

import java.util.Iterator;
import java.util.Random;

/**
 * Created by Devin on 2/22/16.
 */
public class Binary implements Simulator {

    private int numBits;
    private Vector currState;

    public Binary(int numBits) {
        this.numBits = numBits;

        currState = new SparseVector(numBits);
    }

    @Override
    public void reset() {
        currState = new SparseVector(numBits);

    }

    @Override
    public Sample step(int action) {
        boolean bitIsSet = currState.get(action) > .5;
        Vector nextState = currState.copy();
        int reward = -1;
        if (!bitIsSet) {
            nextState.set(action, 1.0);
        }

        boolean isGoalState = isGoal(nextState);

        if(!bitIsSet && isGoalState) {
            reward = 100;
        }

        Sample sample = new Sample(currState, action, nextState, reward, isGoalState);

        currState = nextState;
        return sample;
    }

    @Override
    public boolean isGoal(Vector state) {
        int numSet = 0;
        for (VectorEntry entry : state) {
            if (entry.get() > .5) {
                numSet++;
            }
        }
        return (numSet == numBits);
    }

    @Override
    public boolean isNonGoalTerminal(Vector state) {
        return false;
    }

    @Override
    public boolean isTerminal(Vector state) {
        return isGoal(state);
    }

    @Override
    public void setState(Vector state) {
        currState = state.copy();
    }

    @Override
    public Vector getState() {
        return currState;
    }

    @Override
    public int numStates() {
        return (int)Math.pow(2, this.numBits);
    }

    @Override
    public int numActions() {
        return this.numBits;
    }

    @Override
    public String stateStr(Vector state) {
        return state.toString();
    }

    @Override
    public String actionStr(double action) {
        return "" + action;
    }
}
