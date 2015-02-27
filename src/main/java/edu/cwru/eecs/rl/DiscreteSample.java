package edu.cwru.eecs.rl;

public class DiscreteSample {
	
	public int state;
	public int action;
	public double reward;
	public int nextState;
	
	public DiscreteSample()
	{
		this.state = 0;
		this.action = 0;
		this.reward = 0;
		this.nextState = 0;
	}
	
	public DiscreteSample(int state, int action, int nextState, double reward)
	{
		this.state = state;
		this.action = action;
		this.reward = reward;
		this.nextState = nextState;
	}

	public boolean equals(Object obj)
	{
		if(obj.getClass() != this.getClass())
			return false;
		DiscreteSample sample = (DiscreteSample)obj;
		
		boolean result = true;
		result = result && (sample.state == this.state);
		result = result && (sample.action == this.action);
		result = result && (sample.nextState == this.nextState);
		result = result && (sample.reward == this.reward);
		return result;
	}
}
