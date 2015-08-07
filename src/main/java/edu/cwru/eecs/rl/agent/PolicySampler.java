package edu.cwru.eecs.rl.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import edu.cwru.eecs.rl.domains.Simulator;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;

public class PolicySampler {

    public static final Logger logger = LoggerFactory.getLogger(PolicySampler.class);

    /**
     * Given a simulator and a sampling policy this will collect samples from the specified number
     * of episodes.
     *
     * @param simulator     Domain to collect from
     * @param numEpisodes   Number of episodes to collect samples from
     * @param episodeLength Maximum number of steps per episode
     * @param policy        Sampling policy to collect samples with
     * @return List of samples collected.
     */
    public static List<Sample> sample(Simulator simulator,
                                      int numEpisodes,
                                      int episodeLength,
                                      Policy policy) {

        List<Sample> samples = new ArrayList<Sample>();
        for (int i = 0; i < numEpisodes; i++) {
            simulator.reset();
            for (int j = 0; j < episodeLength; j++) {

                Sample sample = null;
                try {
                    sample = simulator.step(policy.evaluate(simulator.getState()));
                } catch (Exception e) {
                    logger.error("{}", e.getMessage(), e);
                }
                samples.add(sample);
                // if this episode has ended early then start the next one
                if (simulator.isTerminal(sample.nextState)) {
                    break;
                }
            }
        }

        return samples;
    }

    /**
     * Samples the given simulator with the given policy for the specified number of episodes. Then
     * calculates the average reward and returns that.
     *
     * @param simulator     Domain to test
     * @param numEpisodes   Number of episodes to sample from
     * @param episodeLength Maximum number of steps per episode
     * @param policy        Policy to sample with
     * @return The average reward over all of the samples
     */
    public static double evaluatePolicy(Simulator simulator,
                                        int numEpisodes,
                                        int episodeLength,
                                        Policy policy) {
        double totalRewards = 0;

        for (int i = 0; i < numEpisodes; i++) {
            simulator.reset();
            for (int j = 0; j < episodeLength; j++) {
                Sample sample = null;
                try {
                    sample = simulator.step(policy.evaluate(simulator.getState()));
                } catch (Exception e) {
                    logger.error("{}", e.getMessage(), e);
                }
                totalRewards += sample.reward;
                if (simulator.isTerminal(sample.nextState)) {
                    break;
                }
            }
        }

        return totalRewards / numEpisodes;
    }
}
