package edu.cwru.eecs.rl.types;

import Jama.Matrix;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.cwru.eecs.rl.basisFunctions.FakeBasis;

import java.util.Vector;

@SuppressWarnings("deprecation")
public class RandomPolicyTest {
	
	private Policy randomPolicy;
	
	@Before
	public void setUp() {
		
		FakeBasis basis = new FakeBasis();
		
		randomPolicy = new Policy(1, 2, basis, new Matrix(new double[]{2}, 1));
	}

	@Test
	public void testEvaluate() {
        double firstAction = 0;
        try {
            firstAction = randomPolicy.evaluate(new Matrix(new double[]{1}, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // try up to 100 times to get a different action for the same state
		// this is to make sure that random values are being used
		boolean differentAction = false;
		for(int i=0; i<100; i++)
		{
            double action = 0;
            try {
                action = randomPolicy.evaluate(new Matrix(new double[]{1}, 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(action != firstAction)
				differentAction = true;
		}
		Assert.assertTrue(differentAction);
	}
	
	@Test
	public void testQValue() {
        double QValue = 0;
        try {
            QValue = randomPolicy.QValue(new Matrix(new double[]{1}, 1), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals(2, QValue, .0001);
	}
	
	@Test
	public void testgetPhi() {
		Matrix phi = randomPolicy.getPhi(new Matrix(new double[]{1}, 1), 0);
		Assert.assertEquals(1.0, phi.get(0,0));
        Assert.assertEquals(1, phi.getColumnDimension()*phi.getRowDimension());
	}

}
