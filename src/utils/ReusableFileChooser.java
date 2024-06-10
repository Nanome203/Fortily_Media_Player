package utils;

import java.io.File;
import java.util.List;

import javafx.stage.FileChooser;

public class ReusableFileChooser {
    private static final ReusableFileChooser INSTANCE = new ReusableFileChooser();
    private FileChooser fileChooser = new FileChooser();

    private ReusableFileChooser() {
    }

    public static ReusableFileChooser getFileChooser() {
        return INSTANCE;
    }

    // this function only runs one time when the layout is initialized
    public void addSupportedExtensions() {
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Media Files", "*.mp3", "*.wav", "*.m4a", "*.aac", "*.flac",
                        "*.ogg",
                        "*.mp4",
                        "*.flv", "*.mkv", "*.mpeg"));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aac", "*.flac",
                        "*.ogg"));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.flv", "*.mkv", "*.mpeg", "*.ogg"));
        // fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All",
        // "*"));
    }

    public File showOpenDialog() {
        return fileChooser.showOpenDialog(null);
    }

    public List<File> showOpenMultipleDialog() {
        return fileChooser.showOpenMultipleDialog(null);
    }
}
