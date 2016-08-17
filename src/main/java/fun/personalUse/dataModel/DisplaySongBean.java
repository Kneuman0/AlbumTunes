package fun.personalUse.dataModel;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;


public class DisplaySongBean {
	private File file;
	
	// uses string properties to display information in tableview
	private SimpleStringProperty location;
	private SimpleStringProperty songName;
	private SimpleStringProperty album;
	private SimpleStringProperty artist;
	private SimpleStringProperty url;
	private SimpleStringProperty duration;
	
	/**
	 * Accepts a serialized FileBean read from an XML.
	 * FileBean MUST be already populated with metaData fields.
	 * @param fileBean
	 */
	public DisplaySongBean(FileBean fileBean){
		this.file = fileBean.getFile();
		this.location = new SimpleStringProperty(fileBean.getLocation());
		this.songName = new SimpleStringProperty(fileBean.getSongName());
		this.album = new SimpleStringProperty(fileBean.getAlbum());
		this.artist = new SimpleStringProperty(fileBean.getArtist());
		this.url = new SimpleStringProperty(fileBean.getUrl());
		this.duration = new SimpleStringProperty(String.format("%.2f", fileBean.getDuration()));
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

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location.get();
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location.set(location);
	}

	/**
	 * @return the songName
	 */
	public String getSongName() {
		return songName.get();
	}

	/**
	 * @param songName the songName to set
	 */
	public void setSongName(String songName) {
		this.songName.set(songName);
	}

	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album.get();
	}

	/**
	 * @param album the album to set
	 */
	public void setAlbum(String album) {
		this.album.set(album);
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist.get();
	}

	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist.set(artist);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url.get();
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url.set(url);
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return Double.parseDouble(duration.get());
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(double duration) {
		this.duration.set(String.format("%.2f", duration));
	}
	
	/**
	 * Method is called from Callback interface from TableColumn
	 * 
	 * "duration" is passed into the constructor of a PropertyValueFactory which looks for
	 * "The Name Passed In" and Property()... nameProperty()
	 * @return
	 */
	public SimpleStringProperty durationProperty(){
		return duration;
	}
	
	/**
	 * Method is called from Callback interface from TableColumn
	 * @return
	 */
	public SimpleStringProperty songNameProperty(){
		return this.songName;
	}
	
	/**
	 * Method is called from Callback interface from TableColumn
	 * @return
	 */
	public SimpleStringProperty artistProperty(){
		return this.artist;
	}
	
	/**
	 * Method is called from Callback interface from TableColumn
	 * @return
	 */
	public SimpleStringProperty albumProperty(){
		return this.album;
	}
	
	
	
}
