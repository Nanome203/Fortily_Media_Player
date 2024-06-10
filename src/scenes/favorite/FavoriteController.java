package scenes.favorite;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.SongMetadata;
import utils.MediaLoader;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import utils.ReusableFileChooser;

import dao.FavoriteDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

public class FavoriteController implements Initializable {

  private MediaLoader mediaLoader;

  private FavoriteDAO favoriteDAO = new FavoriteDAO();

  @FXML
  private ChoiceBox<String> mediaTypeSelection;
  private String[] mediaType = { "Video", "Audio" };

  @FXML
  private TableView<SongMetadata> mediaTableView;

  private TableViewSelectionModel<SongMetadata> selectionModel;

  @FXML
  private TableColumn<SongMetadata, String> mediaName;

  @FXML
  private TableColumn<SongMetadata, String> artistName;

  @FXML
  private TableColumn<SongMetadata, String> mediaDuration;

  @FXML
  private TableColumn<SongMetadata, String> mediaAlbum;

  @FXML
  private Button btnPlayAll;

  @FXML
  private Button btnAddFile;

  @FXML
  private Button btnClearFile;

  @FXML
  private BorderPane favoriteContainer;

  private SongMetadata getCurrentMediaPlaying;

  @FXML
  private ObservableList<SongMetadata> LAudio = FXCollections.observableArrayList();
  private ObservableList<SongMetadata> LVideo = FXCollections.observableArrayList();

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    // Create selection menu for type of media
    mediaTypeSelection.getItems().addAll(mediaType);
    mediaTypeSelection.getSelectionModel().select("Video");
    mediaTypeSelection.setOnAction(this::mediaSelectionAction);
    selectionModel = mediaTableView.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    mediaLoader = MediaLoader.getMediaLoader();

    favoriteContainer.setOnKeyReleased(evt -> {
      // Ctrl + O Event to open file
      if (evt.getCode().equals(KeyCode.O)) {
        if (evt.isControlDown()) {
          try {
            addFile(null);
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }
      // Delete Event to clear file
      if (evt.getCode().equals(KeyCode.DELETE)) {
        try {
          clearFile(null);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }

      // Refresh table
      if (evt.getCode().equals(KeyCode.F5)) {
        try {
          updateAllTable();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    });

    // Double click event to play file
    mediaTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent evt) {
        if (evt.getButton().equals(MouseButton.PRIMARY)) {
          if (evt.getClickCount() == 2) {
            playSingleMedia(selectionModel.getSelectedItem());
          }
        }
      }
    });

    try {
      updateAllTable();
      mediaSelectionAction(null);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    mediaName.setStyle("-fx-alignment: center-left;");
  }

  @FXML
  public void addFile(MouseEvent evt) throws SQLException {
    ReusableFileChooser chooser = ReusableFileChooser.getFileChooser();
    int success = 0;
    int global_success = 0;
    List<File> files = chooser.showOpenMultipleDialog();
    if (files != null && !files.isEmpty()) {
      // List<MediaPlayer> mediaPlayers = new ArrayList<>();
      for (File file : files) {
        // Insert file path into database
        success = favoriteDAO.insertMedia(file);
        if (success == 1) // Duplicated primary key/ File in the same directory
        {
          global_success = 1;
        }
      }
      if (global_success == 1) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("File duplication");
        alert.setHeaderText("Some media file has been added before!");
        alert.show();
      }
      // Then update the table
      updateAllTable();
    }
  }

  public FavoriteController getFavoriteController() {
    return this;
  }

  public void updateAllTable() throws SQLException {
    // Get all the paths file for both audio and video
    List<File> listFilePath = favoriteDAO.getMediaList();

    for (File filePath : listFilePath) {
      if (filePath != null) {
        Media media = new Media(filePath.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
          String title = media.getMetadata().get("title") != null
              ? media.getMetadata().get("title").toString()
              : filePath.getName();
          String artist = media.getMetadata().get("artist") != null
              ? media.getMetadata().get("artist").toString()
              : "Unknown artist";
          String album = media.getMetadata().get("album") != null
              ? media.getMetadata().get("album").toString()
              : "Unknown album";
          double duration = media.getDuration().toMillis();
          String formatDuration = convertDurationMillis((int) duration);

          SongMetadata getIndex = new SongMetadata(title, artist, album, formatDuration, 0,
              filePath.getAbsolutePath());

          // Check for audio or video type
          String strPath = filePath.getAbsolutePath();
          int getExtension = filePath.getAbsolutePath().lastIndexOf(".");
          if (getExtension > 0) {
            // Audio file
            if (strPath.substring(getExtension).equals(".mp3") ||
                strPath.substring(getExtension).equals(".wav") ||
                strPath.substring(getExtension).equals(".aac")) {
              // Check if the path exists on Audio list
              int tag = 0;
              for (SongMetadata i : LAudio) {
                if (i.getPathname().equals(getIndex.getPathname())) {
                  tag = 1;
                  break;
                }
              }
              if (tag == 0)
                LAudio.add(getIndex);
            }
            // Video file
            else if (strPath.substring(getExtension).equals(".mp4")) {
              int tag = 0;
              for (SongMetadata i : LVideo) {
                if (i.getPathname().equals(getIndex.getPathname())) {
                  tag = 1;
                  break;
                }
              }
              if (tag == 0)
                LVideo.add(getIndex);
            }
          }
        });
      }
    }
  }

  public void clearFileFavButton(String getPathname) {
    if (mediaTypeSelection.getSelectionModel().getSelectedItem().equals("Video")) {
      for (SongMetadata i : LVideo) {
        if (i.getPathname().equals(getPathname)) {
          LVideo.remove(i);
          break;
        }
      }
    } else {
      for (SongMetadata i : LAudio) {
        if (i.getPathname().equals(getPathname)) {
          LAudio.remove(i);
          break;
        }
      }
    }
  }

  @FXML
  public void clearFile(MouseEvent evt) throws SQLException {
    ObservableList<SongMetadata> selectedItems = selectionModel.getSelectedItems(); // Selected Item
    ObservableList<SongMetadata> remainingItems = FXCollections.observableArrayList();
    int tag;
    if (selectedItems.isEmpty())
      return;
    for (SongMetadata getPath : selectedItems) {
      if (getPath.equals(getCurrentMediaPlaying)) {
      }
      favoriteDAO.deleteMedia(getPath.getPathname());
    }

    // Refresh the table
    if (mediaTypeSelection.getSelectionModel().getSelectedItem().equals("Video")) {
      for (SongMetadata getIndex1 : LVideo) {
        tag = 0;
        for (SongMetadata getIndex2 : selectedItems) {
          if (getIndex1.equals(getIndex2)) {
            tag = 1;
            break;
          }
        }
        if (tag == 0) {
          remainingItems.add(getIndex1);
        }
      }
      LVideo.clear();
      for (SongMetadata getIndex : remainingItems) {
        LVideo.add(getIndex);
      }
    } else {
      for (SongMetadata getIndex1 : LAudio) {
        tag = 0;
        for (SongMetadata getIndex2 : selectedItems) {
          if (getIndex1.equals(getIndex2)) {
            tag = 1;
            break;
          }
        }
        if (tag == 0) {
          remainingItems.add(getIndex1);
        }
      }
      LAudio.clear();
      for (SongMetadata getIndex : remainingItems) {
        LAudio.add(getIndex);
      }
    }
  }

  @FXML
  public void playFile(MouseEvent evt) {
    if (mediaLoader.getReceivedList() != null)
      mediaLoader.getReceivedList().clear();
    List<File> getList = new ArrayList<>();
    if (mediaTypeSelection.getSelectionModel().getSelectedItem().equals("Video")) {
      if (LVideo.isEmpty()) {
        return;
      }
      for (SongMetadata i : LVideo) {
        File file = new File(i.getPathname());
        if (file != null)
          getList.add(file);
      }
    } else // Audio
    {
      if (LAudio.isEmpty()) {
        return;
      }
      for (SongMetadata i : LAudio) {
        File file = new File(i.getPathname());
        if (file != null)
          getList.add(file);
      }
    }
    if (!getList.isEmpty()) {
      mediaLoader.receiveListOfMediaFiles(getList);
      for (File i : mediaLoader.getReceivedList()) {
        System.out.println(i);
      }
      mediaLoader.playReceivedList();
    }
  }

  private void playSingleMedia(SongMetadata getSongMetadata) {
    if (mediaLoader.getReceivedList() != null)
      mediaLoader.getReceivedList().clear();
    getCurrentMediaPlaying = getSongMetadata;
    if (getSongMetadata != null) {
      File file = new File(getSongMetadata.getPathname());
      if (file.exists()) {
        // Empty the media loader playlist
        List<File> playSingleFile = new ArrayList<>();
        playSingleFile.add(file);
        mediaLoader.receiveListOfMediaFiles(playSingleFile);
        mediaLoader.playReceivedList();
      }
    }
  }

  public void mediaSelectionAction(ActionEvent evt) {
    // Video List
    if (mediaTypeSelection.getSelectionModel().getSelectedItem().equals("Video")) {
      mediaTableView.setItems(LVideo);
      mediaName.setCellValueFactory(
          cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
      artistName.setCellValueFactory(
          cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist()));
      mediaDuration.setCellValueFactory(
          cellData -> new javafx.beans.property.SimpleStringProperty(
              cellData.getValue().getDuration()));
      mediaAlbum.setCellValueFactory(
          cellData -> new javafx.beans.property.SimpleStringProperty(
              cellData.getValue().getAlbum()));
    }
    // Audio List
    else {
      mediaTableView.setItems(LAudio);
      mediaName.setCellValueFactory(
          cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
      artistName.setCellValueFactory(
          cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist()));
      mediaDuration.setCellValueFactory(
          cellData -> new javafx.beans.property.SimpleStringProperty(
              cellData.getValue().getDuration()));
      mediaAlbum.setCellValueFactory(
          cellData -> new javafx.beans.property.SimpleStringProperty(
              cellData.getValue().getAlbum()));
    }
  }

  public String convertDurationMillis(Integer getDurationInMillis) {
    int getDurationMillis = getDurationInMillis;

    String convertHours = String.format("%02d", TimeUnit.MILLISECONDS.toHours(getDurationMillis));
    String convertMinutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(getDurationMillis) -
        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(getDurationMillis)));
    String convertSeconds = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(getDurationMillis) -
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getDurationMillis)));

    String getDuration = convertHours + ":" + convertMinutes + ":" + convertSeconds;

    return getDuration;
  }

}