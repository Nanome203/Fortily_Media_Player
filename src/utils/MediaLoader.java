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

        if (currentMediaIndex == 0) {
            layoutController.getPrevButton().setDisable(true);
            if (MediaFiles.size() == 1)
                layoutController.getNextButton().setDisable(true);
            else
                layoutController.getNextButton().setDisable(false);
        } else if (currentMediaIndex == MediaFiles.size() - 1) {
            layoutController.getNextButton().setDisable(true);
        } else {
            layoutController.getPrevButton().setDisable(false);
            layoutController.getNextButton().setDisable(false);
        }
        media = new Media(selectedFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        synchronizeWithLayout(selectedFile);

        if (LayoutController.isLooped) {
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
            });
        } else {
            setAutoPlayNextMediaOf(selectedFile);
        }
        mediaPlayer.setRate(LayoutController.speed);
        changeDiskRotateRate();
        mediaPlayer.play();
    }

    private void setAutoPlayNextMediaOf(File selectedFile) {
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
                mediaPlayer.stop();
                layoutController.setPlayButtonImage();
                if (Helpers.isAudioFile(selectedFile)) {
                    mfsController.toStartPosition();
                }
            }
        });
    }

    private void synchronizeWithLayout(File selectedFile) {
        if (LayoutController.isMuted) {
            mediaPlayer.setMute(LayoutController.isMuted);
        }
        layoutController.setPauseButtonImage();
        mediaPlayer.setOnReady(() -> {
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
            layoutController.setTotalDuration(mediaPlayer.getTotalDuration());
            layoutController.getCurrentTimeLabel().setText(Helpers.formatTime(mediaPlayer.getCurrentTime()));
            layoutController.getProgressSlider().setValue(0);
            layoutController.setSongName(selectedFile.getName().replaceFirst("[.].+$", ""));
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

    public void setLoop() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnEndOfMedia(null);
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
            });

        }
    }

    public void setNoLoop() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnEndOfMedia(null);
            setAutoPlayNextMediaOf(MediaFiles.get(currentMediaIndex));
        }
    }

    public void changeDiskRotateRate() {
        if (LayoutController.speed == 0.25)
            mfsController.changeRotationDuration(20000);
        else if (LayoutController.speed == 0.5)
            mfsController.changeRotationDuration(15000);
        else if (LayoutController.speed == 0.75)
            mfsController.changeRotationDuration(10000);
        else if (LayoutController.speed == 1)
            mfsController.changeRotationDuration(5000);
        else if (LayoutController.speed == 1.25)
            mfsController.changeRotationDuration(2500);
        else if (LayoutController.speed == 1.5)
            mfsController.changeRotationDuration(1000);
        else if (LayoutController.speed == 1.75)
            mfsController.changeRotationDuration(500);
        else
            mfsController.changeRotationDuration(100);
    }

    // only call this function when you remove a song from a list/database
    public void removeDeletedMediaFileFromLayout() {
        if (mediaPlayer != null) {
            mediaPlayer = null;
        }
        layoutController.getCurrentTimeLabel().setText("00:00:00");
        layoutController.getTotalDuration().setText("00:00:00");
        layoutController.getProgressSlider().setValue(0);
        layoutController.setSongName("SONG NAME");
        layoutController.setPlayButtonImage();
    }
}
