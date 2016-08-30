package fun.personalUse.utilities;

import java.io.File;
import java.util.Random;

//import java.beans.XMLEncoder;
//import java.io.ByteArrayOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//
//import fun.personalUse.dataModel.FileBean;

public class IdeaTester {

	public static void main(String[] args) {
		
		File file = new File("C:/Users/Karottop/Desktop/lockedDir");
		file.mkdirs();
		file.setReadable(false);
		file.setExecutable(false);		

	}

}
