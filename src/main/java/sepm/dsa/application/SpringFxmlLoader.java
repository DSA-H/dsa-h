package sepm.dsa.application;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;

/**
 * http://koenserneels.blogspot.co.at/2012/11/javafx-2-with-spring.html
 */
public class SpringFxmlLoader {
    private static final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

    public Object load(String url) {
        try (InputStream fxmlStream = SpringFxmlLoader.class
                .getResourceAsStream(url)) {
            System.err.println(SpringFxmlLoader.class
                    .getResourceAsStream(url));
            FXMLLoader loader = new FXMLLoader();
            loader.setControllerFactory(new Callback<Class<?>, Object>() {
                @Override
                public Object call(Class<?> clazz) {
                    return context.getBean(clazz);
                }
            });
            return loader.load(fxmlStream);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
