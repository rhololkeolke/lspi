package edu.cwru.eecs.rl;

import edu.cwru.eecs.rl.basisfunctions.*;
import edu.cwru.eecs.rl.edu.cwru.eecs.rl.core.lspi.LSPITests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.cwru.eecs.rl.agent.PolicySamplerTests;
import edu.cwru.eecs.rl.domains.DeterministicChainTests;
import edu.cwru.eecs.rl.domains.DeterministicPendulumTests;
import edu.cwru.eecs.rl.domains.ProbabilisticChainTests;
import edu.cwru.eecs.rl.domains.ProbabilisticPendulumTests;
import edu.cwru.eecs.rl.types.RandomPolicyTest;

@RunWith(Suite.class)
@SuiteClasses({PolicySamplerTests.class, DeterministicPendulumTests.class, ProbabilisticPendulumTests.class, DeterministicChainTests.class, ProbabilisticChainTests.class, RandomPolicyTest.class, PolynomialBasisTests.class, GaussianRBFTests.class, ExactBasisTests.class, ChainLearnTests.class, LSPITests.class, PendulumLearnTests.class})
public class AllTests {

}
