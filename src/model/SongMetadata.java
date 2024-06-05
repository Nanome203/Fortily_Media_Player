/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class SongMetadata {
    private String Title;
    private String Artist;
    private String Duration;
    private String Album;
    private long Date;

    public SongMetadata(String Title, String Artist, String Duration, String Album, long Date) {
        this.Title = Title;
        this.Artist = Artist;
        this.Duration = Duration;
        this.Album = Album;
        this.Date = Date;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String Artist) {
        this.Artist = Artist;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String Duration) {
        this.Duration = Duration;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String Album) {
        this.Album = Album;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long Date) {
        this.Date = Date;
    }
    
    
}
