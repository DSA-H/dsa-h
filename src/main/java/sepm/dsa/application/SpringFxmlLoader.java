package sepm.dsa.application;

import javafx.fxml.FXMLLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loader for FXML files capable of Spring
 */
public class SpringFxmlLoader implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	private FXMLLoader loader;

	/**
	 * Loads a FXML file
	 * @param url Resource URL
	 * @return Loaded resource
	 */
	public Object load(String url) {
		try {
			InputStream fxmlStream = SpringFxmlLoader.class.getResourceAsStream(url);
			loader = new FXMLLoader();
			loader.setControllerFactory(clazz -> applicationContext.getBean(clazz));
			return loader.load(fxmlStream);
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	/**
	 * Returns the Controller of the last loaded Object
	 * @param <T>
	 * @return
	 */
	public <T> T getController() {
		return (loader != null) ? loader.getController() : null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
