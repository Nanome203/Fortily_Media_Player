package scenes.playList;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class PlayListController implements Initializable {

    @FXML
    private ComboBox<String> ComboList;

    @FXML
    private Button addPlayListButton;
    
    @FXML
    private TilePane playlistContainer;
    
    @FXML
    private GridPane playlistGrid;

    private ObservableList<String> playlists = FXCollections.observableArrayList();
    private int playlistCount = 0; 

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ObservableList<String> listSort = FXCollections.observableArrayList("Sort: A-Z", "Sort: Date modified");
        ComboList.setItems(listSort);
        ComboList.setValue("Sort by: A-Z");

        addPlayListButton.setOnAction(e -> showAddPlaylistDialog());
    }
    
    @FXML
    private void showAddPlaylistDialog() {
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Playlist");
        dialog.setHeaderText(null);
        dialog.setContentText("Playlist name:");
        
        dialog.showAndWait().ifPresent(name -> addPlaylist(name));
    }
    
    @FXML
    private void addPlaylist(String name) {
    	 VBox card = new VBox();
         card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-spacing: 10; -fx-alignment: center;");

         ImageView imageView = new ImageView(new Image("/assets/images/Fortily.png"));
         imageView.setFitWidth(100);
         imageView.setFitHeight(100);
         Label titleLabel = new Label(name);
         titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
         Label itemCountLabel = new Label("0 item");
         itemCountLabel.setStyle("-fx-font-size: 12px;");

         card.getChildren().addAll(imageView, titleLabel, itemCountLabel);
         playlistContainer.getChildren().add(card);
    }
//    @FXML
//    private void updatePlaylistView() {
//        playlistContainer.getChildren().clear();  // Clear existing cards
//        for (Playlist playlist : playlists) {
//            // Create a new FXML loader for playlistCell.fxml
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("playlistCell.fxml"));
//            AnchorPane playlistCell;
//            try {
//                playlistCell = loader.load();
//                PlaylistCellController cellController = loader.getController();
//                cellController.setPlaylist(playlist); // Set playlist data in the cell controller
//                playlistContainer.getChildren().add(playlistCell);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
