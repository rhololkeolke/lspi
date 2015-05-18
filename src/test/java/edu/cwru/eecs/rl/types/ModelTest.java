package edu.cwru.eecs.rl.types;

import Jama.Matrix;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Devin on 5/17/15.
 */
public class ModelTest {

    Model model;
    Sample[] samples = new Sample[8];
    Matrix state0 = new Matrix(new double[]{0}, 1);
    Matrix state1 = new Matrix(new double[]{1}, 1);

    @Before
    public void setUp() {
        samples[0] = new Sample(state0, 0, state0, -1);
        samples[1] = new Sample(state0, 0, state1, 1);
        samples[2] = new Sample(state0, 1, state0, -1);
        samples[3] = new Sample(state0, 1, state1, 1);
        samples[4] = new Sample(state1, 0, state0, -1);
        samples[5] = new Sample(state1, 0, state1, 1);
        samples[6] = new Sample(state1, 1, state0, -1);
        samples[7] = new Sample(state1, 1, state1, 1);

        model = new Model();
        for (int i = 0; i < 4; i++) {
            model.addSample(samples[0]);
        }
        for (int i = 0; i < 8; i++) {
            model.addSample(samples[1]);
        }

        for (int i = 0; i < 13; i++) {
            model.addSample(samples[2]);
        }
        model.addSample(samples[3]);

        for (int i = 0; i < 6; i++) {
            model.addSample(samples[4]);
        }
        for (int i = 0; i < 4; i++) {
            model.addSample(samples[5]);
        }

        for (int i = 0; i < 4; i++) {
            model.addSample(samples[6]);
        }
        for (int i = 0; i < 10; i++) {
            model.addSample(samples[7]);
        }
    }

    @Test
    public void testGetTransitionProbabilities() throws Exception {
        Map<Model.MatrixWrapper, Double> transitionProbs = model.getTransitionProbabilities(state0, 0);
        assertEquals(2, transitionProbs.size());
        assertEquals(.33, transitionProbs.get(new Model.MatrixWrapper(state0)), .01);
        assertEquals(.67, transitionProbs.get(new Model.MatrixWrapper(state1)), .01);

        transitionProbs = model.getTransitionProbabilities(state0, 1);
        assertEquals(2, transitionProbs.size());
        assertEquals(.93, transitionProbs.get(new Model.MatrixWrapper(state0)), .01);
        assertEquals(.07, transitionProbs.get(new Model.MatrixWrapper(state1)), .01);

        transitionProbs = model.getTransitionProbabilities(state1, 0);
        assertEquals(2, transitionProbs.size());
        assertEquals(.6, transitionProbs.get(new Model.MatrixWrapper(state0)), .01);
        assertEquals(.4, transitionProbs.get(new Model.MatrixWrapper(state1)), .01);

        transitionProbs = model.getTransitionProbabilities(state1, 1);
        assertEquals(2, transitionProbs.size());
        assertEquals(.29, transitionProbs.get(new Model.MatrixWrapper(state0)), .01);
        assertEquals(.71, transitionProbs.get(new Model.MatrixWrapper(state1)), .01);
    }

    @Test
    public void testGetReward() throws Exception {
        assertEquals(.33, model.getReward(state0, 0), .01);
        assertEquals(-.86, model.getReward(state0, 1), .01);
        assertEquals(-.2, model.getReward(state1, 0), .01);
        assertEquals(.43, model.getReward(state1, 1), .01);
    }
}