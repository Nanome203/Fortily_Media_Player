package scenes.layout;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.util.Duration;

import java.util.ArrayList;
import scenes.favorite.FavoriteController;
import scenes.mediaFullScreen.MusicFullScreenController;

import java.util.ResourceBundle;

import dao.FavoriteDAO;
import dao.SongDAO;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
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
			settings, playPauseBtn, volumeBtn, back10s, skip10s, nextButton, prevButton, loopButton;

	@FXML
	private VBox sidebarNavigator, sideBarContainer;

	@FXML
	private Slider progressSlider, volumeSlider;

	@FXML
	private Label volumeLabel, currentTimeLabel, mediaDurationLabel, songName;

	@FXML
	private ImageView playPauseBtnImgView, volumeBtnImgView, playlistBtnImgView, favoriteBtnImgView, songImageView,
			loopBtnImgView;

	@FXML
	private Tooltip playPauseTooltip, loopTooltip;

	@FXML
	private ComboBox<String> speedBox;

	@FXML
	private TextField searchBox;

	private MediaView smallMediaView;

	public static boolean isPlayButton = true, isMuted = false, isInPlaylist = false, isFavorite = false,
			isFullScreen = false, isAudioFile = false, isVideoFile = false, isLooped = false;

	public static double prevVolume = 100, volume = 100;
	public static double speed = 1;

	private Parent homeScene, musicLibScene, videoLibScene, recentMediaScene, videoFullScreenScene,
			musicFullScreenScene, prevScene, favoriteScene;

	private MediaLoader mediaLoader;
	private FavoriteController favoriteController;
	private FavoriteDAO favoriteDAO = new FavoriteDAO();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			// create tables if not exists
			String favoriteTable = "CREATE TABLE IF NOT EXISTS Favorite(Media TEXT PRIMARY KEY);";
			String recentMediaTable = "CREATE TABLE IF NOT EXISTS Recent_Media (PathMedia TEXT primary key, LastDateOpened TEXT);";
			Connection connection = SongDAO.getConnection();
			Statement statement = connection.createStatement();
			statement.execute(favoriteTable);
			statement.execute(recentMediaTable);

			// load media screens
			homeScene = FXMLLoader.load(getClass().getResource("/scenes/home/HomeEmpty.fxml"));
			recentMediaScene = FXMLLoader.load(getClass().getResource("/scenes/recentMedia/RecentMedia.fxml"));
			musicLibScene = FXMLLoader.load(getClass().getResource("/scenes/musicLibrary/MusicLibrary.fxml"));
			videoLibScene = FXMLLoader.load(getClass().getResource("/scenes/videoLibrary/VideoLibrary.fxml"));
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/favorite/Favorite.fxml"));
			favoriteScene = loader.load();
			favoriteController = loader.getController();
			videoFullScreenScene = FXMLLoader
					.load(getClass().getResource("/scenes/mediaFullScreen/VideoFullScreen.fxml"));
			loader = null;
			loader = new FXMLLoader(getClass().getResource("/scenes/mediaFullScreen/MusicFullScreen.fxml"));
			musicFullScreenScene = loader.load();
			loader.<MusicFullScreenController>getController().receiveLayoutController(this);
			mainContainer.setCenter(homeScene);
			prevScene = homeScene;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		smallMediaView = new MediaView();
		smallMediaView.setFitWidth(140);
		smallMediaView.setFitHeight(70);
		imageSongContainer.setOnMouseClicked(event -> {
			if (!isFullScreen && isAudioFile) {
				mainContainer.setCenter(musicFullScreenScene);
				isFullScreen = true;
				replaceMediaViewWithImageView();
			}

			else if (!isFullScreen && isVideoFile) {
				setVideoFullScreenScene();
			} else {
				switchToSmallMediaView();
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

		// adding color to speedBox prompt text

		speedBox.getItems().addAll("0.25", "0.5", "0.75", "1", "1.25", "1.5", "1.75", "2");
		speedBox.getSelectionModel().select("1");
		speedBox.setButtonCell(new ListCell<String>() {

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					// styled like -fx-prompt-text-fill:
					setStyle("-fx-text-fill: white;" +
							" -fx-alignment: center;" +
							" -fx-border-color: #2880E8;" +
							" -fx-border-width: 0 2 0 0;");
				} else {
					setStyle("-fx-text-fill: white;" +
							" -fx-alignment: center;" +
							" -fx-border-color: #2880E8;" +
							" -fx-border-width: 0 2 0 0;");
					setText(item.toString());
				}
			}

		});

		// adding color to progress slider's left trackpane
		progressSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				if (mediaLoader.mediaPlayerExists()) {
					StackPane trackPane = (StackPane) progressSlider.lookup(".track");
					String newValString = String.valueOf((int) (new_val.doubleValue() * 100000) / 100000.0);
					String style = "-fx-background-color: linear-gradient(to right, #2880E8 " + newValString
							+ "%, white "
							+ newValString + "%);";
					trackPane.setStyle(style);
				} else {
					progressSlider.setValue(0);
				}
			}
		});

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				prevVolume = old_val.doubleValue();
				volume = new_val.doubleValue();
				String volumeString = String.valueOf((int) (volume * 100000) / 100000.0);
				StackPane trackPane = (StackPane) volumeSlider.lookup(".track");
				String style = "-fx-background-color: linear-gradient(to right, #2880E8 " + volumeString
						+ "%, white " + volumeString
						+ "%);";
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
			prevScene = homeScene;
			isFullScreen = false;
			switchToSmallMediaView();
		}
		if (event.getSource() == sideBarMusicLib) {
			prevScene = musicLibScene;
			isFullScreen = false;
			switchToSmallMediaView();
		}
		if (event.getSource() == sideBarRecentMedia) {
			prevScene = recentMediaScene;
			isFullScreen = false;
			switchToSmallMediaView();
		}
		if (event.getSource() == sideBarVideoLib) {
			prevScene = videoLibScene;
			isFullScreen = false;
			switchToSmallMediaView();
		}
		if (event.getSource() == sideBarFav) {
			prevScene = favoriteScene;
			isFullScreen = false;
			switchToSmallMediaView();
		}
	}

	public void handlePlayPauseBtn(ActionEvent event) {
		if (isPlayButton && mediaLoader.mediaPlayerExists()) {
			setPauseButtonImage();
			mediaLoader.playCurrentMediaFile();
			mediaLoader.changeDiskRotateRate();
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

	private void switchToSmallMediaView() {
		if (mediaLoader.getMediaPlayer() != null && mediaLoader.isVideoFile()) {
			smallMediaView.setVisible(true);
			mediaLoader.layoutControllerSetVideo();
			imageSongContainer.getChildren().set(0, smallMediaView);
		} else if (mediaLoader.getMediaPlayer() != null && !mediaLoader.isVideoFile()) {
			replaceMediaViewWithImageView();
		}
		mainContainer.setCenter(prevScene);
		isFullScreen = false;
	}

	public void setVideoFullScreenScene() {
		isFullScreen = true;
		mediaLoader.vfsControllerSetVideo();
		replaceMediaViewWithImageView();
		mainContainer.setCenter(videoFullScreenScene);
	}

	public void setMusicFullScreenScene() {
		isFullScreen = true;
		mainContainer.setCenter(musicFullScreenScene);
		mediaLoader.vfsControllerRemoveVideo();
		mediaLoader.layoutControllerRemoveVideo();
		replaceMediaViewWithImageView();
	}

	public Label getTotalDuration() {
		return mediaDurationLabel;
	}

	public Slider getProgressSlider() {
		return progressSlider;
	}

	public Label getCurrentTimeLabel() {
		return currentTimeLabel;
	}

	public Button getPrevButton() {
		return prevButton;
	}

	public Button getNextButton() {
		return nextButton;
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

	public MediaView getSmallMediaView() {
		return smallMediaView;
	}

	// public void handlePlaylistBtn(ActionEvent event) {
	// if (isInPlaylist) {
	// File file = new File("src/assets/images/icons8-playlist-100.png");
	// Image image = new Image(file.toURI().toString());
	// playlistBtnImgView.setImage(image);
	// isInPlaylist = false;
	// } else {
	// File file = new File("src/assets/images/icons8-blue-playlist-100.png");
	// Image image = new Image(file.toURI().toString());
	// playlistBtnImgView.setImage(image);
	// isInPlaylist = true;
	// }
	// }

	public void handleFavoriteBtn(ActionEvent event) throws SQLException {
		if (isFavorite) {
			favoriteDAO.deleteMedia(
					mediaLoader.getReceivedList().get(mediaLoader.getCurrentMediaIndex()).getAbsolutePath());
			favoriteController.clearFileFavButton(
					mediaLoader.getReceivedList().get(mediaLoader.getCurrentMediaIndex()).getAbsolutePath());
			setWhiteFavoriteHeartImage();
		} else {
			favoriteDAO.insertMedia(mediaLoader.getReceivedList().get(mediaLoader.getCurrentMediaIndex()));
			setBlueFavoriteHeartImage();
		}
		favoriteController.updateAllTable();
	}

	public void setBlueFavoriteHeartImage() {
		File file = new File("src/assets/images/icons8-blue-heart-100.png");
		Image image = new Image(file.toURI().toString());
		favoriteBtnImgView.setImage(image);
		isFavorite = true;
	}

	public void setWhiteFavoriteHeartImage() {
		File file = new File("src/assets/images/icons8-heart-100.png");
		Image image = new Image(file.toURI().toString());
		favoriteBtnImgView.setImage(image);
		isFavorite = false;
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

	public void handleNextPrevButtons(ActionEvent event) throws SQLException {
		if (event.getSource() == nextButton
				&& mediaLoader.getReceivedListSize() >= 2
				&& mediaLoader.getCurrentMediaIndex() < mediaLoader.getReceivedListSize() - 1) {
			mediaLoader.setCurrentMediaIndex(mediaLoader.getCurrentMediaIndex() + 1);
			int currentMediaIndex = mediaLoader.getCurrentMediaIndex();
			ArrayList<File> MediaFiles = mediaLoader.getReceivedList();
			mediaLoader.playNewMediaFile(MediaFiles.get(currentMediaIndex));
			if (!isFullScreen && !isAudioFile) {
				mediaLoader.layoutControllerSetVideo();
			}
		} else if (event.getSource() == prevButton
				&& mediaLoader.getReceivedListSize() >= 2
				&& mediaLoader.getCurrentMediaIndex() > 0) {
			mediaLoader.setCurrentMediaIndex(mediaLoader.getCurrentMediaIndex() - 1);
			int currentMediaIndex = mediaLoader.getCurrentMediaIndex();
			ArrayList<File> MediaFiles = mediaLoader.getReceivedList();
			mediaLoader.playNewMediaFile(MediaFiles.get(currentMediaIndex));
			if (!isFullScreen && !isAudioFile) {
				mediaLoader.layoutControllerSetVideo();
			}
		}
	}

	public void handleReplayButton(ActionEvent event) {
		if (mediaLoader.getMediaPlayer() != null) {
			mediaLoader.getMediaPlayer().seek(Duration.ZERO);
			if (isPlayButton) {
				mediaLoader.getMediaPlayer().play();
				setPauseButtonImage();
			}
		}
		if (isAudioFile) {
			mediaLoader.startRotationFromLayout();
		}
	}

	public void handleLoopButton(ActionEvent event) {
		if (isLooped) {
			File file = new File("src/assets/images/icons8-double-move-right-100.png");
			Image image = new Image(file.toURI().toString());
			loopBtnImgView.setImage(image);
			loopTooltip.setText("Loop: OFF");
			mediaLoader.setNoLoop();
			isLooped = false;
		} else {
			File file = new File("src/assets/images/icons8-loop-100.png");
			Image image = new Image(file.toURI().toString());
			loopBtnImgView.setImage(image);
			loopTooltip.setText("Loop: ON");
			mediaLoader.setLoop();
			isLooped = true;
		}
	}

	public void handleChangeSpeedButton(ActionEvent event) {
		speed = Double.parseDouble(speedBox.getValue());
		if (!isPlayButton)
			mediaLoader.changeDiskRotateRate();
		if (mediaLoader.getMediaPlayer() != null)
			mediaLoader.getMediaPlayer().setRate(speed);

	}

	public void replaceMediaViewWithImageView() {
		imageSongContainer.getChildren().set(0, songImageView);
	}
}
