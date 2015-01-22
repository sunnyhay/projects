package org.lvyouzaike.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class GetCustomContent {
	public static String get(String filename) throws FileNotFoundException, IOException{
		Properties ctmProp = new Properties();
		ctmProp.load(new BufferedInputStream(new FileInputStream(filename)));
		
		String titles = "";
		int num = Integer.parseInt(ctmProp.getProperty("num"));
		
		for(int i = 1; i <= num; i++){
			titles += ctmProp.getProperty("resort" + i + "_title");
		}
		
		return titles;	
	}
}
