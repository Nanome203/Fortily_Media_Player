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

public class HomeEmptyController implements Initializable {
    @FXML
    Circle testCircle;

    private MediaLoader mediaLoader;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Image oshiNoKoImage = new Image("/assets/images/oshi-no-ko-head-bob.gif");
        testCircle.setFill(new ImagePattern(oshiNoKoImage));
        mediaLoader = MediaLoader.getMediaLoader();
    }

    public void openMediaFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null)
            mediaLoader.playNewMediaFile(selectedFile);
    }
}
