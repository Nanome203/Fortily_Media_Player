package assets.mediaLoader;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MediaLoader {
    private static MediaLoader INSTANCE = null;
    private Media media = null;
    private MediaPlayer mediaPlayer = null;

    private MediaLoader() {
    };

    public static MediaLoader getMediaLoader() {
        if (INSTANCE == null)
            INSTANCE = new MediaLoader();
        return INSTANCE;
    }

    public void playNewMediaFile(String path) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        media = new Media(path);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
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
}
