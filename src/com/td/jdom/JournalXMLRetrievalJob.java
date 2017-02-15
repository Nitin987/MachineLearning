package com.td.jdom;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.jdom2.JDOMException;

public class JournalXMLRetrievalJob {
	public static void main(String[] args) {
		JournalXMLRetrievalJob job = new JournalXMLRetrievalJob();
		//"\\\\172.16.0.53\\gopalsir\\Abhishek\\Data_Files\\XML_Files\\data_files\\txcopy_AANAT_50942_0004.xml"
//		XMLExtractionJob.DTD_PATH
		XMLExtractionJob xmlParseObj  = new XMLExtractionJob();
		File file = new File("\\\\172.16.2.128\\FMS_Archive2\\Elsevier\\journal\\ANIFEE\\13215\\Archive");
		
		File mainXML = job.getTaggedXML(file);
		if(mainXML != null) {
			System.out.println(mainXML);
			try {
				xmlParseObj.parseFile(mainXML.getAbsolutePath());
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public File getTaggedXML(File file) {
		File mainXML = null;
		if(file.isDirectory()) {
			File [] listOfFiles = file.listFiles();
			Arrays.sort(listOfFiles, new Comparator<File>() {
			    public int compare(File f1, File f2) {
			    	try{
				    	int f1int = Integer.parseInt(f1.getName());
				    	int f2int = Integer.parseInt(f2.getName());
				    	return Integer.compare(f1int, f2int);
			    	}
			    	catch(NumberFormatException nfe) {
			    		return Long.compare(f1.lastModified(), f2.lastModified());
					}
			    }
			});
			for(int f=listOfFiles.length-1; f>0; f--) {
				if(listOfFiles[f].isDirectory()) {
					File lastStageXMLFile = new File(listOfFiles[f].getAbsolutePath()+"\\tx1.xml");
					if(lastStageXMLFile.exists()) {
						mainXML = lastStageXMLFile;
						break;
					}
				}
			}
			
		}
		return mainXML;
	}
	
}
