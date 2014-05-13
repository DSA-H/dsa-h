package sepm.dsa.application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sepm.dsa.gui.MainMenuController;

public class Main extends Application {

    public static void main(String[] args) {


        Application.launch(Main.class, (java.lang.String[]) null);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final SpringFxmlLoader loader = new SpringFxmlLoader();
        Parent root = (Parent) loader.load("/gui/mainmenu.fxml");
        final MainMenuController ctrl = loader.getController();
        primaryStage.setTitle("DSA-Händlertool");
        primaryStage.setScene(new Scene(root, 600, 400));
        // close all windows if mainmenu is closed
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                ctrl.closeAllOtherStages();
            }
        });
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
