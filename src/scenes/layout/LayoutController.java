package scenes.layout;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LayoutController implements Initializable {

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
	    
	    @FXML
	    private Slider progressSlider;
	    
	    @FXML
	    private Slider volumeSlider;
	    
	    @FXML
	    private Button settings;
	    
	    

		
	    public void selectItem(ActionEvent event) {
	        for (Node node : sidebarNavigator.getChildren()) {
	                ((Button)node).getStyleClass().remove("active");
	        }
	        
	        settings.getStyleClass().remove("active");

	        // Add 'selected' class to the clicked item
	        ((Button)event.getSource()).getStyleClass().add("active");
	    }


		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			progressSlider.valueProperty().addListener(new ChangeListener<Number>() {
				@Override
	            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
					StackPane trackPane = (StackPane) progressSlider.lookup(".track");
	                String style = String.format("-fx-background-color: linear-gradient(to right, gray %d%%, white %d%%);",
	                        new_val.intValue(), new_val.intValue());
	                trackPane.setStyle(style);
	            }
	        });
			
			volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
				@Override
	            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
					StackPane trackPane = (StackPane) volumeSlider.lookup(".track");
	                String style = String.format("-fx-background-color: linear-gradient(to right, gray %d%%, white %d%%);",
	                        new_val.intValue(), new_val.intValue());
	                trackPane.setStyle(style);
	            }
	        });
			
		}

}
