package com.td.jdom;

import java.io.IOException;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BookChapterRefParsing {
	public String xmlPath = "";
	public String fileName = "";
	XMLExtractionJob xmlExtractorObj;
	ClassPathXmlApplicationContext ctx;

	public BookChapterRefParsing(String pii, String doi,XMLExtractionJob xmlExtractorObj, ClassPathXmlApplicationContext ctx) {
		this.xmlExtractorObj = xmlExtractorObj;
		this.ctx = ctx;
		fileName = pii.replaceAll("\\-", "");
		fileName = fileName.replaceAll("\\.", "");
		fileName+= "\\main.xml";
		xmlPath = xmlExtractorObj.path+"BODY\\";
		System.out.println("chapter reference file is-->"+xmlPath+fileName);
	}
	
	public void parseFile() {
		try {
			
			String fileString = xmlExtractorObj.replaceFileByString(xmlPath+fileName);
	        // Use a SAX builder
	        SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
	        builder.setEntityResolver(new CustomEntityResolver(XMLExtractionJob.DTD_PATH));
	        // build a JDOM2 Document using the SAXBuilder.
	        Document jdomDoc;
	        if(fileString != null) {
	        	jdomDoc = builder.build(new StringReader(fileString));
	        }else {
	        	jdomDoc= builder.build(xmlPath+fileName);
	        }
	        //get the root element
	        Element rootElement = jdomDoc.getRootElement();
	        Element bibElement = rootElement.getChild("bibliography", Namespace.getNamespace("http://www.elsevier.com/xml/common/dtd"));
			new ReferenceParsing(bibElement, xmlExtractorObj, ctx);
				
	   
    	} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
