package sepm.dsa.application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.gui.MainMenuController;
import sepm.dsa.sepm.dsa.util.ValidationMessageUtil;

import javax.validation.ConstraintViolation;

public class Main extends Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {


        Application.launch(Main.class, (java.lang.String[]) null);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {

            log.info("Uncaught error (details): ", throwable);    // exception trace at info log level

            String causeString = "";
            Throwable cause = throwable;
            while (cause.getCause() != null) {
                cause = cause.getCause();
                causeString += "\n\tCaused by: " + cause.getClass() + (cause.getMessage() == null ? "" : ": " + cause.getMessage());
            }
            log.error("Uncaught error: " + throwable.getClass() + (throwable.getMessage() == null ? "" : ": " + throwable.getMessage() + causeString));

            if (cause instanceof DSAValidationException) {
                // show detailed message dialog without error code, listing all constraintViolations
                DSAValidationException ex = (DSAValidationException) cause;
                System.out.println(ex.getMessage());        // TODO view this in (modal?) dialog
                for (ConstraintViolation violation : ex.getConstraintViolations()) {
                    System.out.println(" -> " + ValidationMessageUtil.errorMsg(violation));
                }
            } else if (cause instanceof DSARuntimeException) {
                // show message with error code in dialog (message is "internal error" by default DSARuntimeException
                DSARuntimeException ex = (DSARuntimeException) cause;
                System.out.println(ex.getErrorCode() + ": " + ex.getMessage()); // TODO view this in (modal?) dialog
            } else {
                // show "internal error" message dialog
                System.out.println(DSARuntimeException.INTERNAL_ERROR_MSG); // TODO view this in dialog
            }
            // show error msg dialog
//                if (e instanceof )
        });

        final SpringFxmlLoader loader = new SpringFxmlLoader();
        Parent root = (Parent) loader.load("/gui/mainmenu.fxml");
        final MainMenuController ctrl = loader.getController();
        primaryStage.setTitle("DSA-Händlertool");
        primaryStage.setScene(new Scene(root, 600, 400));
        // close all windows if mainmenu is closed
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (!ctrl.exitProgramm()) {
                    event.consume();
                }
            }
        });
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
