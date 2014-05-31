package sepm.dsa.dbunit;


import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import sepm.dsa.dbunit.dataset.FlatXmlDataSetLoader;
import sepm.dsa.service.SaveCancelService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DbUnitTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class
})
@DbUnitConfiguration(dataSetLoader = FlatXmlDataSetLoader.class)
@DatabaseSetup("/testData.xml")
abstract public class AbstractDatabaseTest {

    @Autowired
    protected SaveCancelService saveCancelService;

    @Before
    public void setUp() {
        saveCancelService.cancel();
    }

    @After
    public void tearDown() {
        saveCancelService.cancel();
        saveCancelService.closeSession();
    }

    public SaveCancelService getSaveCancelService() {
        return saveCancelService;
    }
}
