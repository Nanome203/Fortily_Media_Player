package scenes.mediaFullScreen;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaView;
import utils.MediaLoader;

public class VideoFullScreenController implements Initializable {

    @FXML
    private BorderPane musicVideoContainer;

    @FXML
    private MediaView videoContainer;

    private MediaLoader mediaLoader;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        mediaLoader = MediaLoader.getMediaLoader();
        mediaLoader.receiveVideoFullScreenController(this);
        musicVideoContainer.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                videoContainer.setFitWidth(new_val.doubleValue() * 0.9);
            }
        });
        musicVideoContainer.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                videoContainer.setFitHeight(new_val.doubleValue() * 0.9);
            }
        });
        videoContainer.setPreserveRatio(true);
    }

    public MediaView getVideoContainer() {
        return videoContainer;
    }

}
