package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class EntryPoint extends Application{
	  @Override
	    public void start(Stage primaryStage) throws Exception{
	        Parent root = FXMLLoader.load(getClass().getResource("/scenes/mainScene/TestScene.fxml"));
	        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/images/Fortily.png")));
	        primaryStage.setTitle("Fortily");
	        primaryStage.setScene(new Scene(root));
	        primaryStage.show();
	    }


	    public static void main(String[] args) {
	        launch(args);
	    }
}
