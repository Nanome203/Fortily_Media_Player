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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import model.SongMetadata;
import utils.Helpers;
import utils.MediaLoader;

public class MusicLibraryController implements Initializable {

  @FXML
  private ChoiceBox<String> allSongsFilterChoiceBox, albumsFilterChoiceBox, artistsFilterChoiceBox;
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
  private TableView<SongMetadata> artistsTable;
  @FXML
  private TableColumn<SongMetadata, String> artistsNameColumn;
  @FXML
  private TableView<SongMetadata> albumsTable;
  @FXML
  private TableColumn<SongMetadata, String> albumsNameColumn;

  private List<ChoiceBox<String>> choiceBoxes = new ArrayList<ChoiceBox<String>>();
  private Set<File> selectedAudioDirectories = new HashSet<File>();
  private List<File> audioFilesList = new ArrayList<File>();
  private MediaPlayer mediaPlayer;
  private ObservableList<SongMetadata> allSongsList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> albumsList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> artistsList = FXCollections.observableArrayList();
  Map<String, List<SongMetadata>> artistSongsMap = new HashMap<String, List<SongMetadata>>();
  Map<String, List<SongMetadata>> albumSongsMap = new HashMap<String, List<SongMetadata>>();
  MediaLoader mediaLoader = MediaLoader.getMediaLoader();

  public void initialize(URL arg0, ResourceBundle arg1) {
    choiceBoxes = new ArrayList<ChoiceBox<String>>(
        List.of(allSongsFilterChoiceBox, albumsFilterChoiceBox, artistsFilterChoiceBox));
    for (ChoiceBox<String> choiceBox : choiceBoxes) {
      choiceBox.getItems().setAll("title asc. ⌄", "title des. ⌃", "artist asc. ⌄", "artist des. ⌃", "album asc. ⌄",
          "album des. ⌃", "date asc. ⌄", "date des. ⌃");
      choiceBox.setValue("title asc. ⌄");
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
      case "title asc. ⌄":
        FXCollections.sort(allSongsList, Comparator.comparing(SongMetadata::getTitle, String.CASE_INSENSITIVE_ORDER));
        break;

      case "title des. ⌃":
        FXCollections.sort(allSongsList,
            Comparator.comparing(SongMetadata::getTitle, String.CASE_INSENSITIVE_ORDER).reversed());
        break;

      case "artist asc. ⌄":
        FXCollections.sort(allSongsList,
            Comparator.comparing(SongMetadata::getArtist, String.CASE_INSENSITIVE_ORDER));
        break;

      case "artist des. ⌃":
        FXCollections.sort(allSongsList,
            Comparator.comparing(SongMetadata::getArtist, String.CASE_INSENSITIVE_ORDER).reversed());
        break;

      case "album asc. ⌄":
        FXCollections.sort(allSongsList,
            Comparator.comparing(SongMetadata::getAlbum, String.CASE_INSENSITIVE_ORDER));
        break;

      case "album des. ⌃":
        FXCollections.sort(allSongsList,
            Comparator.comparing(SongMetadata::getAlbum, String.CASE_INSENSITIVE_ORDER).reversed());
        break;

      case "date asc. ⌄":
        FXCollections.sort(allSongsList, Comparator.comparing(SongMetadata::getLastModified));
        break;

      case "date des. ⌃":
        FXCollections.sort(allSongsList, Comparator.comparing(SongMetadata::getLastModified).reversed());
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
      long notReadryLastModified = file.lastModified();
      String pathname = file.getAbsolutePath();
      SongMetadata notReadySongMetadata = new SongMetadata(title, artist, album, notReadyDurationString,
          notReadryLastModified,
          pathname);

      allSongsList.add(notReadySongMetadata);
      mediaPlayer.setOnReady(
          () -> {
            String durationString = Helpers.formatTime(media.getDuration());
            long lastModified = file.lastModified();
            SongMetadata songMetadata = new SongMetadata(title, artist, album, durationString, lastModified, pathname);

            allSongsList.add(songMetadata);
            albumSongsMap.computeIfAbsent(album, k -> new ArrayList<SongMetadata>()).add(songMetadata);
            artistSongsMap.computeIfAbsent(artist, k -> new ArrayList<SongMetadata>()).add(songMetadata);
            updateTable();
            for (ChoiceBox<String> choiceBox : choiceBoxes) {
              filterChoiceBoxValues(choiceBox);
            }
          });
    }
    updateTable();
    for (ChoiceBox<String> choiceBox : choiceBoxes) {
      filterChoiceBoxValues(choiceBox);
    }
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
    mediaLoader.receiveListOfMediaFiles(audioFilesList);
    mediaLoader.playReceivedList();
    // playNextMedia(audioFilesList, 0);
  }

  @FXML
  void shuffleAndPlayAllSongs(MouseEvent event) {
    if (allSongsList.isEmpty()) {
      return;
    }
    List<File> shuffledAudioFilesList = new ArrayList<File>(audioFilesList);
    java.util.Collections.shuffle(shuffledAudioFilesList);
    mediaPlayer.stop();
    mediaLoader.receiveListOfMediaFiles(audioFilesList);
    mediaLoader.playReceivedList();
    // playNextMedia(shuffledAudioFilesList, 0);
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
    SongMetadata selectedMedia = allSongsTable.getSelectionModel().getSelectedItem();
    if (selectedMedia == null) {
      return;
    }
    mediaPlayer.stop();
    List<File> tempFilesList = new ArrayList<File>();
    tempFilesList.add(new File(selectedMedia.getPathname()));

    mediaLoader.receiveListOfMediaFiles(tempFilesList);
    mediaLoader.playReceivedList();
  }
}
