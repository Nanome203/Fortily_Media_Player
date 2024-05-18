package scenes.videoPlayer;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ImportedMediaPlayer {
  File selectedMediaFile = new File("src\\assets\\videos\\test - Rick Astley  Never Gonna Give You Up Official Music Video_v144P.mp4");
  Media media = new Media(selectedMediaFile.toURI().toString());
  MediaPlayer mediaPlayer = new MediaPlayer(media);
  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }
  public void playMediaPlayer() {
    mediaPlayer.play();
  }
}
