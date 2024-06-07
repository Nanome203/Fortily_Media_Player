package utils;

import javafx.util.Duration;
import scenes.layout.LayoutController;
import scenes.mediaFullScreen.MediaFullScreenController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MediaLoader {
    private static MediaLoader INSTANCE = null;
    private Media media = null;
    private MediaPlayer mediaPlayer = null;
    private LayoutController layoutController = null;
    private MediaFullScreenController mfsController = null;

    private ArrayList<File> MediaFiles = null;
    private int currentMediaIndex;

    private MediaLoader() {
        MediaFiles = new ArrayList<File>();
    };

    public static MediaLoader getMediaLoader() {
        if (INSTANCE == null)
            INSTANCE = new MediaLoader();
        return INSTANCE;
    }

    public void receiveListOfMediaFiles(List<File> list) {
        if (!MediaFiles.isEmpty())
            MediaFiles.clear();
        for (File file : list) {
            MediaFiles.add(file);
        }
        currentMediaIndex = 0;
    }

    // This function is only called once
    public void receiveLayoutController(LayoutController layoutController) {
        this.layoutController = layoutController;
    }

    public void receiveMediaFullScreenController(MediaFullScreenController controller) {
        mfsController = controller;
    }

    // this function plays the media files in the received list
    public void playReceivedList() {
        playNewMediaFile(MediaFiles.get(currentMediaIndex));
    }

    // this function plays the media file received from the file chooser
    public void playNewMediaFile(File selectedFile) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        media = new Media(selectedFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        // test code
        if (mfsController != null)
            mfsController.getVideoContainer().setMediaPlayer(mediaPlayer);
        //
        layoutController.setPauseButtonImage();
        synchronizeWithLayout(selectedFile.getName());

        mediaPlayer.setOnEndOfMedia(() -> {
            if (currentMediaIndex < MediaFiles.size() - 1) {
                ++currentMediaIndex;
                playNewMediaFile(MediaFiles.get(currentMediaIndex));
            } else {
                currentMediaIndex = 0;
                playNewMediaFile(MediaFiles.get(currentMediaIndex));
                mediaPlayer.pause();
                layoutController.setPlayButtonImage();

            }
        });

        mediaPlayer.play();
    }

    public void synchronizeWithLayout(String name) {
        if (LayoutController.isMuted) {
            mediaPlayer.setMute(LayoutController.isMuted);
        }
        mediaPlayer.setOnReady(() -> {
            layoutController.setTotalDuration(mediaPlayer.getTotalDuration());
            layoutController.getCurrentTimeLabel().setText(Utils.formatTime(mediaPlayer.getCurrentTime()));
            layoutController.getProgressSlider().setValue(0);
            layoutController.setSongName(name.replaceFirst("[.].+$", ""));
        });

        // Update label according to slider
        layoutController.getProgressSlider().valueProperty().addListener((obs, oldValue, newValue) -> {
            layoutController.getCurrentTimeLabel()
                    .setText(Utils.formatTime(mediaPlayer.getTotalDuration().toMillis() * (double) newValue / 100.0));
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

    // this function plays the media file when user click the play button
    public void playCurrentMediaFile() {
        if (mediaPlayer != null)
            mediaPlayer.play();
    }

    // this function pause the media file when user click the pause button
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

    public int getReceivedListSize() {
        if (MediaFiles != null || !MediaFiles.isEmpty())
            return MediaFiles.size();
        return 0;
    }

    public ArrayList<File> getReceivedList() {
        if (MediaFiles != null)
            return MediaFiles;
        return null;
    }

    public int getCurrentMediaIndex() {
        return currentMediaIndex;
    }

    public void setCurrentMediaIndex(int newIndex) {
        currentMediaIndex = newIndex;
    }
}
