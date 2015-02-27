package edu.cwru.eecs.rl.domains;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import Jama.Matrix;
import org.junit.Test;

import edu.cwru.eecs.rl.types.Sample;

public class ProbabilisticChainTests {

	@Test
	public void testRandomSeed() {
		final int numSamples = 100;
		
		Chain simulator = new Chain(10, .5, 0);
		simulator.setState(new Matrix(new double[]{5}, 1));
		
		List<Sample> samples = new LinkedList<Sample>();
		for(int i=0; i<numSamples; i++)
		{
			samples.add(simulator.step(0));
		}
		
		// now see if the same seed reproduces the same samples
		simulator = new Chain(10, .5, 0);
		simulator.setState(new Matrix(new double[]{5}, 1));
		
		Sample testSample;
		for(int i=0; i<numSamples; i++)
		{
			testSample = simulator.step(0);
			assertTrue(testSample.equals(samples.get(i)));
		}
		
		// now verify that a different seed produces different results
		simulator = new Chain(10, .5, 1);
		simulator.setState(new Matrix(new double[]{5}, 1));
		
		boolean samplesDiffer = false;
		for(int i=0; i<numSamples; i++)
		{
			testSample = simulator.step(0);
			if(testSample != samples.get(i))
			{
				samplesDiffer = true;
				break;
			}
		}
		
		assertTrue("Different seeds produce same results!", samplesDiffer);
		
	}
	
	@Test
	public void testSometimesFailLeftStep() {
		final int numTries = 100;
		
		Chain simulator = new Chain(10, .5, 0);
		
		boolean didFailStep = false;
		for(int i=0; i<numTries; i++)
		{
			simulator.setState(new Matrix(new double[]{4}, 1));
			if((int)simulator.step(0).nextState.get(0, 0) == 5)
			{
				didFailStep = true;
				break;
			}
		}
		
		assertTrue("With success probability less than 1 would expect there to be at least one failure", didFailStep);
	}
	
	@Test
	public void testSometimesFailRightStep() {
		final int numTries = 100;
		
		Chain simulator = new Chain(10, .5, 0);
		
		boolean didFailStep = false;
		for(int i=0; i<numTries; i++)
		{
			simulator.setState(new Matrix(new double[]{4}, 1));
			if((int)simulator.step(1).nextState.get(0, 0) == 3)
			{
				didFailStep = true;
				break;
			}
		}
		
		assertTrue("With success probability less than 1 would expect there to be at least one failure", didFailStep);
	}
	
	@Test
	public void testLeftStepSuccessRate()
	{
		final int numSamples = 1000;
		Chain simulator = new Chain(10, .5, 0);
		
		int numSuccess = 0;
		for(int i=0; i<numSamples; i++)
		{
			simulator.setState(new Matrix(new double[]{4}, 1));
			if((int)simulator.step(0).nextState.get(0, 0) == 3)
				numSuccess++;
		}
		
		assertEquals(.5, numSuccess/(double)numSamples, .02);
	}
	
	@Test
	public void testRightStepSuccessRate()
	{
		final int numSamples = 1000;
		Chain simulator = new Chain(10, .5, 0);
		
		int numSuccess = 0;
		for(int i=0; i<numSamples; i++)
		{
			simulator.setState(new Matrix(new double[]{4}, 1));
			if((int)simulator.step(1).nextState.get(0, 0) == 5)
				numSuccess++;
		}
		
		assertEquals(.5, numSuccess/(double)numSamples, .02);
	}
}
