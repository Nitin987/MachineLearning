package com.td.jdom;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.td.mongodb.bean.Artifact;
import com.td.mongodb.bean.Author;
import com.td.mongodb.dao.ArtifactDao;
import com.td.util.NormalCharConvertor;

public class ParsingUtil {
	private static Logger log = Logger.getLogger(ParsingUtil.class);
	private  Artifact artifactObj;
	private Author auth;
	//private ClassPathXmlApplicationContext ctx = null;
	private XMLExtractionJob xmlExtractorObj;
	NormalCharConvertor normalCharConvertor;
	int count=0;
	public ParsingUtil(Element rootElement, ClassPathXmlApplicationContext ctx, XMLExtractionJob xmlExtractorObj) {
		
		
		//this.ctx = ctx;
		this.xmlExtractorObj = xmlExtractorObj;
		normalCharConvertor = xmlExtractorObj.normalCharConvertor;
		String rootElementName = rootElement.getName();
		Element tailElement =  null;
		Element bodyElement=null;
		if(rootElementName.equalsIgnoreCase("article") || rootElementName.equalsIgnoreCase("simple-article")){
			tailElement = rootElement.getChild("tail", Namespace.getNamespace("http://www.elsevier.com/xml/ja/dtd"));
			bodyElement=   rootElement.getChild("body", Namespace.getNamespace("http://www.elsevier.com/xml/ja/dtd"));
			new BodyProcessing(bodyElement,xmlExtractorObj,ctx);
			new ReferenceParsing(tailElement,xmlExtractorObj,ctx);
	        artifactObjInsertion(rootElement,ctx);
		}	
		else{
			if(rootElementName.equalsIgnoreCase("bibliography")){
				new ReferenceParsing(rootElement,xmlExtractorObj,ctx);
			}else{
				if(rootElementName.equalsIgnoreCase("chapter")){
					tailElement = rootElement.getChild("bibliography", Namespace.getNamespace("http://www.elsevier.com/xml/common/dtd"));
					if(tailElement != null){
						new ReferenceParsing(tailElement,xmlExtractorObj,ctx);
					}
				}else if(rootElementName.equalsIgnoreCase("book")){
					new BookParsing(rootElement, ctx, xmlExtractorObj);
				}
			}
		}
	}
	
	public void artifactObjInsertion(Element rootElement, ClassPathXmlApplicationContext ctx) {
	   	 ArtifactDao ad = ctx.getBean("artifactDao",ArtifactDao.class);
	    //create new artifact object
	   	creatingArtifactObject(rootElement, ad);
	    //setting header info of artifact
	    headerInfoParsing(rootElement);
	   //setting item info of artifact object
	   	itemInfoParsing(rootElement);
	   	artifactObj.setType("Journal");
	   	if(artifactObj.getCreateddate() == null)
	   		artifactObj.setCreateddate(new Date());
	   	else {
	   		artifactObj.setUpdateddate(new Date());
	   	}
	   	//save into database
	   	ad.update(artifactObj);
   }
   
   public void creatingArtifactObject(Element rootElement, ArtifactDao ad) {
		Element headinfo = null;
	   	List<Element> children = rootElement.getChildren();
	   	for(Element childElement : children) {
	   		if(childElement.getName().contains("head")) {
	   			 headinfo = childElement;
	   		}
	   	}
	   	if(headinfo != null) {
	        Element titleElement = headinfo.getChild("title", Namespace.getNamespace("http://www.elsevier.com/xml/common/dtd"));
	        if(titleElement != null) {
	        	List<Content> titleContent = titleElement.getContent();
        		StringBuilder title = new StringBuilder();
        		for (Content element : titleContent) {
        			title.append(element.getValue().trim()+" ");
        		}
        		String titleString = title.toString();
        		titleString = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(titleString));
        		artifactObj = ad.searchByArticleTitle(titleString, "articleTitle");
	        }
	   	}
	   	if(artifactObj == null) {
	   		artifactObj = new Artifact();
	   	}
	   	
   }
   
   public void headerInfoParsing(Element rootElement) {
   	Element headinfo = null;
   	List<Element> children = rootElement.getChildren();
   	for(Element childElement : children) {
   		if(childElement.getName().contains("head")) {
   			 headinfo = childElement;
   		}
   	}
   	
   	//Element headinfo = rootElement.getChildren("head", Namespace.getNamespace("http://www.elsevier.com/xml/ja/dtd")).get(0);
   	
   	if(headinfo != null) {
	        List<Element> headerList = headinfo.getChildren();
	        for (Element headerElement : headerList) {
	        	if(headerElement.getName().equals("title")) {
	        		List<Content> titleContent = headerElement.getContent();
	        		StringBuilder title = new StringBuilder();
	        		 for (Content element : titleContent) {
	        			 title.append(element.getValue().trim()+" ");
	        			}
	        		 String titleString = title.toString();
	        		 titleString = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(titleString));
	        		 
	        		 artifactObj.setArticleTitle(titleString);
	        		 log.debug("title->"+titleString);
	        	}
	        	else if(headerElement.getName().equals("author-group")) {
	        		List<Author> authors = xmlExtractorObj.authorParsing(artifactObj,headerElement);
	        		//artifactObj.setAuthors(authors);
	        		
	        	}
	        	/*else if(headerElement.getName().equals("date-received")){
	        		
	        	}
	        	else if(headerElement.getName().equals("date-revised")){
	        		
	        	}
	        	else if(headerElement.getName().equals("date-accepted")){
	        		int date = xmlExtractorObj.parseNumber(headerElement.getAttribute("day").getValue());
	        		int month = xmlExtractorObj.parseNumber(headerElement.getAttribute("month").getValue());
	        		int year =  xmlExtractorObj.parseNumber(headerElement.getAttribute("year").getValue());
	        		log.debug("accepted date->"+date+"/"+month+"/"+year);

	        		artifactObj.setPublisher(new Publisher("", "", new CustomDate(year, month, date)));
	        	}*/
			}
   	}
   }
   
   public void itemInfoParsing(Element rootElement) {
       Element iteminfo = rootElement.getChild("item-info", Namespace.getNamespace("http://www.elsevier.com/xml/ja/dtd"));
       
       if(iteminfo != null){
	       List<Element> items = iteminfo.getChildren();
	       for (Element element : items) {
	       	String nodeName = element.getName().trim();
				String nodeValue = element.getValue().trim();
				log.debug(nodeName+"->"+nodeValue);
				switch(nodeName) {
					case "pii" : artifactObj.setPii(nodeValue); break;
					case "doi" : artifactObj.setDoi(nodeValue); break;
				}
			}
       }
   }
}
