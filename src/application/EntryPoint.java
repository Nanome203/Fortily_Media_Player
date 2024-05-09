package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EntryPoint extends Application{
	
	  @Override
	    public void start(Stage primaryStage) throws Exception{
	        Parent root = FXMLLoader.load(getClass().getResource("/scenes/layout/Layout.fxml"));
	        Parent splashScreen = FXMLLoader.load(getClass().getResource("/scenes/splashScreen/SplashScreen.fxml"));
	        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/images/Fortily.png")));
	        primaryStage.setTitle("Fortily");
	        primaryStage.setScene(new Scene(splashScreen));
	        primaryStage.setWidth(1232);
	        primaryStage.setHeight(915);
	        primaryStage.show();
	        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.7), event -> {
	            primaryStage.setScene(new Scene(root));
	        }));
	        timeline.play();
	    }


	    public static void main(String[] args) {
	        launch(args);
	    }
}
