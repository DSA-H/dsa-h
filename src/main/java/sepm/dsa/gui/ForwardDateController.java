package sepm.dsa.gui;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.DSADate;
import sepm.dsa.service.SaveCancelService;
import sepm.dsa.service.TimeService;

import java.net.URL;
import java.util.ResourceBundle;

public class ForwardDateController extends BaseControllerImpl {
    private static final Logger log = LoggerFactory.getLogger(ForwardDateController.class);
    private SpringFxmlLoader loader;

    private TimeService timeService;
    private SaveCancelService saveCancelService;

    @FXML
    private TextField day;
    @FXML
    private Label actDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	    super.initialize(location, resources);
    }

    @Override
    public void reload() {
        DSADate date = timeService.getCurrentDate();

        actDate.setText(date.toString());
    }

    @FXML
    public void cancelClicked() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) day.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void saveClicked() {
        log.debug("SaveButtonPressed");

        final int d;
        try {
             d = Integer.parseInt(day.getText());
        }catch(NumberFormatException ex) {
            throw new DSAValidationException("Tag muss eine ganze positive Zahl sein!");
        }
        if(d < 1) {
            throw new DSAValidationException("Das Datum muss mindestens einen Tag nach vorne gestellt werden!");
        }

        timeService.resetProgress();
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws InterruptedException {
                        do {
                            updateProgress(timeService.getForwardProgress(), timeService.getForwardMaxProgress());
                            updateMessage(timeService.getForwardMessage());
                            Thread.sleep(100);
                        } while(timeService.getForwardProgress() != timeService.getForwardMaxProgress());
                        return null;
                    }
                };
            }
        };

        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            saveCancelService.save();
            Stage stage = (Stage) day.getScene().getWindow();
            stage.close();
            }
        });

        service.setOnFailed((t) -> {
            saveCancelService.save();
            throw new DSAValidationException("Fehler! Zeit vorwärtsstellen konnte nicht abgeschlossen werden!");
        });

        Dialogs.create()
                .title("Fortschritt")
                .masthead("Zeit vorwärts stellen ...")
                .showWorkerProgress(service);
        service.start();

        new Thread() {
            public void run() {
                timeService.forwardTime(d);
            }
        }.start();
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
