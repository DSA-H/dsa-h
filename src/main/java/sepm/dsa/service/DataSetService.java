package sepm.dsa.service;

import sepm.dsa.exceptions.DSARuntimeException;

import java.io.File;

public interface DataSetService {

	/**
	 * Saves the current application data set (property file & database) in a zip bundle.
	 * @param file File path where the resulting zip file is saved
	 */
	void saveCurrentDataSet(File file) throws DSARuntimeException;

	/**
	 * Imports a data set from a previously saved (zip) file to the database or to the corresponding files
	 *
	 * This method is also responsible for reloading the application
	 * @param file File path from where the zip bundle is read
	 */
	void importDataSet(File file) throws DSARuntimeException;
}
