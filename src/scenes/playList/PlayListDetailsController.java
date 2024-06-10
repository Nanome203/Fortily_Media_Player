package scenes.playList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.PlayListItem;

public class PlayListDetailsController {
	
	
	private PlayListItem playListItem;
	@FXML
    private Label playlistNameLabel;

    @FXML
    private Label numOfItemsLabel;

    @FXML
    private ImageView imgSrcView;

    public void setPlaylist(PlayListItem _playListItem) {
    	this.playListItem = _playListItem;
        playlistNameLabel.setText(playListItem.getPlayListName());
        numOfItemsLabel.setText(Integer.toString(playListItem.getNumOfItems()));
        Image image = new Image(getClass().getResourceAsStream("/assets/images/musical-symbol-icon.jpg"));
        imgSrcView.setImage(image);
    }
}
