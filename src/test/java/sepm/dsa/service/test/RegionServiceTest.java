package sepm.dsa.service.test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.Temperature;
import sepm.dsa.service.RegionService;

import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class RegionServiceTest extends DBTestCase {

	@Autowired
	private RegionService rs;

    private Region addRegion;
    private Region deleteRegion;
    private Region updateRegion;

    public RegionServiceTest(){

        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbc.JDBCDriver" );
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:hsqldb:file:testDB" );
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa" );
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "" );
        // System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, "" );



        //TODO: Import directly into testDB
        addRegion = new Region();
        addRegion.setName("testRegionAdd");
        addRegion.setColor("000000");
        addRegion.setComment("comment");
        addRegion.setTemperature(Temperature.ARCTIC);
        addRegion.setRainfallChance(RainfallChance.LOW);

        deleteRegion = new Region();
        deleteRegion.setName("testRegionDelete");
        deleteRegion.setColor("000000");
        deleteRegion.setComment("comment");
        deleteRegion.setTemperature(Temperature.HIGH);
        deleteRegion.setRainfallChance(RainfallChance.MEDIUM);

        updateRegion = new Region();
        updateRegion.setName("testRegionUpdate");
        updateRegion.setColor("000000");
        updateRegion.setComment("comment");
        updateRegion.setTemperature(Temperature.MEDIUM);
        updateRegion.setRainfallChance(RainfallChance.HIGH);

    }

    @Before
    public void setup() {
        System.out.println("testSetup");
        rs.add(deleteRegion);
        rs.add(updateRegion);
    }

    @After
    public void teardown() {
        // Teardown for data used by the unit tests
    }

    @DatabaseSetup("testData.xml")
    @Test
    public void testXML(){
        System.out.println("DB-File 0 = " + rs.getAll().toString());
    }

    @Test
    public void testAdd() {
        System.out.println("DB-File 0 = " + rs.get(0));
        int size = rs.getAll().size();
        int id = rs.add(addRegion);

        assertTrue(rs.getAll().size() - 1 == size);
        assertTrue(rs.get(id).equals(addRegion));
        assertEquals(rs.get(id), addRegion);
    }

    @Test
    public void testRemove() {
        int size = rs.getAll().size();
        rs.remove(deleteRegion);
        assertTrue(rs.getAll().size() + 1 == size);
    }

    @Test
    public void testUpdate() {
        int size = rs.getAll().size();
        updateRegion.setName("testRegionUpdate2");
        updateRegion.setColor("999999");
        updateRegion.setComment("comment");
        updateRegion.setTemperature(Temperature.LOW);

        rs.update(updateRegion);
        assertTrue (rs.getAll().size() == size);
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("testData.xml"));
    }
}
