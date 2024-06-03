package scenes.home;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class HomeEmptyController implements Initializable {
    @FXML
    Circle testCircle;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Image oshiNoKoImage = new Image("/assets/images/oshi-no-ko-head-bob.gif");
        testCircle.setFill(new ImagePattern(oshiNoKoImage));
    }

}
