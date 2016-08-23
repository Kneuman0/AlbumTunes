package fun.personalUse.mainAlbumTunesApp;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import fun.personalUse.customExceptions.NoPlaylistsFoundException;
import fun.personalUse.dataModel.CurrentSongBean;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;
import fun.personalUse.utilities.XMLMediaPlayerHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

public class AlbumTunesController {

    @FXML
    private Label 
    	userWarningLabel,
    	digLabel;

    @FXML
    private Button 
    	pauseButton,
    	startButton,
    	resumeButton,
    	nextButton,
    	restartAlbumButton,
    	addPlaylistButton,
    	addSongsToPlaylistButton,
    	mineMP3sButton;
    
    @FXML
    private TextField 
    	pathTextField,
    	searchBox;
    
    @FXML
    private AnchorPane 
    	MediaPlayerAnchorPane,
    	AlbumPlayerAnchorPane;
    
    @FXML
    private TableColumn<FileBean, String> 
    	durationCol,
    	artistCol,
    	songNameCol,
    	albumCol;

    @FXML
    private TableView<FileBean> metaDataTable;
    
    @FXML
    private TableView<PlaylistBean> playlistTable;

    @FXML
    private TableColumn<PlaylistBean, String> playlistColumn;
    
    @FXML
    private CheckBox shuffleBox;
     

	MediaPlayer currentPlayer;
	ObservableList<FileBean> songsInAlbum;
	ArrayList<Integer> songIndexes;
	private int songNumber;
	XMLMediaPlayerHelper musicHandler;

	public void initialize() {
		setBackgroundImage();
		// index used to keep track of next song
		songNumber = 0;
		
		// loads all the playlist and songs from the XML file and 
		// displays them in TableView objects
		initalizeTableView();
		songsInAlbum = metaDataTable.getItems();
		songIndexes = new ArrayList<>();
		
		// Highligts the first index in the playlist tableview
		Platform.runLater(new SelectIdexOnTable(playlistTable, 0));
		
		// Highligts the first index in the song tableview
		Platform.runLater(new SelectIdexOnTable(metaDataTable, 0));
		
	}

	public void startButtonListener() throws FileNotFoundException {
		songsInAlbum = metaDataTable.getItems();
		startAlbum(metaDataTable.getSelectionModel().getSelectedIndex());
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
		// restarts the album from the first index
		startAlbum(0);
		songNumber = 0;
	}
		
	public void addPlaylistButtonListener(){
		TextInputDialog dialog = new TextInputDialog();
		dialog.setHeaderText("Enter Playlist Name");
		dialog.setTitle("New Playlist");
		dialog.showAndWait();
		
		musicHandler.addPlaylist(dialog.getEditor().getText());
	}
	
	public void addSongsToPlaylistButtonListener(){
		ObservableList<FileBean> songsToBeAdded = 
				metaDataTable.getSelectionModel().getSelectedItems();
		PlaylistBean playlist = playlistTable.getSelectionModel().getSelectedItem();
		playlist.getSongsInPlaylist().addAll(songsToBeAdded);
	}
	
	public void displayPlaylistButtonListener(MouseEvent event){
		if(event.getClickCount() == 2){
			PlaylistBean playlist = playlistTable.getSelectionModel().getSelectedItem();
			metaDataTable.setItems(playlist.getSongsInPlaylist());
			musicHandler.setCurrentPlaylist(playlist.getSongsInPlaylist());
		}
	}
	
	public void playSelectedSong(MouseEvent event){
		 if (event.getClickCount() == 2) {
			 songsInAlbum = metaDataTable.getItems();
	         startAlbum(metaDataTable.getSelectionModel().getSelectedIndex());
	            
	        }
	}
	
	public void onTableSort(){
		
	}
	
	public void onPlaylistSearch(){
		String search = searchBox.getText();
		if(search.equals("")){
			metaDataTable.setItems(musicHandler.getCurrentPlaylist());
		}else{
			metaDataTable.setItems(
					musicHandler.getsubListFromSearchResult(
							search, metaDataTable.getItems()));
		}
	}
	
	public void mineMP3sButtonListener(){
		metaDataTable.refresh();
		String title = "Please make a selection";
		String header = "mp3 mining options...";
		String content = "Do you want to import a single mp3\n"
				+ "or a folder containing many mp3s?";
		findNewSongs(title, header, content);
	}
	
	public void onDeleteSong(KeyEvent event){
		if(event.getCode().equals(KeyCode.DELETE)
				|| event.getCode().equals(KeyCode.BACK_SPACE)){
			ObservableList<Integer> songIndicesToDelete = 
					metaDataTable.getSelectionModel().getSelectedIndices();
			
			for(Integer index : songIndicesToDelete){
				System.out.println(musicHandler.getCurrentPlaylist().remove(index.intValue()));
			}
			
		}
	}
	
	public void onDeletePlaylist(KeyEvent event){
		if(event.getCode().equals(KeyCode.DELETE)
				|| event.getCode().equals(KeyCode.BACK_SPACE)){
			Alert deleteOK = new Alert(AlertType.CONFIRMATION);
			deleteOK.setHeaderText(null);
			deleteOK.setContentText("Are you sure you want to delete this playlist?");
			deleteOK.showAndWait();
			
			if(deleteOK.getResult() == ButtonType.OK){
				int index = playlistTable.getSelectionModel().getSelectedIndex();
				musicHandler.getPlaylists().remove(index);
			}
			
			
		}
	}
	
	
	/**
	 * Attempts to find existing xml files in two parent directories removed from the location
	 * of this Media player's jar file.
	 * 
	 * If no xml's are found, the user will be prompted to locate a directory containing mp3s
	 * so that the program can search for them.
	 */
	private void initalizeTableView(){
		musicHandler = new XMLMediaPlayerHelper();
		songNameCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("songName"));
		albumCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("album"));
		artistCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("artist"));
		durationCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("duration"));
		playlistColumn.setCellValueFactory(new PropertyValueFactory<PlaylistBean, String>("playlistName"));
		
		// does not halt program during startup
		Platform.runLater(new LoadAllMusicFiles(metaDataTable));
		
		
		// need to make similar methods for playlist loading
		
		metaDataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	/**
	 * The method will display an Alert box prompting the user to locate a 
	 * song or directory that contains mp3s
	 * 
	 * The parameters passed is the text the user will see in the Alert box.
	 * The Alert box will come with 3 new buttons: 1)Single mp3, 2)Folder of mp3s
	 * and 3)Cancel. If the user selects the first button they will be
	 * presented with a FileChooser display to select a song. If they press
	 * the second button, the user will be prompted with a DirectoryChooser
	 * display. The third button displays nothing and closes the Alert box.
	 * 
	 * The following outlines where each parameter will be displayed in the
	 * Alert box
	 * 
	 * title: very top of the box in the same latitude as the close button.
	 * header: inside the Alert box at the top.
	 * content: in the middle of the box. This is the best place to explain
	 * the button options to the user.
	 * @param title
	 * @param header
	 * @param content
	 */
	private void findNewSongs(String title, String header, String content){
		Alert importType = new Alert(AlertType.CONFIRMATION);
		importType.setTitle(title);
		importType.setHeaderText(header);
		importType.setContentText(content);
		
		ButtonType singleMp3 = new ButtonType("Single mp3");
		ButtonType folderOfmp3s = new ButtonType("Folder Of mp3s");
		ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		importType.getButtonTypes().setAll(singleMp3, folderOfmp3s, cancel);
		
		Optional<ButtonType> result = importType.showAndWait();
		if(result.get() == singleMp3){
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Location of mp3s");
//			fileChooser.setSelectedExtensionFilter(
//					new ExtensionFilter("Audio Files", "*.mp3"));
			
			File selectedFile = fileChooser.showOpenDialog(resumeButton.getScene().getWindow());
			
			if(selectedFile == null){
				return;
			}
			Thread findSongs = new Thread(new DigSongs(selectedFile.getAbsolutePath()));
			findSongs.start();
			
		}else if(result.get() == folderOfmp3s){
			DirectoryChooser fileChooser = new DirectoryChooser();
			fileChooser.setTitle("Location to mine for mp3s");
			
			File selectedFile = fileChooser.showDialog(resumeButton.getScene().getWindow());
			
			if(selectedFile == null){
				return;
			}
			Thread findSongs = new Thread(new DigSongs(selectedFile.getAbsolutePath()));
			findSongs.start();
			
		}else{
			return;
		}
	}
	
	/*
	 * initiates both the playing or replaying of the album
	 */
	private void startAlbum(int startIndex){
		System.out.println("Songs in album " + songsInAlbum.size());
		songNumber = 0;
		songIndexes.removeAll(songIndexes);
		
		/*
		 * populates and arraylist of indices that will be used
		 * to choose songs to play. Indices start where user selected the song
		 */
		for(int i = startIndex; i < songsInAlbum.size(); i++){
			songIndexes.add(i);
		}
		
		/*
		 * Stops playing the current playing if one exists
		 */
		if(currentPlayer != null){
			currentPlayer.stop();
			currentPlayer = null;
		}
		
		/*
		 *  Assigns the current directory path so that the album can
		 *  only be played from the restart album button
		 */
//		albumDirectoryPath = pathTextField.getText();
		
		
//		songsInAlbum = gatherAllMediaFiles();
		if (shuffleBox.isSelected()) {
			Collections.shuffle(songIndexes);
		}
		
		// index 0 will be removed each time a song is played
		// so there will be no repeats
		playASong(songsInAlbum.get(songIndexes.get(0)));
//		songNumber++;

	}

	/**
	 * increments songNumber by 1. 
	 * Creates a new media player by using the FileBean passed in and
	 * stores that media player in the currentPlayer variable.
	 * 
	 * @param songFile
	 * @return
	 */
	private CurrentSongBean playASong(FileBean songFile) {
		
		
		Media song = new Media(String.format("file:///%s", songFile.getUrl()));
		currentPlayer = new MediaPlayer(song);
		
		EndOfMediaEventHandler stopEvent = new EndOfMediaEventHandler();
		currentPlayer.setOnEndOfMedia(stopEvent);
		currentPlayer.setOnStopped(stopEvent);
		
		/*
		 * The media takes some time to load so you need to resister 
		 * a listener with the MediaPlayer object to commence playing
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
		AlbumPlayerAnchorPane.setBackground(background);
		MediaPlayerAnchorPane.setBackground(background);
	}
	
	protected SaveEverything saveChanges(){
		return new SaveEverything();
	}
	
	private class OnMediaReadyEvent implements Runnable{
		private FileBean songFile;
		
		public OnMediaReadyEvent(FileBean songFile) {
			this.songFile = songFile;
		}
		
		@Override
		public void run() {
			// increments index variable 'songNumber' each time playASong() is called
			songNumber++;
			double duration = currentPlayer.getTotalDuration().toMillis() / 60_000.0;
			String songInfo = String.format(
					"Now Playing: %s\nArtist: %s\nAlbum: %s\nDuration: %.2f", 
					songFile.getSongName(), songFile.getArtist(), songFile.getAlbum(),
					duration);
			songFile.setDuration(duration);
			Platform.runLater(new UpdateLabel(userWarningLabel, songInfo));
			currentPlayer.play();
		}
		
	}

	private class EndOfMediaEventHandler implements Runnable {

		@Override
		public void run() {
			
			
			if (songNumber < songsInAlbum.size()) {
				int nextIndex = songIndexes.get(songNumber);
				FileBean songPath = songsInAlbum.get(nextIndex);
				playASong(songPath);
						
			} else {
				Platform.runLater(new UpdateLabel(userWarningLabel, "Album Finished"));
			}

		}

	}
	
	public class DigSongs implements Runnable{
		String path;
		
		public DigSongs(String path) {
			this.path = path;
		}
		@Override
		public void run() {
			Platform.runLater(new UpdateLabel(digLabel, "loading..."));
//			XMLMediaPlayerHelper musicHandler = new XMLMediaPlayerHelper(
//					"C:/Users/Karottop/git/AlbumTunes/SimplePlayer/infoDirectory/playlists.xml");
			try {
				musicHandler.findNewSongs(path);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ObservableList<FileBean> songArray = musicHandler.getMainPlaylist().getSongsInPlaylist();
			Platform.runLater(new UpdateLabel(digLabel, "complete: " + songArray.size()));
		}
		
	}
	
	public class LoadAllMusicFiles implements Runnable{
		
		private TableView<FileBean> tableView;
		
		public LoadAllMusicFiles(TableView<FileBean> tableView) {
			this.tableView = tableView;
		}	
		
		@Override
		public void run() {
			try {
				musicHandler.loadAllPlaylists();
				tableView.setItems(musicHandler.getMainPlaylist().getSongsInPlaylist());
				playlistTable.setItems(musicHandler.getPlaylists());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (NoPlaylistsFoundException e) {
				String title = "Mine for mp3s";
				String header = "No playlists were found.\n"
						+ "These are your mp3 mining options...";
				String content = "Do you want to import a single mp3\n"
						+ "or a folder containing many mp3s?\n\n"
						+ "**Note For large volumes of songs this may take a while.\n"
						+ "Grab some coffee or something..**";
				findNewSongs(title, header, content);
				// need to handle file not found exception in new thread
//				Platform.runLater(new UpdateLabel(digLabel, "loading..."));
				tableView.setItems(musicHandler.getMainPlaylist().getSongsInPlaylist());
				playlistTable.setItems(musicHandler.getPlaylists());
				Platform.runLater(new SelectIdexOnTable(playlistTable, 0));
				tableView.getSelectionModel().selectFirst();
								
			}
			
		}
		
	}
	
	public class ExportPlaylistsToXML implements Runnable{

		@Override
		public void run() {
			musicHandler.exportPlaylistsToXML();
			
		}
		
	}
	
	private class SelectIdexOnTable implements Runnable{
		
		private TableView<?> table;
		private int index;
		
		public SelectIdexOnTable(TableView<?> table, int index) {
			this.table = table;
			this.index = index;					
		}

		@Override
		public void run() {
			this.table.requestFocus();
			this.table.getSelectionModel().selectFirst();
			this.table.getFocusModel().focus(index);
		}
		
	}
	
	private class UpdateLabel implements Runnable{

		Label label;
		String textToUpdate;
		public UpdateLabel(Label label, String textToUpdate) {
			this.label = label;
			this.textToUpdate = textToUpdate;
		}
		
		@Override
		public void run() {
			label.setText(textToUpdate);			
		}
		
	}
	
	private class SaveEverything implements EventHandler<WindowEvent>{

		@Override
		public void handle(WindowEvent event) {
			Alert alertBox = new Alert(AlertType.NONE);
			alertBox.setTitle("Saving Content");
			alertBox.setHeaderText("Hang tight while I save your preferences");
			alertBox.setContentText("Saving...");
			alertBox.show();
			musicHandler.exportPlaylistsToXML();
			event.consume();
			Platform.exit();
		}
		
	}
	

}
