package sepm.dsa;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import sepm.dsa.dao.test.RegionDaoTests;
import sepm.dsa.service.test.*;

/**
 * Created by Chris on 13.05.2014.
 */
@RunWith(Suite.class)
@SuiteClasses({ RegionServiceTest.class, RegionBorderServiceTest.class, RegionServiceTests.class, RegionDaoTests.class })
public class TestSuiteAll{

}


