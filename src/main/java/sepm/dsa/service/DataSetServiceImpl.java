package sepm.dsa.service;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
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
		IDatabaseConnection sourceConnection = new DatabaseConnection(dataSource.getConnection());
		sourceConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
		IDataSet dataSet = sourceConnection.createDataSet();

		zip.putNextEntry(new ZipEntry("dataset.xml"));
		FlatXmlDataSet.write(dataSet, zip);
	}

	public void setDataSource(DriverManagerDataSource dataSource) {
		this.dataSource = dataSource;
	}
}
