package com.td.util;

import java.io.File;
import java.io.FileNotFoundException;

public class FileUtility {

	public File[] listFiles(String path) {
		File[] listOfFiles = null;
		File folder = new File(path);
		if(folder != null) {
			listOfFiles = folder.listFiles(); 
		}
	  return listOfFiles;
	}
	public boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && deleteRecursive(f);
            }
        }
//        System.out.println("deleting file-->"+path.getAbsolutePath());
        return ret && path.delete();
    }
	
	public boolean moveFile(String src, String dest) {
		try {
			File file = new File(src);
			
			File moveFile = new File(dest+ file.getName());
			moveFile.getParentFile().mkdirs();
			if(file.renameTo(moveFile)){
				return true;
    	   }else{
    		   return false;
    	   }
		}
		catch(Exception ie) {
			ie.printStackTrace();
			return false;
		}
	}
	
	public static void main(String arg[]) {
		FileUtility fileUtil = new FileUtility();
		//\\\\172.16.2.128\\XML_FOR_REF_DATABASE\\elsevier_journal_acvd_821_tx1.xml
		System.out.println(fileUtil.moveFile("\\\\172.16.2.128\\XML_FOR_REF_DATABASE\\elsevier_journal_pep_69466_tx1.xml", "C:\\SUCCESS\\"));
	}
}
