package sepm.dsa.gui;

import com.sun.javafx.stage.StageHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.*;
import org.controlsfx.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainMenuController.class);
    private SpringFxmlLoader loader;

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu dateiMenu;
    @FXML
    private MenuItem dateiImport;
    @FXML
    private MenuItem dateiExport;
    @FXML
    private MenuItem dateiExit;
    @FXML
    private Menu dateiVerwaltenMenu;
    @FXML
    private MenuItem verwaltenHaendlerKategorie;
    @FXML
    private MenuItem verwaltenGebieteGrenzen;
    @FXML
    private MenuItem verwaltenWaehrungen;
    @FXML
    private MenuItem verwaltenWaren;
    @FXML
    private Menu verwaltenWeltkarte;
    @FXML
    private MenuItem weltkarteImportieren;
    @FXML
    private MenuItem weltkarteExportieren;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {

    }

    @FXML
    private void onGrenzenGebieteClicked() {
        log.debug("onGrenzenGebieteClicked - open Grenzen und Gebiete Window");
        Stage stage = new Stage();
        Parent scene = (Parent) loader.load("/gui/regionlist.fxml");

        stage.setTitle("Grenzen und Gebiete");
        stage.setScene(new Scene(scene, 600, 438));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void onExitClicked() {
        log.debug("onExitClicked - exit Programm Request");
        if (exitProgramm()) {
            Stage primaryStage = (Stage) menuBar.getScene().getWindow();
            primaryStage.close();
        }
    }

    @FXML
    private void onWeltkarteImportierenPressed() {
        log.debug("onWeltkarteImportierenPressed called");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Weltkarte auswählen");
        File newMap = fileChooser.showOpenDialog(new Stage());
        String extNew = FilenameUtils.getExtension(newMap.getAbsolutePath());

        File activeDir = new File("maps/active");
        File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("worldMap");
            }
        });
        if (matchingFiles != null && matchingFiles.length >= 1) {
            File oldMap = matchingFiles[0];
            String extOld = FilenameUtils.getExtension(oldMap.getAbsolutePath());
            try {
                FileUtils.copyFile(oldMap, new File("maps/alternative/lastWorldMap." + extOld));
                log.debug("copied old map to alternative");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        File worldMap = new File("maps/active/worldMap." + extNew);
        try {
            FileUtils.copyFile(newMap, worldMap);
            log.debug("copied new map");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onWeltkarteExportierenPressed() {
    }

    /**
     * Shows a exit-confirm-dialog if more than the primaryStage are open and close all other stages if confirmed
     *
     * @return false if the user cancle or refuse the dialog, otherwise true
     */
    public boolean exitProgramm() {
        Stage primaryStage = (Stage) menuBar.getScene().getWindow();
        List<Stage> stages = new ArrayList<Stage>(StageHelper.getStages());

        // only primaryStage
        if (stages.size() <= 1) {
            return true;
        }

        log.debug("open Dialog - Confirm-Exit-Dialog");
        Action response = Dialogs.create()
                .owner(primaryStage)
                .title("Programm beenden?")
                .masthead(null)
                .message("Wollen Sie das Händertool wirklich beenden? Nicht gespeicherte Änderungen gehen dabei verloren.")
                .showConfirm();

        if (response == Dialog.Actions.YES) {
            log.debug("Confirm-Exit-Dialog confirmed");
            for (Stage s : stages) {
                if (!s.equals(primaryStage)) {
                    s.close();
                }
            }
            return true;

        } else {
            log.debug("Confirm-Exit-Dialog refused");
            return false;
        }
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }
}
