package edu.cwru.eecs.rl.domains;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DeterministicChainTests.class, ProbabilisticChainTests.class })
public class AllTests {

}
