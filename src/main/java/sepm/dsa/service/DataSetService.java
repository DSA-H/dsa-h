package sepm.dsa.service;

import sepm.dsa.exceptions.DSARuntimeException;

import java.io.File;

public interface DataSetService {

	/**
	 * Saves the current application data set (property file & database) in a zip bundle.
	 * @param file File path where the resulting zip file is saved
	 */
	void saveCurrentDataSet(File file) throws DSARuntimeException;
}
