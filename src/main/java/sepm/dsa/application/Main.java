package sepm.dsa.application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.gui.MainMenuController;
import sepm.dsa.util.ValidationMessageUtil;

import javax.validation.ConstraintViolation;

public class Main extends Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Application.launch(Main.class, (java.lang.String[]) null);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {


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

                StringBuilder sb = new StringBuilder();

                sb.append(ex.getMessage());
                for (ConstraintViolation violation : ex.getConstraintViolations()) {
                    sb.append("\n" + ValidationMessageUtil.errorMsg(violation));
                }

                Dialogs.create()
                        .title("Validierungs Fehler")
                        .masthead(null)
                        .message(sb.toString())
                        .showWarning();

            } else if (cause instanceof DSARuntimeException) {
                // show message with error code in dialog (message is "internal error" by default DSARuntimeException
                log.info("Uncaught error (details): ", throwable);    // exception trace at info log level

                DSARuntimeException ex = (DSARuntimeException) cause;

                Dialogs.create()
                        .title("Interner Fehler")
                        .masthead(null)
                        .message(ex.getErrorCode() + ": " + ex.getMessage())
                        .showError();
            } else {
                // show "internal error" message dialog
                log.info("Uncaught error (details): ", throwable);    // exception trace at info log level

                Dialogs.create()
                        .title("Interner Fehler")
                        .masthead(null)
                        .message(DSARuntimeException.INTERNAL_ERROR_MSG)
                        .showError();
            }
            // show error msg dialog
//                if (e instanceof )
        });

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        final SpringFxmlLoader loader = (SpringFxmlLoader) context.getBean("loader");
//	    Parent root = (Parent) loader.load("/gui/mainmenu.fxml");
        Parent root = (Parent) loader.load("/gui/mainmenuResizable.fxml");
        final MainMenuController ctrl = loader.getController();
        primaryStage.setTitle("DSA-Händlertool");
        primaryStage.setScene(new Scene(root, 1045, 600));
        // close all windows if mainmenu is closed
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                log.debug("CloseRequest - exit Programm Request");
                if (!ctrl.exitProgramm()) {
                    event.consume();
                }
            }
        });
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
