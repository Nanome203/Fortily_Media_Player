package scenes.recentMedia;

import java.io.File;
import java.net.URL;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import dao.SongDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.SongMetadata;
import utils.MediaLoader;

public class RecentMediaController implements Initializable {

  private MediaLoader mediaLoader;

  private SongDAO songDAO = new SongDAO();

  @FXML
  private TableView<SongMetadata> recentMediaTable;

  private TableViewSelectionModel<SongMetadata> selectionModel;

  @FXML
  private TableColumn<SongMetadata, String> allMediaTitleColumn;

  @FXML
  private TableColumn<SongMetadata, String> allMediaLastDateOpenedColumn;

  @FXML
  private Button btnClearFile;

  @FXML
  private Button btnPlaySingleMedia;

  @FXML
  private Button btnPlayAll;

  // private MediaPlayer mediaPlayer;
  private SongMetadata getCurrentMediaPlaying;

  private File getCurrentFilePlaying;

  private ObservableList<SongMetadata> LSong = FXCollections.observableArrayList();

  public void setCurrentFilePlaying(File file) {
    this.getCurrentFilePlaying = file;
  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    selectionModel = recentMediaTable.getSelectionModel();
    selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    mediaLoader = MediaLoader.getMediaLoader();
    mediaLoader.receiveRecentMediaController(this);
    // Double click event to play file
    recentMediaTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
      showRecentMedia();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    allMediaTitleColumn.setStyle("-fx-alignment: center-left;");
  }

  public static String convertDurationMillis(Integer getDurationInMillis) {
    int getDurationMillis = getDurationInMillis;

    String convertHours = String.format("%02d", TimeUnit.MILLISECONDS.toHours(getDurationMillis));
    String convertMinutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(getDurationMillis) -
        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(getDurationMillis)));
    String convertSeconds = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(getDurationMillis) -
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getDurationMillis)));

    String getDuration = convertHours + ":" + convertMinutes + ":" + convertSeconds;

    return getDuration;

  }

  @FXML
  public void playSingleFile(ActionEvent evt) throws SQLException {
    playSingleMedia(selectionModel.getSelectedItem());
  }

  @FXML
  public void playAllFile(ActionEvent evt) {
    List<File> getList = new ArrayList<>();

    if (LSong.isEmpty()) {
      return;
    }
    for (SongMetadata i : LSong) {
      File file = new File(i.getPathname());
      if (file != null)
        getList.add(file);
    }

    if (!getList.isEmpty()) {
      mediaLoader.receiveListOfMediaFiles(getList);
      mediaLoader.playReceivedList();
    }
  }

  public void showRecentMedia() throws SQLException {
    LSong.clear();
    List<SongMetadata> listMedia = songDAO.getAllMedia();

    List<SongMetadata> tempList = new ArrayList<>();
    // final int[] count = {0};

    for (SongMetadata song : listMedia) {
      File filePath = new File(song.getPathname());
      if (filePath != null) {
        Media media = new Media(filePath.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        String title = filePath.getName();
        String formatDuration = song.getDuration();

        SongMetadata getIndex = new SongMetadata(title, null, null, formatDuration, 0, filePath.getAbsolutePath());
        getIndex.setLastDayOpened(song.getLastDayOpened());
        tempList.add(getIndex);
      }
    }

    LSong.clear();
    LSong.addAll(tempList);
    recentMediaTable.setItems(LSong);

    allMediaTitleColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));
    allMediaLastDateOpenedColumn.setCellValueFactory(
        cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLastDayOpened()));
    // allMediaDurationColumn.setCellValueFactory(
    // cellData -> new
    // javafx.beans.property.SimpleStringProperty(cellData.getValue().getDuration()));
  }

  @FXML
  public void clearFile(ActionEvent evt) throws SQLException {
    ObservableList<SongMetadata> selectedItems = selectionModel.getSelectedItems(); // Selected Item
    if (selectedItems.isEmpty())
      return;

    for (SongMetadata getPath : selectedItems) {
      if (getPath.getPathname().equals(getCurrentFilePlaying.getAbsolutePath())) {
        // mediaLoader.deleteCurrentFileInLSong(getPath);

        mediaLoader.removeDeletedMediaFileFromLayout();
      }

      // LSong.remove(getPath);
      songDAO.deleteMedia(getPath.getPathname());
    }
    Platform.runLater(() -> {
      LSong.removeAll(selectedItems);
      recentMediaTable.refresh();
    });

    showRecentMedia();

  }

  private void playSingleMedia(SongMetadata selectedItem) {
    // stopPlaying();
    mediaLoader.pauseCurrentMediaFile();
    getCurrentMediaPlaying = selectedItem;
    // mediaLoader.playNewMediaFile(new File(selectedItem.getPathname()));

    List<File> selectedFileList = new ArrayList<File>();
    selectedFileList.add(new File(selectedItem.getPathname()));
    mediaLoader.receiveListOfMediaFiles(selectedFileList);
    mediaLoader.playReceivedList();
  }

  // private void stopPlaying() {
  // if(mediaPlayer != null)
  // {
  // mediaPlayer.stop();
  // }
  // getCurrentMediaPlaying = null;
  //
  // }

}
