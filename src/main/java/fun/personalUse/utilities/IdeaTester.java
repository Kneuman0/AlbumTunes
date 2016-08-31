package fun.personalUse.utilities;

import java.io.File;

public class IdeaTester {

	public static void main(String[] args) {
		
		File file = new File("C:/Users/Karottop/Desktop/lockedDir");
		file.mkdirs();
		file.setReadable(false);
		file.setExecutable(false);		

	}

}
