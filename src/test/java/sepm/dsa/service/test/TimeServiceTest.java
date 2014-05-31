package sepm.dsa.service.test;


import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.DSADate;
import sepm.dsa.service.TimeService;

public class TimeServiceTest extends AbstractDatabaseTest {

    @Autowired
    private TimeService timeService;

    @Test
    public void changeDateTest() {
        DSADate dsaDate = new DSADate(17);

        timeService.setCurrentDate(dsaDate);
        dsaDate = timeService.getCurrentDate();

        Assert.assertEquals(dsaDate.getTimestamp(), 17);
    }
}
