package utils;

import java.io.File;

import javafx.util.Duration;

public class Helpers {
  public static String formatTime(Duration duration) {
    int intDuration = (int) Math.floor(duration.toSeconds());
    int hours = intDuration / 3600;
    int minutes = (intDuration % 3600) / 60;
    int seconds = intDuration % 60;

    if (hours > 0) {
      return String.format("%d:%02d:%02d", hours, minutes, seconds);
    } else {
      return String.format("%02d:%02d", minutes, seconds);
    }
  }

  public static String formatTime(double duration) {
    int intDuration = (int) duration / 1000;
    int hours = intDuration / 3600;
    int minutes = (intDuration % 3600) / 60;
    int seconds = intDuration % 60;

    if (hours > 0) {
      return String.format("%d:%02d:%02d", hours, minutes, seconds);
    } else {
      return String.format("%02d:%02d", minutes, seconds);
    }
  }

  public static Duration formatTime(String duration) {
    String[] parts = duration.split(":");
    int hours = 0;
    int minutes = 0;
    int seconds = 0;

    if (parts.length == 3) {
      hours = Integer.parseInt(parts[0]);
      minutes = Integer.parseInt(parts[1]);
      seconds = Integer.parseInt(parts[2]);
    } else {
      minutes = Integer.parseInt(parts[0]);
      seconds = Integer.parseInt(parts[1]);
    }

    return Duration.seconds(hours * 3600 + minutes * 60 + seconds);
  }

  public static boolean isAudioFile(File file) {
    return Constants.AUDIO_FILE_PATTERN.matcher(file.getName()).matches();
  }

  public static boolean isVideoFile(File file) {
    return Constants.VIDEO_FILE_PATTERN.matcher(file.getName()).matches();
  }
}
