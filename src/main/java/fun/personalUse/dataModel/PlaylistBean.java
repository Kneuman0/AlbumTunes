package fun.personalUse.dataModel;

import java.util.Comparator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Data model for storing a set of FileBeans (songs) defined by a user
 * @author Karottop
 *
 */
public class PlaylistBean implements Comparator<PlaylistBean>, Comparable<PlaylistBean>{
	/*
	 * user defined playlists are int values of 1, and the main
	 * playlist has an int value of 0;
	 * 
	 */
	private PLAYLIST_TYPES PLAYLIST_TYPE;
	
	
	public enum PLAYLIST_TYPES {USER_DEFINED, MAIN}
		
	protected ObservableList<FileBean> songsInPlaylist;
	protected SimpleStringProperty playlistName;
	
	public PlaylistBean(){
		playlistName = new SimpleStringProperty("empty");
		songsInPlaylist = FXCollections.observableArrayList();
		PLAYLIST_TYPE = PLAYLIST_TYPES.USER_DEFINED;
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
	public PLAYLIST_TYPES getPLAYLIST_TYPE() {
		return PLAYLIST_TYPE;
	}

	/**
	 * Compares another PlaylistBean based on it's name using the String.compareToIgnoreCase()
	 */
	@Override
	public int compareTo(PlaylistBean bean) {

		if(this.getName().compareToIgnoreCase(bean.getName()) > 0){
			return 1;
		}else if(this.getName().compareToIgnoreCase(bean.getName()) < 0){
			return -1;
		}else{
			return 0;
		}
	}

	/**
	 * Compares two beans based on the overriden compareTo method from the comparab
	 */
	@Override
	public int compare(PlaylistBean bean1, PlaylistBean bean2) {
		// TODO Auto-generated method stub
		return bean1.compareTo(bean2);
	}
	
	
	
	
	

}
