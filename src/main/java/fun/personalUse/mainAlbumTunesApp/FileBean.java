package fun.personalUse.mainAlbumTunesApp;

import java.io.File;
import java.io.FileNotFoundException;

public class FileBean {
	File file;
	String location;
	String name;
	
	public FileBean(File file) throws FileNotFoundException{
		this.file = file;
		location = file.getAbsolutePath().replace("\\", "/");
		System.out.println(location);
		String[] folders = location.split("/");
		this.name = folders[folders.length - 1];
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
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return getLocation();
	}
	
	
}
