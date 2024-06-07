package scenes.musicLibrary;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.DirectoryChooser;
import model.SongMetadata;
import utils.Helpers;

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

  private Set<File> selectedAudioDirectories = new HashSet<File>();
  private List<File> audioFilesList = new ArrayList<File>();
  private MediaPlayer mediaPlayer;
  private ObservableList<SongMetadata> allSongsList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> albumsList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> artistsList = FXCollections.observableArrayList();
  Map<String, List<SongMetadata>> artistSongsMap = new HashMap<String, List<SongMetadata>>();
  Map<String, List<SongMetadata>> albumSongsMap = new HashMap<String, List<SongMetadata>>();

  public void initialize(URL arg0, ResourceBundle arg1) {
    List<ChoiceBox<String>> choiceBoxes = new ArrayList<ChoiceBox<String>>(
        List.of(allSongsFilterChoiceBox, albumsFilterChoiceBox, artistsFilterChoiceBox));
    for (ChoiceBox<String> choiceBox : choiceBoxes) {
      choiceBox.getItems().setAll("title", "artist", "album");
      choiceBox.setValue("title");
      choiceBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
        if (!isShowing) {
          filterChoiceBoxValues(choiceBox);
        }
      });
    }
  }

  void filterChoiceBoxValues(ChoiceBox<String> choiceBox) {
    String selectedValue = choiceBox.getValue();
    if (selectedValue == null) {
      return;
    }
    switch (selectedValue) {
      case "title":
        FXCollections.sort(allSongsList, Comparator.comparing(song -> song.getTitle().toLowerCase()));
        break;

      case "artist":
        FXCollections.sort(allSongsList, Comparator.comparing(song -> song.getArtist().toLowerCase()));
        break;

      case "album":
        FXCollections.sort(allSongsList, Comparator.comparing(song -> song.getAlbum().toLowerCase()));
        break;

      default:
        break;
    }
  }

  @FXML
  void addFolder(MouseEvent event) {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Add Music Files");
    directoryChooser
        .setInitialDirectory(new File(System.getProperty("user.dir") + "/src/assets/audios"));
    File selectedDirectory = directoryChooser.showDialog(null);
    if (selectedDirectory == null) {
      return;
    }
    selectedAudioDirectories.add(selectedDirectory);
    List<File> tempAudioFilesList = new ArrayList<File>();
    for (File directory : selectedAudioDirectories) {
      Helpers.findFilesRecursively(directory, tempAudioFilesList, "audio");
    }
    audioFilesList = tempAudioFilesList;

    for (File file : audioFilesList) {
      Media media = new Media(file.toURI().toString());
      mediaPlayer = new MediaPlayer(media);

      String title = media.getMetadata().get("title") != null ? media.getMetadata().get("title").toString()
          : file.getName();
      String artist = media.getMetadata().get("artist") != null ? media.getMetadata().get("artist").toString()
          : "Unknown Artist";
      String album = media.getMetadata().get("album") != null ? media.getMetadata().get("album").toString()
          : "Unknown Album";
      String notReadyDurationString = Helpers.formatTime(media.getDuration());
      long notReadyDateModified = file.lastModified();
      SongMetadata notReadySongMetadata = new SongMetadata(title, artist, notReadyDurationString, album,
          notReadyDateModified);

      allSongsList.add(notReadySongMetadata);

      mediaPlayer.setOnReady(
          () -> {
            String durationString = Helpers.formatTime(media.getDuration());
            long dateModified = file.lastModified();
            SongMetadata songMetadata = new SongMetadata(title, artist, durationString, album, dateModified);

            allSongsList.add(songMetadata);
            albumSongsMap.computeIfAbsent(album, k -> new ArrayList<SongMetadata>()).add(songMetadata);
            artistSongsMap.computeIfAbsent(artist, k -> new ArrayList<SongMetadata>()).add(songMetadata);
            updateTable();
          });
    }
    updateTable();
  }

  private void updateTableAllSongs() {
    Map<String, SongMetadata> longestDurationMap = new HashMap<>();
    for (SongMetadata song : allSongsList) {
      longestDurationMap.merge(
          song.getTitle(),
          song,
          (oldSong, newSong) -> Helpers.formatTime(newSong.getDuration())
              .compareTo(Helpers.formatTime(oldSong.getDuration())) > 0 ? newSong : oldSong);
    }
    Set<String> tempTitleSet = new HashSet<>();
    List<SongMetadata> tempSongsList = longestDurationMap.values().stream()
        .filter(song -> tempTitleSet.add(song.getTitle()))
        .collect(Collectors.toList());

    System.out.println("Longest Duration Map:");
    for (Map.Entry<String, SongMetadata> entry : longestDurationMap.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue().getDuration());
    }
    allSongsList = FXCollections.observableArrayList(tempSongsList);

    allSongsTable.setItems(allSongsList);
    allSongsTitleColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
    allSongsArtistColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getArtist()));
    allSongsAlbumColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getAlbum()));
    allSongsDurationColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getDuration()));
  }

  private void updateTableAllArtists() {
    artistsList.clear();
    for (String artistsNameColumn : artistSongsMap.keySet()) {
      artistsList.add(artistSongsMap.get(artistsNameColumn).get(0));
    }
    artistsTable.setItems(artistsList);
    artistsNameColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getArtist()));
  }

  private void updateTableAllAlbums() {
    albumsList.clear();
    for (String albumsNameColumne : albumSongsMap.keySet()) {
      albumsList.add(albumSongsMap.get(albumsNameColumne).get(0));
    }
    albumsTable.setItems(albumsList);
    albumsNameColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getAlbum()));
  }

  private void updateTable() {
    updateTableAllSongs();
    updateTableAllArtists();
    updateTableAllAlbums();
  }

  @FXML
  void playAllSongs(MouseEvent event) {
    if (allSongsList.isEmpty()) {
      return;
    }
    mediaPlayer.stop();
    playNextMedia(audioFilesList, 0);
  }

  @FXML
  void shuffleAndPlayAllSongs(MouseEvent event) {
    if (allSongsList.isEmpty()) {
      return;
    }
    List<File> shuffledAudioFilesList = new ArrayList<File>(audioFilesList);
    java.util.Collections.shuffle(shuffledAudioFilesList);
    mediaPlayer.stop();
    playNextMedia(shuffledAudioFilesList, 0);
  }

  private void playNextMedia(List<File> filesList, int currentIndex) {
    if (currentIndex >= filesList.size()) {
      System.out.println("Reached the end of the playlist.");
      return;
    }
    Media tempMedia = new Media(filesList.get(currentIndex).toURI().toString());
    mediaPlayer = new MediaPlayer(tempMedia);
    mediaPlayer.play();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }

    mediaPlayer.setOnEndOfMedia(new Runnable() {
      public void run() {
        playNextMedia(filesList, currentIndex + 1);
      }
    });
  }

}
