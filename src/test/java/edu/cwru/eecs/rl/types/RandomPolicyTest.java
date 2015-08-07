package edu.cwru.eecs.rl.types;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.cwru.eecs.rl.basisfunctions.FakeBasis;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

@SuppressWarnings("deprecation")
public class RandomPolicyTest {

    public static final Logger logger = LoggerFactory.getLogger(RandomPolicyTest.class);

    private Policy randomPolicy;

    /**
     * Setups the random policy for testing.
     */
    @Before
    public void setUp() {

        FakeBasis basis = new FakeBasis();

        randomPolicy = new Policy(1, 2, basis, new DenseVector(new double[]{2}));
    }

    @Test
    public void testEvaluate() {
        double firstAction = 0;
        try {
            firstAction = randomPolicy.evaluate(new DenseVector(new double[]{1}));
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        // try up to 100 times to get a different action for the same state
        // this is to make sure that random values are being used
        boolean differentAction = false;
        for (int i = 0; i < 100; i++) {
            double action = 0;
            try {
                action = randomPolicy.evaluate(new DenseVector(new double[]{1}));
            } catch (Exception e) {
                logger.error("{}", e.getMessage(), e);
            }
            if (action != firstAction) {
                differentAction = true;
            }
        }
        Assert.assertTrue(differentAction);
    }

    @Test
    public void testQValue() {
        double qValue = 0;
        try {
            qValue = randomPolicy.stateActionValue(new DenseVector(new double[]{1}), 0);
        } catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
        }
        Assert.assertEquals(2, qValue, .0001);
    }

    @Test
    public void testgetPhi() {
        Vector phi = randomPolicy.getPhi(new DenseVector(new double[]{1}), 0);
        Assert.assertEquals(1.0, phi.get(0), .001);
        Assert.assertEquals(1, phi.size());
    }

}
