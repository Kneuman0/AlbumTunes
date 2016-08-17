package fun.personalUse.customExceptions;

public class NoPlaylistsFoundException extends RuntimeException{
	
	public NoPlaylistsFoundException(){
		super("No Playlists were found in infoDirectory");
	}

}
