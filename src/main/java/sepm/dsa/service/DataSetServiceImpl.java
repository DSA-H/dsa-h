package sepm.dsa.service;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import sepm.dsa.exceptions.DSARuntimeException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DataSetServiceImpl implements DataSetService {
	private DriverManagerDataSource dataSource;

	public void saveCurrentDataSet(File file) throws DSARuntimeException {
		try {
			ZipOutputStream zip = getZipOutputStream(file);
			addPropertyFile(zip);
			addDatabaseDump(zip);
			zip.close();
		} catch (IOException|SQLException|DatabaseUnitException e) {
			throw new DSARuntimeException("Speichern der Datei fehlgeschlagen", e);
		}
	}

	private ZipOutputStream getZipOutputStream(File file) throws FileNotFoundException {
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
		return new ZipOutputStream(outputStream);
	}

	private void addPropertyFile(ZipOutputStream zip) throws IOException {
		zip.putNextEntry(new ZipEntry("properties"));
		zip.write(Files.readAllBytes(Paths.get("properties")));
	}

	private void addDatabaseDump(ZipOutputStream zip) throws IOException, SQLException, DatabaseUnitException {
		IDatabaseConnection connection = getDatabaseConnection();

		ITableFilter filter = new DatabaseSequenceFilter(connection);
		IDataSet dataSet = new FilteredDataSet(filter, connection.createDataSet());

		zip.putNextEntry(new ZipEntry("dataset.xml"));
		XmlDataSet.write(dataSet, zip);
	}

	private IDatabaseConnection getDatabaseConnection() throws DatabaseUnitException, SQLException {
		IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
		connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
		return connection;
	}

	public void setDataSource(DriverManagerDataSource dataSource) {
		this.dataSource = dataSource;
	}
}
