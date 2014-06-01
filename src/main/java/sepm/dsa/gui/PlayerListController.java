package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class PlayerListController implements Initializable {

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
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize PlayerListController");
        // init table
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        ObservableList<Player> data = FXCollections.observableArrayList(playerService.getAll());
        playerTable.setItems(data);

        checkFocus();
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateClicked - open Player Window");

        EditPlayerController.setPlayer(null);

        Stage stage = (Stage) playerTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editplayer.fxml");

        stage.setTitle("Spieler Erstellen");
        stage.setScene(new Scene(scene, 850, 414));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onWarenClicked - open Player Window");

        EditPlayerController.setPlayer(playerTable.getFocusModel().getFocusedItem());

        Stage stage = (Stage) playerTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editplayer.fxml");

        stage.setTitle("Spieler bearbeiten");
        stage.setScene(new Scene(scene, 850, 414));
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
        Player selectedPlayer = playerTable.getFocusModel().getFocusedItem();
        if (selectedPlayer == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }
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
