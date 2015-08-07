package edu.cwru.eecs.rl.domains;

import edu.cwru.eecs.rl.types.Sample;
import no.uib.cipr.matrix.Vector;

public interface Simulator {

    void reset();

    Sample step(int action);

    boolean isGoal(Vector state);

    boolean isNonGoalTerminal(Vector state);

    boolean isTerminal(Vector state);

    void setState(Vector state);

    Vector getState();

    int numStates();

    int numActions();

    String stateStr(Vector state);

    String actionStr(double action);
}
