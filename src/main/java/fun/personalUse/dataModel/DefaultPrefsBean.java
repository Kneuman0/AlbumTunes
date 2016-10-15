package fun.personalUse.dataModel;

public interface DefaultPrefsBean {
	
	String getInfoDirectoryLocation();
	
	String getDefaultParentInfoDirectory();
	
	String getDefaultPrefsBeanLoc();
	
	String getBackgroundImageLoc();
	
	boolean isShuffle();
	
	DefaultPrefsBean getSerializableDefaultPrefsBean();

}
