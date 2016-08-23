package fun.personalUse.mainAlbumTunesApp;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AlbumTunesMain extends Application{

	public void start(Stage stage) {
		FXMLLoader loader = null;
			Parent parent = null;
			try {
				loader = new FXMLLoader(getClass().getResource("/resources/AlbumPlayerGUI.fxml"));
				parent = (Parent)loader.load();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			AlbumTunesController controller = (AlbumTunesController)loader.getController();
			
			Scene scene = new Scene(parent);

			// window title
			stage.setTitle("Album Player");
			stage.setScene(scene);
			stage.show();
			
			scene.getWindow().setOnCloseRequest(controller.saveChanges());
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
