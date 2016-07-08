package fun.personalUse.mainAlbumTunesApp;

import java.io.File;
import java.io.IOException;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.scene.media.MediaPlayer;

public class CurrentSongBean {
	
	private double duration;
	private MediaPlayer player;
	private String songName;
	private String artistName;
	
	public CurrentSongBean(double duration, MediaPlayer player) {
		this.duration = duration;
		this.player = player;
	}
	
	public CurrentSongBean(File musicFile, MediaPlayer player) throws UnsupportedTagException, InvalidDataException, IOException{
		Mp3File mp3File = new Mp3File(musicFile.getAbsolutePath());
		this.artistName = mp3File.getId3v2Tag().getArtist();
		System.out.println(artistName);
		this.songName = mp3File.getId3v2Tag().getTitle();
		this.duration = mp3File.getLengthInMilliseconds();
		this.player = player;
	}
	
	

	/**
	 * @return the songName
	 */
	public String getSongName() {
		return songName;
	}

	/**
	 * @param songName the songName to set
	 */
	public void setSongName(String songName) {
		this.songName = songName;
	}

	/**
	 * @return the artistName
	 */
	public String getArtistName() {
		return artistName;
	}

	/**
	 * @param artistName the artistName to set
	 */
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(double duration) {
		this.duration = duration;
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
	
	

}
