package edu.cwru.eecs.rl;

import Jama.Matrix;
import edu.cwru.eecs.rl.basisFunctions.ExactBasis;
import edu.cwru.eecs.rl.core.lspi.LSPI;
import edu.cwru.eecs.rl.types.BasisFunctions;
import edu.cwru.eecs.rl.types.Policy;
import edu.cwru.eecs.rl.types.Sample;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rhol on 8/4/14.
 */
public class LSTDQProfile {

    public static void main(String[] args)
    {
        // make sure a file is provided
        if(args.length < 1)
        {
            System.err.println("Must provide a csv file of samples");
            return;
        }
        // optional argument specifying how many rows to parse
        int numSamples = 1000;
        if(args.length >= 2)
        {
            numSamples = Integer.parseInt(args[1]);
        }

        String dataFilePath = args[0];
        if(dataFilePath.startsWith("~" + File.pathSeparator))
        {
            dataFilePath = System.getProperty("user.home") + dataFilePath.substring(1);
        }

        // verify file exists
        File dataFile = new File(dataFilePath);
        if(!dataFile.exists())
        {
            System.err.println("File " + dataFile + " does not exist");
            return;
        }

        System.out.println("Opening samples file");
        List<Sample> samples = new ArrayList<Sample>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));

            System.out.println("Processing samples");
            String line = null;
            while((line = br.readLine()) != null && samples.size() < numSamples)
            {
                String[] cols = line.split(",");

                int numStateVars = (cols.length - 2)/2;

                Matrix currState = new Matrix(numStateVars, 1);
                for(int i=0; i<numStateVars; i++)
                {
                    currState.set(i, 0, Double.parseDouble(cols[i]));
                }
                int action = Integer.parseInt(cols[numStateVars]);
                double reward = Double.parseDouble(cols[numStateVars+1]);
                Matrix nextState = new Matrix(numStateVars, 1);
                for(int i=numStateVars+2; i<cols.length; i++)
                {
                    nextState.set(i - numStateVars - 2, 0, Double.parseDouble(cols[i]));
                }
                Sample newSample = new Sample(currState, action, nextState, reward);
                samples.add(newSample);
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Loaded " + samples.size() + " samples");

        BasisFunctions exact_basis = new ExactBasis(new int[]{5, 5, 6, 4}, 6);
        Policy learnedPolicy = new Policy(0,
                6,
                exact_basis,
                Matrix.random(exact_basis.size(), 1));

        System.out.println("Profiling LSTDQ");
        long startTime = System.currentTimeMillis();
        LSPI.learn(samples, learnedPolicy, .9, 1e-5, 10, LSPI.PolicyImprover.LSTDQ);
        long totalTimeLSTDQ = System.currentTimeMillis() - startTime;
        System.out.println("LSTDQ took " + totalTimeLSTDQ/1000.0 + " seconds to finish");

        learnedPolicy = new Policy(0,
                6,
                exact_basis,
                Matrix.random(exact_basis.size(), 1));

        System.out.println("Profiling LSTDQExact");
        startTime = System.currentTimeMillis();
        LSPI.learn(samples, learnedPolicy, .9, 1e-5, 10, LSPI.PolicyImprover.LSTDQ_EXACT);
        long totalTimeLSTDQExact = System.currentTimeMillis() - startTime;
        System.out.println("LSTDQ Exact took " + totalTimeLSTDQExact/1000.0 + " seconds to finish");

        System.out.println("LSTDQExact was " + totalTimeLSTDQ/totalTimeLSTDQExact + " times faster than LSTDQ");
    }
}
