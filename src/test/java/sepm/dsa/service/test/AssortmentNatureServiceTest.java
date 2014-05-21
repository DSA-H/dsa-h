package sepm.dsa.service.test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
public class AssortmentNatureServiceTest {

    @Autowired


    @Test
    @DatabaseSetup("/testData.xml")
    public void testAdd() throws Exception {
        //TODO mock
        assert(false);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testAdd1() throws Exception {
        //TODO mock
        assert(false);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testUpdate() throws Exception {
        //TODO mock
        assert(false);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testRemove() throws Exception {
        //TODO mock
        assert(false);
    }

    @Test
    @DatabaseSetup("/testData.xml")
    public void testGetAll() throws Exception {
        //TODO mock
        assert(false);
    }
}