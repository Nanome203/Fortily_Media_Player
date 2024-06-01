package scenes.videoPlayer;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.Utils;

public class VideoPlayerController implements Initializable {

  @FXML
  private HBox imageSongContainer;

  @FXML
  private BorderPane mainContainer;

  @FXML
  private Button sideBarFav;

  @FXML
  private Button sideBarHome;

  @FXML
  private Button sideBarPlaylist;

  @FXML
  private VBox sidebarNavigator;

  @FXML
  private Button sideBarMusicLib;

  @FXML
  private Button sideBarVideoLib;

  @FXML
  private VBox sideBarContainer;

  @FXML
  private Slider progressSlider;

  @FXML
  private Slider volumeSlider;

  @FXML
  private Label volumeLabel, currentTimeLabel, mediaDurationLabel;

  @FXML
  private Button settings;

  @FXML
  private Button playPauseBtn, volumeBtn;

  @FXML
  private ImageView playPauseBtnImgView,
      volumeBtnImgView;

  @FXML
  private Button selectMainMediaButton;

  @FXML
  private StackPane mainMediaContainer;

  @FXML
  private MediaView mainMediaView;

  private static boolean isPlayButton = true,
      isMuted = false;

  private static double prevVolume = 100, volume = 100;
  private static MediaPlayer mediaPlayer;
  private static Duration totalMediaDuration;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    sideBarHome.setOnMouseClicked(event -> {
      Stage currentStage = (Stage) sideBarVideoLib.getScene().getWindow();
      try {
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/layout/Layout.fxml"));
        currentStage.setScene(new Scene(root));
        System.out.println("Switched to layout");
        if (mediaPlayer != null)
          mediaPlayer.stop();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    // start:dev
    // ImportedMediaPlayer importedMediaPlayer = new ImportedMediaPlayer();
    // mainMediaView.setMediaPlayer(importedMediaPlayer.getMediaPlayer());
    // importedMediaPlayer.playMediaPlayer();
    // end:dev

    selectMainMediaButton.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setInitialDirectory(new File("./src/assets/videos"));
      fileChooser.getExtensionFilters()
          .add(new FileChooser.ExtensionFilter("Media Files", "*.mp3", "*.wav", "*.m4a", "*.flac", "*.ogg", "*.mp4",
              "*.flv", "*.mkv", "*.mpeg"));
      fileChooser.getExtensionFilters()
          .add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.flac", "*.ogg"));
      fileChooser.getExtensionFilters()
          .add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.flv", "*.mkv", "*.mpeg", "*.ogg"));
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All", "*"));
      File selectedMediaFile = fileChooser.showOpenDialog(null);

      if (selectedMediaFile != null) {
        String selectedMediaFileExtension = Optional.ofNullable(selectedMediaFile.getName())
            .filter(f -> f.contains("."))
            .map(f -> f.substring(selectedMediaFile.getName().lastIndexOf(".") + 1)).map(Object::toString)
            .orElse("");
        boolean isAudioFile = selectedMediaFileExtension.matches("mp3|wav|m4a|flac|ogg");
        boolean isVideoFile = selectedMediaFileExtension.matches("mp4|flv|mkv|mpeg|ogg");
        if (isAudioFile || isVideoFile) {
          if (mediaPlayer != null) {
            mediaPlayer.stop();
          }
          Media media = new Media(selectedMediaFile.toURI().toString());
          mediaPlayer = new MediaPlayer(media);
          mediaPlayer.setVolume(volume / 100);
          mediaPlayer.setMute(isMuted);
          mediaPlayer.setAutoPlay(true);

          mainMediaView.setMediaPlayer(mediaPlayer);
          mainMediaView.fitWidthProperty().bind(mainMediaContainer.widthProperty());
          mainMediaView.fitHeightProperty().bind(mainMediaContainer.heightProperty());

          // Hide select button
          selectMainMediaButton.setVisible(false);

          // Change play/pause button to play
          File file = new File("src/assets/images/icons8-pause-button-100.png");
          Image image = new Image(file.toURI().toString());
          playPauseBtnImgView.setImage(image);
          isPlayButton = false;

          if (mediaPlayer.getStatus() == MediaPlayer.Status.UNKNOWN) {
            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
              if (newStatus == MediaPlayer.Status.READY) {
                totalMediaDuration = mediaPlayer.getTotalDuration();
                mediaDurationLabel.setText(Utils.formatTime(totalMediaDuration));
                System.out.println(Utils.formatTime(mediaPlayer.getTotalDuration()));
              }
            });
          } else {
            totalMediaDuration = mediaPlayer.getTotalDuration();
            mediaDurationLabel.setText(Utils.formatTime(totalMediaDuration));
            System.out.println(Utils.formatTime(mediaPlayer.getTotalDuration()));
          }

          // Seek the video when the slider is dragged
          progressSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
              if (progressSlider.isValueChanging() && totalMediaDuration != null
                  && totalMediaDuration.greaterThan(Duration.ZERO)) {
                mediaPlayer.seek(totalMediaDuration.multiply(progressSlider.getValue() / 100));
              }
            }
          });
          progressSlider.setOnMousePressed(e -> {
            if (!isPlayButton) {
              mediaPlayer.pause();
            }
          });
          progressSlider.setOnMouseReleased(e -> {
            if (totalMediaDuration != null
                && totalMediaDuration.greaterThan(Duration.ZERO)) {
              mediaPlayer.seek(Duration.millis(totalMediaDuration.toMillis() * progressSlider.getValue() / 100.0));
              if (!isPlayButton) {
                mediaPlayer.play();
              }
            }
          });

          // Update the slider as the video plays
          mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (totalMediaDuration != null
                && totalMediaDuration.greaterThan(Duration.ZERO)) {
              progressSlider.setValue(newValue.toMillis() / totalMediaDuration.toMillis() * 100);
              currentTimeLabel.setText(Utils.formatTime(newValue));
            }
          });

        }
      }
    });

    progressSlider.setMin(0);
    progressSlider.setMax(100);
    progressSlider.setValue(0);
    progressSlider.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
        StackPane trackPane = (StackPane) progressSlider.lookup(".track");
        String style = String.format("-fx-background-color: linear-gradient(to right, #2880E8 %d%%, white %d%%);",
            new_val.intValue(), new_val.intValue());
        trackPane.setStyle(style);
      }
    });

    volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
        prevVolume = old_val.doubleValue();
        volume = new_val.doubleValue();
        StackPane trackPane = (StackPane) volumeSlider.lookup(".track");
        String style = String.format("-fx-background-color: linear-gradient(to right, #2880E8 %d%%, white %d%%);",
            (int) volume, (int) volume);
        trackPane.setStyle(style);
        if (volumeSlider.getValue() == 0) {
          File file = new File("src/assets/images/icons8-mute-100.png");
          Image image = new Image(file.toURI().toString());
          volumeBtnImgView.setImage(image);
          isMuted = true;
        }

        else {
          File file = new File("src/assets/images/icons8-volume-100.png");
          Image image = new Image(file.toURI().toString());
          volumeBtnImgView.setImage(image);
          isMuted = false;
        }

        // Set volume
        if (mediaPlayer != null) {
          mediaPlayer.setVolume(volume / 100);
          mediaPlayer.setMute(isMuted);
        }

        // Change label
        volumeLabel.setText(String.format("%d", (int) volume));
      }
    });
  }

  public void selectItem(ActionEvent event) {
    for (Node node : sidebarNavigator.getChildren()) {
      ((Button) node).getStyleClass().remove("active");
    }

    settings.getStyleClass().remove("active");

    // Add 'selected' class to the clicked item
    ((Button) event.getSource()).getStyleClass().add("active");
  }

  public void handlePlayPauseBtn(ActionEvent event) {
    if (isPlayButton) {
      File file = new File("src/assets/images/icons8-pause-button-100.png");
      Image image = new Image(file.toURI().toString());
      playPauseBtnImgView.setImage(image);
      isPlayButton = false;

      // play video
      if (mediaPlayer != null) {
        mediaPlayer.play();
      }
    } else {
      File file = new File("src/assets/images/icons8-play-button-100.png");
      Image image = new Image(file.toURI().toString());
      playPauseBtnImgView.setImage(image);
      isPlayButton = true;
      if (mediaPlayer != null)
        mediaPlayer.pause();
    }
  }

  public void handleVolumeBtn(ActionEvent event) {
    if (isMuted) {
      File file = new File("src/assets/images/icons8-volume-100.png");
      Image image = new Image(file.toURI().toString());
      volumeBtnImgView.setImage(image);
      volumeSlider.setValue(prevVolume);
      isMuted = false;
    } else {
      File file = new File("src/assets/images/icons8-mute-100.png");
      Image image = new Image(file.toURI().toString());
      volumeBtnImgView.setImage(image);
      prevVolume = volume != 0 ? volume : prevVolume;
      volumeSlider.setValue(0);
      isMuted = true;
    }
  }

}
