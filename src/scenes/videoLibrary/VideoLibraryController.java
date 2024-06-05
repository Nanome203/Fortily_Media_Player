package scenes.videoLibrary;

import java.io.File;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import model.SongMetadata;
import utils.Utils;

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
  private ObservableList<SongMetadata> LSong = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> LAlbums = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> LArtists = FXCollections.observableArrayList();
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

  List<Media> mediaList = new ArrayList<>();
  private MediaPlayer mediaPlayer;
  Map<String, List<SongMetadata>> ListSongsOfArtist = new HashMap<>();
  // key là tên ca sĩ, value là list nhạc của ca sĩ đó
  Map<String, List<SongMetadata>> ListSongsOfAlbum = new HashMap<>();
  // key là tên album, value là list nhạc của album đó

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
        FXCollections.sort(LSong, Comparator.comparing(song -> song.getTitle().toLowerCase()));
        break;

      case "artist":
        FXCollections.sort(LSong, Comparator.comparing(song -> song.getArtist().toLowerCase()));
        break;

      case "album":
        FXCollections.sort(LSong, Comparator.comparing(song -> song.getAlbum().toLowerCase()));
        break;

      default:
        break;
    }
  }

  @FXML
  void addFolder(MouseEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Add Music Files");
    // fileChooser
    // .setInitialDirectory(new
    // File("C:/MyLife/Schoolings/IE303/Project/Fortily_Media_Player/src/assets/videos"));
    fileChooser.getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.flv", "*.mkv", "*.mpeg", "*.ogg"));
    List<File> files = fileChooser.showOpenMultipleDialog(null);
    if (files == null || files.isEmpty()) {
      return;
    }
    for (File file : files) {
      String selectedFile = file.toURI().toString();
      Media media = new Media(selectedFile);
      mediaPlayer = new MediaPlayer(media);
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
      updateTable();
    }
  }

  private void GetAllArtist() {
    LArtists.clear(); // Clear the existing list of artists
    for (String artistsNameColumn : ListSongsOfArtist.keySet()) {
      LArtists.add(ListSongsOfArtist.get(artistsNameColumn).get(0));
    }
    // artistsTable.setItems(LArtists);
    // artistsNameColumn.setCellValueFactory(
    // cellData -> new
    // javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist()));
  }

  private void GetAllAlbum() {
    LAlbums.clear(); // Clear the existing list of artists
    for (String albumsNameColumne : ListSongsOfAlbum.keySet()) {
      LAlbums.add(ListSongsOfAlbum.get(albumsNameColumne).get(0));
    }
    // albumsTable.setItems(LAlbums);
    // albumsNameColumn.setCellValueFactory(
    // cellData -> new
    // javafx.beans.property.SimpleStringProperty(cellData.getValue().getAlbum()));
  }

  private void updateTable() {
    videoLibraryTable.setItems(LSong);
    videoLibraryTitleColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
    videoLibraryArtistColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist()));
    videoLibraryAlbumColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAlbum()));
    videoLibraryDurationColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDuration()));
    GetAllArtist();
    GetAllAlbum();
  }

  @FXML
  void playAllSongs(MouseEvent event) {
    if (LSong.isEmpty()) {
      return;
    }
    mediaPlayer.stop();
    playNextMedia(mediaList, 0);
  }

  @FXML
  void shuffleAndPlayAllSongs(MouseEvent event) {
    if (LSong.isEmpty()) {
      return;
    }
    List<Media> shuffledMediaList = new ArrayList<>(mediaList);
    java.util.Collections.shuffle(shuffledMediaList);
    mediaPlayer.stop();
    playNextMedia(shuffledMediaList, 0);
  }

  private void playNextMedia(List<Media> mediaList, int currentMediaIndex) {
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
