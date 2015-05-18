package edu.cwru.eecs.rl.types;

import Jama.Matrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Model {

    public static class MatrixUtils {
        private MatrixUtils() {}

        public static boolean equals(Matrix a, Matrix b) {
            if (a == b) {
                return true;
            }

            if (a == null || b == null) {
                return false;
            }

            if (a.getRowDimension() != b.getRowDimension()) {
                return false;
            }

            if (a.getColumnDimension() != b.getColumnDimension()) {
                return false;
            }

            for (int i = 0; i < a.getArray().length; ++i) {
                if (!Arrays.equals(a.getArray()[i], b.getArray()[i])) {
                    return false;
                }
            }

            return true;
        }

        public static int hashCode(Matrix a) {
            if (a == null) {
                return 0;
            }

            int result = 1;
            for (double[] innerArray : a.getArray()) {
                result = 31 * result + Arrays.hashCode(innerArray);
            }
            return result;
        }
    }

    public static class StateActionTuple {
        public final Matrix s;
        public final int a;

        public StateActionTuple(Matrix s, int a) {
            this.s = s.copy();
            this.a = a;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StateActionTuple saTuple = (StateActionTuple) o;

            return a == saTuple.a && MatrixUtils.equals(s, saTuple.s);
        }

        @Override
        public int hashCode() {
            int result = a;
            return 31 * result + MatrixUtils.hashCode(s);
        }
    }

    public static class MatrixWrapper {
        public final Matrix m;

        public MatrixWrapper(Matrix m) {
            this.m = m.copy();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (o.getClass() == MatrixWrapper.class) {
                MatrixWrapper matWrapper = (MatrixWrapper)o;
                return MatrixUtils.equals(m, matWrapper.m);
            } else if (o.getClass() == Matrix.class) {
                Matrix mat = (Matrix)o;
                return MatrixUtils.equals(m, mat);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return MatrixUtils.hashCode(m);
        }
    }

    private Map<StateActionTuple, Integer> stateActionCounts;
    private Map<StateActionTuple, Map<MatrixWrapper, Integer>> transitionCounts;
    private Map<StateActionTuple, Double> rewardSums;

    public Model() {
        stateActionCounts = new HashMap<>();
        transitionCounts = new HashMap<>();
        rewardSums = new HashMap<>();
    }

    public void addSample(Sample sample) {
        StateActionTuple key = new StateActionTuple(sample.currState, sample.action);

        Integer count = stateActionCounts.getOrDefault(key, 0);
        stateActionCounts.put(key, count + 1);

        Map<MatrixWrapper, Integer> stateCounts = transitionCounts.get(key);
        if (stateCounts == null) {
            stateCounts = new HashMap<>();
            transitionCounts.put(key, stateCounts);
        }
        MatrixWrapper nextStateWrapper = new MatrixWrapper(sample.nextState);
        stateCounts.put(nextStateWrapper, stateCounts.getOrDefault(nextStateWrapper, 0) + 1);

        rewardSums.put(key, rewardSums.getOrDefault(key, 0.0) + sample.reward);
    }

    public Map<MatrixWrapper, Double> getTransitionProbabilities(Matrix state, int action) {
        StateActionTuple key = new StateActionTuple(state, action);
        return getTransitionProbabilities(key);
    }

    public Map<MatrixWrapper, Double> getTransitionProbabilities(StateActionTuple saTuple) {
        Map<MatrixWrapper, Double> transitionProbabilities = new HashMap<>();
        int stateActionCount = stateActionCounts.getOrDefault(saTuple, 0);
        if (stateActionCount == 0) {
            return transitionProbabilities;
        }

        Map<MatrixWrapper, Integer> counts = transitionCounts.get(saTuple);

        for (Map.Entry<MatrixWrapper, Integer> transitionCount : counts.entrySet()) {
            transitionProbabilities.put(transitionCount.getKey(), transitionCount.getValue()/(double)stateActionCount);
        }
        return transitionProbabilities;
    }

    public double getTransitionProbability(Matrix state, int action, Matrix statePrime) {
        StateActionTuple key = new StateActionTuple(state, action);

        int stateActionCount = stateActionCounts.getOrDefault(key, 0);
        if (stateActionCount == 0) {
            return 0;
        }

        Map<MatrixWrapper, Integer> counts = transitionCounts.get(key);

        return counts.getOrDefault(new MatrixWrapper(statePrime), 0)/(double)stateActionCount;
    }

    public double getReward(Matrix state, int action) {
        StateActionTuple key = new StateActionTuple(state, action);
        return getReward(key);
    }

    public double getReward(StateActionTuple saTuple) {
        int stateActionCount = stateActionCounts.getOrDefault(saTuple, 0);
        if (stateActionCount == 0) {
            return 0;
        }

        return rewardSums.get(saTuple)/stateActionCount;
    }

    public Set<StateActionTuple> getAllStateActions() {
        return stateActionCounts.keySet();
    }
}
