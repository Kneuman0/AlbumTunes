package fun.personalUse.customExceptions;

@SuppressWarnings("serial")
public class InvalidUserInputException extends RuntimeException{
	
	public InvalidUserInputException(String path) {
		super(path + "is not valid");
	}

}
