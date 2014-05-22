package sepm.dsa.gui;

import com.sun.javafx.stage.StageHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.*;
import org.controlsfx.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;

import java.net.URL;
import java.util.List;

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
    @FXML
    private MenuItem location;
    @FXML
    private ImageView worldMapImageView;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        updateWorldMap();
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
    private void onTraderCategoriesClicked() {
        log.debug("onTraderCategoriesClicked - open Trader Categories Window");
        Stage stage = new Stage();
        Parent scene = (Parent) loader.load("/gui/tradercategorylist.fxml");

        stage.setTitle("Händlerkategorien");
        stage.setScene(new Scene(scene, 600, 438));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void onWarenClicked() {
        log.debug("onWarenClicked - open Waren Window");
        Stage stage = new Stage();

        Parent scene = (Parent) loader.load("/gui/productslist.fxml");

        stage.setTitle("Waren");
        stage.setScene(new Scene(scene, 600, 438));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void onShowLocationsClicked() {
        log.debug("onShowLocationsClicked - open Location Window");
        Stage stage = new Stage();
        Parent scene = (Parent) loader.load("/gui/locationlist.fxml");

        stage.setTitle("Orte verwalten");
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

        File alternativeDir = new File("maps/alternative");
        File activeDir = new File("maps/active");

        //choose new File
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Weltkarte auswählen");
        if (alternativeDir.isDirectory()) {
            if (alternativeDir.list().length > 0) {
                fileChooser.setInitialDirectory(new File("maps/alternative"));
            }
        }
        List<String> extensions = new ArrayList<String>();
        extensions.add("*.jpg");
        extensions.add("*.png");
        extensions.add("*.gif");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", extensions),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File newMap = fileChooser.showOpenDialog(new Stage());

        if (newMap == null) {
            return;
        }

        //check if old File exists
        File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("worldMap");
            }
        });

	    //copy old file to temp
        if (matchingFiles != null && matchingFiles.length >= 1) {
            File oldMap = matchingFiles[0];
            String extOld = FilenameUtils.getExtension(oldMap.getAbsolutePath());
            File temp = new File("maps/alternative/lastWorldMapTemp." + extOld);
            try {
                FileUtils.copyFile(oldMap, temp);
                log.debug("copied old map to alternative (temp)");
                oldMap.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //copy new File to dir
        String extNew = FilenameUtils.getExtension(newMap.getAbsolutePath());
        File worldMap = new File("maps/active/worldMap." + extNew);
        try {
            FileUtils.copyFile(newMap, worldMap);
            log.debug("copied new map");
        } catch (IOException e) {
            e.printStackTrace();
        }

	    //rename temp
        matchingFiles = alternativeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("lastWorldMapTemp");
            }
        });
        if (matchingFiles != null && matchingFiles.length >= 1) {
            File oldMap = matchingFiles[0];
            String extOld = FilenameUtils.getExtension(oldMap.getAbsolutePath());
            File dest =  new File("maps/alternative/ehemaligeWeltkarte." + extOld);
            int k=1;
            while(dest.exists() && !dest.isDirectory()){
                dest =  new File("maps/alternative/ehemaligeWeltkarte("+k+")." + extOld);
                k++;
            }
            try {
                FileUtils.copyFile(oldMap, dest);
                log.debug("copied temp to alternative");
                oldMap.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        updateWorldMap();
    }

    @FXML
    private void onWeltkarteExportierenPressed() {
        //choose File to export
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export-Datei auswählen");
        List<String> extensions = new ArrayList<String>();
        extensions.add("*.jpg");
        extensions.add("*.png");
        extensions.add("*.gif");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", extensions),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File exportFile = fileChooser.showSaveDialog(new Stage());

        if (exportFile == null) {
            return;
        }

        //look for world map
        File activeDir = new File("maps/active");
        File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("worldMap");
            }
        });

        if (matchingFiles != null && matchingFiles.length >= 1) {
            File worldMap = matchingFiles[0];
            String extSource = FilenameUtils.getExtension(worldMap.getAbsolutePath());
            String extTarget = FilenameUtils.getExtension(exportFile.getAbsolutePath());
            if(exportFile.exists() && !exportFile.isDirectory()){
            }
            try {
                if (extTarget == "") {
                    FileUtils.copyFile(worldMap, new File(exportFile.getAbsolutePath() + "." + extSource));
                    exportFile.delete();
                } else {
                    FileUtils.copyFile(worldMap, exportFile);
                }
                    log.debug("exported worldMap");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateWorldMap() {
        log.debug("updateWorldMap called");
        File activeDir = new File("maps/active");
        File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("worldMap");
            }
        });

        if (matchingFiles != null && matchingFiles.length >= 1) {
            File worldMap = matchingFiles[0];
            String ext = FilenameUtils.getExtension(worldMap.getAbsolutePath());
            Image image = new Image("file:"+worldMap.getAbsolutePath(), true);
            worldMapImageView.setImage(image);
            worldMapImageView.setFitHeight(550);
            worldMapImageView.setFitWidth(400);
            worldMapImageView.setSmooth(true);
            worldMapImageView.setPreserveRatio(false);
        }
    }

    /**
     * Shows a exit-confirm-dialog if more than the primaryStage are open and close all other stages if confirmed
     * @return false if the user cancle or refuse the dialog, otherwise true
     */
    public boolean exitProgramm() {
        Stage primaryStage = (Stage)menuBar.getScene().getWindow();
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
                .message("Wollen Sie das Händlertool wirklich beenden? Nicht gespeicherte Änderungen gehen dabei verloren.")
                .showConfirm();

        if (response == Dialog.Actions.YES) {
            log.debug("Confirm-Exit-Dialog confirmed");
            for (Stage s : stages) {
                if (!s.equals(primaryStage)) {
                    s.close();
                }
            }
            return true;

        }else {
            log.debug("Confirm-Exit-Dialog refused");
            return false;
        }
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }
}
