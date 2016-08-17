package fun.personalUse.mainAlbumTunesApp;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AlbumTunesMain extends Application{

	public void start(Stage stage) {
			Parent parent = null;
			try {
				parent = FXMLLoader.load(getClass().getResource("/resources/AlbumPlayerGUI.fxml"));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			Scene scene = new Scene(parent);

			// window title
			stage.setTitle("Album Player");
			stage.setScene(scene);
			stage.show();
			
		}
	
	@Override
	public void stop(){
	    System.out.println("Stage is closing");
	    // Save file
	}

		/**
		 * creates application in memory
		 * 
		 * @param args
		 */
			public static void main(String[] args) {
				launch(args);

			}
}
