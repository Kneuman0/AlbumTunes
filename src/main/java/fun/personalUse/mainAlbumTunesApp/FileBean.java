package fun.personalUse.mainAlbumTunesApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileBean {
	private File file;
	private String location;
	private String name;
	private String url;
	
	public FileBean(File file) throws FileNotFoundException, UnsupportedEncodingException{
		this.file = file;
		location = file.getAbsolutePath().replace("\\", "/");
		
		/*
		 * encode all special characters.
		 * URLEncoder puts a '+' where a ' ' is so change all '+' to encoded space '%20'.
		 * Then change the Unicode '%2B' back to '+' in case there is an actual '+' in the path.
		 */
		url = URLEncoder.encode(location, "UTF-8").replace("+", "%20").replace("%2B", "+");
		System.out.println(location);
		String[] folders = location.split("/");
		this.name = folders[folders.length - 1];
	}

	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
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
		return getUrl();
	}
	
	
}
