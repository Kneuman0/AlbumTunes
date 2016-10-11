package fun.personalUse.dataModel;

import java.io.File;

import biz.personalAcademics.lib.pathClasses.PathGetter;

public class PreferencesBean {
	
	private String infoDirectoryLocation;
	private String backgroundImageLoc;
	private String defaultPrefsBeanLoc;
	private boolean shuffle;
	
	public PreferencesBean() {
		setInfoDirectoryLocation("n/a");
		setBackgroundImageLoc("n/a");
		setShuffle(false);
	}
	
	public String getDefaultParentInfoDirectory(){
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
		
		if(file.mkdirs()){
			System.out.println("Directory sucessfully created");
		}else{
			System.out.println("Directory not created");
		}
		
		return file.getAbsolutePath();
	}

	/**
	 * @return the defaultPrefsBeanLoc
	 */
	public String getDefaultPrefsBeanLoc() {
		return defaultPrefsBeanLoc;
	}

	/**
	 * @param defaultPrefsBeanLoc the defaultPrefsBeanLoc to set
	 */
	public void setDefaultPrefsBeanLoc(String defaultPrefsBeanLoc) {
		this.defaultPrefsBeanLoc = defaultPrefsBeanLoc;
	}

	/**
	 * @return the infoDirectoryLocation
	 */
	public String getInfoDirectoryLocation() {
		return infoDirectoryLocation;
	}

	/**
	 * @param infoDirectoryLocation the infoDirectoryLocation to set
	 */
	public void setInfoDirectoryLocation(String infoDirectoryLocation) {
		this.infoDirectoryLocation = infoDirectoryLocation;
	}

	/**
	 * @return the backgroundImageLoc
	 */
	public String getBackgroundImageLoc() {
		return backgroundImageLoc;
	}

	/**
	 * @param backgroundImageLoc the backgroundImageLoc to set
	 */
	public void setBackgroundImageLoc(String backgroundImageLoc) {
		this.backgroundImageLoc = backgroundImageLoc;
	}

	/**
	 * @return the shuffle
	 */
	public boolean isShuffle() {
		return shuffle;
	}

	/**
	 * @param shuffle the shuffle to set
	 */
	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}
	
	public PreferencesBean getSerializableDefaultPrefsBean(){
		PreferencesBean temp = new PreferencesBean();
		temp.setBackgroundImageLoc("/resources/MusicBackground.jpg");
		temp.setDefaultPrefsBeanLoc("/resources/prefs.xml");
		temp.setInfoDirectoryLocation(getDefaultParentInfoDirectory());
		temp.setShuffle(false);
		return temp;
		
	}
	
	@Override
	public String toString(){
		return String.format("Info Directory: %s\nImage Location %s\nShuffle: %b\n", 
				infoDirectoryLocation, backgroundImageLoc, shuffle);
	}
	
	
}
