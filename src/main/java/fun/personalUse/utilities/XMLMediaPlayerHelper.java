package fun.personalUse.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import biz.personalAcademics.lib.pathClasses.PathGetter;
import fun.personalUse.customExceptions.NoPlaylistsFoundException;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;
import fun.personalUse.dataModel.PlaylistBeanMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class servers as a song and playlist manager for 
 * a media player
 * @author Karottop
 *
 */
public class XMLMediaPlayerHelper extends XmlUtilities {
	
	private ObservableList<PlaylistBean> playlists;
	private File file;
	private String xmlDirectory;
	private ObservableList<FileBean> currentPlaylist;
	
	/**
	 * Should be the default constructor used. This constructor will
	 * look in the parent directory of the parent directory containing the jar file 
	 * version of this program.
	 */
	public XMLMediaPlayerHelper(){
		playlists = FXCollections.observableArrayList();
		PathGetter pathGetter = new PathGetter(this);
		String parentDirectory = pathGetter.getAbsoluteSubfolderPath();
		String[] folders = parentDirectory.split("/");
		String parentOfParent = "";
		for(int i = 0; i < (folders.length - 1); i++){
			parentOfParent += folders[i] + "/";
		}
		parentOfParent += "Media_Player_5000/infoDirectory/";
		
		if(parentOfParent == "/Media Player 5000/infoDirectory"){
			parentOfParent = parentDirectory + "Media_Player_5000/infoDirectory/";
		}
		
		System.out.printf("Parent: %s, ParentOfParent: %s\n", parentDirectory, parentOfParent);
		File file = new File(parentOfParent);
		this.file = file;
		
		if(file.mkdirs()){
			System.out.println("Directory sucessfully created");
		}else{
			System.out.println("Directory not created");
		}
		
		this.setXmlDirectory(parentOfParent);
	}
	
	/**
	 * Initializes the object with the location to the directory
	 * containing the XML files
	 * @param directoryPathToXMLs
	 */
	public XMLMediaPlayerHelper(String directoryPathToXMLs){
		super(directoryPathToXMLs);
		playlists = FXCollections.observableArrayList();
		file = new File(directoryPathToXMLs);
		this.xmlDirectory = directoryPathToXMLs;
	}
	
	/**
	 * This method will return an aboservable list of playlist that have been loaded from an XML
	 * in the infoDirectory
	 * @return
	 * @throws FileNotFoundException
	 * @throws NoPlaylistsFoundException
	 */
	public void loadAllPlaylists() 
			throws FileNotFoundException, NoPlaylistsFoundException{
		
		
		File[] playlistXMLs = null;
		if(file.isDirectory() && file.canRead()){
			playlistXMLs = file.listFiles();
		}else{
			throw new NoPlaylistsFoundException();
		}
		
		boolean playlistXMLDoesNotExist = true;
		for(File xml : playlistXMLs){
			if(xml.getAbsolutePath().endsWith("playlists.xml")){
				playlists = super.importPlaylists(xml);
				playlistXMLDoesNotExist = false;
			}
		}
		
		if(playlistXMLDoesNotExist){
			throw new NoPlaylistsFoundException();
		}
		
		/**
		 * Always sets the current playlist to to the main playlist upon loading
		 */
		currentPlaylist = getMainPlaylist().getSongsInPlaylist();
		
	}
	
	/**
	 * This method will search for songs in a new directory and add them to the song list
	 * in the main playlist
	 * @param newDirectory
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public PlaylistBean findNewSongs(String newDirectory) 
			throws FileNotFoundException, UnsupportedEncodingException{
		PlaylistBean main = getMainPlaylist();
		File file = new File(newDirectory);
		
		// add new songs to existing main playlist
		digSongs(main.getSongsInPlaylist(), file);
		
//		deleteIncapatableMediaTypes(main.getSongsInPlaylist());
		
		return main;
	}
	
	/**
	 * method used to seek all mp3 files in a specified directory and save them
	 * to an ObservableArrayList
	 * 
	 * @param existingSongs
	 * @param directory
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected ObservableList<FileBean> digSongs(ObservableList<FileBean> existingSongs,
			File directory) throws FileNotFoundException,
			UnsupportedEncodingException {
		/*
		 * Each directory is broken into a list and passed back into the digSongs().
		 */
		if (directory.isDirectory() && directory.canRead()) {

			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				digSongs(existingSongs, files[i]);
			}
			
			/*
			 * if a file is not a directory, then is it checked to see if it's
			 * an mp3 file
			 */
		} else if (directory.getAbsolutePath().endsWith(".mp3") 
				|| directory.getAbsolutePath().endsWith(".m4a")
				) {
			FileBean songBean = new FileBean(directory).getSerializableJavaBean();
			
			existingSongs.add(songBean);
			
			songBean.getPlayer().setOnReady(new OnMediaReadyEvent(songBean));
			songBean.getPlayer().setOnError(new OnMediaPlayerStalled(existingSongs, songBean));
//			
			/*
			 * if it's not a directory or mp3 file, then do nothing
			 */
		} else {

			return existingSongs;

		}

		return existingSongs;
	}
	
	/**
	 * Searches through the entire playlist for songs that match the search.
	 * 
	 * The search keys off of the song title, album name, and artist
	 * @param search
	 * @param playlist
	 * @return
	 */
	public ObservableList<FileBean> getsubListFromSearchResult(String search, 
			ObservableList<FileBean> playlist){
		setCurrentPlaylist(playlist);
		ObservableList<FileBean> subList = FXCollections.observableArrayList();
		
		for(FileBean song : playlist){
			if(song.toString().toLowerCase().contains(search.toLowerCase().trim())){
				subList.add(song);
			}
		}
		
		return subList;
	}
	
	/**
	 * Returns the main playlist from a list of playlists. The main
	 * playlist contains all the music in the media player.
	 * @return
	 */
	public PlaylistBean getMainPlaylist(){
		PlaylistBean temp = new PlaylistBeanMain();
		
		boolean noMainPlaylist = true;
		for(PlaylistBean main : playlists){
			if(main.getPLAYLIST_TYPE() == 0){
				temp = main;
				noMainPlaylist = false;
			}
		}
		
		if(noMainPlaylist){
			// created a new main playlist and adds it to the playlists ObservableList
			temp = makeMainPlaylistBean();
		}
		
		return temp;
	}
	
	/**
	 * Adds a new PlaylistBean to the playlist ObservableList with the specifed name
	 * @param name
	 */
	public void addPlaylist(String name){
		PlaylistBean newPlaylist = new PlaylistBean();
		newPlaylist.setName(name);
		playlists.add(newPlaylist);
	}


	/**
	 * @return the playlists
	 */
	public ObservableList<PlaylistBean> getPlaylists() {
		return playlists;
	}


	/**
	 * @param playlists the playlists to set
	 */
	public void setPlaylists(ObservableList<PlaylistBean> playlists) {
		this.playlists = playlists;
	}


	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}


	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	public void exportPlaylistsToXML(){
		super.exportPlaylistsToXML(xmlDirectory, playlists);
	}


	/**
	 * @return the xmlDirectory
	 */
	public String getXmlDirectory() {
		return xmlDirectory;
	}


	/**
	 * @param xmlDirectory the xmlDirectory to set
	 */
	public void setXmlDirectory(String xmlDirectory) {
		this.xmlDirectory = xmlDirectory;
	}
	
	


	/**
	 * @return the currentPlaylist
	 */
	public ObservableList<FileBean> getCurrentPlaylist() {
		return currentPlaylist;
	}

	/**
	 * @param currentPlaylist the currentPlaylist to set
	 */
	public void setCurrentPlaylist(ObservableList<FileBean> currentPlaylist) {
		this.currentPlaylist = currentPlaylist;
	}

	/**
	 * Creates a new main playlist which should contain all the songs.
	 * This method should not be used if a main playlist already exists.
	 * @return
	 */
	protected PlaylistBean makeMainPlaylistBean(){
		PlaylistBean main = new PlaylistBeanMain();
		ObservableList<FileBean> observableSongs = FXCollections.observableArrayList();
		main.setName("All Music");
		main.setSongsInPlaylist(observableSongs);
		playlists.add(main);
		setCurrentPlaylist(main.getSongsInPlaylist());
		
		return main;
	}
	
	public static String convertDecimalMinutesToTimeMinutes(double minutes){
		DecimalFormat time = new DecimalFormat("00");
		int fullMinutes = (int)minutes;
		int secondsRemainder = (int)((minutes - fullMinutes) * 60);
		return String.format("%d.%s", fullMinutes, time.format(secondsRemainder));
	}
	
	public ObservableList<FileBean> getDeepCopyOfPlaylist(ObservableList<FileBean> playlist){
		ObservableList<FileBean> temp = FXCollections.observableArrayList();
		for(FileBean song : playlist){
			FileBean bean = new FileBean();
			bean.setAlbum(song.getAlbum());
			bean.setArtist(song.getArtist());
			bean.setDuration(song.getDuration());
			bean.setLocation(song.getLocation());
			bean.setSongName(song.getSongName());
			bean.setUrl(song.getUrl());
			temp.add(bean);
		}
		return temp;
	}
	
	
	
}
