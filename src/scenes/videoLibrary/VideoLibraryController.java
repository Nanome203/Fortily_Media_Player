package scenes.videoLibrary;

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
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import model.SongMetadata;
import utils.Helpers;

public class VideoLibraryController implements Initializable {
  @FXML
  private VBox videoLibraryComponentsContainer;
  @FXML
  private TableColumn<SongMetadata, String> videoLibraryTitleColumn;
  @FXML
  private TableColumn<SongMetadata, String> videoLibraryArtistColumn;
  @FXML
  private TableColumn<SongMetadata, String> videoLibraryAlbumColumn;
  @FXML
  private TableColumn<SongMetadata, String> videoLibraryDurationColumn;
  @FXML
  private Button addFolderButton;
  @FXML
  private ChoiceBox<String> filterChoiceBox;
  @FXML
  private TableView<SongMetadata> artistsTable;
  @FXML
  private TableColumn<SongMetadata, String> artistsNameColumn;

  @FXML
  private TableView<SongMetadata> albumsTable;
  @FXML
  private TableColumn<SongMetadata, String> albumsNameColumn;
  @FXML
  private TableView<SongMetadata> videoLibraryTable;

  private Set<File> selectedVideoDirectories = new HashSet<File>();
  private List<File> videoFilesList = new ArrayList<File>();
  private MediaPlayer mediaPlayer;
  private ObservableList<SongMetadata> allVideosList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> albumsList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> artistsList = FXCollections.observableArrayList();
  Map<String, List<SongMetadata>> artistSongsMap = new HashMap<String, List<SongMetadata>>();
  Map<String, List<SongMetadata>> albumSongsMap = new HashMap<String, List<SongMetadata>>();

  public void initialize(URL arg0, ResourceBundle arg1) {
    filterChoiceBox.getItems().setAll("title", "artist", "album");
    filterChoiceBox.setValue("title");
    filterChoiceBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
      if (!isShowing) {
        filterChoiceBoxValues(filterChoiceBox);
      }
    });
  }

  void filterChoiceBoxValues(ChoiceBox<String> choiceBox) {
    String selectedValue = choiceBox.getValue();
    if (selectedValue == null) {
      return;
    }
    switch (selectedValue) {
      case "title":
        FXCollections.sort(allVideosList, Comparator.comparing(song -> song.getTitle().toLowerCase()));
        break;

      case "artist":
        FXCollections.sort(allVideosList, Comparator.comparing(song -> song.getArtist().toLowerCase()));
        break;

      case "album":
        FXCollections.sort(allVideosList, Comparator.comparing(song -> song.getAlbum().toLowerCase()));
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
        .setInitialDirectory(new File(System.getProperty("user.dir") + "/src/assets/videos"));
    File selectedDirectory = directoryChooser.showDialog(null);
    if (selectedDirectory == null) {
      return;
    }
    selectedVideoDirectories.add(selectedDirectory);
    List<File> tempVideoFilesList = new ArrayList<File>();
    for (File directory : selectedVideoDirectories) {
      Helpers.findFilesRecursively(directory, tempVideoFilesList, "video");
    }
    videoFilesList = tempVideoFilesList;

    for (File file : videoFilesList) {
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

      allVideosList.add(notReadySongMetadata);

      mediaPlayer.setOnReady(
          () -> {
            String durationString = Helpers.formatTime(media.getDuration());
            long dateModified = file.lastModified();
            SongMetadata songMetadata = new SongMetadata(title, artist, durationString, album, dateModified);

            allVideosList.add(songMetadata);
            updateTable();
          });
    }
    updateTable();
  }

  private void GetAllArtist() {
    artistsList.clear(); // Clear the existing list of artists
    for (String artistsNameColumn : artistSongsMap.keySet()) {
      artistsList.add(artistSongsMap.get(artistsNameColumn).get(0));
    }
    // artistsTable.setItems(artistsList);
    // artistsNameColumn.setCellValueFactory(
    // cellData -> new
    // javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist()));
  }

  private void GetAllAlbum() {
    albumsList.clear(); // Clear the existing list of artists
    for (String albumsNameColumne : albumSongsMap.keySet()) {
      albumsList.add(albumSongsMap.get(albumsNameColumne).get(0));
    }
    // albumsTable.setItems(albumsList);
    // albumsNameColumn.setCellValueFactory(
    // cellData -> new
    // javafx.beans.property.SimpleStringProperty(cellData.getValue().getAlbum()));
  }

  private void updateTable() {
    Map<String, SongMetadata> longestDurationMap = new HashMap<>();
    for (SongMetadata song : allVideosList) {
      longestDurationMap.merge(
          song.getTitle(),
          song,
          (oldSong, newSong) -> Helpers.formatTime(newSong.getDuration())
              .compareTo(Helpers.formatTime(oldSong.getDuration())) > 0 ? newSong : oldSong);
    }
    Set<String> tempTitleSet = new HashSet<String>();
    List<SongMetadata> tempSongsList = longestDurationMap.values().stream()
        .filter(song -> tempTitleSet.add(song.getTitle()))
        .collect(Collectors.toList());

    allVideosList = FXCollections.observableArrayList(tempSongsList);

    videoLibraryTable.setItems(allVideosList);
    videoLibraryTitleColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
    videoLibraryArtistColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getArtist()));
    videoLibraryAlbumColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getAlbum()));
    videoLibraryDurationColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getDuration()));
    // GetAllArtist();
    // GetAllAlbum();
  }

  @FXML
  void playAllSongs(MouseEvent event) {
    if (allVideosList.isEmpty()) {
      return;
    }
    mediaPlayer.stop();
    playNextMedia(videoFilesList, 0);
  }

  @FXML
  void shuffleAndPlayAllSongs(MouseEvent event) {
    if (allVideosList.isEmpty()) {
      return;
    }
    List<File> shuffledVideoFilesList = new ArrayList<File>(videoFilesList);
    java.util.Collections.shuffle(shuffledVideoFilesList);
    mediaPlayer.stop();
    playNextMedia(shuffledVideoFilesList, 0);
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
