package scenes.playList;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import dao.PlayListDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import model.PlayListItem;
import scenes.layout.LayoutController;

public class PlayListController_2 implements Initializable{
	
	private PlayListDAO playListDAO = new PlayListDAO();

	@FXML
    private Button addPlayList;
	
    @FXML
    private GridPane grid;

    @FXML
    private ScrollPane scroll;
    
    private List<PlayListItem> playListItems = new ArrayList<>();
	
    @FXML
    private void showAddPlaylistDialog() throws SQLException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Playlist");
        dialog.setHeaderText(null);
        dialog.setContentText("Playlist name:");
        dialog.showAndWait().ifPresent(name -> addPlaylist(name));
        showPlayListItem();
    }
    
    
    private void addPlaylist(String name) {
    	PlayListItem playListItem = new PlayListItem();
    	playListItem.setPlayListName(name);
    	playListItem.setImgSrc("/scenes/assets/images/musical-symbol-icon.jpg");
    	playListItem.setNumOfItems(0);
    	playListItems.add(playListItem);
    	try {
			playListDAO.addPlayList(name);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void showPlayListItem() throws SQLException {
    	playListItems = playListDAO.getAllPlayList();
    	grid.getChildren().clear();
    	int column = 0;
    	int row = 0;
    	try {
	    	for(int i = 0; i < playListItems.size(); i++) {
	    		FXMLLoader fxmlLoader = new FXMLLoader();
	    		fxmlLoader.setLocation(getClass().getResource("/scenes/playList/playlist-item.fxml"));
	    		
	    		AnchorPane anchorPane = fxmlLoader.load();
	    		
	    		PlayListItemController playListItemController = fxmlLoader.getController(); 
	    		playListItemController.setData(playListItems.get(i));
	    		if(column > 3) {
	    			column = 0;
	    			row++;
	    		}
	    		grid.add(anchorPane, column++, row);
	    		GridPane.setMargin(anchorPane,new Insets(10));
	    	}
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	try {
			showPlayListItem();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
