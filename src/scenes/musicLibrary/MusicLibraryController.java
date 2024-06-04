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

public class MusicLibraryController implements Initializable {

  @FXML
  private Button shuffleAndPlayAllSongs;
  @FXML
  private Button playAll;
  @FXML
  private MediaPlayer mediaPlayer;
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
  private Button addFolder;
  private ObservableList<SongMetadata> LSong = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> LAlbums = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> LArtists = FXCollections.observableArrayList();
  @FXML
  private ChoiceBox<String> allSongsFilterChoiceBox;
  @FXML
  private TableView<SongMetadata> artistsTable;
  @FXML
  private TableColumn<SongMetadata, String> artistsNameColumn;

  @FXML
  private TableView<SongMetadata> albumsTable;
  @FXML
  private TableColumn<SongMetadata, String> albumsNameColumn;

  Map<String, List<SongMetadata>> ListSongsOfArtist = new HashMap<>(); // key là tên ca sĩ, value là list nhạc của ca sĩ
                                                                       // đó
  Map<String, List<SongMetadata>> ListSongsOfAlbum = new HashMap<>(); // key là tên album, value là list nhạc của album
                                                                      // đó.

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {

    allSongsFilterChoiceBox.getStyleClass().add("centered-choice-box");
    allSongsFilterChoiceBox.getItems().setAll("title", "artist", "album");
    allSongsFilterChoiceBox.setValue("title");
    allSongsTable.getStyleClass().add("noheader");
    artistsTable.getStyleClass().add("noheader");
    albumsTable.getStyleClass().add("noheader");
  }

  @FXML
  void addFolder(MouseEvent event) {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Add Music Files");
    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac"));
    List<File> files = chooser.showOpenMultipleDialog(null);
    if (files != null && !files.isEmpty()) {
      List<MediaPlayer> mediaPlayers = new ArrayList<>();
      for (File file : files) {
        String selectedFile = file.toURI().toString();
        Media media = new Media(selectedFile);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayers.add(mediaPlayer);
        mediaPlayer.setOnReady(() -> {
          String artist = media.getMetadata().get("artist") != null ? media.getMetadata().get("artist").toString()
              : "Unknown Artist";
          String title = media.getMetadata().get("title") != null ? media.getMetadata().get("title").toString()
              : file.getName();
          double duration = media.getDuration().toMinutes();
          String formattedDuration = String.format("%.2f", duration);
          String albun = media.getMetadata().get("album") != null ? media.getMetadata().get("album").toString()
              : "Unknown Album";
          long dateModified = file.lastModified();

          SongMetadata songMetadata = new SongMetadata(title, artist, formattedDuration, albun, dateModified);

          LSong.add(songMetadata);

          if (mediaPlayers.stream().allMatch(mp -> mp.getStatus() == MediaPlayer.Status.READY)) {
            AddSongToArtist(artist, songMetadata);
            AddSongToAlbum(albun, songMetadata);
            updateTableAndArtists();
          }

        });
      }
    }
  }

  void AddSongToArtist(String artist, SongMetadata songMetadata) {
    ListSongsOfArtist.computeIfAbsent(artist, k -> new ArrayList<>()).add(songMetadata);
  }

  void GetAllArtist() {

    LArtists.clear(); // Clear the existing list of artists
    for (String artistsNameColumn : ListSongsOfArtist.keySet()) {

      LArtists.add(ListSongsOfArtist.get(artistsNameColumn).get(0));

    }
    artistsTable.setItems(LArtists);
    artistsNameColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist()));
  }

  void AddSongToAlbum(String album, SongMetadata songMetadata) {
    ListSongsOfAlbum.computeIfAbsent(album, k -> new ArrayList<>()).add(songMetadata);
  }

  void GetAllAlbum() {

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
  void changebox(MouseEvent event) {
    allSongsFilterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if ("title".equals(newValue)) {
        FXCollections.sort(LSong, Comparator.comparing(SongMetadata::getTitle));
      } else if ("artist".equals(newValue)) {
        FXCollections.sort(LSong, Comparator.comparing(SongMetadata::getArtist));
      } else if ("album".equals(newValue)) {
        FXCollections.sort(LSong, Comparator.comparing(SongMetadata::getAlbum));
      }
    });
  }

}
