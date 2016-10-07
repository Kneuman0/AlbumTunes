package fun.personalUse.mainAlbumTunesApp;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fun.personalUse.controllers.MediaViewController;
import fun.personalUse.customExceptions.NoPlaylistsFoundException;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;
import fun.personalUse.utilities.XMLMediaPlayerHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
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
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class AlbumTunesController {

    @FXML
    private Label 
    	mediaDescLeft,
    	mediaDescRight,
    	digLabel,
    	currentSongTime;

    @FXML
    private Button 
    	playBackButton,
    	nextButton,
    	restartAlbumButton,
    	addPlaylistButton,
    	addSongsToPlaylistButton,
    	mineMP3sButton,
    	launchButton;
        
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
    private TextField searchBox;

    @FXML
    private TableView<FileBean> metaDataTable;
    
    @FXML
    private TableView<PlaylistBean> playlistTable;

    @FXML
    private TableColumn<PlaylistBean, String> playlistColumn;
    
    @FXML
    private CheckBox shuffleBox;
    
    @FXML
    private ScrollBar songScrollBar;
     

	private MediaPlayer currentPlayer;
	private List<FileBean> songsInAlbum;
	private ArrayList<Integer> songIndexes;
	
	/*
	 * is incremented each time a song is played
	 */
	private int songNumber;
	private XMLMediaPlayerHelper musicHandler;
	private Stage currentStage;

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
		Platform.runLater(new SelectIndexOnTable(playlistTable, 0));
		
		// Highligts the first index in the song tableview
		Platform.runLater(new SelectIndexOnTable(metaDataTable, 0));
		
		initalizeScrollBar();		
	}
	
	public void executePlayback(){
		System.out.println("|" + playBackButton.getText() + "|");
		/*
		 * Media player just opened and nothing has been played yet or album has finished
		 */
		if(playBackButton.getText().equals("Play") && currentPlayer == null){
			playBackButton.setText("Pause");
			songsInAlbum = metaDataTable.getItems();
			startAlbum(metaDataTable.getSelectionModel().getSelectedIndex(), true);
		/*
		 * 	Song is pause, user wants to resume play
		 */
		} else if(playBackButton.getText().equals("Play") && currentPlayer != null){
			playBackButton.setText("Pause");
			currentPlayer.play();
		
		}else if(playBackButton.getText().equals("Pause")){
		
			playBackButton.setText("Play");
			currentPlayer.pause();
		}
	}

	public void nextSongButtonListener() {
		if(currentPlayer != null){
			currentPlayer.stop();
			Platform.runLater(new EndOfMediaEventHandler());
		}
		
	}
	
	public void backButtonListener(){
		if (currentPlayer == null){
			return;
		}
		
		System.out.println("|" + currentSongTime.getText() + "|");
		if(!currentSongTime.getText().equals("0.00")){
			songScrollBar.setValue(0.0);
		}else if(currentSongTime.getText().equals("0.00") && currentPlayer != null
				&& songNumber >= 2){
			songNumber -= 2;
			currentPlayer.stop();
			Platform.runLater(new EndOfMediaEventHandler());
		}else{
			// do nothing because player has not been started
			return;
		}
	}
	
	public void restartAlbumButtonListener(){
		// restarts the album from the first index
//		Platform.runLater(new SelectIndexOnTable(metaDataTable, 0));
		metaDataTable.requestFocus();
		metaDataTable.getSelectionModel().clearAndSelect(0);
		metaDataTable.getSelectionModel().focus(0);
		
		// play new song even if paused. If paused, change to label
		if(playBackButton.getText().equals("Play")){
			playBackButton.setText("Pause");
		}
		songNumber = 0;
		startAlbum(0, true);
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
			digLabel.setText("Songs: " + playlist.getSongsInPlaylist().size());
		}
	}
	
	public void playSelectedSong(MouseEvent event){
		 if (event.getClickCount() == 2) {
			 playBackButton.setText("Pause");
			 startAlbum(metaDataTable.getSelectionModel().getSelectedIndex(), true);
	            
	        }
	}
	
	public void onTableSort(){
		
	}
	
	public void onPlaylistSearch(){
		String search = searchBox.getText();
		if(search.equals("")){
			metaDataTable.setItems(musicHandler.getCurrentPlaylist());
			digLabel.setText("Songs: " + musicHandler.getCurrentPlaylist().size());
		}else{
			ObservableList<FileBean> subList = musicHandler.getsubListFromSearchResult(
					search, metaDataTable.getItems());
			metaDataTable.setItems(subList);
			digLabel.setText("Songs: " + subList.size());
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
	
	public void launchButtonListener(){
		
		Stage stage = new Stage();
		FXMLLoader loader = null;
		Parent parent = null;
		try {
			loader = new FXMLLoader(getClass().getResource("/resources/MediaPlayerGUI.fxml"));
			parent = (Parent)loader.load();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// controller for the application you are about to launch
		MediaViewController controller = (MediaViewController)loader.getController();
		
		/*
		 *  Pass in a reference to the controller that launched it (it's parent), hence 'this'.
		 *  
		 *  Once you have that stored, you can change values in this controller using the 
		 *  other controller, or you can change values in the other controller using this one.
		 *  You have complete freedom.
		 */
		controller.setParentController(this);
		controller.setCurrentStage(stage);
		stage.setOnCloseRequest(controller.getOnExit());
		stage.maximizedProperty().addListener(controller.getOnMaximizedPressed());
		Scene scene = new Scene(parent);
		
		// window title
		stage.setTitle("Video Player");
		stage.setScene(scene);
		stage.show();
	}
	
	public void closeMenuListener(){
		exitApplication();
	}
	
	public void preferencesMenuListener(){
		
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
	
	public void onShuffleSelected(){
		startAlbum(metaDataTable.getSelectionModel().getSelectedIndex(), false);
	}
	
	
	
	/**
	 * @return the currentStage
	 */
	public Stage getCurrentStage() {
		return currentStage;
	}

	/**
	 * @param currentStage the currentStage to set
	 */
	public void setCurrentStage(Stage currentStage) {
		this.currentStage = currentStage;
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
			ArrayList<String> extensions = new ArrayList<>();
			extensions.add("*.mp3");
			fileChooser.getExtensionFilters().add(
					new ExtensionFilter("Audio Files", getSupportedFileTypes()));
			
			File selectedFile = fileChooser.showOpenDialog(playBackButton.getScene().getWindow());
			
			if(selectedFile == null){
				return;
			}
			Thread findSongs = new Thread(new DigSongs(selectedFile.getAbsolutePath()));
			findSongs.start();
			
		}else if(result.get() == folderOfmp3s){
			DirectoryChooser fileChooser = new DirectoryChooser();
			fileChooser.setTitle("Location to mine for mp3s");
			
			File selectedFile = fileChooser.showDialog(playBackButton.getScene().getWindow());
			
			if(selectedFile == null){
				return;
			}
			Thread findSongs = new Thread(new DigSongs(selectedFile.getAbsolutePath()));
			findSongs.start();
			
		}else{
			return;
		}
	}
	
	private void startNextPlayer(FileBean selectedSong){
		if(currentPlayer == null){
			playASong(selectedSong);
		}else{
			currentPlayer.stop();
			playASong(selectedSong);
		}
	}
	
	private void restartPlaySelectedAndShuffle(int startIndex){
		songNumber = 0;
		/*
		 * Add all indexes of current TableView of songs except the current song
		 */
		for(int i = 0; i < songsInAlbum.size(); i++){
			if(i != startIndex){
				songIndexes.add(new Integer(i));
			}
			
		}
		
		// shuffle all indexes. Better than using Random because there will be no repeats
		Collections.shuffle(songIndexes);
		// play selected song
		songIndexes.add(0, new Integer(startIndex));
		
		FileBean selectedSong = metaDataTable.getItems().get(songIndexes.get(0));
		startNextPlayer(selectedSong);
	}
	
	public void restartDoNotPlaySelectedAndShuffle(){
		songNumber = 0;
		// add all indexes
		for(int i = 0; i < songsInAlbum.size(); i++){
			songIndexes.add(new Integer(i));					
		}
		
		// shuffle all indexes. Better than using Random because there will be no repeats
		Collections.shuffle(songIndexes);
		// Play first song in shuffled indexes
		FileBean randomSong = metaDataTable.getItems().get(songIndexes.get(0));
		startNextPlayer(randomSong);
	}
	
	/*
	 * initiates both the playing or replaying of the album
	 */
	private void startAlbum(int startIndex, boolean playSelectedSong){
		if(currentPlayer != null){
			currentPlayer.stop();
		}
		
		if (shuffleBox.isSelected()) {
			Platform.runLater(new UpdateLabel(mediaDescLeft, "Shuffling..."));
			Platform.runLater(new UpdateLabel(mediaDescRight, "Shuffling..."));
			// de-reference old index list and make a new list
			songIndexes = null;
			songIndexes = new ArrayList<>();
			
			// Start songNumber at the beginning
			
			// get all songs in TableView
			songsInAlbum = metaDataTable.getItems();
			
			// shuffle all songs in playlist but still play selected song
			if(playSelectedSong){
				
				restartPlaySelectedAndShuffle(startIndex);
			/*
			 *  if selected song is not to be played, start with the songs at the
			 *  first shuffled index 
			 */
			}else{
				restartDoNotPlaySelectedAndShuffle();
			}
		// Shuffle box not selected	
		}else{

			FileBean selectedSong = metaDataTable.getSelectionModel().getSelectedItem();
			
			// Start songNumber where selected song is
			this.songNumber = startIndex;
			
			/*
			 *  get a sublist to iterate through sequentially from the place of selection through
			 *  the end of the playlist
			 */
			songsInAlbum = metaDataTable.getItems();
			
			if(playSelectedSong){
				// start with selected song
				startNextPlayer(selectedSong);
				
			}else{
				// wait for song to end and start unshuffled list with the next song
			}
		}
		

	}

	/**
	 * Creates a new media player by using the FileBean passed in and
	 * stores that media player in the currentPlayer variable.
	 * 
	 * @param songFile
	 * @return
	 */
	private void playASong(FileBean songFile) {
		System.out.println("Playing a song");
		Media song = new Media(String.format("file:///%s", songFile.getUrl()));
		if(currentPlayer != null){
			currentPlayer.dispose();
		}
		currentPlayer = new MediaPlayer(song);
		
		currentPlayer.setOnEndOfMedia(new EndOfMediaEventHandler());
		currentPlayer.setOnStopped(new OnMediaStopped(currentPlayer));
		currentPlayer.setAudioSpectrumInterval(1.0);
		currentPlayer.setAudioSpectrumListener(new OnMediaProgressUpdate());
		
		/*
		 * The media takes some time to load so you need to resister 
		 * a listener with the MediaPlayer object to commence playing
		 * once the status is switched to READY
		 */
		currentPlayer.setOnReady(new OnMediaReadyEvent(songFile));
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
	
	private ArrayList<String> getSupportedFileTypes(){
		ArrayList<String> extensions = new ArrayList<>();
		extensions.add("*.MP4");
		extensions.add("*.mp4");
		extensions.add("*.M4V");
		extensions.add("*.m4v");
		extensions.add("*.M4A");
		extensions.add("*.m4a");
		extensions.add("*.mp3");
		extensions.add("*.MP3");
		return extensions;
	}
	
	private void initalizeScrollBar(){
		songScrollBar.setMax(1.0);
		songScrollBar.setMin(0.0);
		songScrollBar.setValue(0.0);
		songScrollBar.valueProperty().addListener(new OnScrollBarValueChange());
	}
	

	
	protected SaveEverything saveChanges(){
		return new SaveEverything();
	}
	
	private void exitApplication(){
		if(currentPlayer != null){
			currentPlayer.stop();
		}
		
		// close the window
		getCurrentStage().close();
		// save the xml in the background
		Platform.runLater(new ExitApplication());		
	}
	
	private class OnMediaReadyEvent implements Runnable{
		private FileBean songFile;
		
		public OnMediaReadyEvent(FileBean songFile) {
			this.songFile = songFile;
		}
		
		@Override
		public void run() {
			
			String tempDescLeft = String.format(
					"Now Playing: %s\nArtist: %s", songFile.getSongName(), songFile.getArtist());
			String tempDescRight = String.format(
					"Album: %s\nDuration: %.2f", songFile.getAlbum(), songFile.getDuration());
						
			songFile.setDuration(songFile.getDuration());
			Platform.runLater(new UpdateLabel(mediaDescLeft, tempDescLeft));
			Platform.runLater(new UpdateLabel(mediaDescRight, tempDescRight));
			Platform.runLater(new SelectIndexOnTable(metaDataTable, metaDataTable.getItems().indexOf(songFile)));
			currentPlayer.play();
			
			/*
			 * if the playback button reads "Play," the player is paused. re-pause the player for the next song
			 */
			if(playBackButton.getText().equals("Play")){
				currentPlayer.pause();
			}
			
			// increments index variable 'songNumber' each time playASong() is called
			songNumber++;
		}
		
	}

	private class EndOfMediaEventHandler implements Runnable {

		@Override
		public void run() {
			System.out.printf("Song number: %d\nSongs in Album: %d", songNumber, songsInAlbum.size());
			// if shuffle is on
			if(shuffleBox.isSelected() && songNumber < songsInAlbum.size()){
				
				int nextRandomIndex = songIndexes.get(songNumber);
				FileBean nextSong = metaDataTable.getItems().get(nextRandomIndex);
				playASong(nextSong);
			
			// if shuffle is off
			}else if(!shuffleBox.isSelected() && songNumber < songsInAlbum.size()){
				
				FileBean nextSong = metaDataTable.getItems().get(songNumber);
				playASong(nextSong);
				
			}else{
				Platform.runLater(new UpdateLabel(mediaDescLeft, "Album Finished"));
				Platform.runLater(new UpdateLabel(mediaDescRight, "Album Finished"));
			}

		}

	}
	
	private class OnMediaStopped implements Runnable{
		
		private MediaPlayer player;
		
		public OnMediaStopped(MediaPlayer player) {
			this.player = player;
		}
		
		@Override
		public void run() {
			if(player != null){
				this.player.dispose();
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
				digLabel.setText("Complete: " + musicHandler.getCurrentPlaylist().size());
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
				tableView.setItems(musicHandler.getMainPlaylist().getSongsInPlaylist());
				playlistTable.setItems(musicHandler.getPlaylists());
				Platform.runLater(new SelectIndexOnTable(playlistTable, 0));
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
	
	private class SelectIndexOnTable implements Runnable{
		
		private TableView<?> table;
		private int index;
		
		public SelectIndexOnTable(TableView<?> table, int index) {
			this.table = table;
			this.index = index;					
		}

		@Override
		public void run() {
			this.table.requestFocus();
			this.table.getSelectionModel().clearAndSelect(index);
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
			event.consume();
			exitApplication();
		}
		
	}
	
	private class OnMediaProgressUpdate implements javafx.scene.media.AudioSpectrumListener{

		@Override
		public void spectrumDataUpdate(double timestamp, double duration,
				float[] magnitudes, float[] phases) {
			double scrollBarValue = currentPlayer.getCurrentTime().toSeconds()/
					currentPlayer.getTotalDuration().toSeconds();
			songScrollBar.setValue(scrollBarValue);		
			currentSongTime.setText(XMLMediaPlayerHelper.convertDecimalMinutesToTimeMinutes(
					(currentPlayer.getCurrentTime().toMinutes())));
		}
		
	}
	
	private class OnScrollBarValueChange implements ChangeListener<Number>{

		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			if(currentPlayer == null){
				return; // currentPlayer has not been initalized, do nothing
			}
			
			double ratio = currentPlayer.getCurrentTime().toMinutes()/
					currentPlayer.getCycleDuration().toMinutes();
			
			// make sure the change is not from the AudioSpectrumListener update
			if(!String.format("%.5f", newValue.doubleValue()).equals(String.format("%.5f", ratio))){
				
				// pause media player so it doesn't trigger an
				// event with the AudioSpectrumListener.. may not be necessary
				currentPlayer.pause();
				
				// get time relative to scroll bar in milliseconds
				double millis = currentPlayer.getCycleDuration().toMillis() * newValue.doubleValue();
				
				// convert to minutes and display above scroll bar
				String time = XMLMediaPlayerHelper.convertDecimalMinutesToTimeMinutes(millis/60000.0);
				currentSongTime.setText(time);
				
				// seek to the new found song location and play it
				Duration duration = new Duration(millis);
				currentPlayer.seek(duration);
				if(playBackButton.getText().equals("Pause")){ // if playback button reads "Pause" a song is 
					currentPlayer.play();					  // supposed to be playing so resume playing
				}	        								  // otherwise leave it paused
					
				
					
			}
			
			
		}
		
	}
	
	private class ExitApplication implements Runnable{

		@Override
		public void run() {
			musicHandler.exportPlaylistsToXML();
			Platform.exit();
		}
		
	}

}
