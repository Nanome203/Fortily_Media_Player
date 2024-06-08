package scenes.layout;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import utils.MediaLoader;
import utils.ReusableFileChooser;
import utils.Helpers;

public class LayoutController implements Initializable {

	@FXML
	private HBox imageSongContainer;

	@FXML
	private BorderPane mainContainer;

	@FXML
	private Button sideBarFav, sideBarHome, sideBarPlaylist, sideBarMusicLib, sideBarVideoLib, sideBarRecentMedia,
			settings, playPauseBtn, volumeBtn, back10s, skip10s, nextButton, prevButton;

	@FXML
	private VBox sidebarNavigator, sideBarContainer;

	@FXML
	private Slider progressSlider, volumeSlider;

	@FXML
	private Label volumeLabel, currentTimeLabel, mediaDurationLabel, songName;

	@FXML
	private ImageView playPauseBtnImgView, volumeBtnImgView, playlistBtnImgView, favoriteBtnImgView, songImageView;

	@FXML
	private Tooltip playPauseTooltip;

	public static boolean isPlayButton = true, isMuted = false, isInPlaylist = false, isFavorite = false,
			isFullScreen = false, isAudioFile = false, isVideoFile = false;

	private static double prevVolume = 100, volume = 100;

	private Parent homeScene, musicLibScene, videoLibScene, recentMediaScene, videoFullScreenScene,
			musicFullScreenScene, prevScene;

	private MediaLoader mediaLoader;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			homeScene = FXMLLoader.load(getClass().getResource("/scenes/home/HomeEmpty.fxml"));
			recentMediaScene = FXMLLoader.load(getClass().getResource("/scenes/recentMedia/RecentMedia.fxml"));
			musicLibScene = FXMLLoader.load(getClass().getResource("/scenes/musicLibrary/MusicLibrary.fxml"));
			videoLibScene = FXMLLoader.load(getClass().getResource("/scenes/videoLibrary/VideoLibrary.fxml"));
			videoFullScreenScene = FXMLLoader
					.load(getClass().getResource("/scenes/mediaFullScreen/VideoFullScreen.fxml"));
			musicFullScreenScene = FXMLLoader
					.load(getClass().getResource("/scenes/mediaFullScreen/MusicFullScreen.fxml"));
			mainContainer.setCenter(homeScene);
			prevScene = homeScene;
		} catch (IOException e) {
			e.printStackTrace();
		}
		imageSongContainer.setOnMouseClicked(event -> {
			if (!isFullScreen && isAudioFile) {
				mainContainer.setCenter(musicFullScreenScene);
				isFullScreen = true;
			}

			else if (!isFullScreen && isVideoFile) {
				mainContainer.setCenter(videoFullScreenScene);
				isFullScreen = true;
			} else {
				mainContainer.setCenter(prevScene);
				isFullScreen = false;
			}
		});
		mediaLoader = MediaLoader.getMediaLoader();
		mediaLoader.receiveLayoutController(this);

		// adding border radius to song's image view
		Rectangle clip = new Rectangle(songImageView.getFitWidth(), songImageView.getFitHeight());
		clip.setArcWidth(30);
		clip.setArcHeight(30);
		songImageView.setClip(clip);
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.TRANSPARENT);
		WritableImage image = songImageView.snapshot(parameters, null);
		songImageView.setClip(null);
		songImageView.setImage(image);

		// adding color to progress slider's left trackpane
		progressSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				StackPane trackPane = (StackPane) progressSlider.lookup(".track");
				String style = String.format(
						"-fx-background-color: linear-gradient(to right, #2880E8 %.5f%%, white %.5f%%);",
						new_val.doubleValue(), new_val.doubleValue());
				trackPane.setStyle(style);
			}
		});

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				prevVolume = old_val.doubleValue();
				volume = new_val.doubleValue();
				StackPane trackPane = (StackPane) volumeSlider.lookup(".track");
				String style = String.format(
						"-fx-background-color: linear-gradient(to right, #2880E8 %.5f%%, white %.5f%%);",
						volume, volume);
				trackPane.setStyle(style);
				if (volumeSlider.getValue() == 0.0) {
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
				if (mediaLoader.getMediaPlayer() != null) {
					mediaLoader.getMediaPlayer().setVolume(volume / 100);
					mediaLoader.getMediaPlayer().setMute(isMuted);
				}

				// Change label
				volumeLabel.setText(String.format("%d", (int) volume));
			}
		});
		ReusableFileChooser fileChooser = ReusableFileChooser.getFileChooser();
		fileChooser.addSupportedExtensions();

	}

	public void selectItem(ActionEvent event) {
		for (Node node : sidebarNavigator.getChildren()) {
			((Button) node).getStyleClass().remove("active");
		}

		settings.getStyleClass().remove("active");

		// Add 'selected' class to the clicked item
		((Button) event.getSource()).getStyleClass().add("active");
		if (event.getSource() == sideBarHome) {
			mainContainer.setCenter(homeScene);
			prevScene = homeScene;
		}
		if (event.getSource() == sideBarMusicLib) {
			mainContainer.setCenter(musicLibScene);
			prevScene = musicLibScene;
		}
		if (event.getSource() == sideBarRecentMedia) {
			mainContainer.setCenter(recentMediaScene);
			prevScene = recentMediaScene;
		}
		if (event.getSource() == sideBarVideoLib) {
			mainContainer.setCenter(videoLibScene);
			prevScene = videoLibScene;
		}
	}

	public void handlePlayPauseBtn(ActionEvent event) {
		if (isPlayButton && mediaLoader.mediaPlayerExists()) {
			setPauseButtonImage();
			mediaLoader.playCurrentMediaFile();
		} else if (!isPlayButton && mediaLoader.mediaPlayerExists()) {
			setPlayButtonImage();
			mediaLoader.pauseCurrentMediaFile();
		}
	}

	public void setPauseButtonImage() {
		File file = new File("src/assets/images/icons8-pause-button-100.png");
		Image image = new Image(file.toURI().toString());
		playPauseTooltip.setText("Pause");
		playPauseBtnImgView.setImage(image);
		isPlayButton = false;
	}

	public void setPlayButtonImage() {
		File file = new File("src/assets/images/icons8-play-button-100.png");
		Image image = new Image(file.toURI().toString());
		playPauseTooltip.setText("Play");
		playPauseBtnImgView.setImage(image);
		isPlayButton = true;
	}

	public void setSongName(String name) {
		songName.setText(name);
	}

	public void setTotalDuration(Duration duration) {
		mediaDurationLabel.setText(Helpers.formatTime(duration));
	}

	public void setVideoFullScreenScene() {
		isFullScreen = true;
		mainContainer.setCenter(videoFullScreenScene);
	}

	public void setMusicFullScreenScene() {
		isFullScreen = true;
		mainContainer.setCenter(musicFullScreenScene);
	}

	public Slider getProgressSlider() {
		return progressSlider;
	}

	public Label getCurrentTimeLabel() {
		return currentTimeLabel;
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
			prevVolume = volume != 0.0 ? volume : prevVolume;
			volumeSlider.setValue(0.0);
			isMuted = true;
		}
	}

	public void handlePlaylistBtn(ActionEvent event) {
		if (isInPlaylist) {
			File file = new File("src/assets/images/icons8-playlist-100.png");
			Image image = new Image(file.toURI().toString());
			playlistBtnImgView.setImage(image);
			isInPlaylist = false;
		} else {
			File file = new File("src/assets/images/icons8-blue-playlist-100.png");
			Image image = new Image(file.toURI().toString());
			playlistBtnImgView.setImage(image);
			isInPlaylist = true;
		}
	}

	public void handleFavoriteBtn(ActionEvent event) {
		if (isFavorite) {
			File file = new File("src/assets/images/icons8-heart-100.png");
			Image image = new Image(file.toURI().toString());
			favoriteBtnImgView.setImage(image);
			isFavorite = false;
		} else {
			File file = new File("src/assets/images/icons8-blue-heart-100.png");
			Image image = new Image(file.toURI().toString());
			favoriteBtnImgView.setImage(image);
			isFavorite = true;
		}
	}

	public void handleBackSkipButtons(ActionEvent event) {
		if (event.getSource() == back10s && mediaLoader.getMediaPlayer() != null) {
			if (mediaLoader.getMediaPlayer().getTotalDuration() != null
					&& mediaLoader.getMediaPlayer().getCurrentTime().lessThanOrEqualTo(Duration.seconds(10))) {
				mediaLoader.getMediaPlayer()
						.seek(Duration.seconds(0));
				return;
			}
			mediaLoader.getMediaPlayer()
					.seek(Duration.seconds(mediaLoader.getMediaPlayer().getTotalDuration().toSeconds()
							* progressSlider.getValue() / 100.0 - 10));
		} else if (event.getSource() == skip10s && mediaLoader.getMediaPlayer() != null) {
			if (mediaLoader.getMediaPlayer().getTotalDuration() != null
					&& (mediaLoader.getMediaPlayer().getTotalDuration().toSeconds()
							- mediaLoader.getMediaPlayer().getCurrentTime().toSeconds()) <= 10) {
				mediaLoader.getMediaPlayer().seek(mediaLoader.getMediaPlayer().getTotalDuration());
				setPlayButtonImage();
				return;
			}
			mediaLoader.getMediaPlayer()
					.seek(Duration.seconds(mediaLoader.getMediaPlayer().getTotalDuration().toSeconds()
							* progressSlider.getValue() / 100.0 + 10));
		}
	}

	public void handleNextPrevButtons(ActionEvent event) {
		if (event.getSource() == nextButton
				&& mediaLoader.getReceivedListSize() >= 2
				&& mediaLoader.getCurrentMediaIndex() < mediaLoader.getReceivedListSize() - 1) {
			mediaLoader.setCurrentMediaIndex(mediaLoader.getCurrentMediaIndex() + 1);
			int currentMediaIndex = mediaLoader.getCurrentMediaIndex();
			ArrayList<File> MediaFiles = mediaLoader.getReceivedList();
			mediaLoader.playNewMediaFile(MediaFiles.get(currentMediaIndex));
		} else if (event.getSource() == prevButton
				&& mediaLoader.getReceivedListSize() >= 2
				&& mediaLoader.getCurrentMediaIndex() > 0) {
			mediaLoader.setCurrentMediaIndex(mediaLoader.getCurrentMediaIndex() - 1);
			int currentMediaIndex = mediaLoader.getCurrentMediaIndex();
			ArrayList<File> MediaFiles = mediaLoader.getReceivedList();
			mediaLoader.playNewMediaFile(MediaFiles.get(currentMediaIndex));
		}
	}

	public void handleStopButton(ActionEvent event) {
		if (mediaLoader.getMediaPlayer() != null) {
			mediaLoader.getMediaPlayer().seek(Duration.ZERO);
			setPauseButtonImage();
		}
		if (isAudioFile) {
			mediaLoader.startRotationFromLayout();
		}
	}
}
