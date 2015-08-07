package edu.cwru.eecs.rl.domains;

import edu.cwru.eecs.rl.types.Sample;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

import java.util.Random;

public class Pendulum implements Simulator {

    private double dt;
    private double tol;
    private double noise;
    private Random rng;
    private Vector currState;

    private static final double[] actions = {-50.0, 0.0, 50.0};

    private Matrix beta;
    private Matrix gamma;
    private double pow;

    public Pendulum() {
        pendulumInit(.1, 1e-5, 10, 0);
    }

    public Pendulum(long distSeed) {
        pendulumInit(.1, 1e-5, 10, distSeed);
    }

    public Pendulum(double dt, double tol, double noise, long distSeed) {
        pendulumInit(dt, tol, noise, distSeed);
    }

    private void pendulumInit(double dt, double tol, double noise, long distSeed) {
        this.dt = dt;
        this.tol = tol;
        this.noise = noise;

        rng = new Random(distSeed);

        this.reset();

        double[][] betaArray = {
            {.25, 0, 0, 0, 0, 0},
            {3.0 / 32.0, 9.0 / 32.0, 0, 0, 0, 0},
            {1932.0 / 2197.0, -7200.0 / 2197.0, 7296.0 / 2197.0, 0, 0, 0},
            {8341.0 / 4104.0, -32832.0 / 4104.0,
             29440.0 / 4104.0, -845.0 / 4104.0, 0, 0},
            {-6080.0 / 20520.0, 41040.0 / 20520.0,
             -28352.0 / 20520.0, 9295.0 / 20520.0, -5643.0 / 20520.0, 0}};
        beta = new DenseMatrix(6, 5);
        new DenseMatrix(betaArray).transpose(beta);

        double[][] gammaArray = {{902880.0 / 7618050.0, 0, 3953664.0 / 7618050.0,
                                  3855735.0 / 7618050.0, -1371249.0 / 7618050.0,
                                  277020.0 / 7618050.0},
                                 {-2090.0 / 752400.0, 0, 22528.0 / 752400.0,
                                  21970.0 / 752400.0, -15048.0 / 752400.0, -27360.0 / 752400.0}};
        gamma = new DenseMatrix(6, 2);
        new DenseMatrix(gammaArray).transpose(gamma);

        pow = 1.0 / 5.0;
    }

    @Override
    public void reset() {
        // pendulum starts at angle 0 (vertical)
        // and angular velocity 0 with some perturbation
        currState = new DenseVector(2);

        // generate noise for angle
        currState.set(0, (2 * rng.nextDouble() - 1) * .2);
        currState.set(1, (2 * rng.nextDouble() - 1) * .2);
    }

    @Override
    public Sample step(int action) {
        double controlInput = actions[action] + rng.nextGaussian() * noise;

        // the following is a translation of the pendulum_ode45 method
        // that comes with the Lspi example code
        DenseVector y0 = new DenseVector(3);
        y0.set(0, currState.get(0));
        y0.set(1, currState.get(1));
        y0.set(2, controlInput);

        double hmax = dt;
        double hmin = dt / 1000.0;
        double timeStep = dt;
        Vector vecY = y0.copy();
        Matrix matF = new DenseMatrix(3, 6);

        Matrix matY = new DenseMatrix(vecY);
        matY.mult(new DenseMatrix(1, 6), matF);

        Vector yout = vecY.copy();
        double tau;

        double time = 0;
        double tfinal = dt;
        while ((time < tfinal) && (timeStep >= hmin)) {
            if (time + timeStep > tfinal) {
                timeStep = tfinal - time;
            }


            Vector xDot = pendulumSim(vecY, 2.0, 8.0, .5, 9.8);
            matF.set(0, 0, xDot.get(0));
            matF.set(1, 0, xDot.get(1));
            matF.set(2, 0, xDot.get(2));
            for (int j = 0; j < 5; j++) {
                Vector betaColumn = Matrices.getColumn(beta, j);
                Matrix betaColumnMat = new DenseMatrix(betaColumn);

                Matrix newMatF = new DenseMatrix(matF.numRows(), 1);
                matF.mult(timeStep, betaColumnMat, newMatF);
                Vector newVecY = vecY.copy();
                newVecY.add(Matrices.getColumn(newMatF, 0));
                xDot = pendulumSim(newVecY, 2.0, 8.0, .5, 9.8);

                matF.set(0, j+1, xDot.get(0));
                matF.set(1, j+1, xDot.get(1));
                matF.set(2, j+1, xDot.get(2));
            }

            Vector gammaColumn = Matrices.getColumn(gamma, 1);
            Matrix gammaColumnMat = new DenseMatrix(gammaColumn);

            Matrix newMatF = new DenseMatrix(3,1);
            matF.mult(timeStep, gammaColumnMat, newMatF);
            double delta = newMatF.norm(Matrix.Norm.Infinity);

            tau = tol * Math.max(vecY.norm(Vector.Norm.Infinity), 1.0);

            // update the solution only if the error is acceptable
            if (delta <= tau) {
                time = time + timeStep;
                gammaColumn = Matrices.getColumn(gamma, 0);
                gammaColumnMat = new DenseMatrix(gammaColumn);

                newMatF.zero();
                matF.mult(timeStep, gammaColumnMat, newMatF);

                vecY.add(Matrices.getColumn(newMatF, 0));

                yout = vecY.copy();
            }

            // update the step size
            if (delta != 0.0) {
                timeStep = Math.min(hmax, .8 * timeStep * Math.pow(tau / delta, pow));
            }
        }

        if (time < tfinal) {
            System.out.println("SINGULARITY LIKELY: " + time);
        }

        Vector nextState = yout.copy();

        int reward = 0;
        if (Math.abs(nextState.get(0)) > Math.PI / 2) {
            reward = -1;
        }

        Sample sample = new Sample(currState,
                                   action, nextState, reward, this.isTerminal(currState));
        currState = nextState;

        return sample;
    }

    private Vector pendulumSim(Vector stateMat,
                               double pendulumMass,
                               double cartMass,
                               double pendulumLength,
                               double gravity) {
        Vector xdot = new DenseVector(3);
        double controlInput = stateMat.get(2);
        double cx = Math.cos(stateMat.get(0));

        xdot.set(0, stateMat.get(1));
        double accel = 1.0 / (cartMass + pendulumMass);

        xdot.set(1, ((gravity * Math.sin(stateMat.get(0)))
                        - (accel * pendulumMass * pendulumLength
                           * stateMat.get(1) * stateMat.get(1)
                           * Math.sin(2.0 * stateMat.get(0)) / 2.0)
                        - (accel * Math.cos(stateMat.get(0)) * controlInput))
                       / (4.0 / 3.0 * pendulumLength - accel
                                                       * pendulumMass * pendulumLength * cx * cx));

        xdot.set(2, 0);

        return xdot;
    }


    @Override
    public boolean isGoal(Vector state) {
        return false;
    }

    @Override
    public boolean isNonGoalTerminal(Vector state) {
        return isTerminal(state);
    }

    @Override
    public boolean isTerminal(Vector state) {
        if (Math.abs(state.get(0)) > Math.PI / 2) {
            return true;
        }
        return false;
    }

    @Override
    public void setState(Vector state) {
        if (state.size() != 2) {
            return;
        }
        currState = state;
    }

    @Override
    public Vector getState() {
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
    public String stateStr(Vector state) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String actionStr(double action) {
        // TODO Auto-generated method stub
        return null;
    }

}
