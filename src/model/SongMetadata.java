package model;

public class SongMetadata {
  private String title;
  private String artist;
  private String album;
  private String duration;
  private long lastModified;
  private String pathname;
  private String lastDayOpened; // Format "yyyy-mm-dd HH:mm:ss"
  
  public String getLastDayOpened() {
	return lastDayOpened;
}

  public SongMetadata() {
	  
  }
  
public void setLastDayOpened(String lastDayOpened) {
	this.lastDayOpened = lastDayOpened;
}

public SongMetadata(String title, String artist, String album, String duration, long lastModified, String pathname) {
    this.title = title;
    this.artist = artist;
    this.album = album;
    this.duration = duration;
    this.lastModified = lastModified;
    this.pathname = pathname;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getAlbum() {
    return album;
  }

  public void setAlbum(String album) {
    this.album = album;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public long getLastModified() {
    return lastModified;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  public String getPathname() {
    return pathname;
  }

  public void setPathname(String pathname) {
    this.pathname = pathname;
  }
}
