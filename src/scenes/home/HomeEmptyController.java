package scenes.home;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import assets.mediaLoader.MediaLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import scenes.layout.LayoutController;

public class HomeEmptyController implements Initializable {
    @FXML
    Circle testCircle;

    private MediaLoader mediaLoader;

    private LayoutController layoutController;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Image oshiNoKoImage = new Image("/assets/images/oshi-no-ko-head-bob.gif");
        testCircle.setFill(new ImagePattern(oshiNoKoImage));
        mediaLoader = MediaLoader.getMediaLoader();
    }

    public void openMediaFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        mediaLoader.playNewMediaFile(selectedFile.toURI().toString());
        layoutController.setPauseButtonImage();
        layoutController.setSongName(selectedFile.getName());

    }

    public void receiveParentController(LayoutController layoutController) {
        this.layoutController = layoutController;
    }
}
