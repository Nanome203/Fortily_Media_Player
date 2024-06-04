package scenes.musicLibrary;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import model.SongMetadata;
import utils.Utils;

public class MusicLibraryController implements Initializable {

  @FXML
  private Button shuffleAndPlayAllSongs;
  @FXML
  private Button playAll;
  @FXML
  private TableView<SongMetadata> allSongsTable;
  @FXML
  private TableColumn<SongMetadata, String> allSongsTitleColumn;
  @FXML
  private TableColumn<SongMetadata, String> allSongsArtistColumn;
  @FXML
  private TableColumn<SongMetadata, String> allSongsAlbumColumn;
  @FXML
  private TableColumn<SongMetadata, String> allSongsDurationColumn;
  @FXML
  private Button addFolderButton;
  private ObservableList<SongMetadata> LSong = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> LAlbums = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> LArtists = FXCollections.observableArrayList();
  @FXML
  private ChoiceBox<String> allSongsFilterChoiceBox, albumsFilterChoiceBox, artistsFilterChoiceBox;
  @FXML
  private TableView<SongMetadata> artistsTable;
  @FXML
  private TableColumn<SongMetadata, String> artistsNameColumn;

  @FXML
  private TableView<SongMetadata> albumsTable;
  @FXML
  private TableColumn<SongMetadata, String> albumsNameColumn;

  List<Media> mediaList = new ArrayList<>();
  private MediaPlayer mediaPlayer;
  Map<String, List<SongMetadata>> ListSongsOfArtist = new HashMap<>();
  // key là tên ca sĩ, value là list nhạc của ca sĩ đó
  Map<String, List<SongMetadata>> ListSongsOfAlbum = new HashMap<>();
  // key là tên album, value là list nhạc của album đó

  public void initialize(URL arg0, ResourceBundle arg1) {
    List<ChoiceBox<String>> choiceBoxes = new ArrayList<ChoiceBox<String>>(
        List.of(allSongsFilterChoiceBox, albumsFilterChoiceBox, artistsFilterChoiceBox));
    for (ChoiceBox<String> choiceBox : choiceBoxes) {
      choiceBox.getItems().setAll("title", "artist", "album");
      choiceBox.setValue("title");
    }
  }

  @FXML
  void addFolder(MouseEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Add Music Files");
    fileChooser.getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aac", "*.flac", "*.ogg"));
    List<File> files = fileChooser.showOpenMultipleDialog(null);
    if (files == null || files.isEmpty()) {
      return;
    }
    for (File file : files) {
      String selectedFile = file.toURI().toString();
      Media media = new Media(selectedFile);
      mediaPlayer = new MediaPlayer(media);
      mediaPlayer.setOnReady(() -> {
        mediaList.add(media);
        String artist = media.getMetadata().get("artist") != null ? media.getMetadata().get("artist").toString()
            : "Unknown Artist";
        String title = media.getMetadata().get("title") != null ? media.getMetadata().get("title").toString()
            : file.getName();
        String durationString = Utils.formatTime(media.getDuration());
        String album = media.getMetadata().get("album") != null ? media.getMetadata().get("album").toString()
            : "Unknown Album";
        long dateModified = file.lastModified();

        SongMetadata songMetadata = new SongMetadata(title, artist, durationString, album, dateModified);

        LSong.add(songMetadata);

        ListSongsOfAlbum.computeIfAbsent(album, k -> new ArrayList<>()).add(songMetadata);
        ListSongsOfArtist.computeIfAbsent(artist, k -> new ArrayList<>()).add(songMetadata);
        updateTableAndArtists();
      });
    }
  }

  private void GetAllArtist() {
    LArtists.clear(); // Clear the existing list of artists
    for (String artistsNameColumn : ListSongsOfArtist.keySet()) {
      LArtists.add(ListSongsOfArtist.get(artistsNameColumn).get(0));
    }
    artistsTable.setItems(LArtists);
    artistsNameColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist()));
  }

  private void GetAllAlbum() {
    LAlbums.clear(); // Clear the existing list of artists
    for (String albumsNameColumne : ListSongsOfAlbum.keySet()) {
      LAlbums.add(ListSongsOfAlbum.get(albumsNameColumne).get(0));
    }
    albumsTable.setItems(LAlbums);
    albumsNameColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAlbum()));
  }

  private void updateTableAndArtists() {
    allSongsTable.setItems(LSong);
    allSongsTitleColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
    allSongsArtistColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist()));
    allSongsAlbumColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAlbum()));
    allSongsDurationColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDuration()));
    GetAllArtist();
    GetAllAlbum();
  }

  @FXML
  void changebox(ActionEvent event) {
    @SuppressWarnings("unchecked")
    ChoiceBox<String> sourceChoiceBox = (ChoiceBox<String>) event.getSource();
    String selectedValue = sourceChoiceBox.getValue();
    if (selectedValue == null) {
      return;
    }
    switch (selectedValue) {
      case "title":
        FXCollections.sort(LSong, Comparator.comparing(SongMetadata::getTitle));
        break;

      case "artist":
        FXCollections.sort(LSong, Comparator.comparing(SongMetadata::getArtist));
        break;

      case "album":
        FXCollections.sort(LSong, Comparator.comparing(SongMetadata::getAlbum));
        break;

      default:
        break;
    }
  };

  @FXML
  void playAllSongs(MouseEvent event) {
    if (LSong.isEmpty()) {
      return;
    }
    playNextMedia(mediaList, 0);
  }

  @FXML
  void shuffleAndPlayAllSongs(MouseEvent event) {
    if (LSong.isEmpty()) {
      return;
    }
    List<Media> shuffledMediaList = new ArrayList<>(mediaList);
    java.util.Collections.shuffle(shuffledMediaList);
    playNextMedia(shuffledMediaList, 0);
  }

  private void playNextMedia(List<Media> mediaList, int currentMediaIndex) {
    System.out.println(mediaList + "\n" + mediaList.size());
    System.out.println("Runnin" + currentMediaIndex);
    if (currentMediaIndex >= mediaList.size()) {
      System.out.println("Reached the end of the playlist.");
      return;
    }
    mediaPlayer = new MediaPlayer(mediaList.get(currentMediaIndex));
    mediaPlayer.play();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }

    mediaPlayer.setOnEndOfMedia(new Runnable() {
      public void run() {
        playNextMedia(mediaList, currentMediaIndex + 1);
      }
    });

  }

}
