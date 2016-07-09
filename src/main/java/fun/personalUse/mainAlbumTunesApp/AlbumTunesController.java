package fun.personalUse.mainAlbumTunesApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import javafx.application.Platform;
import javafx.fxml.FXML;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

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
	boolean pause;
	ArrayList<FileBean> songsInAlbum;
	private int songNumber;
	private String albumDirectoryPath;

	public void initialize() {
		setBackgroundImage();
		songNumber = 0;
		pause = false;
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
		songNumber = 0;
		albumDirectoryPath = "";
		songsInAlbum = null;
		startAlbum();
	}
	
	private void startAlbum(){
		if(currentPlayer != null){
			currentPlayer.stop();
			currentPlayer = null;
		}
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
		while(flowMaintainer == null)
			try {
				flowMaintainer = playASong(songsInAlbum.get(songNumber));
			} catch (MediaException e) {
				System.out.println("Not a supported File: " + songsInAlbum.get(songNumber - 1));
			} catch (IndexOutOfBoundsException e){
				userWarningLabel.setText("Album Finished");
				flowMaintainer = new Object();
		}
	}

	private ArrayList<FileBean> gatherAllMediaFiles() {
		ArrayList<FileBean> musicFiles = new ArrayList<>();

		File directory = new File(pathTextField.getText());
		if (directory.isDirectory() && directory.canRead()) {
			File[] files = directory.listFiles();

			for (int i = 0; i < files.length; i++) {
				try {
					musicFiles.add(new FileBean(files[i]));
				} catch (FileNotFoundException e) {
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
		songNumber++;
		
		// %20 encodes spaces into the URL so that there are no illegal
		// characters
		// the '///' are necessary for a URL
		Media song = new Media(String.format("file:///%s", songFile
				.getLocation().replaceAll("\\s", "%20")));
		currentPlayer = new MediaPlayer(song);
		currentPlayer.setOnEndOfMedia(new EndOfMediaEventHandler());
		currentPlayer.setOnStopped(new EndOfMediaEventHandler());
		currentPlayer.setOnPaused(new PauseEventHandler());
		
		/*
		 * The media takes some time to load so you need to resister 
		 * a listen with the MediaPlayer object to commence playing
		 * once the status is switched to READY
		 */
		currentPlayer.setOnReady(new OnMediaReadyEvent(songFile));
		

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
			currentPlayer.getTotalDuration().toMillis();
			updateLabelLater(userWarningLabel, songInfo);
			currentPlayer.play();
			
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
					FileBean songPath = songsInAlbum.get(songNumber);
					
					try {
						currentSong = playASong(songPath);
						
						updateLabelLater(userWarningLabel, String.format(
								"Now Playing: %s\nDuration: %.2f", songPath.getName(),
								currentSong.getDuration() / 60_000.0));
						
					} catch (MediaException e) {
						System.out.println("Not a supported File:" + songPath);
						
					} catch (IndexOutOfBoundsException e){
						userWarningLabel.setText("Album Finished");
						currentSong = new CurrentSongBean(0.0, null);
					}

					
				}

			} else {
				updateLabelLater(userWarningLabel, "Album Finished");
			}

		}

	}


	private class PauseEventHandler implements Runnable {

		@Override
		public void run() {
			pause = true;

		}

	}


}
