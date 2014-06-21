package sepm.dsa.service;

import org.apache.commons.io.FileUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import sepm.dsa.application.ReloadEvent;
import sepm.dsa.exceptions.DSARuntimeException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class DataSetServiceImpl implements DataSetService, ApplicationEventPublisherAware {
	private DriverManagerDataSource dataSource;
	private ApplicationEventPublisher applicationEventPublisher;
	private MapService mapService;

	public void importDataSet(File file) throws DSARuntimeException {
		try {
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				switch (zipEntry.getName()) {
					case "dataset.xml":
						restoreDatabase(zipFile.getInputStream(zipEntry));
						break;
					case "properties":
						break;
					case "maps/active/":
						clearMapDirectory();
						break;
					default:
						if (zipEntry.getName().startsWith("maps/active")) {
							restoreMap(zipEntry, zipFile.getInputStream(zipEntry));
						}
				}
			}

			applicationEventPublisher.publishEvent(new ReloadEvent(this));
		} catch (FileNotFoundException e) {
			throw new DSARuntimeException("Die Datei konnte nicht gefunden werden.", e);
		} catch (IOException | SQLException | DatabaseUnitException e) {
			throw new DSARuntimeException("Importieren des Datensets fehlgeschlagen", e);
		}
	}

	private void restoreMap(ZipEntry zipEntry, InputStream inputStream) throws IOException {
		File file = new File(zipEntry.getName());
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		byte[] buffer = new byte[1024];
		int bytes;
		while ((bytes = inputStream.read(buffer)) > -1){
			out.write(buffer, 0, bytes);
		}
		out.close();
	}

	private void clearMapDirectory() throws IOException {
		FileUtils.deleteDirectory(mapService.getActiveDir());
		mapService.getActiveDir().mkdir();
	}

	public void saveCurrentDataSet(File file) throws DSARuntimeException {
		try {
			ZipOutputStream zip = getZipOutputStream(file);
			addPropertyFile(zip);
			addDatabaseDump(zip);
			addMapDirectory(zip);
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

	private void restoreDatabase(InputStream inputStream) throws IOException, DatabaseUnitException, SQLException {
		IDataSet dataSet = new XmlDataSet(inputStream);
		DatabaseOperation.CLEAN_INSERT.execute(getDatabaseConnection(), dataSet);
	}

	private void addMapDirectory(ZipOutputStream zip) throws IOException {
		File activeDir = mapService.getActiveDir();
		File[] files = activeDir.listFiles();
		if (files == null) {
			return;
		}

		zip.putNextEntry(new ZipEntry("maps/active/"));
		for (File file: files) {
			zip.putNextEntry(new ZipEntry("maps/active/" + file.getName()));
			zip.write(Files.readAllBytes(file.toPath()));
		}
	}

	private IDatabaseConnection getDatabaseConnection() throws DatabaseUnitException, SQLException {
		IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
		connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
		return connection;
	}

	public void setDataSource(DriverManagerDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void setMapService(MapService mapService) {
		this.mapService = mapService;
	}
}
