package scenes.mediaFullScreen;

import java.net.URL;
import java.util.ResourceBundle;
import utils.MediaLoader;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class MusicFullScreenController implements Initializable {

    @FXML
    private Circle diskImage;

    private MediaLoader mediaLoader;
    private RotateTransition rotate;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Image disk = new Image("/assets/images/R.png");
        diskImage.setFill(new ImagePattern(disk));
        mediaLoader = MediaLoader.getMediaLoader();
        mediaLoader.receiveMusicFullScreenController(this);
        rotate = new RotateTransition();
        rotate.setNode(diskImage);
        rotate.setDuration(Duration.millis(5000));
        rotate.setCycleCount(TranslateTransition.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setByAngle(-360);
    }

    public void startRotation() {
        if (rotate.statusProperty().get() == Animation.Status.RUNNING) {
            toStartPosition();
        }
        rotate.play();
    }

    public void continueRotation() {
        rotate.play();
    }

    public void stopRotation() {
        rotate.stop();
    }

    public void toStartPosition() {
        rotate.stop();
        diskImage.setRotate(0);
    }

    public void changeRotationDuration(double duration) {
        rotate.stop();
        rotate.setDuration(Duration.millis(duration));
        rotate.play();
    }
}
