package com.td.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author 74414
 *
 */
public class ExtensionFilter implements FileFilter {

	private String[] extList;

	public ExtensionFilter(String[] extList){
		this.extList = extList;
	}

	@Override
	public boolean accept(File file) {
		if(extList!=null){
			String fName = file.getName().toLowerCase();
			for(String ext: extList){
				if(fName.endsWith(ext.toLowerCase())){
					return true;
				}
			}
			return false;
		}else{
			return true;
		}
	}
}
