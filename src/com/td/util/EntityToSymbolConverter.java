package com.td.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Properties;

public class EntityToSymbolConverter {

	private String inbuf="";
		private byte outbuf[]=null;
		private int outbufLength=0;
		private int maxLen=0;
		private Properties entProp=null;

	public Properties load(String arg) {
		Properties p = new Properties();
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(new File(arg));
			p.load(fs);

		} catch (Exception e) {
			System.out.println("Failed to open Property file " + arg);
			e.printStackTrace();
			p = null;
		} finally {
			try {

				if (fs != null) {
					fs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return p;
	}

		public void read(String p1,String p2) {
			entProp=load(p2);
	        Enumeration<?> en = entProp.propertyNames();
	        while(en.hasMoreElements()) {
	            String name = (String)en.nextElement();
	            
	            if (maxLen<name.length()) maxLen=name.length();
	        }

			String s=null;
			try {
			   File f1=new File(p1);
			   outbuf=new byte[2*(int)f1.length()];
			   BufferedReader in = new BufferedReader(new FileReader(p1));
			   String sep="";
			   while (((s=in.readLine())!=null)) {
				
			   	  inbuf=inbuf+sep+s;
			   	  sep="\r\n";
			   }
			   in.close();
			} catch (Exception e) {
				e.printStackTrace();
				inbuf=null;
				return;
			}
		
		}

	public void write(String arg) {
		FileOutputStream ps = null;
		try {
			ps = new FileOutputStream(arg);
			
			ps.write(outbuf, 0, outbufLength);
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

		public String utf8(int c) {
			char b1, b2, b3, b4;
			if (c<0x80)
				return ""+((char)c);
			else if (c<0x0800) {
				b1 = (char)((c>>6  & 0x1F | 0xC0)& 0xFF);
				b2 = (char)((c>>0  & 0x3F | 0x80)& 0xFF);
				return ""+b1+""+b2;
			} else if (c<0x010000) {
				b1 = (char)((c>>12 & 0x0F | 0xE0)& 0xFF);
				b2 = (char)((c>>6  & 0x3F | 0x80)& 0xFF);
				b3 = (char)((c>>0  & 0x3F | 0x80)& 0xFF);
				return ""+b1+""+b2+""+b3;
			} else if (c<0x110000) {
				b1 = (char)((c>>18 & 0x07 | 0xF0)& 0xFF);
				b2 = (char)((c>>12 & 0x3F | 0x80)& 0xFF);
				b3 = (char)((c>>6  & 0x3F | 0x80)& 0xFF);
				b4 = (char)((c>>0  & 0x3F | 0x80)& 0xFF);
				return ""+b1+""+b2+""+b3+""+b4;
			}
			return null;
		}

		public int check(int p1) {
		
			StringBuilder srch=new StringBuilder("");
			String r=null;
			char ch=0;
			for (int i=p1;i<inbuf.length() && i<p1+maxLen;i++) {

				srch.append(ch=inbuf.charAt(i));
				if (ch==';') break;
			}
			if (ch==';') {
				r=entProp.getProperty(srch.toString());
				
				if (r==null && srch.toString().charAt(1)=='#' && srch.toString().charAt(2)=='x') {
					r=utf8(Integer.parseInt(srch.toString().substring(3,srch.toString().length()-1),16));
				}else if (r==null && srch.charAt(1)=='#') {
					r=utf8(Integer.parseInt(srch.toString().substring(2,srch.toString().length()-1)));
				}
			}
			if (r!=null) {
				for (int i=0;i<r.length();i++) outbuf[outbufLength++]=(byte)r.charAt(i);
				return p1+srch.toString().length()-1;
			}
			outbuf[outbufLength++]=(byte)'&';
			
			return p1;
		}

		public void convert() {
			for (int i=0;i<inbuf.length();i++) {
				char ch=inbuf.charAt(i);
				if (ch=='&'){
				
					i=check(i);
				}
				else
				    outbuf[outbufLength++]=(byte)ch;
			}
		}

		public void process(String p1, String p2, String p3) {
			read(p1,p3);
			convert();
			write(p2);
		
		}
		public static void main(String arg[]) {
			//new Replace().process(arg[0],arg[1],arg[2]);
			
			
			//.process(temptx1.getAbsolutePath(), filePath+"tx1_ne.xml", ".\\WebContent\\LWWJournal\\EntityMapping.ini");
		//	new EntityToSymbolConverter().process("C:\\Conversion\\MNT_ELSEVIER_JOURNAL_APSUSC_27318_110\\tx1.xml","C:\\Conversion\\MNT_ELSEVIER_JOURNAL_APSUSC_27318_110\\tx1_ne.xml",".\\utf.ini");
			
			new EntityToSymbolConverter().process("D:\\74414\\Asworkspace\\TestJDom\\MNT_ELSEVIER_JOURNAL_ACTHIS_50946_110_tx1.xml", "D:\\74414\\Asworkspace\\TestJDom\\tmp_50946_110_tx1.xml", "Entities.ini");
		}
	}
