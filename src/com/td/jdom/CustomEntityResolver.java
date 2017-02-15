package com.td.jdom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.td.util.ExtensionFilter;

public class CustomEntityResolver implements EntityResolver {
	String mainDTDPath = "";
	public CustomEntityResolver(String mainDTDPath) {
		this.mainDTDPath = mainDTDPath;
	}
/*	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if(publicId !=null && publicId.contains("520.dtd")){
			System.out.println("DTD 520 in public");
			return new InputSource("art520/art520.dtd");		
//			InputStream dtdStream = CustomEntityResolver.class.getResourceAsStream("/art520/art520.dtd");
//			return new InputSource(dtdStream);
		}
		else if(systemId != null && systemId.contains("520.dtd")){
			System.out.println("DTD 520 in system");
			return new InputSource("art520/art520.dtd");
//			InputStream dtdStream = CustomEntityResolver.class.getResourceAsStream("/art520/art520.dtd");
//			return new InputSource(dtdStream);
		}
//		 else if (systemId.endsWith("common120.ent")) {
//			 System.out.println("ent common120 in system id");
//             return new InputSource(getClass().getResourceAsStream("/art520/common120.ent"));
//        } 

		if(publicId !=null && publicId.contains("530.dtd")){
			System.out.println("DTD 530 in public");
			return new InputSource("ELSDTD/book530.dtd");			
		}
		else if(systemId != null && systemId.contains("530.dtd")){
			System.out.println("DTD 530 in system");
			return new InputSource("ELSDTD/book530.dtd");
		}
		return null;
	}*/

	public InputSource resolveEntity (String publicId, String systemId){
		String dtdPath="";

		dtdPath = dtdFinder(new File(mainDTDPath), systemId);
//		System.out.println("########################### "+systemId+"(systemId) using dtdFinder at::> "+dtdPath+" ########################### ");

		if (dtdPath!=null) return new InputSource(dtdPath);
		return null;
	}

	public String dtdFinder(File dtdDir, String dtdToFind){

		if(new File(dtdToFind).exists())
			return dtdToFind;
		
		String dtdToFindName = new File(dtdToFind).getName();
		String requiredDtdFilePath = null;
		File[] dtdFileList = dtdDir.listFiles();
		if(dtdFileList!=null && dtdFileList.length>0){
			for(File dtdFile : dtdFileList){
				if(dtdFile.isDirectory()){
					requiredDtdFilePath = dtdFinder(dtdFile, dtdToFindName);
					if(requiredDtdFilePath!=null)
						break;
				}else{
					if(dtdFile.getName().equalsIgnoreCase(dtdToFindName)){
						requiredDtdFilePath = dtdFile.getAbsolutePath();
						break;
					}
				}
			}
		}
		return requiredDtdFilePath;
	}
}
