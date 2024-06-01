package scenes.layout;

import java.io.File;
import java.io.IOException;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import scenes.MusicLibrary.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import utils.Utils;

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

	private static boolean isPlayButton = true,
			isMuted = false;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			Parent homeScene = FXMLLoader.load(getClass().getResource("/scenes/spinningDisk/SpinningDisk.fxml"));
			mainContainer.setCenter(homeScene);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	}

	public void handlePlayPauseBtn(ActionEvent event) {
		if (isPlayButton) {
			File file = new File("src/assets/images/icons8-pause-button-100.png");
			Image image = new Image(file.toURI().toString());
			playPauseBtnImgView.setImage(image);
			isPlayButton = false;
		} else {
			File file = new File("src/assets/images/icons8-play-button-100.png");
			Image image = new Image(file.toURI().toString());
			playPauseBtnImgView.setImage(image);
			isPlayButton = true;
		}
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

	@FXML
	void openMusicLibrary(MouseEvent event) throws IOException {
		Parent musicLibrary = FXMLLoader.load(getClass().getResource("/scenes/MusicLibrary/MusicLibrary.fxml"));
		Scene scene = new Scene(musicLibrary);
		Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		primaryStage.setScene(scene);
		primaryStage.setTitle("Music Library");
		primaryStage.show();
	}
}
