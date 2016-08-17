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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;
import fun.personalUse.dataModel.PlaylistBeanMain;

/**
 * class that handles importing exporting of objects to XML files.
 * 
 * @author Karottop
 *
 */
public class XmlUtilities {

	protected String directoryPath;

	public XmlUtilities() {
		directoryPath = null;
	}

	public XmlUtilities(String directoryToPlaylistXMLs) {
		File directory = new File(directoryToPlaylistXMLs.replace("\\", "/"));
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
	 * Exports all mp3 files found in a directory to an XML at the specified
	 * location including name of xml file
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
	 * Exports all PlaylistBeans containing mp3 files found in a directory to an
	 * XML at the specified location that includes the name of xml file
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

	// /**
	// * Imports songs from an XML and store them in an ArrayList. Location of
	// XML
	// * must be passed in.
	// *
	// * @param importLocation
	// * @return
	// * @throws FileNotFoundException
	// */
	// public static ArrayList<FileBean> importSongsFromXMLToArrayList(
	// String importLocation) throws FileNotFoundException {
	// // Reads xml file in from source and passes it to a BufferedInputStream
	// FileInputStream xmlFile = new FileInputStream(importLocation);
	// BufferedInputStream fileIn = new BufferedInputStream(xmlFile);
	// XMLDecoder decoder = new XMLDecoder(fileIn);
	//
	// //Decoder decodes arraylist filled with FileBeans. Must cast accordingly
	// ArrayList<FileBean> songList = (ArrayList<FileBean>) decoder
	// .readObject();
	// decoder.close();
	// return songList;
	// }

	// /**
	// * Import songs from an XML and stores them in an ObservableList. Location
	// * of XML must be passed in
	// *
	// * @throws FileNotFoundException
	// */
	// public static ObservableList<FileBean>
	// importSongsFromXMLToObservableList(
	// String importLocation) throws FileNotFoundException {
	//
	// ArrayList<FileBean> songList =
	// importSongsFromXMLToArrayList(importLocation);
	//
	// //Populate an observable list filled with DisplaySongBeans which have
	// // methods to display contents in TableView
	// ObservableList<FileBean> observableSongList = FXCollections
	// .observableArrayList(songList);
	// System.out.println(observableSongList.get(0).getSongName());
	//
	// return observableSongList;
	// }

	public static ObservableList<PlaylistBean> importPlaylists(File xml)
			throws FileNotFoundException {

		ObservableList<PlaylistBean> tempPlaylists;

		FileInputStream xmlFile = new FileInputStream(xml.getAbsolutePath());
		BufferedInputStream fileIn = new BufferedInputStream(xmlFile);
		XMLDecoder decoder = new XMLDecoder(fileIn);

		// ObservableLists apparently cannot be encoded so it is converted from
		// ArrayList to an OnservableArrayList
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

	public static ObservableList<PlaylistBean> importPlaylists(
			String xmlLocation) throws FileNotFoundException {

		ObservableList<PlaylistBean> tempPlaylists;

		FileInputStream xmlFile = new FileInputStream(xmlLocation);
		BufferedInputStream fileIn = new BufferedInputStream(xmlFile);
		XMLDecoder decoder = new XMLDecoder(fileIn);

		// ObservableLists apparently cannot be encoded so it is converted from
		// ArrayList to an OnservableArrayList
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
	 * very often and it saves a ton of memory during normal use.
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
			// Retrieve and set track song title
			String songName = (String) fileBean.getMedia().getMetadata()
					.get("title");
			if (songName != null)
				fileBean.setSongName(songName);

			// Retrieve and set Album title
			String album = (String) fileBean.getMedia().getMetadata()
					.get("album");
			if (album != null)
				fileBean.setAlbum(album);

			// Retrieve and set Artist title
			String artist = (String) fileBean.getMedia().getMetadata()
					.get("artist");
			if (artist != null)
				fileBean.setArtist(artist);

			// Retrieve and set Track duration
			fileBean.setDuration(fileBean.getMedia().getDuration().toMillis() / 60_000.0);

			fileBean.setMedia(null);
			fileBean.setPlayer(null);

		}

	}

}
