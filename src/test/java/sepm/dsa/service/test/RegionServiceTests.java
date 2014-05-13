package sepm.dsa.service.test;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

/**
 * Created by Michael on 13.05.2014.
 */
@RunWith(JUnit4.class)
public class RegionServiceTests {

    private RegionService regionService;

    @Before
    public void setUp() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        regionService = (RegionService) ctx.getBean("regionService");
    }

    @After
    public void tearDown() {

    }

    @Test
    public void add_shouldPersistEntry() {
        Region r = new Region();
        r.setName("Region 1");
        r.setColor("424242");
        int id = regionService.add(r);
        TestCase.assertTrue(r.getId() != null);
        Region persistedRegion = regionService.get(id);
        TestCase.assertTrue(persistedRegion != null);

        regionService.remove(r);
    }


    private void copiedFromMain() {

        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        RegionService regionService = (RegionService) ctx.getBean("regionService");
        RegionBorderService regionBorderService = (RegionBorderService) ctx.getBean("regionBorderService");

        System.out.println("Region ID 1:" + regionService.get(1));

        Region r = new Region();
        r.setName("Region 1");
        r.setColor("424242");
        int id = regionService.add(r);
        System.out.println("Created: " + id);
        System.out.println(r.getName());
        r.setName("New Region name 1");
        regionService.update(r);
        Region r2 = regionService.get(r.getId());
        System.out.println(r2.getName());

        System.out.println("From DB:" + regionService.get(r.getId()));

        System.out.println("\nbefore removing[");
        for (Region region : regionService.getAll()) {
            System.out.println("\t" + region.getId() + ": " + region.getName());
        }
        System.out.println("]");

        regionService.remove(r);

        System.out.println("\nafter removing[");
        for (Region region : regionService.getAll()) {
            System.out.println("\t" + region.getId() + ": " + region.getName());
        }
        System.out.println("]");


        System.out.println("From DB after remove:" + regionService.get(r.getId()));

        Region region1 = new Region();
        region1.setName("Ankrador");
        region1.setColor("84A1CE");
        Region region2 = new Region();
        region2.setName("Bandrakon");
        region2.setColor("8AC5EA");
        Region region3 = new Region();
        region3.setName("Craditurien");
        region3.setColor("4122BE");
        regionService.add(region1);
        regionService.add(region2);
        regionService.add(region3);

        RegionBorder border1 = new RegionBorder();
        border1.getPk().setRegion1(region1);
        border1.getPk().setRegion2(region2);
        border1.setBorderCost(14);

        regionBorderService.add(border1);

        RegionBorder border2 = new RegionBorder();
        border2.getPk().setRegion1(region1);
        border2.getPk().setRegion2(region3);
        border2.setBorderCost(8);

        regionBorderService.add(border2);

        System.out.println("\nborders for region2:");
        for (RegionBorder border : regionBorderService.getAllForRegion(region2.getId())) {
            System.out.println(border);
        }

        System.out.println("\nborders before remove:");
        for (RegionBorder border : regionBorderService.getAll()) {
            System.out.println(border);
        }

        border2.setBorderCost(4);
        regionBorderService.update(border2);

        System.out.println("\nborders before remove:");
        for (RegionBorder border : regionBorderService.getAll()) {
            System.out.println(border);
        }

        System.out.println("get border2: " + regionBorderService.get(border2.getPk()));

        regionBorderService.remove(border1);
        regionBorderService.remove(border2);

        System.out.println("\nborders after remove:");
        for (RegionBorder border : regionBorderService.getAll()) {
            System.out.println(border);
        }

        regionService.remove(region1);
        regionService.remove(region2);
        regionService.remove(region3);


    }

}
