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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import model.SongMetadata;
import utils.Helpers;

public class VideoLibraryController implements Initializable {
  @FXML
  private ChoiceBox<String> filterChoiceBox;
  @FXML
  private TableView<SongMetadata> videoLibraryTable;
  @FXML
  private TableColumn<SongMetadata, String> videoLibraryTitleColumn;
  @FXML
  private TableColumn<SongMetadata, String> videoLibraryArtistColumn;
  @FXML
  private TableColumn<SongMetadata, String> videoLibraryAlbumColumn;
  @FXML
  private TableColumn<SongMetadata, String> videoLibraryDurationColumn;
  @FXML
  private TableView<SongMetadata> artistsTable;
  @FXML
  private TableColumn<SongMetadata, String> artistsNameColumn;
  @FXML
  private TableView<SongMetadata> albumsTable;
  @FXML
  private TableColumn<SongMetadata, String> albumsNameColumn;

  private Set<File> selectedVideoDirectories = new HashSet<File>();
  private List<File> videoFilesList = new ArrayList<File>();
  private MediaPlayer mediaPlayer;
  private ObservableList<SongMetadata> allVideosList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> albumsList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> artistsList = FXCollections.observableArrayList();
  Map<String, List<SongMetadata>> artistSongsMap = new HashMap<String, List<SongMetadata>>();
  Map<String, List<SongMetadata>> albumSongsMap = new HashMap<String, List<SongMetadata>>();

  public void initialize(URL arg0, ResourceBundle arg1) {
    filterChoiceBox.getItems().setAll("title asc. ⌄", "title des. ⌃", "artist asc. ⌄", "artist des. ⌃", "album asc. ⌄",
        "album des. ⌃", "date asc. ⌄", "date des. ⌃");
    filterChoiceBox.setValue("title asc. ⌄");
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
      case "title asc. ⌄":
        FXCollections.sort(allVideosList, Comparator.comparing(SongMetadata::getTitle, String.CASE_INSENSITIVE_ORDER));
        break;

      case "title des. ⌃":
        FXCollections.sort(allVideosList,
            Comparator.comparing(SongMetadata::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
        break;

      case "artist asc. ⌄":
        FXCollections.sort(allVideosList,
            Comparator.comparing(SongMetadata::getArtist, String.CASE_INSENSITIVE_ORDER));
        break;

      case "artist des. ⌃":
        FXCollections.sort(allVideosList,
            Comparator.comparing(SongMetadata::getArtist, String.CASE_INSENSITIVE_ORDER).reversed());
        break;

      case "album asc. ⌄":
        FXCollections.sort(allVideosList,
            Comparator.comparing(SongMetadata::getAlbum, String.CASE_INSENSITIVE_ORDER));
        break;

      case "album des. ⌃":
        FXCollections.sort(allVideosList,
            Comparator.comparing(SongMetadata::getAlbum, String.CASE_INSENSITIVE_ORDER).reversed());
        break;

      case "date asc. ⌄":
        FXCollections.sort(allVideosList, Comparator.comparing(SongMetadata::getLastModified));
        break;

      case "date des. ⌃":
        FXCollections.sort(allVideosList, Comparator.comparing(SongMetadata::getLastModified).reversed());
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
      long notReadryLastModified = file.lastModified();
      String pathname = file.getAbsolutePath();
      SongMetadata notReadySongMetadata = new SongMetadata(title, artist, album, notReadyDurationString,
          notReadryLastModified,
          pathname);

      allVideosList.add(notReadySongMetadata);
      mediaPlayer.setOnReady(
          () -> {
            String durationString = Helpers.formatTime(media.getDuration());
            long lastModified = file.lastModified();
            SongMetadata songMetadata = new SongMetadata(title, artist, album, durationString, lastModified, pathname);

            allVideosList.add(songMetadata);
            updateTable();
            filterChoiceBoxValues(filterChoiceBox);
          });
    }
    updateTable();
    filterChoiceBoxValues(filterChoiceBox);
  }

  private void GetAllArtist() {
    artistsList.clear();
    for (String artistsNameColumn : artistSongsMap.keySet()) {
      artistsList.add(artistSongsMap.get(artistsNameColumn).get(0));
    }
    // artistsTable.setItems(artistsList);
    // artistsNameColumn.setCellValueFactory(
    // cellData -> new
    // javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist()));
  }

  private void GetAllAlbum() {
    albumsList.clear();
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

    GetAllArtist();
    GetAllAlbum();
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

  @FXML
  void playMediaOnRowClick(MouseEvent event) {
    SongMetadata selectedMedia = videoLibraryTable.getSelectionModel().getSelectedItem();
    if (selectedMedia == null) {
      return;
    }
    mediaPlayer.stop();
    Media media = new Media(new File(selectedMedia.getPathname()).toURI().toString());
    mediaPlayer = new MediaPlayer(media);
    mediaPlayer.play();
  }
}
