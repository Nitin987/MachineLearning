package com.td.jdom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.td.mongodb.bean.Artifact_Book;
import com.td.mongodb.bean.Author;
import com.td.mongodb.bean.Chapter;
import com.td.mongodb.bean.DivisionName;
import com.td.mongodb.bean.Editor;
import com.td.mongodb.bean.Organization;
import com.td.mongodb.bean.Pages;
import com.td.mongodb.dao.ArtifactDao;
import com.td.mongodb.dao.AuthorsDao;
import com.td.mongodb.dao.OrganizationDao;
import com.td.util.NormalCharConvertor;

public class BookParsing {
	private  Artifact_Book artifactObj;
	private Author auth;
	private ClassPathXmlApplicationContext ctx = null;
	private XMLExtractionJob xmlExtractorObj;
	boolean referenceExist = false;
	NormalCharConvertor normalCharConv;
	String [] refArray = {"references","reference","refrencia", "referencias","bibliographie","bibliographies","bibliografia","bibliografias","referenzen","bibliographien","referensi","bibliografi", "referinte","bibliografii"};
	public BookParsing(Element rootElement,ClassPathXmlApplicationContext ctx, XMLExtractionJob xmlExtractorObj) {
		this.ctx = ctx;
		this.xmlExtractorObj = xmlExtractorObj;
		normalCharConv = xmlExtractorObj.normalCharConvertor;
		//artifactObjInsertion(rootElement);
		bookArtifactObjInsertion(rootElement);
	}
	
	public void artifactObjInsertion(Element rootElement) {
	   	 ArtifactDao ad = ctx.getBean("artifactDao",ArtifactDao.class);
		 //create new artifact object
        creatingArtifactObject(rootElement, ad);
        rearInfoParsing(rootElement);   //to check if it contains References and parse corresponding file if it contains that reference
       	itemInfoParsing(rootElement);  
       	topInfoParsing(rootElement);
       	bodyInfoParsing(rootElement);
	   	
       	artifactObj.setType("Book");
       	if(artifactObj.getCreateddate() == null)
       		artifactObj.setCreateddate(new Date());
       	else {
       		artifactObj.setUpdateddate(new Date());
       	}
       	//save into database
       	ad.update_Book(artifactObj);
   }
	
	public void bookArtifactObjInsertion(Element rootElement) {
	   	ArtifactDao ad = ctx.getBean("artifactDao",ArtifactDao.class);
		//create new artifact object
	   	creatingArtifactObject(rootElement, ad);
        //rearInfoParsing(rootElement); //Kuldeep   //to check if it contains References and parse corresponding file if it contains that reference
      	itemInfoParsing(rootElement);  
      	topInfoParsing(rootElement);
      	bodyInfoParsing(rootElement);
	   	
      	artifactObj.setType("Book");
      	if(artifactObj.getCreateddate() == null)
      		artifactObj.setCreateddate(new Date());
      	else {
      		artifactObj.setUpdateddate(new Date());
      	}
      	//save into database
      	ad.update_Book(artifactObj);
  }

	 public void creatingArtifactObject(Element rootElement, ArtifactDao ad) {
		 	Element topInfo = rootElement.getChild("top",Namespace.getNamespace("http://www.elsevier.com/xml/bk/dtd"));
		 	 if(topInfo != null) {
		        Element titleElement = topInfo.getChild("title", Namespace.getNamespace("http://www.elsevier.com/xml/common/dtd"));
		        if(titleElement != null) {
		        	String title = normalCharConv.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(titleElement.getValue()));
					artifactObj = ad.searchByChapterTitle(title, "chapterTitle");
					System.out.println("title->"+title);
		        }
			   	if(artifactObj == null) {
			   		artifactObj = new Artifact_Book();
			   	}
		 	 }else{
		 		artifactObj = new Artifact_Book();
		 	 }
	   }
	 
	public void rearInfoParsing(Element rootElement) {
		  Element rear = rootElement.getChild("rear", Namespace.getNamespace("http://www.elsevier.com/xml/bk/dtd"));
		  if(rear != null) {
			  Element rearPart = rear.getChild("rearpart", Namespace.getNamespace("http://www.elsevier.com/xml/bk/dtd"));
			  if(rearPart != null) {
				  for(Element item : rearPart.getChildren()) {
					  if(item.getName().equals("include-item")) {
						  String pii = "", doi ="", title = "";
						  for(Element itemElement : item.getChildren()) {
							  String nodeName = itemElement.getName().trim();
							  String nodeValue = itemElement.getValue().trim();
//							  System.out.println("rear->"+nodeName+"->"+nodeValue);
							  switch (nodeName) {
							  case "pii" : pii= nodeValue; break;
							  case "doi" : doi = nodeValue; break;
							  case "title" : title = normalCharConv.normalCharConversion(nodeValue); break;
							  }
						  }
						  for(String refString : refArray) {
							  if(title.toLowerCase().contains(refString)) {
								  referenceExist = true;
								  System.out.println("reference exist in rear portion");
								  //todo reference parsing from rear folder of book
								  BookRearRefParsing rearRefObj = new BookRearRefParsing(pii, doi,ctx,xmlExtractorObj);
								  if(!rearRefObj.fileName.equals("") && !rearRefObj.xmlPath.equals("")) {
									  rearRefObj.parseFile();
								  }
								  break;
							  }
						  }
					  }
				  }
			  }
		  }
	}
	 public void itemInfoParsing(Element rootElement) {
       Element iteminfo = rootElement.getChild("info", Namespace.getNamespace("http://www.elsevier.com/xml/bk/dtd"));
       
       List<Element> items = iteminfo.getChildren();
       for (Element element : items) {
       		String nodeName = element.getName().trim();
			String nodeValue = element.getValue().trim();
			System.out.println(nodeName+"->"+nodeValue);
			switch(nodeName) {
				case "pii" : artifactObj.setPii(nodeValue); break;
				case "doi" : artifactObj.setDoi(nodeValue); break;
				//case "isbn" : artifactObj.setIsbn(nodeValue);break;//Kuldeep
				case "issn" : artifactObj.setIssn(nodeValue);break;
			}
		}
	 }
	 
	 public void topInfoParsing(Element rootElement) {
		 Element topInfo = rootElement.getChild("top",Namespace.getNamespace("http://www.elsevier.com/xml/bk/dtd"));
		 if(topInfo != null){
			 List<Author> authors = new ArrayList<Author>();
			for( Element element : topInfo.getChildren()) {
				String nodeName = element.getName().trim();
				String nodeValue = element.getValue().trim();
	//			System.out.println(nodeName+"->"+nodeValue);
				if(nodeName.equals("title")) {
					String title = normalCharConv.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(nodeValue));
					//artifactObj.setTitle(title); //Kuldeep
					System.out.println("title->"+title);
				}
				else if(nodeName.equals("author-group")) {
					authors.add(authorParsing(element));
				}
				else if(nodeName.equals("editors")) {
					Element authorGroupElement = element.getChild("author-group", Namespace.getNamespace("http://www.elsevier.com/xml/common/dtd"));
					if(authorGroupElement != null) {
						authors.add(authorParsing(authorGroupElement));
					}
				}
			}
			//artifactObj.setAuthors(authors);
			
		 }
	 }
	 
	 public Author authorParsing(Element authorGroupElement) {
		 Author authobj=null;
		 List<Organization> orgList = new ArrayList<Organization>();
		 String givenName = "", surname = "", fullName = "", email = "", initial = "";
		 List<String> degrees = null;
		 for(Element authorElement : authorGroupElement.getChildren()) {
			if(authorElement.getName().equals("author")) {
				for (Element namePart : authorElement.getChildren()) {
					String nodeValue = normalCharConv.normalCharConversion(namePart.getValue());
					switch(namePart.getName().trim()) {
						case "given-name" :givenName =nodeValue; break;
						case "surname" : surname = nodeValue; break;
						case "e-address" : email = nodeValue; break;
						case "initials": initial = nodeValue; break;
						case "degrees" : {
							degrees = new ArrayList<String>();
							String[] degreeArray = null;
							if(namePart.getValue().indexOf(",") > -1) {
								degreeArray = nodeValue.split(",");
							}
							else {
								degreeArray = new String[1];
								degreeArray[0] = nodeValue.trim();
							}
							
							degrees = Arrays.asList(ArrayUtils.toString(degreeArray));
						} break;
					}
				}
				fullName = initial+" "+givenName+" "+surname;
				System.out.println("givenName->"+givenName+"** surname->"+surname+"** email->"+email+"** initial->"+initial+"** fullName->"+fullName);
			}
			else if(authorElement.getName().equals("affiliation")) {
				String affiliationValue = xmlExtractorObj.removeWhiteSpaces(authorElement.getValue());
				OrganizationDao od = ctx.getBean("organizationDao", OrganizationDao.class);
				
				Organization orgObj = null;
				
        		orgObj = od.readByName(affiliationValue);
				if(orgObj == null) {
					List<DivisionName> divisionList = new ArrayList<DivisionName>();
					String orgName = normalCharConv.normalCharConversion(affiliationValue), orgAddr = "", orgState = "", orgCountry = "", orgPostalCode = "";                       
					orgObj = new Organization(orgName, divisionList, "", orgAddr,null,  null);
					od.create(orgObj);
				}
				orgList.add(orgObj);
			}
		}
		 AuthorsDao ad = ctx.getBean("authorDao", AuthorsDao.class);
			authobj=null;
			authobj=ad.readByName(fullName.trim());
			if(authobj==null){
				authobj=new Author(false, initial, degrees, "", givenName, surname, "", "", fullName.trim(), "", email, "", null, null);
				ad.create(authobj);
			}
			
			
		 return new Author(false, initial, degrees, "", givenName, surname, "", "", fullName.trim(), "", email, "", null, null);
	 }
	 public void bodyInfoParsing(Element rootElement) {
		 Element bodyElement = rootElement.getChild("body",Namespace.getNamespace("http://www.elsevier.com/xml/bk/dtd"));
		 if(bodyElement != null){
			 List<Chapter> chapters = new ArrayList<Chapter>();
			 for(Element element : bodyElement.getChildren()) {
				 if(element.getName().equals("include-item")) {
					Chapter chapter =  chapterSubInfoParsing(element);
					chapters.add(chapter);
					// Kuldeep
					/*if(!referenceExist) {  //if reference does not exist in rear portion then check in chapter xml
						 BookChapterRefParsing chapterRef = new BookChapterRefParsing(chapter.getPii(),chapter.getDoi(), xmlExtractorObj, ctx);
						 if(!chapterRef.fileName.equals("") && !chapterRef.xmlPath.equals("")) {
							 chapterRef.parseFile();
						 }
					}	*/	
				 }
				 else if(element.getName().equals("section") || element.getName().equals("part")){
					 if(element.getChild("part", Namespace.getNamespace("http://www.elsevier.com/xml/bk/dtd")) != null) {
						 System.out.println("part exist");
						 element = element.getChild("part", Namespace.getNamespace("http://www.elsevier.com/xml/bk/dtd"));
					 }
					 for(Element sectionElement : element.getChildren("include-item",Namespace.getNamespace("http://www.elsevier.com/xml/common/dtd")) ) {
						 if(sectionElement != null) {
							 Chapter chapter =  chapterSubInfoParsing(sectionElement);
							 chapters.add(chapter);
							 // Kuldeep
							 /*if(!referenceExist) {
								 BookChapterRefParsing chapterRef = new BookChapterRefParsing(chapter.getPii(),chapter.getDoi(), xmlExtractorObj, ctx);
								 if(!chapterRef.fileName.equals("") && !chapterRef.xmlPath.equals("")) {
									 chapterRef.parseFile();
								 }
							 }*/
						 }
					 }
				 }
			 }
			 artifactObj.setChapters(chapters); //Kuldeep
		 }
	 }
	 
	 public Chapter chapterSubInfoParsing(Element sectionElement) {
		 String chapterPii = "", chapterDoi = "", chapterTitle = ""; int firstPage  = 0, lastPage = 0;
		 for(Element chapterElement : sectionElement.getChildren()) {
			 String nodeName = chapterElement.getName().trim();
				String nodeValue = normalCharConv.normalCharConversion(chapterElement.getValue().trim());
				System.out.println("chapter:"+nodeName+"->"+nodeValue);
				switch(nodeName) {
					case "pii"   : chapterPii= nodeValue; break;
					case "doi"   : chapterDoi = nodeValue; break;
					case "title" : chapterTitle = xmlExtractorObj.removeWhiteSpaces(nodeValue); break;
					case "pages" : 
									if(firstPage == 0 && lastPage == 0) {
										for(Element pageElement : chapterElement.getChildren()) {
											if(pageElement.getName().equals("first-page")) {
												firstPage = xmlExtractorObj.parseNumber(pageElement.getValue());
											}
											else if(pageElement.getName().equals("last-page")) {
												lastPage = xmlExtractorObj.parseNumber(pageElement.getValue());
											}
										}
									}
					               break;
				}
		 }
		 return new Chapter(chapterTitle, new Pages(firstPage, lastPage), chapterPii, chapterDoi);
	 }
}
