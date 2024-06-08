package scenes.recentMedia;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import dao.SongDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private TableColumn<SongMetadata, String> allMediaDurationColumn;
	
	@FXML
	private Button btnClearFile;
	
	private SongMetadata getCurrentMediaPlaying;
	
	private MediaPlayer mediaPlayer;
	
	private ObservableList<SongMetadata> LSong = FXCollections.observableArrayList();
	
	
	public String convertDurationMillis(Integer getDurationInMillis){
	    int getDurationMillis = getDurationInMillis;

	    String convertHours = String.format("%02d", TimeUnit.MILLISECONDS.toHours(getDurationMillis)); 
	    String convertMinutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(getDurationMillis) -
	            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(getDurationMillis))); 
	    String convertSeconds = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(getDurationMillis) -
	            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getDurationMillis)));

	    String getDuration = convertHours + ":" + convertMinutes + ":" + convertSeconds;

	    return getDuration;

	}
	
	public void showRecentMedia() throws SQLException {
		List<File> listFilePath‎ = songDAO.getAllMedia();
		System.out.println(listFilePath‎);
		for(File filePath : listFilePath‎) {
			if(filePath != null)
			{
				Media media = new Media(filePath.toURI().toString());
				
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				
				mediaPlayer.setOnReady(() -> {
					String title = media.getMetadata().get("title") != null ? media.getMetadata().get("title").toString() :
						filePath.getName();
					
					double duration = media.getDuration().toMillis();
					String formatDuration = convertDurationMillis((int) duration);
					
					SongMetadata getIndex = new SongMetadata(title, null, formatDuration, null, 0);
					getIndex.setFilePath(filePath.getAbsolutePath());
				
				});
			}
		}
	}
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		selectionModel = recentMediaTable.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
		mediaLoader = MediaLoader.getMediaLoader();
		
		//Double click event to play file
		recentMediaTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent evt) {
						if(evt.getButton().equals(MouseButton.PRIMARY)) {
							if(evt.getClickCount() == 2) {
								playSingleMedia(selectionModel.getSelectedItem());
							}
						}
					}				
		});
		
		try {
			showRecentMedia();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void playSingleMedia(SongMetadata selectedItem) {
		stopPlaying();
		getCurrentMediaPlaying = selectedItem;
		mediaLoader.playNewMediaFile(new File(selectedItem.getFilePath()));
	}

	private void stopPlaying() {
		if(mediaPlayer != null)
		{
			mediaPlayer.stop();
		}
		getCurrentMediaPlaying = null;
		
	}	
	
}
