package assets.mediaLoader;

import javafx.util.Duration;
import scenes.layout.LayoutController;
import utils.Utils;

import java.io.File;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MediaLoader {
    private static MediaLoader INSTANCE = null;
    private Media media = null;
    private MediaPlayer mediaPlayer = null;
    private LayoutController layoutController = null;

    private MediaLoader() {
    };

    public static MediaLoader getMediaLoader() {
        if (INSTANCE == null)
            INSTANCE = new MediaLoader();
        return INSTANCE;
    }

    public void receiveLayoutController(LayoutController layoutController) {
        this.layoutController = layoutController;
    }

    public void playNewMediaFile(File selectedFile) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        media = new Media(selectedFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        if (LayoutController.isMuted) {
            mediaPlayer.setMute(LayoutController.isMuted);
        }
        mediaPlayer.play();
        layoutController.setPauseButtonImage();
        // mediaLoader.getMediaPlayer().statusProperty().addListener((obs, oldStatus,
        // newStatus) -> {
        // if (newStatus == MediaPlayer.Status.READY) {

        // System.out.println(mediaLoader.getTotalDuration());
        // layoutController.setTotalDuration(mediaLoader.getTotalDuration());
        // }
        // });
        mediaPlayer.setOnReady(() -> {
            layoutController.setTotalDuration(mediaPlayer.getTotalDuration());
        });

        // Update the slider as the video plays
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer.getTotalDuration() != null
                    && mediaPlayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                layoutController.getProgressSlider()
                        .setValue(newValue.toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100);
                layoutController.getCurrentTimeLabel().setText(Utils.formatTime(newValue));
            }
        });
        layoutController.setSongName(selectedFile.getName().replaceFirst("[.].+$", ""));

        // Seek the video when the slider is dragged
        layoutController.getProgressSlider().valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (layoutController.getProgressSlider().isValueChanging() && mediaPlayer.getTotalDuration() != null
                        && mediaPlayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                    mediaPlayer.seek(mediaPlayer.getTotalDuration()
                            .multiply(layoutController.getProgressSlider().getValue() / 100));
                }
            }
        });
        layoutController.getProgressSlider().setOnMousePressed(e -> {
            if (!LayoutController.isPlayButton) {
                mediaPlayer.pause();
            }
        });
        layoutController.getProgressSlider().setOnMouseReleased(e -> {
            if (mediaPlayer.getTotalDuration() != null
                    && mediaPlayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                mediaPlayer.seek(Duration.millis(mediaPlayer.getTotalDuration().toMillis()
                        * layoutController.getProgressSlider().getValue() / 100.0));
                if (!LayoutController.isPlayButton) {
                    mediaPlayer.play();
                }
            }
        });
    }

    public void playCurrentMediaFile() {
        if (mediaPlayer != null)
            mediaPlayer.play();
    }

    public void pauseCurrentMediaFile() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public boolean mediaPlayerExists() {
        if (mediaPlayer != null)
            return true;
        return false;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
