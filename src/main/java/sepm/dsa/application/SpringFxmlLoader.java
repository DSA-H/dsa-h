package sepm.dsa.application;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import sepm.dsa.gui.BaseController;

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
     * @param stage The stage in which the scene will be loaded
     * @return Loaded resource
     */
    public Object load(String url, Stage stage) {
        Object result;
        try {
            InputStream fxmlStream = SpringFxmlLoader.class.getResourceAsStream(url);
            loader = new FXMLLoader();
            loader.setControllerFactory(clazz -> applicationContext.getBean(clazz));
            result = loader.load(fxmlStream);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
        EventHandler<WindowEvent> event = stage.getOnHidden();
        final BaseController ctrl = ((BaseController)getController());
        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                ctrl.unload();
                stage.setOnHidden(null);   // free for garbage collecting
                // event chaining
                if(event != null) {
                    event.handle(windowEvent);
                }
            }
        });
        return result;
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
