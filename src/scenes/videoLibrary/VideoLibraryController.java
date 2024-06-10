package scenes.videoLibrary;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import dao.VideoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import model.SongMetadata;
import utils.Helpers;
import utils.MediaLoader;

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

  private VideoDAO videoDAO = new VideoDAO();
  private Set<File> selectedVideoDirectories = new HashSet<File>();
  private List<File> videoFilesList = new ArrayList<File>();
  private MediaPlayer mediaPlayer;
  private TableViewSelectionModel<SongMetadata> videoLibrarySelectionModel;
  private ObservableList<SongMetadata> allVideosList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> albumsList = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> artistsList = FXCollections.observableArrayList();
  Map<String, List<SongMetadata>> artistSongsMap = new HashMap<String, List<SongMetadata>>();
  Map<String, List<SongMetadata>> albumSongsMap = new HashMap<String, List<SongMetadata>>();
  MediaLoader mediaLoader = MediaLoader.getMediaLoader();

  public void initialize(URL arg0, ResourceBundle arg1) {
    filterChoiceBox.getItems().setAll("title asc. ⌄", "title des. ⌃", "artist asc. ⌄", "artist des. ⌃", "album asc. ⌄",
        "album des. ⌃", "date asc. ⌄", "date des. ⌃");
    filterChoiceBox.setValue("title asc. ⌄");
    filterChoiceBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
      if (!isShowing) {
        filterChoiceBoxValues(filterChoiceBox);
      }
    });

    getFromDatabase();
    updateTable();
    videoLibrarySelectionModel = videoLibraryTable.getSelectionModel();
    videoLibrarySelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

    videoLibraryTable.setRowFactory(tv -> {
      TableRow<SongMetadata> row = new TableRow<SongMetadata>();
      Tooltip tooltip = new Tooltip(
          "Press ctrl to select multiple rows\nPress ctrl+click to deselect a row\nPress shift to select continuous rows");
      tooltip.setShowDelay(Duration.millis(100));
      tooltip.setStyle("-fx-font-size: 1em;");
      row.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
        if (row.isEmpty()) {
          return;
        }
        if (isNowHovered) {
          row.setStyle("-fx-background-color: #1D2129; -fx-cursor: hand;");
        } else {
          row.setStyle("");
        }
      });
      row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
        if (row.isEmpty()) {
          return;
        }
        if (wasSelected) {
          Tooltip.uninstall(row, null);
        } else if (isNowSelected) {
          Tooltip.install(row, tooltip);
        }
      });
      row.setOnMousePressed(event -> {
        if (row.isEmpty()) {
          return;
        }
        if (row.isHover()) {
          row.setStyle("-fx-background-color: #2880E8;");
        }
      });
      row.setOnMouseClicked(event -> {
        if (row.isEmpty()) {
          return;
        }
        if (videoLibrarySelectionModel.getSelectedItems().size() == 1 && event.getClickCount() == 2) {
          SongMetadata selectedMedia = row.getItem();
          if (selectedMedia != null) {
            mediaPlayer.stop();
            List<File> tempFilesList = new ArrayList<File>();
            tempFilesList.add(new File(selectedMedia.getPathname()));
            mediaLoader.receiveListOfMediaFiles(tempFilesList);
            mediaLoader.playReceivedList();
          }
        }
        if (!row.isSelected()) {
          row.setStyle("-fx-background-color: #1D2129; -fx-cursor: hand;");
        }
      });
      return row;
    });
    videoLibraryTitleColumn.setStyle("-fx-alignment: center-left;");
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

  private void getFromDatabase() {
    try {
      videoFilesList = videoDAO.getMediaList();
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
            albumSongsMap.computeIfAbsent(album, k -> new ArrayList<SongMetadata>()).add(songMetadata);
            artistSongsMap.computeIfAbsent(artist, k -> new ArrayList<SongMetadata>()).add(songMetadata);
            updateTable();
            filterChoiceBoxValues(filterChoiceBox);
          });
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

      if (FXCollections.observableArrayList(allVideosList).stream().map(song -> song.getPathname())
          .noneMatch(songPathname -> songPathname.equals(file.getAbsolutePath()))) {
        insertToDatabase(file);
      }

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

  private void insertToDatabase(File file) {
    try {
      videoDAO.insertMedia(file);
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
  void playAllVideos(MouseEvent event) {
    if (allVideosList.isEmpty()) {
      return;
    }
    mediaPlayer.stop();
    mediaLoader.receiveListOfMediaFiles(videoFilesList);
    mediaLoader.playReceivedList();
    // playNextMedia(videoFilesList, 0);
  }

  @FXML
  void playSelectedSongs(MouseEvent event) {
    if (allVideosList.isEmpty()) {
      return;
    }
    List<SongMetadata> selectedSongs = videoLibraryTable.getSelectionModel().getSelectedItems();
    List<File> selectedFiles = new ArrayList<File>();
    for (SongMetadata song : selectedSongs) {
      selectedFiles.add(new File(song.getPathname()));
    }
    mediaPlayer.stop();
    mediaLoader.receiveListOfMediaFiles(selectedFiles);
    mediaLoader.playReceivedList();
    // playNextMedia(videoFilesList, 0);
  }

  @FXML
  void shuffleAllVideos(MouseEvent event) {
    if (allVideosList.isEmpty()) {
      return;
    }
    List<File> shuffledVideoFilesList = new ArrayList<File>(videoFilesList);
    java.util.Collections.shuffle(shuffledVideoFilesList);
  }

  @FXML
  void deleteSongs(MouseEvent event) {
    if (allVideosList.isEmpty()) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No Songs");
      alert.setHeaderText(null);
      alert.setContentText("There are no songs to delete.");
      alert.showAndWait();
      return;
    }
    List<SongMetadata> selectedDeleteSongs = videoLibrarySelectionModel.getSelectedItems();
    if (selectedDeleteSongs.isEmpty()) {
      return;
    }
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Songs");
    alert.setHeaderText(null);
    alert.setContentText("Do you want to delete " + selectedDeleteSongs.size() + " songs?");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
      for (SongMetadata song : selectedDeleteSongs) {
        try {
          videoDAO.deleteMedia(song.getPathname());
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      allVideosList.removeAll(selectedDeleteSongs);
      videoLibrarySelectionModel.clearSelection();
      updateTable();
    }
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