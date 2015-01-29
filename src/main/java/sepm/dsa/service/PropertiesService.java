package sepm.dsa.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public class PropertiesService {

	private static Properties properties;

	public static Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			Path file = Paths.get("resources/properties");
			Path directory = Paths.get("resources");
			if (!Files.exists(file)) {
				try {
					Files.createDirectory(directory);
					Files.createFile(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return properties;
	}

}
