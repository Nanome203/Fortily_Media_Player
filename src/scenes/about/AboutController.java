package scenes.about;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class AboutController implements Initializable {
  @FXML
  private BorderPane mainAboutContainer;

  public void initialize(URL arg0, ResourceBundle arg1) {
    if (mainAboutContainer == null) {
      return;
    }
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("You Trial Has Ended");
    alert.setHeaderText(null);
    alert.setContentText("Your free trial has ended. Please pay 23.000VND for full access to Fortily.");
    ButtonType payButton = new ButtonType("Pay");
    ButtonType cancelButton = new ButtonType("I'll use Spotify instead");
    alert.getButtonTypes().setAll(payButton, cancelButton);
    alert.showAndWait().ifPresent(buttonType -> {
      if (buttonType == payButton) {
        return;
      } else if (buttonType == cancelButton) {
        return;
      }
    });
  }
}