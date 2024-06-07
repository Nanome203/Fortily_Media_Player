package utils;

import javafx.util.Duration;

public class Utils {
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
}
