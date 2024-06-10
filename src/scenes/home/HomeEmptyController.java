package scenes.home;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import dao.SongDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import utils.MediaLoader;
import utils.ReusableFileChooser;

public class HomeEmptyController implements Initializable {
    @FXML
    Circle testCircle;
    


    private MediaLoader mediaLoader;
    private ReusableFileChooser fileChooser;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Image oshiNoKoImage = new Image("/assets/images/oshi-no-ko-head-bob.gif");
        testCircle.setFill(new ImagePattern(oshiNoKoImage));
        mediaLoader = MediaLoader.getMediaLoader();
        fileChooser = ReusableFileChooser.getFileChooser();
    }

    public void openMediaFile(ActionEvent event) {
        List<File> selectedFileList = fileChooser.showOpenMultipleDialog();
        if (selectedFileList != null) {
            mediaLoader.receiveListOfMediaFiles(selectedFileList);
            mediaLoader.playReceivedList();
        }

    }
}
