package scenes.layout;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import assets.mediaLoader.MediaLoader;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import scenes.home.HomeEmptyController;

public class LayoutController implements Initializable {

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
	private Button sideBarRecentMedia;

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
	private Label songName;

	@FXML
	private Button playPauseBtn, volumeBtn;

	@FXML
	private ImageView playPauseBtnImgView, volumeBtnImgView, playlistBtnImgView, favoriteBtnImgView, songImageView;

	private static boolean isPlayButton = true, isMuted = false, isInPlaylist = false, isFavorite = false;

	private Parent homeScene, musicLibScene, videoLibScene, recentMediaScene;

	private MediaLoader mediaLoader;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/home/HomeEmpty.fxml"));
			homeScene = loader.load();
			HomeEmptyController homeEmptyController = loader.getController();
			homeEmptyController.receiveParentController(this);
			recentMediaScene = FXMLLoader.load(getClass().getResource("/scenes/recentMedia/RecentMedia.fxml"));
			musicLibScene = FXMLLoader.load(getClass().getResource("/scenes/musicLibrary/MusicLibrary.fxml"));
			videoLibScene = FXMLLoader.load(getClass().getResource("/scenes/videoLibrary/VideoLibrary.fxml"));
			mainContainer.setCenter(homeScene);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mediaLoader = MediaLoader.getMediaLoader();

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
						new_val.floatValue(), new_val.floatValue());
				trackPane.setStyle(style);
			}
		});

		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				StackPane trackPane = (StackPane) volumeSlider.lookup(".track");
				String style = String.format(
						"-fx-background-color: linear-gradient(to right, #2880E8 %d%%, white %d%%);",
						new_val.intValue(), new_val.intValue());
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
		if (event.getSource() == sideBarHome) {
			mainContainer.setCenter(homeScene);
		}
		if (event.getSource() == sideBarMusicLib) {
			mainContainer.setCenter(musicLibScene);
		}
		if (event.getSource() == sideBarRecentMedia) {
			mainContainer.setCenter(recentMediaScene);
		}
		if (event.getSource() == sideBarVideoLib) {
			mainContainer.setCenter(videoLibScene);
		}
	}

	public void handlePlayPauseBtn(ActionEvent event) {
		if (isPlayButton && mediaLoader.mediaPlayerExists()) {
			setPauseButtonImage();
			mediaLoader.playCurrentMediaFile();
		} else if (!isPlayButton && mediaLoader.mediaPlayerExists()) {
			File file = new File("src/assets/images/icons8-play-button-100.png");
			Image image = new Image(file.toURI().toString());
			playPauseBtnImgView.setImage(image);
			isPlayButton = true;
			mediaLoader.pauseCurrentMediaFile();
		}
	}

	public void setPauseButtonImage() {
		File file = new File("src/assets/images/icons8-pause-button-100.png");
		Image image = new Image(file.toURI().toString());
		playPauseBtnImgView.setImage(image);
		isPlayButton = false;
	}

	public void setSongName(String name) {
		songName.setText(name);
	}

	public void handleVolumeBtn(ActionEvent event) {
		if (isMuted) {
			File file = new File("src/assets/images/icons8-volume-100.png");
			Image image = new Image(file.toURI().toString());
			volumeBtnImgView.setImage(image);
			volumeSlider.setValue(100.0);
			isMuted = false;
		} else {
			File file = new File("src/assets/images/icons8-mute-100.png");
			Image image = new Image(file.toURI().toString());
			volumeBtnImgView.setImage(image);
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
}
