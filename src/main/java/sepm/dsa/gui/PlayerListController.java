package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Player;
import sepm.dsa.service.PlayerService;
import sepm.dsa.service.SaveCancelService;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerListController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(PlayerListController.class);
    SpringFxmlLoader loader;

    private PlayerService playerService;
    private SaveCancelService saveCancelService;

    @FXML
    private TableView<Player> playerTable;
    @FXML
    private TableColumn currencyColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        // init table
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    @Override
    public void reload() {
        log.debug("reload PlayerListController");

	    playerTable.getItems().setAll(playerService.getAll());

        checkFocus();
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateClicked - open Player Window");

	    Stage stage = new Stage();
	    Parent scene = (Parent) loader.load("/gui/editplayer.fxml", stage);
	    EditPlayerController ctrl = loader.getController();
	    ctrl.setPlayer(null);
	    ctrl.reload();

	    stage.setTitle("Spieler bearbeiten");
	    stage.setScene(new Scene(scene, 850, 414));
	    stage.setResizable(false);
        stage.setResizable(false);
	    stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onWarenClicked - open Player Window");

	    Stage stage = new Stage();
	    Parent scene = (Parent) loader.load("/gui/editplayer.fxml", stage);
	    EditPlayerController ctrl = loader.getController();
	    ctrl.setPlayer(playerTable.getSelectionModel().getSelectedItem());
	    ctrl.reload();

	    stage.setTitle("Spieler bearbeiten");
	    stage.setScene(new Scene(scene, 850, 414));
	    stage.setResizable(false);
        stage.setResizable(false);
	    stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Player");
        Player selectedPlayer = (playerTable.getSelectionModel().getSelectedItem());

        if (selectedPlayer != null) {
            log.debug("open Confirm-Delete-Player Dialog");
            org.controlsfx.control.action.Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie die Spieler '" + selectedPlayer.getName() + "' wirklich endgültig löschen?")
                    .actions(Dialog.Actions.NO, Dialog.Actions.YES)
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                playerService.remove(selectedPlayer);
                saveCancelService.save();
                playerTable.getItems().remove(selectedPlayer);
            }
        }
        checkFocus();
    }

    @FXML
    private void checkFocus() {
        Player selectedPlayer = playerTable.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();
        if (selectedPlayer == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }
    }

    @FXML
    private void onClosePressed() {
        Stage stage = (Stage) playerTable.getScene().getWindow();
        stage.close();
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
