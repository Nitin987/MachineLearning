package com.td.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.td.jdom.XMLExtractionJob;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

public class SevenZipExtract {


	public static boolean extractZip(File zipFile,File destLocation, String[] extList){

		boolean status = true;
		RandomAccessFile randomAccessFile = null;
		ISevenZipInArchive inArchive = null;
		
		if(!destLocation.exists())
			destLocation.mkdirs();
		
		File oldFile = new File(destLocation.getAbsolutePath()+"/thom"+zipFile.getName().substring(0, zipFile.getName().indexOf(".zip")));
		if(oldFile.exists()) {
			XMLExtractionJob.log.debug("delete folder as it already exists-->"+oldFile.getAbsolutePath());
			FileUtility fileutil = new FileUtility();
			try {
				fileutil.deleteRecursive(oldFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			randomAccessFile = new RandomAccessFile(zipFile, "r");
			inArchive = SevenZip.openInArchive(null, // autodetect archive type
					new RandomAccessFileInStream(randomAccessFile));

			ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
			for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
				boolean processExt = false;
				if(extList!=null){
					for(String ext : extList){
						if(item.getPath().toLowerCase().endsWith(ext.toLowerCase())){
							processExt = true;
							break;
						}
					}
				}else{
					processExt  = true;
				}
				if(!processExt)
					continue;
				
				File destFile = new File(destLocation, item.getPath());
				if(destFile!=null)
					destFile.getParentFile().mkdirs();
				if (!item.isFolder()) {
					ExtractOperationResult result;
					MyISequentialOutStream myISequentialOutStream = new MyISequentialOutStream(destFile);
					result = item.extractSlow(myISequentialOutStream);
					if (result == ExtractOperationResult.OK) {
//						System.out.println("item extracted successfully ::> "+destFile.getAbsolutePath());
						status = true;
					} else {
						System.err.println("Error extracting item: " + result);
						status = false;
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error occurs: " + e);
		} finally {
			if (inArchive != null) {
				try {
					inArchive.close();
				} catch (SevenZipException e) {
					System.err.println("Error closing archive: " + e);
				}
			}
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					System.err.println("Error closing file: " + e);
				}
			}
		}

		return status;
	}

	public static void main(String[] args) {

		File srcFile = new File("C:\\Journals\\11-2-2015\\Done\\02618292.zip");
		
//		File[] files = srcFile.listFiles();
//		for(File file : files ){
//		}
		extractZip(srcFile, new File("C:\\Journals\\extractedZipTemp") , new String[]{".xml"});

	}

}
