package fun.personalUse.dataModel;

/**
 * This data model is used to store the main playlist in
 * a media player. There should only be one main playlist
 * and it should contain all the songs
 * @author Karottop
 *
 */
public class PlaylistBeanMain extends PlaylistBean{
	
	private PlaylistTypes PLAYLIST_TYPE;
	
	public PlaylistBeanMain(){
		PLAYLIST_TYPE = PlaylistTypes.MAIN;
	}

	/**
	 * @return the pLAYLIST_TYPE
	 */
	public PlaylistTypes getPLAYLIST_TYPE() {
		return PLAYLIST_TYPE;
	}
	
	
	
	
	
	
	

}
