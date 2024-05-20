package scenes.videoPlayer;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

public class ImportedMediaPlayer {
  FileChooser fileChooser;
  File selectedMediaFile;
  Media media;
  MediaPlayer mediaPlayer;

  ImportedMediaPlayer() {
    fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(new File("./src/assets/videos"));
    while (selectedMediaFile == null) {
      selectedMediaFile = fileChooser.showOpenDialog(null);
    }
    media = new Media(selectedMediaFile.toURI().toString());
    mediaPlayer = new MediaPlayer(media);
  }

  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }

  public void playMediaPlayer() {
    mediaPlayer.play();
  }
}