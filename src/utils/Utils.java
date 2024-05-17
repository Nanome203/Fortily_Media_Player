package utils;

import javafx.util.Duration;

public class Utils {
  public static String formatTime(Duration duration) {
    int intDuration = (int) Math.floor(duration.toSeconds());
    int hours = intDuration / (60 * 60);
    if (hours > 0) {
      intDuration -= hours * 60 * 60;
    }
    int minutes = intDuration / 60;
    int seconds = intDuration - hours * 60 * 60 - minutes * 60;

    if (hours > 0) {
      return String.format("%d:%02d:%02d", hours, minutes, seconds);
    } else {
      return String.format("%02d:%02d", minutes, seconds);
    }
  }
}
