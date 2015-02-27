package edu.cwru.eecs.rl.agent;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;

public class PolicySampler {
	
	public static List<Sample> sample(Simulator simulator, int numEpisodes, int episodeLength, Policy policy)
	{
		List<Sample> samples = new ArrayList<Sample>();
		for(int i=0; i<numEpisodes; i++)
		{
			simulator.reset();
			for(int j=0; j<episodeLength; j++)
			{

                Sample sample = null;
                try {
                    sample = simulator.step(policy.evaluate(simulator.getState()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                samples.add(sample);
				// if this episode has ended early then start the next one
				if(simulator.isTerminal(sample.nextState))
					break;
			}
		}
		
		return samples;
	}
	
	public static double evaluatePolicy(Simulator simulator, int numEpisodes, int episodeLength, Policy policy)
	{
		double totalRewards = 0;
		
		for(int i=0; i<numEpisodes; i++)
		{
			simulator.reset();
			for(int j=0; j<episodeLength; j++)
			{
                Sample sample = null;
                try {
                    sample = simulator.step(policy.evaluate(simulator.getState()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalRewards += sample.reward;
				if(simulator.isTerminal(sample.nextState))
					break;
			}
		}
		
		return totalRewards/numEpisodes;
	}
}
