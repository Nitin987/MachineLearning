/**
 * 
 */
package com.td.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class CommonMethods {

	public static Properties loadProp(String name) {

		FileReader fReader = null;
		Properties prop = new Properties();
		try {
			fReader = new FileReader(new File(name));
			prop.load(fReader);
			return prop;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fReader != null)
				try {
					fReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	public static void sleep(int n){
		try {Thread.sleep(n*1000); } catch (InterruptedException e) {}
	}


	public static void writeFile(File destFile, String content){
		
		FileOutputStream fop = null;
		try {
			fop = new FileOutputStream(destFile);
			if (!destFile.exists()) {
				destFile.createNewFile();
			}
			byte[] contentInBytes = content.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			System.out.println("File Successfuly Created");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
}
