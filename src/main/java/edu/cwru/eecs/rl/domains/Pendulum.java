package edu.cwru.eecs.rl.domains;

import java.util.Random;

import Jama.Matrix;
import edu.cwru.eecs.rl.types.Sample;

public class Pendulum implements Simulator{

	private double dt, tol, noise;
	private Random rng;
	private Matrix currState;
	
	private static final double[] actions = {-50.0, 0.0, 50.0};
	
	private Matrix alpha;
	private Matrix beta, gamma;
	private double pow;
	
	public Pendulum()
	{
		pendulumInit(.1, 1e-5, 10, 0);
	}
	
	public Pendulum(long distSeed)
	{
		pendulumInit(.1, 1e-5, 10, distSeed);
	}
	
	public Pendulum(double dt, double tol, double noise, long distSeed)
	{
		pendulumInit(dt, tol, noise, distSeed);
	}
	
	private void pendulumInit(double dt, double tol, double noise, long distSeed)
	{
		this.dt = dt;
		this.tol = tol;
		this.noise = noise;
		
		rng = new Random(distSeed);
		
		this.reset();
		
		double[] alphaArray = {.25, 3.0/8.0, 12.0/13.0, 1, .5};
		alpha = new Matrix(alphaArray, alphaArray.length);
		
		double[][] betaArray = { 
				{.25, 0, 0, 0, 0, 0},
				{3.0/32.0, 9.0/32.0, 0, 0, 0, 0},
				{1932.0/2197.0, -7200.0/2197.0, 7296.0/2197.0, 0, 0, 0},
				{8341.0/4104.0, -32832.0/4104.0, 29440.0/4104.0, -845.0/4104.0, 0, 0},
				{-6080.0/20520.0, 41040.0/20520.0, -28352.0/20520.0, 9295.0/20520.0, -5643.0/20520.0, 0} };
		beta = new Matrix(betaArray).transpose();
		
		double[][] gammaArray = { 
	    		 {902880.0/7618050.0, 0, 3953664.0/7618050.0, 3855735.0/7618050.0, -1371249.0/7618050.0, 277020.0/7618050.0},
	    		 {-2090.0/752400.0, 0, 22528.0/752400.0, 21970.0/752400.0, -15048.0/752400.0, -27360.0/752400.0} };
		gamma = new Matrix(gammaArray).transpose();
		
		pow = 1.0/5.0;
	}
	
	@Override
	public void reset() {
		// pendulum starts at angle 0 (vertical)
		// and angular velocity 0 with some perturbation
		currState = new Matrix(2, 1);
		
		// generate noise for angle
		currState.set(0, 0, (2*rng.nextDouble()-1)*.2);
		currState.set(1, 0, (2*rng.nextDouble()-1)*.2);
	}

	@Override
	public Sample step(int action) {
		double u = actions[action] + rng.nextGaussian()*noise;
		
		// the following is a translation of the pendulum_ode45 method
		// that comes with the LSPI example code
		double t = 0;
		double tfinal = dt;
		Matrix y0 = new Matrix(3, 1);
		y0.set(0, 0, currState.get(0, 0));
		y0.set(1, 0, currState.get(1, 0));
		y0.set(2, 0, u);
		
		double hmax = dt;
		double hmin = dt/1000.0;
		double h = dt;
		Matrix y = y0.copy();
		Matrix f = y.times(new Matrix(1, 6));
		Matrix yout = y.copy();
		double tau = tol*Math.max(y.normInf(), 1.0);
		
		while((t < tfinal) && (h >= hmin))
		{
			if(t+h > tfinal)
				h = tfinal - t;
			
			f.setMatrix(new int[]{0, 1, 2}, new int[]{0}, pendulumSim(t, y, 2.0, 8.0, .5, 9.8));
			for(int j=0; j<5; j++)
			{
                Matrix betaColumn = new Matrix(beta.getRowDimension(), 1);
                for(int i=0; i<betaColumn.getRowDimension(); i++)
                {
                    betaColumn.set(i, 0, beta.get(i, j));
                }

				f.setMatrix(new int[]{0, 1, 2}, new int[]{j+1}, pendulumSim(t + alpha.get(j, 0)*h, y.plus(f.times(h).times(betaColumn)),
						2.0, 8.0, .5, 9.8));
			}

            Matrix gammaColumn = new Matrix(gamma.getRowDimension(), 1);
            for(int i=0; i<gammaColumn.getRowDimension(); i++)
            {
                gammaColumn.set(i, 0, gamma.get(i, 1));
            }
			double delta = f.times(h).times(gammaColumn).normInf();
			tau = tol*Math.max(y.normInf(), 1.0);
			
			// update the solution only if the error is acceptable
			if(delta <= tau)
			{
				t = t + h;
                for(int i=0; i<gammaColumn.getRowDimension(); i++)
                {
                    gammaColumn.set(i, 0, gamma.get(i, 0));
                }
				y.plusEquals(f.times(h).times(gammaColumn));
				yout = y.copy();
			}
			
			// update the step size
			if(delta != 0.0)
			{
				h = Math.min(hmax, .8*h*Math.pow(tau/delta, pow));
			}
		}
		
		if(t < tfinal)
		{
			System.out.println("SINGULARITY LIKELY: " + t);
		}
		
		Matrix nextState = new Matrix(2, 1);
		nextState.set(0, 0, yout.get(0, 0));
		nextState.set(1, 0, yout.get(1, 0));
		
		int reward = 0;
		if(Math.abs(nextState.get(0, 0)) > Math.PI/2)
			reward = -1;
		
		Sample sample = new Sample(currState, action, nextState, reward, this.isTerminal(currState));
		currState = nextState;
		
		return sample;
	}
	
	private Matrix pendulumSim(double t, Matrix x,
			double pendulumMass, double cartMass, double pendulumLength, double gravity)
	{
		Matrix xdot = new Matrix(3, 1);
		double u = x.get(2, 0);
		double cx = Math.cos(x.get(0, 0));
		
		xdot.set(0, 0, x.get(1, 0));
		double accel = 1.0/(cartMass + pendulumMass);
		
		xdot.set(1, 0, ((gravity*Math.sin(x.get(0, 0))) -
				(accel*pendulumMass*pendulumLength*x.get(1, 0)*x.get(1, 0)*Math.sin(2.0*x.get(0, 0))/2.0) -
				(accel*Math.cos(x.get(0, 0))*u))/(4.0/3.0*pendulumLength - accel*pendulumMass*pendulumLength*cx*cx));
		
		xdot.set(2, 0, 0);
		
		return xdot;
	}
	
	

	@Override
	public boolean isGoal(Matrix state) {
		return false;
	}

	@Override
	public boolean isNonGoalTerminal(Matrix state) {
		return isTerminal(state);
	}

	@Override
	public boolean isTerminal(Matrix state) {
		if(Math.abs(state.get(0, 0)) > Math.PI/2)
			return true;
		return false;
	}

	@Override
	public void setState(Matrix state) {
		if(state.getRowDimension()*state.getColumnDimension() != 2)
			return;
		currState = state;
	}

	@Override
	public Matrix getState() {
		return currState;
	}

	@Override
	public int numStates() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numActions() {
		return 3;
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
