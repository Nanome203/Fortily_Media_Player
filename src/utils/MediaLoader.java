package utils;

import javafx.util.Duration;
import scenes.layout.LayoutController;
import scenes.mediaFullScreen.MusicFullScreenController;
import scenes.mediaFullScreen.VideoFullScreenController;

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
    private VideoFullScreenController vfsController = null;
    private MusicFullScreenController mfsController = null;

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

    public void receiveVideoFullScreenController(VideoFullScreenController controller) {
        vfsController = controller;
    }

    public void receiveMusicFullScreenController(MusicFullScreenController controller) {
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

        if (Helpers.isVideoFile(selectedFile)) {
            layoutController.setVideoFullScreenScene();
            vfsController.getVideoContainer().setMediaPlayer(mediaPlayer);
            LayoutController.isVideoFile = true;
            LayoutController.isAudioFile = false;
        } else if (Helpers.isAudioFile(selectedFile)) {
            layoutController.setMusicFullScreenScene();
            LayoutController.isAudioFile = true;
            LayoutController.isVideoFile = false;
            mfsController.startRotation();
        }

        layoutController.setPauseButtonImage();
        synchronizeWithLayout(selectedFile.getName());

        mediaPlayer.setOnEndOfMedia(() -> {
            if (currentMediaIndex < MediaFiles.size() - 1) {
                ++currentMediaIndex;
                playNewMediaFile(MediaFiles.get(currentMediaIndex));
                if (Helpers.isAudioFile(selectedFile)) {
                    mfsController.startRotation();
                }
            } else {
                currentMediaIndex = 0;
                playNewMediaFile(MediaFiles.get(currentMediaIndex));
                mediaPlayer.pause();
                layoutController.setPlayButtonImage();
                if (Helpers.isAudioFile(selectedFile)) {
                    mfsController.toStartPosition();
                }
            }
        });

        mediaPlayer.play();
    }

    private void synchronizeWithLayout(String name) {
        if (LayoutController.isMuted) {
            mediaPlayer.setMute(LayoutController.isMuted);
        }
        mediaPlayer.setOnReady(() -> {
            layoutController.setTotalDuration(mediaPlayer.getTotalDuration());
            layoutController.getCurrentTimeLabel().setText(Helpers.formatTime(mediaPlayer.getCurrentTime()));
            layoutController.getProgressSlider().setValue(0);
            layoutController.setSongName(name.replaceFirst("[.].+$", ""));
        });

        // Update label according to slider
        layoutController.getProgressSlider().valueProperty().addListener((obs, oldValue, newValue) -> {
            layoutController.getCurrentTimeLabel()
                    .setText(Helpers.formatTime(mediaPlayer.getTotalDuration().toMillis() * (double) newValue / 100.0));
        });

        // Update the slider as the video plays
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer.getTotalDuration() != null
                    && mediaPlayer.getTotalDuration().greaterThan(Duration.ZERO)) {
                layoutController.getProgressSlider()
                        .setValue(newValue.toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100);
                layoutController.getCurrentTimeLabel().setText(Helpers.formatTime(newValue));
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
        if (mediaPlayer != null) {
            mediaPlayer.play();
            mfsController.continueRotation();
        }
    }

    // this function pause the media file when user click the pause button
    public void pauseCurrentMediaFile() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mfsController.stopRotation();
        }
    }

    public void startRotationFromLayout() {
        if (mediaPlayer != null)
            mfsController.startRotation();
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
