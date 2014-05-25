package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Player;
import sepm.dsa.service.PlayerService;

public class EditPlayerController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditPlayerController.class);
    private SpringFxmlLoader loader;
    private SessionFactory sessionFactory;

    private PlayerService playerService;
    private static Player selectedPlayer;
    private boolean isNewPlaper;

    @FXML
    private TextField nameField;
    @FXML
    private TextField valueToBaseRateField;
    @FXML
    private TextArea commentField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize EditPlayerController");

        if (selectedPlayer != null) {
            isNewPlaper = false;
            nameField.setText(selectedPlayer.getName());
            commentField.setText(selectedPlayer.getComment().toString());
        } else {
            isNewPlaper = true;
            selectedPlayer = new Player();
        }
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) nameField.getScene().getWindow();

        Parent scene = (Parent) loader.load("/gui/playerlist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        if (nameField.getText().isEmpty()) {
            throw new DSAValidationException("Bitte Namen eingeben");
        }
        String name = nameField.getText();

        selectedPlayer.setName(name);

        if (isNewPlaper) {
            playerService.add(selectedPlayer);
        } else {
            playerService.update(selectedPlayer);
        }

        // return to players-list
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/playerlist.fxml");
        stage.setScene(new Scene(scene, 600, 438));
    }


    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public static void setPlayer(Player player) {
        selectedPlayer = player;
    }
}
