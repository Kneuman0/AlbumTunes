package fun.personalUse.dataModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Comparator;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class FileBean implements Comparator, Comparable{
	private File file;
	private SimpleStringProperty location;
	private SimpleStringProperty songName;
	private SimpleStringProperty  album;
	private SimpleStringProperty  artist;
	private SimpleStringProperty  url;
	private Media media;
	private MediaPlayer player;
	private SimpleStringProperty  duration;
//	private MediaPlayer tempPlayer;
	
	public FileBean(){
		media = null;
		file = null;
		location = new SimpleStringProperty();
		songName = new SimpleStringProperty();
		album = new SimpleStringProperty();
		artist = new SimpleStringProperty();
		url = new SimpleStringProperty();
		
		/**
		 *  must initialize with a number because this field will be called
		 *  before the MediaPlayer's status has changed which would cause a 
		 *  null pointer exception to be thrown if not initialized
		 */
		duration = new SimpleStringProperty("0.0");
	}
	
	public FileBean(File file) throws FileNotFoundException, UnsupportedEncodingException{
		location = new SimpleStringProperty();
		songName = new SimpleStringProperty();
		album = new SimpleStringProperty();
		artist = new SimpleStringProperty();
		url = new SimpleStringProperty();
		
		/**
		 *  must initialize with a number because this field will be called
		 *  before the MediaPlayer's status has changed which would cause a 
		 *  null pointer exception to be thrown if not initialized
		 */
		duration = new SimpleStringProperty("0.0");
		this.file = file;
		location.set(file.getAbsolutePath().replace("\\", "/"));
		
		/*
		 * encode all special characters.
		 * URLEncoder puts a '+' where a ' ' is so change all '+' to encoded space '%20'.
		 */
		url.set(URLEncoder.encode(location.get(), "UTF-8").replace("+", "%20"));
		
		/*
		 * Could not easily figure out how to set an action event for when the Media
		 * object is done loading. Using the MediaPlayer status change event instead.
		 * Looking for a better option
		 */
		media = new Media("file:///" + url.get());
//		media.getMetadata().addListener(new SetMetaData());
		this.player = new MediaPlayer(media);
//		tempPlayer.setOnReady(new OnMediaReadyEvent());
		setDefaultSongNameAndArtist();
	}
	
	private void setDefaultSongNameAndArtist(){
		String[] songLocation = getLocation().split("/");
		String[] songFragment = songLocation[songLocation.length - 1].split("[.]");
		setSongName(songFragment[0]);
		
		setAlbum(songLocation[songLocation.length - 2] + "?");
		setArtist(songLocation[songLocation.length - 3] + "?");
		
	}

	
	
	/**
	 * @return the player
	 */
	public MediaPlayer getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(MediaPlayer player) {
		this.player = player;
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
	 * @return the media
	 */
	public Media getMedia() {
		return media;
	}



	/**
	 * @param media the media to set
	 */
	public void setMedia(Media media) {
		this.media = media;
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
	 * @return the name
	 */
	public String getSongName() {
		return songName.get();
	}

	/**
	 * @param name the name to set
	 */
	public void setSongName(String name) {
		this.songName.set(name);
	}
	
	/**
	 * returns the songName property
	 * @return
	 */
	public SimpleStringProperty songNameProperty(){
		return songName;
	}
	
	/**
	 * returns the artist property
	 * @return
	 */
	public SimpleStringProperty artistProperty(){
		return artist;
	}
	
	/**
	 * returns the album property
	 * @return
	 */
	public SimpleStringProperty albumProperty(){
		return album;
	}
	
	/**
	 * returns the duration property
	 * @return
	 */
	public SimpleStringProperty durationProperty(){
		return duration;
	}
	
	public FileBean getSerializableJavaBean(){
		FileBean temp = new FileBean();
		temp.setAlbum(this.getAlbum());
		temp.setArtist(this.getArtist());
		temp.setDuration(this.getDuration());
//		temp.setFile(this.getFile());
		temp.setLocation(this.getLocation());
		temp.setMedia(this.getMedia());
		temp.setPlayer(player);
		temp.setSongName(this.getSongName());
		temp.setUrl(this.getUrl());
		
		return temp;
	}
	
	/**
	 * Method used to return a fully populated FileBean after decoded from XML.
	 * @return
	 */
	public FileBean getFullFileBean(){
		
		try {
			return new FileBean(new File(getLocation()));
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileBean temp = new FileBean();
			temp.setLocation("error");
			return temp;
		}
	}
		
	@Override
	public String toString(){
		return String.format("%s, %s, %s", getSongName(), getArtist(), getAlbum());
	}

	@Override
	public int compare(Object FileBean1, Object FileBean2) {
		FileBean bean1 = (FileBean)FileBean1;
		FileBean bean2 = (FileBean)FileBean2;
		
		return bean1.compareTo(bean2);
	}

	@Override
	public int compareTo(Object o) {
		FileBean bean = (FileBean)o;
		
		if(this.getLocation().compareTo(bean.getLocation()) > 0){
			return 1;
		}else if(this.getLocation().compareTo(bean.getLocation()) < 0){
			return -1;
		} else{
			return 0;
		}
	}
	
	
}
