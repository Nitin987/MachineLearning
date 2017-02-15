package com.td.util;

import java.io.File;
import java.io.FileOutputStream;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;

public class MyISequentialOutStream implements ISequentialOutStream{

	private File destFile;

	public MyISequentialOutStream(File destFile) {
		this.destFile = destFile;
	}

	@Override
	public int write(byte[] data) throws SevenZipException {

		FileOutputStream destStream = null;
		try {
			destStream = new FileOutputStream(destFile,true);
 			destStream.write(data);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally{
			try {
				if(destStream!=null)
					destStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data.length;
	}
}
