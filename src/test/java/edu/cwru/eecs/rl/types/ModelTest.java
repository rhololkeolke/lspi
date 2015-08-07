package edu.cwru.eecs.rl.types;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Devin on 5/17/15.
 */
public class ModelTest {

    Model model;
    Sample[] samples = new Sample[8];
    Vector state0 = new DenseVector(new double[]{0});
    Vector state1 = new DenseVector(new double[]{1});

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
        Map<Model.VectorWrapper, Double> transitionProbs = model.getTransitionProbabilities(state0, 0);
        assertEquals(2, transitionProbs.size());
        assertEquals(.33, transitionProbs.get(new Model.VectorWrapper(state0)), .01);
        assertEquals(.67, transitionProbs.get(new Model.VectorWrapper(state1)), .01);

        transitionProbs = model.getTransitionProbabilities(state0, 1);
        assertEquals(2, transitionProbs.size());
        assertEquals(.93, transitionProbs.get(new Model.VectorWrapper(state0)), .01);
        assertEquals(.07, transitionProbs.get(new Model.VectorWrapper(state1)), .01);

        transitionProbs = model.getTransitionProbabilities(state1, 0);
        assertEquals(2, transitionProbs.size());
        assertEquals(.6, transitionProbs.get(new Model.VectorWrapper(state0)), .01);
        assertEquals(.4, transitionProbs.get(new Model.VectorWrapper(state1)), .01);

        transitionProbs = model.getTransitionProbabilities(state1, 1);
        assertEquals(2, transitionProbs.size());
        assertEquals(.29, transitionProbs.get(new Model.VectorWrapper(state0)), .01);
        assertEquals(.71, transitionProbs.get(new Model.VectorWrapper(state1)), .01);
    }

    @Test
    public void testGetTransitionProbability() throws Exception {
        assertEquals(.33, model.getTransitionProbability(state0, 0, state0), .01);
        assertEquals(.67, model.getTransitionProbability(state0, 0, state1), .01);
        assertEquals(.93, model.getTransitionProbability(state0, 1, state0), .01);
        assertEquals(.07, model.getTransitionProbability(state0, 1, state1), .01);
        assertEquals(.6, model.getTransitionProbability(state1, 0, state0), .01);
        assertEquals(.4, model.getTransitionProbability(state1, 0, state1), .01);
        assertEquals(.29, model.getTransitionProbability(state1, 1, state0), .01);
        assertEquals(.71, model.getTransitionProbability(state1, 1, state1), .01);
    }

    @Test
    public void testGetReward() throws Exception {
        assertEquals(.33, model.getReward(state0, 0), .01);
        assertEquals(-.86, model.getReward(state0, 1), .01);
        assertEquals(-.2, model.getReward(state1, 0), .01);
        assertEquals(.43, model.getReward(state1, 1), .01);
    }

    @Test
    public void testGetAllStateActions() throws Exception {
        Set<Model.StateActionTuple> stateActions = model.getAllStateActions();
        assertEquals(4, stateActions.size());
        assertTrue(stateActions.contains(new Model.StateActionTuple(state0, 0)));
        assertTrue(stateActions.contains(new Model.StateActionTuple(state0, 1)));
        assertTrue(stateActions.contains(new Model.StateActionTuple(state1, 0)));
        assertTrue(stateActions.contains(new Model.StateActionTuple(state1, 1)));
    }
}