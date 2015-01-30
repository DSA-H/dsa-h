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
	private SaveCancelService saveCancelService;

    @Override
	public void importDataSet(File file) throws DSARuntimeException {
		try {
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			// check first if dataset is complete
			boolean hasDataset= false, hasProperties= false, hasNameFile= false, hasMaps= false, hasWorldMap = false;
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				switch (zipEntry.getName()) {
					case "dataset.xml":
						hasDataset = true;
						break;
					case "resources/properties":
						hasProperties = true;
						break;
					case "resources/nameFile.txt":
						hasNameFile = true;
						break;
					case "maps/active/":
						hasMaps = true;
						break;
				}
				if(zipEntry.getName().startsWith("maps/active/worldMap")) {
					hasWorldMap = true;
				}
			}
			if(!hasDataset) {
				throw new DSARuntimeException("Das gewählte Dataset ist ungültig. Datenbank fehlt. (dataset.xml)");
			}else if(!hasProperties) {
				throw new DSARuntimeException("Das gewählte Dataset ist ungültig. Properties fehlen. (resources/properties) ");
			}else if(!hasNameFile) {
				throw new DSARuntimeException("Das gewählte Dataset ist ungültig. Namens-Datei fehlt. (resources/nameFile.txt)");
			}else if(!hasMaps) {
				throw new DSARuntimeException("Das gewählte Dataset ist ungültig. Karten Ordner fehlt. (maps/active/)");
			}else if(!hasWorldMap) {
				throw new DSARuntimeException("Das gewählte Dataset ist ungültig. Keine Weltkarte gefunden.");
			}

			// load the dataset
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				InputStream inputStream = zipFile.getInputStream(zipEntry);
				switch (zipEntry.getName()) {
					case "dataset.xml":
						restoreDatabase(inputStream);
						break;
					case "resources":
						clearResourcesDirectory();
						break;
					case "resources/properties":
						restoreProperties(inputStream);
						break;
					case "resources/nameFile.txt":
						restoreNameFile(inputStream);
						break;
					case "maps/active/":
						clearMapDirectory();
						break;
					default:
						if (zipEntry.getName().startsWith("maps/active")) {
							restoreMap(zipEntry, inputStream);
						}
				}
			}
			applicationEventPublisher.publishEvent(new ReloadEvent(this));
		} catch (FileNotFoundException e) {
			throw new DSARuntimeException("Die Datei konnte nicht gefunden werden.", e);
		} catch (IOException | SQLException | DatabaseUnitException e) {
			throw new DSARuntimeException("Importieren des Datensets fehlgeschlagen. Aktuelles Datenset möglicherweise beschädigt.", e);
		}
	}

	private void restoreProperties(InputStream inputStream) throws IOException {
		File file = new File("resources/properties");
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		byte[] buffer = new byte[1024];
		int bytes;
		while ((bytes = inputStream.read(buffer)) > -1){
			out.write(buffer, 0, bytes);
		}
		out.close();
	}

	private void restoreNameFile(InputStream inputStream) throws IOException {
		File file = new File("resources/nameFile.txt");
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		byte[] buffer = new byte[1024];
		int bytes;
		while ((bytes = inputStream.read(buffer)) > -1){
			out.write(buffer, 0, bytes);
		}
		out.close();
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

	private void clearResourcesDirectory() throws IOException {
		File resourcesDirectory = new File("resources");
		FileUtils.deleteDirectory(resourcesDirectory);
		resourcesDirectory.mkdir();
	}

    @Override
    public void saveCurrentDataSet(File file) throws DSARuntimeException {
		try {
			ZipOutputStream zip = getZipOutputStream(file);
			addResourceDirectory(zip);
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

	private void addResourceDirectory(ZipOutputStream zip) throws IOException {
		zip.putNextEntry(new ZipEntry("resources/"));

		// Add properties file
		String propertiesPath = "resources/properties";
		zip.putNextEntry(new ZipEntry(propertiesPath));
		zip.write(Files.readAllBytes(Paths.get(propertiesPath)));

		// Add nameFile.txt
		String nameFilePath = "resources/nameFile.txt";
		zip.putNextEntry(new ZipEntry(nameFilePath));
		zip.write(Files.readAllBytes(Paths.get(nameFilePath)));
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
		saveCancelService.closeSession();
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

	public void setSaveCancelService(SaveCancelService saveCancelService) {
		this.saveCancelService = saveCancelService;
	}
}
