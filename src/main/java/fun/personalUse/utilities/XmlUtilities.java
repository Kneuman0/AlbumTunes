package fun.personalUse.utilities;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;

/**
 * class that handles importing exporting of objects to XML files.
 * 
 * @author Karottop
 *
 */
public class XmlUtilities {

	protected String directoryPath;
	protected File directory;

	public XmlUtilities() {
		directoryPath = null;
	}

	public XmlUtilities(String directoryToPlaylistXMLs) {
		this.directory = new File(directoryToPlaylistXMLs.replace("\\", "/"));
		this.directoryPath = directoryToPlaylistXMLs;

	}

	/**
	 * @return the directoryPath
	 */
	public String getDirectoryPath() {
		return directoryPath;
	}

	/**
	 * @param directoryPath
	 *            the directoryPath to set
	 */
	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	/**
	 * Exports all FileBeans passed in to an XML at the specified
	 * location including name of xml file.
	 * 
	 * @param exportLocation
	 */
	public void exportSongsToXML(String exportLocation,
			ObservableList<FileBean> songs) {

		FileWriter file = null;
		PrintWriter fileOut = null;
		try {

			file = new FileWriter(exportLocation);
			fileOut = new PrintWriter(file);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ObservableLists apparently cannot be encoded so it is converted to an
		// ArrayList
		ArrayList<FileBean> toXML = new ArrayList<>();
		toXML.addAll(songs);
		ByteArrayOutputStream songList = new ByteArrayOutputStream();
		XMLEncoder write = new XMLEncoder(songList);
		write.writeObject(songs);
		write.close();
		fileOut.println(songList.toString());

		try {
			fileOut.close();
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Exports an array of PlaylistBeans containing all the users
	 * playlist and their associated FileBeans. The XML file will
	 * be saved in the directory passed in with the name playlists.xml
	 * 
	 * @param exportLocation
	 */
	public void exportPlaylistsToXML(String exportLocation,
			ObservableList<PlaylistBean> playlists) {

		FileWriter file = null;
		PrintWriter fileOut = null;
		try {

			file = new FileWriter(exportLocation + "/playlists.xml");
			fileOut = new PrintWriter(file);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ObservableLists apparently cannot be encoded so it is converted to an
		// ArrayList
		ArrayList<PlaylistBean> toXML = new ArrayList<>();
		toXML.addAll(playlists);
		ByteArrayOutputStream songList = new ByteArrayOutputStream();
		XMLEncoder write = new XMLEncoder(songList);
		write.writeObject(toXML);
		write.close();
		fileOut.println(songList.toString());

		try {
			fileOut.close();
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will import an XML file containing playlistBeans.
	 * 
	 * Should only be used when the XML file location is different
	 * than the location passed into the object. The File  passed in
	 * should be either contain the XML with an absolute path that is
	 * retrievable
	 * @param xml
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ObservableList<PlaylistBean> importPlaylists(File xml)
			throws FileNotFoundException {

		ObservableList<PlaylistBean> tempPlaylists;

		FileInputStream xmlFile = new FileInputStream(xml.getAbsolutePath());
		BufferedInputStream fileIn = new BufferedInputStream(xmlFile);
		XMLDecoder decoder = new XMLDecoder(fileIn);

		// ObservableLists apparently cannot be encoded so it is converted from
		// ArrayList to an OnservableArrayList
		@SuppressWarnings("unchecked")
		ArrayList<PlaylistBean> fromXML = (ArrayList<PlaylistBean>) decoder
				.readObject();
		tempPlaylists = FXCollections.observableArrayList(fromXML);
		decoder.close();
		try {
			fileIn.close();
			xmlFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tempPlaylists;
	}
	
	/**
	 * This method will import an XML file containing playlistBeans.
	 * 
	 * Should only be used when the XML file location is different
	 * than the location passed in. The location should be either 
	 * a relative or absolute path that also contains the name
	 * of the XML file in the path
	 * @param xmlLocation
	 * @return
	 * @throws FileNotFoundException
	 */
	public static ObservableList<PlaylistBean> importPlaylists(
			String xmlLocation) throws FileNotFoundException {

		ObservableList<PlaylistBean> tempPlaylists;

		FileInputStream xmlFile = new FileInputStream(xmlLocation);
		BufferedInputStream fileIn = new BufferedInputStream(xmlFile);
		XMLDecoder decoder = new XMLDecoder(fileIn);

		// ObservableLists apparently cannot be encoded so it is converted from
		// ArrayList to an OnservableArrayList
		@SuppressWarnings("unchecked")
		ArrayList<PlaylistBean> fromXML = (ArrayList<PlaylistBean>) decoder
				.readObject();
		tempPlaylists = FXCollections.observableArrayList(fromXML);
		decoder.close();
		try {
			fileIn.close();
			xmlFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tempPlaylists;
	}

	/**
	 * This class will populate the FileBean metaData after the MediaPlayer's
	 * status has been changed to READY. Uses the FileBean's setter methods so
	 * that they will be picked up by the XMLEncoder. This allows the use of the
	 * Media's ID3v2 tag reading abilities. If tags are not read due to
	 * incompatibility, they are not changed.
	 * 
	 * This step is computationally expensive but should not need to be done
	 * very often and it saves a ton of memory during normal use. Setting the 
	 * Media and MediaPlayer objects to null make this run much faster and uses
	 * less memory
	 * 
	 * @author Karottop
	 *
	 */
	protected class OnMediaReadyEvent implements Runnable {
		private FileBean fileBean;

		public OnMediaReadyEvent(FileBean fileBean) {
			this.fileBean = fileBean;
		}

		@Override
		public void run() {
			String songName = null;
			String album = null;
			String artist = null;
			double duration = 0.0;
			try{
				// Retrieve track song title
				songName = (String) fileBean.getMedia().getMetadata()
						.get("title");
				
				// Retrieve Album title
				album = (String) fileBean.getMedia().getMetadata()
						.get("album");
				
				// Retrieve Artist title
				artist = (String) fileBean.getMedia().getMetadata()
						.get("artist");
				
				// Retrieve Track duration
				duration = fileBean.getMedia().getDuration().toMillis() / 60_000.0;
			}catch(NullPointerException e){
				System.out.println(e.getMessage());
			}
			// Set track song title
			
			if (songName != null)
				fileBean.setSongName(songName);

			// Set Album title
			
			if (album != null)
				fileBean.setAlbum(album);

			// Retrieve and set Artist title
			
			if (artist != null)
				fileBean.setArtist(artist);

			// Set Track duration
			fileBean.setDuration(duration);

			fileBean.setMedia(null);
			fileBean.setPlayer(null);
			
			// sets a variable telling whether or not the media player got hung
//			fileBean.setMediaInitalized(true);

		}

	}
	
	
}
