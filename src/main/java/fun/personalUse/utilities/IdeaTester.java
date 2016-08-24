package fun.personalUse.utilities;

import java.text.DecimalFormat;

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
//		XmlUtilities musicSeeker = new XmlUtilities("D:/Pictures/Music/Music");
//		FileWriter file = null;
//		PrintWriter fileOut = null;
//		try {
//			file = new FileWriter("XMLTest.xml");
//			fileOut = new PrintWriter(file);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ByteArrayOutputStream songList = new ByteArrayOutputStream();
//		XMLEncoder write = new XMLEncoder(songList);
//		ArrayList<FileBean> songArray = musicSeeker.getSongs();
//		write.writeObject(songArray);
//		write.close();
//		fileOut.println(songList.toString());
		System.out.println("Converted: " + convert(3.14088586545));
		

	}
	
	private static String convert(double seconds){
		DecimalFormat time = new DecimalFormat("00");
		int fullMinutes = (int)seconds;
		int secondsRemainder = (int)((seconds - fullMinutes) * 60);
		System.out.println("Remaining Seconds" + time.format(secondsRemainder));
		return String.format("%d.%s", fullMinutes, time.format(secondsRemainder));
	}

}
