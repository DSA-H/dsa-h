package sepm.dsa.service.test;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.Tavern;
import sepm.dsa.service.TavernService;

import java.util.Arrays;

import static org.junit.Assert.*;


public class TavernServiceTest extends AbstractDatabaseTest {
    @Autowired
    private TavernService tavernService;

    @Test
    public void testCalculateUseage1() {
        Tavern t = new Tavern();
        t.setBeds(10);
        for(int i=0; i<100; i++) {
            int usedBeds = tavernService.calculateBedsUseage(t);
            assertTrue(usedBeds >= 0 && usedBeds <= 10);
        }
    }

    /**
     * In this the gaussian distribution can be controlled (manually)
     */
    @Test
    public void testCalculateUseage2() {
        Tavern t = new Tavern();
        t.setBeds(20);
        int dist[] = new int[21];
        for(int i=0; i<10000; i++) {
            int usedBeds = tavernService.calculateBedsUseage(t);
            assertTrue(usedBeds >= 0 && usedBeds <= 20);
            dist[usedBeds]++;
        }
       // System.out.println(Arrays.toString(dist));
    }
}
