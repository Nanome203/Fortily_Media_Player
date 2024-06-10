package scenes.playList;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.PlayListItem;
import scenes.layout.LayoutController;

public class PlayListItemController implements Initializable  {
	
	@FXML
    private AnchorPane rootPane;

	@FXML
    private ImageView img;

    @FXML
    private Label numberOfItems;

    @FXML
    private Label playListName;

    private PlayListItem playListItem;
    
    private LayoutController layoutController;
    
	public void setData(PlayListItem i) {
		this.playListItem = i;
		this.playListName.setText(playListItem.getPlayListName());
		this.numberOfItems.setText(String.valueOf(playListItem.getNumOfItems()));
		Image image = new Image(getClass().getResourceAsStream("/assets/images/musical-symbol-icon.jpg"));
		img.setImage(image);
	}
	
	
	
	public void setLayoutController(LayoutController l) {
    	this.layoutController = l;
    	
    }
	
	public LayoutController getLayoutController() {
		return this.layoutController;
	}

	@FXML
	private void showPlaylistDetails() throws IOException {
		
		System.out.println("showPlaylistDetails called. layoutController: " + (layoutController == null));
	    System.out.println(1);

		 
				
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(getClass().getResource("/scenes/playList/playlist-details.fxml"));
	            AnchorPane detailsPane = loader.load();
	            AnchorPane scene = new AnchorPane(detailsPane);
	            PlayListDetailsController controller = loader.getController();        
	            controller.setPlaylist(playListItem);
//	            layoutController.getMainContainer().setCenter(scene);
//	            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
//	            window.setScene(scene);
//			    getLayoutController().changePlayListSceneToDetails(playListItem);
//	            layoutController.initialize(null, null);
	            System.out.println(1);
	            
	        
	}



	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
//		 layoutController = LayoutController.getInstance();
//		 System.out.println(layoutController.getMainContainer()==null);
//		showPlaylistDetails();
		
	}



	


	

	
	
    
}
