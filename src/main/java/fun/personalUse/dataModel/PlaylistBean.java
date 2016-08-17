package fun.personalUse.dataModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlaylistBean {
	/** 
	 * user defined playlists are int values of 1, and the main
	 * playlist has an int value of 0;
	 * 
	 */
	private int PLAYLIST_TYPE;
		
	protected ObservableList<FileBean> songsInPlaylist;
	protected SimpleStringProperty playlistName;
	
	public PlaylistBean(){
		playlistName = new SimpleStringProperty("empty");
		songsInPlaylist = FXCollections.observableArrayList();
		PLAYLIST_TYPE = 1;
	}

	/**
	 * @return the songsInPlaylist
	 */
	public ObservableList<FileBean> getSongsInPlaylist() {
		return songsInPlaylist;
	}

	/**
	 * @param songsInPlaylist the songsInPlaylist to set
	 */
	public void setSongsInPlaylist(ObservableList<FileBean> songsInPlaylist) {
		this.songsInPlaylist = songsInPlaylist;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return playlistName.get();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.playlistName.set(name);
	}

	
	/**
	 * Allows playlistName to be displayed in tableview
	 * @return
	 */
	public SimpleStringProperty playlistNameProperty(){
		return playlistName;
	}

	/**
	 * @return the playlistName
	 */
	public SimpleStringProperty getPlaylistName() {
		return playlistName;
	}

	/**
	 * @param playlistName the playlistName to set
	 */
	public void setPlaylistName(SimpleStringProperty playlistName) {
		this.playlistName = playlistName;
	}

	/**
	 * @return the pLAYLIST_TYPE
	 */
	public int getPLAYLIST_TYPE() {
		return PLAYLIST_TYPE;
	}
	
	
	
	
	

}
