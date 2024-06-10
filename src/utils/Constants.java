package utils;

import java.util.regex.Pattern;

public class Constants {

  public static final Pattern AUDIO_FILE_PATTERN = Pattern.compile(".*\\.(mp3|wav|m4a|aac|flac|ogg)$",
      Pattern.CASE_INSENSITIVE);

  public static final Pattern VIDEO_FILE_PATTERN = Pattern.compile(".*\\.(mp4|flv|mkv|mpeg|ogg)$",
      Pattern.CASE_INSENSITIVE);
}
