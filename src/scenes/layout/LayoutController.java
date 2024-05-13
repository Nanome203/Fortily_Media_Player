package scenes.layout;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LayoutController {

	  @FXML
	    private HBox imageSongContainer;

	    @FXML
	    private BorderPane mainContainer;

	    @FXML
	    private Button sideBarFav;

	    @FXML
	    private Button sideBarHome;

	    @FXML
	    private Button sideBarPlaylist;

	    @FXML
	    private VBox sidebarNavigator;

	    @FXML
	    private Button siderBarMusicLib;

	    @FXML
	    private Button siderBarVideoLib;
	    
	    @FXML
	    private VBox sideBarContainer;

		
	    public void selectItem(ActionEvent event) {
	        for (Node node : sidebarNavigator.getChildren()) {
	                ((Button)node).getStyleClass().remove("active");
	        }

	        // Add 'selected' class to the clicked item
	        ((Button)event.getSource()).getStyleClass().add("active");
	    }

}
