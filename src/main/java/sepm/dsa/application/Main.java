package sepm.dsa.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

public class Main extends Application {

    public static void main(String[] args) {


        Application.launch(Main.class, (java.lang.String[]) null);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SpringFxmlLoader loader = new SpringFxmlLoader();
        Parent root = (Parent) loader.load("/gui/mainmenu.fxml");
        primaryStage.setTitle("DSA-Händlertool");
        primaryStage.setScene(new Scene(root, 300, 275));
        // close all windows if mainmenu is closed
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
            }
        });
        primaryStage.show();
    }
}
