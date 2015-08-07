package edu.cwru.eecs.rl.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;

public class Model {

    public static class VectorUtils {
        private VectorUtils() {}

        public static boolean equals(Vector a, Vector b) {
            if (a == b) {
                return true;
            }

            if (a == null || b == null) {
                return false;
            }

            if (a.size() != b.size()) {
                return false;
            }

            for (int i = 0; i < a.size(); ++i) {
                if (a.get(i) != b.get(i)) {
                    return false;
                }
            }

            return true;
        }

        public static int hashCode(Vector a) {
            if (a == null) {
                return 0;
            }

            int result = 1;
            for (VectorEntry value : a) {
                result = 31 * new Double(value.get()).hashCode();
            }
            return result;
        }
    }

    public static class StateActionTuple {
        public final Vector s;
        public final int a;

        public StateActionTuple(Vector s, int a) {
            this.s = s.copy();
            this.a = a;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StateActionTuple saTuple = (StateActionTuple) o;

            return a == saTuple.a && VectorUtils.equals(s, saTuple.s);
        }

        @Override
        public int hashCode() {
            int result = a;
            return 31 * result + VectorUtils.hashCode(s);
        }
    }

    public static class VectorWrapper {
        public final Vector v;

        public VectorWrapper(Vector v) {
            this.v = v.copy();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (o.getClass() == VectorWrapper.class) {
                VectorWrapper matWrapper = (VectorWrapper)o;
                return VectorUtils.equals(v, matWrapper.v);
            } else if (o instanceof Vector) {
                Vector vec = (Vector)o;
                return VectorUtils.equals(v, vec);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return VectorUtils.hashCode(v);
        }
    }

    private Map<StateActionTuple, Integer> stateActionCounts;
    private Map<StateActionTuple, Map<VectorWrapper, Integer>> transitionCounts;
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

        Map<VectorWrapper, Integer> stateCounts = transitionCounts.get(key);
        if (stateCounts == null) {
            stateCounts = new HashMap<>();
            transitionCounts.put(key, stateCounts);
        }
        VectorWrapper nextStateWrapper = new VectorWrapper(sample.nextState);
        stateCounts.put(nextStateWrapper, stateCounts.getOrDefault(nextStateWrapper, 0) + 1);

        rewardSums.put(key, rewardSums.getOrDefault(key, 0.0) + sample.reward);
    }

    public Map<VectorWrapper, Double> getTransitionProbabilities(Vector state, int action) {
        StateActionTuple key = new StateActionTuple(state, action);
        return getTransitionProbabilities(key);
    }

    public Map<VectorWrapper, Double> getTransitionProbabilities(StateActionTuple saTuple) {
        Map<VectorWrapper, Double> transitionProbabilities = new HashMap<>();
        int stateActionCount = stateActionCounts.getOrDefault(saTuple, 0);
        if (stateActionCount == 0) {
            return transitionProbabilities;
        }

        Map<VectorWrapper, Integer> counts = transitionCounts.get(saTuple);

        for (Map.Entry<VectorWrapper, Integer> transitionCount : counts.entrySet()) {
            transitionProbabilities.put(transitionCount.getKey(), transitionCount.getValue()/(double)stateActionCount);
        }
        return transitionProbabilities;
    }

    public double getTransitionProbability(Vector state, int action, Vector statePrime) {
        StateActionTuple key = new StateActionTuple(state, action);

        int stateActionCount = stateActionCounts.getOrDefault(key, 0);
        if (stateActionCount == 0) {
            return 0;
        }

        Map<VectorWrapper, Integer> counts = transitionCounts.get(key);

        return counts.getOrDefault(new VectorWrapper(statePrime), 0)/(double)stateActionCount;
    }

    public double getReward(Vector state, int action) {
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
