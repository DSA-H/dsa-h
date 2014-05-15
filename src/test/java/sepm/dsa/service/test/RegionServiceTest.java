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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class RegionServiceTest{//} extends DBTestCase {

	@Autowired
	private RegionService rs;

    private Region addRegion;
    private Region deleteRegion;
    private Region updateRegion;

    private static String _testDir        = "src/test/resources";//"test/resources";
    private static String _dbFile         = "testData.xml";
    private static String _driverClass    = "org.hsqldb.jdbc.JDBCDriver";//"org.apache.derby.jdbc.EmbeddedDriver";
    private static String _jdbcConnection = "jdbc:hsqldb:file:testDB"; //"jdbc:derby:testdb";
    private static String _testTableName  = "Regions";

    public RegionServiceTest(){

        /*System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbc.JDBCDriver" );
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:hsqldb:file:testDB" );
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa" );
        System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "" );*/
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
        //rs.add(deleteRegion);
        //rs.add(updateRegion);
    }

    @After
    public void teardown() {
        // Teardown for data used by the unit tests
    }

    //@DatabaseSetup("testData.xml")
    @Test
    public void testXML()throws ClassNotFoundException, DatabaseUnitException, IOException, SQLException {
        fullDatabaseImport(new File(_testDir, _dbFile));
        ITable actualTable = getConnection().createDataSet().getTable(_testTableName);
        System.out.println("DB-File 0 = " + rs.getAll().toString());
        System.out.println(actualTable.getRowCount());

        IDataSet expectedDataSet = new FlatXmlDataSet(new File(_testDir, _dbFile));
        ITable expectedTable = expectedDataSet.getTable(_testTableName);
        Assertion.assertEquals(expectedTable, actualTable);
    }

/*
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
    }*/

   /* @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("testData.xml"));
    }*/

    public static void fullDatabaseExport(File file) throws ClassNotFoundException, DatabaseUnitException, DataSetException, IOException, SQLException {
        IDatabaseConnection connection = getConnection();

        ITableFilter filter = new DatabaseSequenceFilter(connection);
        IDataSet dataset    = new FilteredDataSet(filter, connection.createDataSet());
        FlatXmlDataSet.write(dataset, new FileOutputStream(file));
    }

    public static void fullDatabaseImport(File file) throws ClassNotFoundException, DatabaseUnitException, IOException, SQLException {
        IDatabaseConnection connection = getConnection();
        IDataSet dataSet = new FlatXmlDataSet(file, true);

        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
    }

    public static IDatabaseConnection getConnection() throws ClassNotFoundException, DatabaseUnitException, SQLException {
        // database connection
        Class driverClass = Class.forName(_driverClass);
        Connection jdbcConnection = DriverManager.getConnection(_jdbcConnection);
        return new DatabaseConnection(jdbcConnection);
    }
}
