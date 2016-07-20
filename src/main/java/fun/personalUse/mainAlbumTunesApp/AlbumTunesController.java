package fun.personalUse.mainAlbumTunesApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlbumTunesController {

	@FXML
	private Button startButton;

	@FXML
	private TextField pathTextField;

	@FXML
	private Button nextButton;

	@FXML
	private Label userWarningLabel;

	@FXML
	private Button pauseButton;

	@FXML
	private CheckBox shuffleBox;

	@FXML
	private AnchorPane anchorPane;
	

    @FXML
    private Button restartAlbumButton;

	MediaPlayer currentPlayer;
	ArrayList<FileBean> songsInAlbum;
	private int songNumber;
	private String albumDirectoryPath;

	public void initialize() {
		setBackgroundImage();
		songNumber = 0;
		albumDirectoryPath = "";
	}

	public void startButtonListener() throws FileNotFoundException {
		if (!albumDirectoryPath.equals(pathTextField.getText())) {
			startAlbum();
		}

	}

	public void nextSongButtonListener() {
		currentPlayer.stop();
	}

	public void resumeButtonListener() {
		currentPlayer.play();
	}

	public void pauseButtonListener() {
		currentPlayer.pause();
	}
	
	public void restartAlbumButtonListener(){
		// reset album directory path
		albumDirectoryPath = "";
		startAlbum();
	}
	
	/*
	 * initiates both the playing or replaying of the album
	 */
	private void startAlbum(){
		
		// reset index variable to 0
		songNumber = 0;
		
		/*
		 * Stops playing the current playing if one exists
		 */
		if(currentPlayer != null){
			currentPlayer.stop();
			currentPlayer = null;
		}
		
		/*
		 *  Assigns the current directory path so that album can
		 *  only be played from the restart album button
		 */
		albumDirectoryPath = pathTextField.getText();
		
		
		songsInAlbum = gatherAllMediaFiles();
		if (shuffleBox.isSelected()) {
			Collections.shuffle(songsInAlbum);
		}
		
		/*
		 * While loop makes sure unsupported file types are skipped but does not
		 * stop the flow of the album
		 */
		Object flowMaintainer = null;
		while(flowMaintainer == null){
			
			FileBean songPath = null;
			try {
				songPath = songsInAlbum.get(songNumber);
				flowMaintainer = playASong(songPath);
			} catch (MediaException e) {
				System.out.println("Not a supported File: " + songPath);
				System.out.println(e.getMessage());
				
				/*
				 * if last file is not supported, end the while loop
				 */
			} catch (IndexOutOfBoundsException e){
				userWarningLabel.setText("Album Finished");
				flowMaintainer = new Object();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gathers all files in the directory. Does not filter based on file extension
	 * @return
	 */
	private ArrayList<FileBean> gatherAllMediaFiles() {
		ArrayList<FileBean> musicFiles = new ArrayList<>();

		File directory = new File(pathTextField.getText());
		if (directory.isDirectory() && directory.canRead()) {
			File[] files = directory.listFiles();

			for (int i = 0; i < files.length; i++) {
				try {
					musicFiles.add(new FileBean(files[i]));
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			throw new InvalidUserInputException(directory.getAbsolutePath());
		}

		return musicFiles;

	}

	private CurrentSongBean playASong(FileBean songFile) {
		// increments index variable 'songNumber' each time playASong() is called
		songNumber++;
		
		// %20 encodes spaces into the URL so that there are no illegal
		// characters
		// the '///' are necessary for a URL
		
		Media song = new Media(String.format("file:///%s", songFile.getUrl()));
		currentPlayer = new MediaPlayer(song);
		currentPlayer.setOnEndOfMedia(new EndOfMediaEventHandler());
		currentPlayer.setOnStopped(new EndOfMediaEventHandler());
		
		/*
		 * The media takes some time to load so you need to resister 
		 * a listen with the MediaPlayer object to commence playing
		 * once the status is switched to READY
		 */
		currentPlayer.setOnReady(new OnMediaReadyEvent(songFile));
		System.out.println("Just got created: " + currentPlayer);

		CurrentSongBean currentSong = new CurrentSongBean(currentPlayer
				.getTotalDuration().toMillis(), currentPlayer);

		return currentSong;
	}

	private void setBackgroundImage() {
		Image logo = new Image(
				AlbumTunesController.class
						.getResourceAsStream("/resources/MusicBackground.jpg"));
		BackgroundSize logoSize = new BackgroundSize(600, 400, false, false,
				true, true);
		BackgroundImage image = new BackgroundImage(logo,
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, logoSize);
		Background background = new Background(image);
		anchorPane.setBackground(background);
	}

	public void updateLabelLater(final Label label, final String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				label.setGraphic(null);
				label.setText(message);
			}
		});
	}
	
	private class OnMediaReadyEvent implements Runnable{
		private FileBean songFile;
		
		public OnMediaReadyEvent(FileBean songFile) {
			this.songFile = songFile;
		}
		
		@Override
		public void run() {
			
			String songInfo = String.format(
					"Now Playing: %s\nDuration: %.2f", songFile.getName(), currentPlayer
					.getTotalDuration().toMillis() / 60_000.0);
			System.out.println("Just got called: " + currentPlayer);
			updateLabelLater(userWarningLabel, songInfo);
			
			currentPlayer.play();
//			MediaView movie = new MediaView(currentPlayer);
//			  FXMLLoader loader = new FXMLLoader(
//					    getClass().getResource(
//					      "/resources/MediaPlayerGUI.fxml"
//					    )
//					  );
//
//					  Stage stage = new Stage(StageStyle.DECORATED);
//					 
//					  VideoPlayerController controller = new VideoPlayerController();
//					  try {
//							stage.setScene(
//							    new Scene(
//							      (Pane) loader.load()
//							    )
//							  );
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					  loader.setController(controller);
//					  controller.playMovie();
//
//					  stage.show();
			
		}
		
	}

	private class EndOfMediaEventHandler implements Runnable {

		@Override
		public void run() {
					
			if (songNumber < songsInAlbum.size()) {
				
				/*
				 * While loop makes sure unsupported file types are skipped but does not
				 * stop the flow of the album
				 */
				CurrentSongBean currentSong = null;
				while(currentSong == null){
					
					FileBean songPath = null;
					try {
						System.out.println(songNumber);
						songPath = songsInAlbum.get(songNumber);
						currentSong = playASong(songPath);
						
						updateLabelLater(userWarningLabel, String.format(
								"Now Playing: %s\nDuration: %.2f", songPath.getName(),
								currentSong.getDuration() / 60_000.0));
						
					} catch (MediaException e) {
						System.out.println("Not a supported File:" + songPath);
						
						/*
						 * If last file is not supported, end the while loop
						 */
					} catch (IndexOutOfBoundsException e){
						updateLabelLater(userWarningLabel, "Album Finished");
						currentSong = new CurrentSongBean(0.0, null);
					}

					
				}

			} else {
				updateLabelLater(userWarningLabel, "Album Finished");
			}

		}

	}
	
//	public class VideoPlayerController{
//		
//		@FXML private MediaView mediaView;
//		@FXML public Label label;
//		
//		
//		public void initialize(){
//			label.setText("Can You Read This?");
//		}
//		
//		public void playMovie(){
//			mediaView.setMediaPlayer(currentPlayer);
//		}
//		
//		
//	}


}
