package com.td.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;

import com.td.jdom.XMLExtractionJob;

public class ZipExtraction {

	
	public static void main(String[] args) {
		
		//String rootPath = "\\\\fmshe\\Elsevier Books Datasets\\All-Dataset";
		String rootPath = "D:\\fms\\Book-Dataset";
		ArrayList<File> zipFileList = new ArrayList<File>();
		getZipFile(rootPath, zipFileList);
		int count = 0;
		for(File zipFile : zipFileList){
			System.out.println(++count +" : "+zipFile.getAbsolutePath());
			getInputStream(zipFile);
		}
	}
	
	public String getInputStreamContent(InputStream inputStream){
		
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(inputStream, writer, "UTF-8");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return writer.toString();
	}
	
	public static void getZipFile(String rootPath, ArrayList<File> zipFiles){
		
		File [] fileList = new File(rootPath).listFiles();
		for(File file : fileList){
			if(file.isDirectory()){
				getZipFile(file.getAbsolutePath(), zipFiles);
			}
			else if(file.isFile() && file.getParentFile().getName().equalsIgnoreCase("s300") && file.getName().toLowerCase().endsWith(".zip")){
				zipFiles.add(file);
			}
		}

	}
	
	public static void getInputStream(File zip){
		ZipFile zipFile = null;
		try {
			if(zip.exists() && zip.isFile() && zip.getName().toLowerCase().endsWith(".zip")){
				zipFile = new ZipFile(zip);
			    Enumeration<? extends ZipEntry> entries = zipFile.entries();
			    while(entries.hasMoreElements()){
			        ZipEntry entry = entries.nextElement();
			        String entryName = entry.getName();
			        if(entryName.trim().toLowerCase().endsWith("main.xml")){
			        	XMLExtractionJob x = new XMLExtractionJob();
			        	try {
							x.parseFile(zipFile.getInputStream(entry));
						} catch (JDOMException e) {
							e.printStackTrace();
						}
			        }
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(zipFile != null)
					zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getInputStream(String zipPath){
		
		getInputStream(new File(zipPath));
	}
}
