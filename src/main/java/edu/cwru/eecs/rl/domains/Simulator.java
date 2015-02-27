package edu.cwru.eecs.rl.domains;

import Jama.Matrix;
import edu.cwru.eecs.rl.types.Sample;

public interface Simulator {
	void reset();
	Sample step(int action);
	
	boolean isGoal(Matrix state);
	boolean isNonGoalTerminal(Matrix state);
	boolean isTerminal(Matrix state);
	
	void setState(Matrix state);
	Matrix getState();
		
	int numStates();
	int numActions();
	
	String stateStr(Matrix state);
	String actionStr(double action);
}
