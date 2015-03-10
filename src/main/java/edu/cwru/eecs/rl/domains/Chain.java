package edu.cwru.eecs.rl.domains;

import edu.cwru.eecs.rl.types.Sample;
import Jama.Matrix;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Chain implements Simulator {

    private int numStates;
    private double[][][] transitions;
    private Random rng;
    private Set<Integer> rewardStates;

    private Matrix currState;

    /**
     * Constructs a new Chain domain instance. The chain length is specified by numStates. The
     * probability of an action succeeding is specified by successProb. The distSeed specifies the
     * random number generator seed.
     *
     * @param numStates   Length of the chain
     * @param successProb Probably of an action succeeding
     * @param distSeed    The random number generator seed.
     */
    public Chain(int numStates, double successProb, long distSeed) {
        this.numStates = numStates;
        // stores transition success probabilities
        this.transitions = new double[numStates][2][numStates];

        // this will make the experiments reproducible
        rng = new Random(distSeed);

        // fill in the transition matrix
        for (int i = 0; i < numStates; i++) {
            // TODO: Compute this on the fly instead of storing it
            transitions[i][0][Math.max(0, i - 1)] = successProb;
            transitions[i][0][Math.min(numStates - 1, i + 1)] = 1 - successProb;
            transitions[i][1][Math.min(numStates - 1, i + 1)] = successProb;
            transitions[i][1][Math.max(0, i - 1)] = 1 - successProb;
            transitions[i][0][i] = 0;
            transitions[i][1][i] = 0;
        }

        // for now use the ends of the chain as the reward states
        // however, maybe make this an option in the future
        // hence the use of the set
        rewardStates = new HashSet<Integer>();
        rewardStates.add(new Integer(0));
        rewardStates.add(new Integer(numStates - 1));

        // in case initialize isn't called
        currState = new Matrix(new double[]{rng.nextInt(numStates)}, 1);
    }

    @Override
    public void reset() {
        currState = new Matrix(new double[]{rng.nextInt(numStates)}, 1);
    }

    @Override
    public Sample step(int action) {
        int icurrState = (int) currState.get(0, 0);
        int inextState = icurrState;
        double totProb = 0;
        for (int i = icurrState - 1; i <= icurrState + 1; i += 2) {
            int newState = Math.max(0, Math.min(numStates - 1, i));
            totProb += transitions[icurrState][action][newState];
            if (rng.nextDouble() <= totProb) {
                inextState = newState;
                break;
            }
        }

        // TODO: should this be nextState or state?
        Matrix nextState = new Matrix(new double[]{inextState}, 1);
        int reward = rewardStates.contains(inextState) ? 1 : 0;
        Sample sample = new Sample(currState, action, nextState, reward);

        currState = nextState;

        return sample;
    }

    @Override
    public boolean isGoal(Matrix state) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNonGoalTerminal(Matrix state) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isTerminal(Matrix state) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setState(Matrix state) {
        // TODO Auto-generated method stub
        currState = state;
    }

    @Override
    public Matrix getState() {
        return currState;
    }

    @Override
    public int numStates() {
        // TODO Auto-generated method stub
        return numStates;
    }

    @Override
    public int numActions() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public String stateStr(Matrix state) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String actionStr(double action) {
        // TODO Auto-generated method stub
        return null;
    }

}
