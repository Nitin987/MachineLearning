package com.td.jdom;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.td.mongodb.bean.Artifact;
import com.td.mongodb.bean.Artifact_Book;
import com.td.mongodb.bean.Author;
import com.td.mongodb.bean.Editor;
import com.td.mongodb.bean.Publisher;
import com.td.mongodb.dao.ArtifactDao;
import com.td.mongodb.dao.AuthorsDao;
import com.td.mongodb.dao.PublisherDao;
import com.td.util.NormalCharConvertor;

public class ReferenceParsing {
	private static Logger log = Logger.getLogger(ReferenceParsing.class);
	XMLExtractionJob xmlExtractorObj;
	List<Element> bibReferenceList;
	NormalCharConvertor normalCharConvertor;
	public ReferenceParsing(Element element, XMLExtractionJob xmlExtractorObj, ClassPathXmlApplicationContext ctx) {
		Publisher publ=new Publisher();
		this.xmlExtractorObj = xmlExtractorObj;
		normalCharConvertor = xmlExtractorObj.normalCharConvertor;
		bibReferenceList = new ArrayList<Element>();
		if(element != null)
			getRefList("reference", element);
		log.debug("ref size-->"+bibReferenceList.size());
		XMLExtractionJob.refCount += bibReferenceList.size();
		int counterATNotFoundBook=0, counterInsertedBook=0, counterUpdatedBook=0;
		int counterATNotFoundArt=0, counterInsertedArt=0, counterUpdatedArt=0;
		
		for(Element refElement : bibReferenceList) {
			try {
				ArtifactDao ad = ctx.getBean("artifactDao", ArtifactDao.class);
				AuthorsDao au = ctx.getBean("authorDao", AuthorsDao.class);
				PublisherDao pd = ctx.getBean("publishDao", PublisherDao.class);
				boolean bookTypeReference = checkBookTypeReference(refElement);
					if(bookTypeReference)
					{
						Author auInd=new Author();
						
						log.debug("****adding book artifact***");
						Artifact_Book obj = new Artifact_Book();
						obj.setType("Book");
						parseBookRef(refElement, obj,auInd,publ);
						if(auInd!=null){
							Author authorInd=null;
							if(auInd.getFullName()!=null){
								authorInd=au.readByName(auInd.getFullName());
							
						if(authorInd==null){
							log.debug("Inserting Author into Repository");
							au.create(auInd);
							counterInsertedArt = counterInsertedArt+1;
							
						}
							}	
						}
						//if(obj.getChapterTitle() != null ) {
						if(obj != null ) {
							Artifact_Book artifactObj = null;
							if(obj.getChapterTitle() != null ) {
								artifactObj = ad.searchByChapterTitle(obj.getChapterTitle(), "chapterTitle");
							}
							if(artifactObj == null) {   //check if artifact with same title already exist or not
								obj.setCreateddate(new Date());
								ad.create_Book(obj);
								counterInsertedBook = counterInsertedBook+1;
							}
							else {
								counterUpdatedBook = counterUpdatedBook+1;
								updateArtifactBookObject(artifactObj, obj, ad);
							}
						}
						/*else {
							counterATNotFoundBook=counterATNotFoundBook+1;
							log.debug("title is not found with this artifact");
						}*/
					
						Publisher publish=null;
						if(publ.getName()!=null){
							publish=pd.searchByPName(publ.getName());
							if(publish==null){
								log.debug("Inserting Publisher into Repository");
								pd.create(publ);
							}
						}
						
						
					}else{
						log.debug("****adding artifact***");
						Artifact obj = new Artifact();
						Author auInd=new Author();
						obj.setType("Journal");
						parseRef(refElement, obj,auInd);
						if(auInd!=null){
							Author authorInd=null;
							if(auInd.getFullName()!=null){
								authorInd=au.readByName(auInd.getFullName());
							
						if(authorInd==null){
							log.debug("Inserting Author into Repository");
							au.create(auInd);
							counterInsertedArt = counterInsertedArt+1;
							
						}
							}	
						}
						//if(obj.getArticleTitle() != null ) {
						if(obj != null ) {
							Artifact artifactObj = null;
							if(obj.getArticleTitle() != null ) {
								artifactObj = ad.searchByArticleTitle(obj.getArticleTitle(), "articleTitle");
							}
							if(artifactObj == null) {   //check if artifact with same title already exist or not
								log.debug("Inserting new record");
								obj.setCreateddate(new Date());
								ad.create_Article(obj);
								//ad.create_Authors(authorInd);
								counterInsertedArt = counterInsertedArt+1;
							}
							else {
								counterUpdatedArt = counterUpdatedArt+1;
								log.debug("Updating existing record");
								updateArtifactObject(artifactObj, obj, ad);
							}
						}
						/*else {
							counterATNotFoundArt=counterATNotFoundArt+1;
							log.debug("title is not found with this artifact");
						}*/
						obj = null;
						auInd=null;
					}
			}
			catch(Exception e) {
				log.error(e.getMessage());
			}
		}
		System.out.println("Counter Article: "+counterATNotFoundArt+", "+counterInsertedArt+", "+counterUpdatedArt);
		System.out.println("Counter Book: "+counterATNotFoundBook+", "+counterInsertedBook+", "+counterUpdatedBook);
	}
	
	private boolean checkBookTypeReference(Element refElement) {
		boolean foundBookTypeReference = false;
	    for (Element info : refElement.getChildren("host", Namespace.getNamespace("http://www.elsevier.com/xml/common/struct-bib/dtd"))) {
	        if(info.getChildren("edited-book", Namespace.getNamespace("http://www.elsevier.com/xml/common/struct-bib/dtd")).size() > 0) {
	        	foundBookTypeReference = true;
	        }
	        if(info.getChildren("book", Namespace.getNamespace("http://www.elsevier.com/xml/common/struct-bib/dtd")).size() > 0) {
	        	foundBookTypeReference = true;
	        }
	    }
		
		return foundBookTypeReference;
	}

	public void updateArtifactBookObject(Artifact_Book artifactObj, Artifact_Book obj, ArtifactDao ad) {

		List<Author> authors;
		if(artifactObj.getAuthors() != null) {
			authors = artifactObj.getAuthors();
			for(Author author : obj.getAuthors()) {
				boolean authorExist = false;
				for(Author auth : authors) {
					if(auth.getFamily_name().equalsIgnoreCase(author.getFamily_name()) ){ //added check in case if artifact object already exist in db
						/*if(auth.getOrganization() == null && author.getOrganization() != null) {
							auth.setOrganization(author.getOrganization());
						}*/
						if(auth.getDegrees() == null && author.getDegrees() != null) {
							auth.setDegrees(author.getDegrees());
						}
						if(auth.getPrefix() == null || auth.getPrefix().equals("")){
							auth.setPrefix(author.getPrefix());
						}
						if(auth.getAlt_name() == null || auth.getAlt_name().equals("")){
							auth.setAlt_name(author.getAlt_name());
						}
						if(auth.getInitials() == null || auth.getInitials().equals("")){
							auth.setInitials(author.getInitials());
						}
						if(auth.getOrcid() == null || auth.getOrcid().equals("")){
							auth.setOrcid(author.getOrcid());
						}
						if(auth.getEmail() == null || auth.getEmail().equals("")){
							auth.setEmail(author.getEmail());
						}
						
						if(auth.getSuffix() == null || auth.getSuffix().equals("")){
							auth.setSuffix(author.getSuffix());
						}
						if(auth.getUrl() == null || auth.getUrl().equals("")){
							auth.setUrl(author.getUrl());
						}
						/*if(auth.getAddress() == null ) {
							auth.setAddress(author.getAddress());
						}*/
						auth.setCorrespAuthor(author.getCorrespAuthor());
						authorExist = true;
						break;
					}
				}	
				if(!authorExist || authors.size() == 0) {
					authors.add(author);
				}
			}
		}
		else {
			authors = obj.getAuthors();
		}
			
		//artifactObj.setAuthors(authors);
		
		if(artifactObj.getIssue() == null) {
			artifactObj.setIssue(obj.getIssue());
		}
		
		artifactObj.setUpdateddate(new Date());
		
		ad.update_Book(artifactObj);
	}
	
	public void updateArtifactObject(Artifact artifactObj, Artifact obj, ArtifactDao ad) {

		List<Author> authors;
		if(artifactObj.getAuthors() != null) {
			authors = artifactObj.getAuthors();
			for(Author author : obj.getAuthors()) {
				boolean authorExist = false;
				for(Author auth : authors) {
					if(auth.getFamily_name().equalsIgnoreCase(author.getFamily_name()) ){ //added check in case if artifact object already exist in db
						/*if(auth.getOrganization() == null && author.getOrganization() != null) {
							auth.setOrganization(author.getOrganization());
						}*/
						if(auth.getDegrees() == null && author.getDegrees() != null) {
							auth.setDegrees(author.getDegrees());
						}
						if(auth.getPrefix() == null || auth.getPrefix().equals("")){
							auth.setPrefix(author.getPrefix());
						}
						if(auth.getAlt_name() == null || auth.getAlt_name().equals("")){
							auth.setAlt_name(author.getAlt_name());
						}
						if(auth.getInitials() == null || auth.getInitials().equals("")){
							auth.setInitials(author.getInitials());
						}
						if(auth.getOrcid() == null || auth.getOrcid().equals("")){
							auth.setOrcid(author.getOrcid());
						}
						if(auth.getEmail() == null || auth.getEmail().equals("")){
							auth.setEmail(author.getEmail());
						}
						
						if(auth.getSuffix() == null || auth.getSuffix().equals("")){
							auth.setSuffix(author.getSuffix());
						}
						if(auth.getUrl() == null || auth.getUrl().equals("")){
							auth.setUrl(author.getUrl());
						}
						/*if(auth.getAddress() == null ) {
							auth.setAddress(author.getAddress());
						}*/
						auth.setCorrespAuthor(author.getCorrespAuthor());
						authorExist = true;
						break;
					}
				}	
				if(!authorExist || authors.size() == 0) {
					authors.add(author);
				}
			}
		}
		else {
			authors = obj.getAuthors();
		}
			
		//artifactObj.setAuthors(authors);
		
		if(artifactObj.getIssue() == null) {
			artifactObj.setIssue(obj.getIssue());
		}
		
		artifactObj.setUpdateddate(new Date());
		
		ad.update(artifactObj);
	}
	
	public void getRefList(String which,Element startElem) {
	    List<Element> nodes = startElem.getChildren();
	    Iterator<Element> iter = nodes.iterator();

	    if(startElem.getName().equals(which))
	    	bibReferenceList.add(startElem);
	    
		while (iter.hasNext()) {
			Element elem = iter.next();
			getRefList(which, elem);
		}
	 }
	 
	 public void parseRef(Element refElement, Artifact obj,Author auInd) {
			 
			 Element hostElement = refElement.getChild("host", Namespace.getNamespace("http://www.elsevier.com/xml/common/struct-bib/dtd"));
			 if(hostElement != null ) {
				 for(Element hostPartElement : hostElement.getChildren()) {
					 if(hostPartElement.getName().equals("issue")) {
						 hostIssueElementParsing(hostPartElement, obj,auInd);
					 }
					 else if(hostPartElement.getName().equals("pages")) {
						 String firstpage = "", lastpage  = "";
						 for(Element pageElement : hostPartElement.getChildren()) {
							 String nodeName = pageElement.getName();
							 String nodeValue = pageElement.getValue();
							 log.debug(nodeName+"-->"+nodeValue);
							 
							 if(nodeName.equals("first-page")) {
								 firstpage = nodeValue;
							 }
							 else if(nodeName.equals("last-page")){
								 lastpage  = nodeValue;
							 }
						 }
						 obj.setFirstPage(firstpage);
						 obj.setLastPage(lastpage);
					 }else if(hostPartElement.getName().equals("doi")) {
						 String doi = hostPartElement.getValue();
						 obj.setDoi(doi);
					 }
				 }
			 }
			 
			 Element contrElement = refElement.getChild("contribution", Namespace.getNamespace("http://www.elsevier.com/xml/common/struct-bib/dtd"));
			 if(contrElement != null) {
				 for(Element contrPartElement : contrElement.getChildren()) {
					 if(contrPartElement.getName().equals("title")) {
						 String title = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(contrPartElement.getValue()));
						 obj.setArticleTitle(title);
						 log.debug("set As article title->"+title);
					 }
					 else if(contrPartElement.getName().equals("authors")) {
						 List<Author> authors = xmlExtractorObj.authorParsing(obj,contrPartElement);
						// auIdent.setAuthors(authors);
					 }
				 }
			 }
	 }
	 
	 public void parseBookRef(Element refElement, Artifact_Book obj,Author auInd, Publisher publ) {
		 
			 Element hostElement = refElement.getChild("host", Namespace.getNamespace("http://www.elsevier.com/xml/common/struct-bib/dtd"));
			 if(hostElement != null ) {
				 for(Element hostPartElement : hostElement.getChildren()) {
					 if(hostPartElement.getName().equals("issue")) {
						 hostBookIssueElementParsing(hostPartElement, obj,auInd);
					 }
					 else if(hostPartElement.getName().equals("edited-book")) {
						 hostEditedBookParsing(hostPartElement, obj,auInd,publ);
					 }
					 else if(hostPartElement.getName().equals("book")) {
						 hostBookParsing(hostPartElement, obj,auInd,publ);
					 }
					 else if(hostPartElement.getName().equals("pages")) {
						 String firstpage = "", lastpage  = "";
						 for(Element pageElement : hostPartElement.getChildren()) {
							 String nodeName = pageElement.getName();
							 String nodeValue = pageElement.getValue();
							 log.debug(nodeName+"-->"+nodeValue);
							 
							 if(nodeName.equals("first-page")) {
								 firstpage = nodeValue;
							 }
							 else if(nodeName.equals("last-page")){
								 lastpage  = nodeValue;
							 }
						 }
						 obj.setFirstPage(firstpage);
						 obj.setLastPage(lastpage);
					 }else if(hostPartElement.getName().equals("doi")) {
						 String doi = hostPartElement.getValue();
						 obj.setDoi(doi);
					 }
				 }
			 }
			 
			 Element contrElement = refElement.getChild("contribution", Namespace.getNamespace("http://www.elsevier.com/xml/common/struct-bib/dtd"));
			 if(contrElement != null) {
				 for(Element contrPartElement : contrElement.getChildren()) {
					 if(contrPartElement.getName().equals("title")) {
						 String title = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(contrPartElement.getValue()));
						 if(obj.getType() != null && obj.getType().equalsIgnoreCase("Book")){
							/*if(obj.getBookTitle() != null && !obj.getBookTitle().trim().equals("")){  //if title is already set in host element then this would be chapter title
								 obj.setChapterTitle(title);
								 log.debug("set As chapter title->"+title);
							 }
							else if(obj.getEditedBookTitle() != null && !obj.getEditedBookTitle().equals("")){
								 obj.setChapterTitle(title);
								 log.debug("set As chapter title->"+title);
							 }
							 else {      //this is the case if in host's edited-book/book element title element was not there, then this title will be book title
								 obj.setBookTitle(title);
								 log.debug("set As book title->"+title);
							 }*/
							 obj.setChapterTitle(title);
						 }
					 }
					 else if(contrPartElement.getName().equals("authors")) {
						 List<Author> authors = xmlExtractorObj.authorParsing(obj,contrPartElement);
						// obj.setAuthors(authors);
					 }
				 }
			 }
	 }
	 
	 /*
	  * issue element parsing
	  */
	 public void hostIssueElementParsing(Element hostElement, Artifact obj,Author auInd) {
		 List<Editor> editorList = null;
		 
		 for(Element issueElement : hostElement.getChildren()) {
			 if (issueElement.getName().equals("editors")) {
					editorList = new ArrayList<Editor>();
					for(Element editorElement : issueElement.getChildren()) {
						if(editorElement.getName().equals("editor")) {
							String givenName = "", surName = "", orcId = ""; 
							for(Element editorName : editorElement.getChildren()) {
								if(editorName.getName().equals("given-name")) {
									givenName = normalCharConvertor.normalCharConversion(editorName.getValue());
								}
								else if(editorName.getName().equals("surname")) {
									surName = normalCharConvertor.normalCharConversion(editorName.getValue());
								}
							}
							if(editorElement.getAttribute("orcid") != null) {
								log.debug("att orcid is-->"+orcId);
								orcId = editorElement.getAttribute("orcid").getValue(); 
							}
							editorList.add(new Editor(givenName, surName, orcId));
						}
					}
					auInd.setEditorlist(editorList);
			}
			 else if(issueElement.getName().equals("series")) {
				 for(Element seriesElement : issueElement.getChildren()) {
					 if(seriesElement.getName().equals("volume-nr")) {
						 String volNo = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(seriesElement.getValue()));
						 obj.setVolume(volNo);
					 }
					 else if(seriesElement.getName().equals("title")) {
						 String seriesTitle = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(seriesElement.getValue()));
						 obj.setJournalTitle(seriesTitle);
					 }
					 else if(seriesElement.getName().equals("issn")) {
						 String issn = seriesElement.getValue();
						 obj.setIssn(issn);
					 }
				 }
			 }
			 else if(issueElement.getName().equals("title")) {
				 String title = normalCharConvertor.normalCharConversion(issueElement.getValue());
				 obj.setJournalTitle(title);
			 }
			 else if(issueElement.getName().equals("date")) {
				String date = issueElement.getValue();
				obj.setYear(date);
			 }
			 else if(issueElement.getName().equals("issue-nr")) {
				 String issueNr = issueElement.getValue();
				 obj.setIssue(issueNr);
			 }
		 }
	 }
	 
	 public void hostBookIssueElementParsing(Element hostElement, Artifact_Book obj,Author auInd) {
		 List<Editor> editorList = null;
		 for(Element issueElement : hostElement.getChildren()) {
			 if (issueElement.getName().equals("editors")) {
					editorList = new ArrayList<Editor>();
					for(Element editorElement : issueElement.getChildren()) {
						if(editorElement.getName().equals("editor")) {
							String givenName = "", surName = "", orcId = ""; 
							for(Element editorName : editorElement.getChildren()) {
								if(editorName.getName().equals("given-name")) {
									givenName = normalCharConvertor.normalCharConversion(editorName.getValue());
								}
								else if(editorName.getName().equals("surname")) {
									surName = normalCharConvertor.normalCharConversion(editorName.getValue());
								}
							}
							if(editorElement.getAttribute("orcid") != null) {
								log.debug("att orcid is-->"+orcId);
								orcId = editorElement.getAttribute("orcid").getValue(); 
							}
							editorList.add(new Editor(givenName, surName, orcId));
						}
					}
					//obj.setEditors(editorList);
					auInd.setEditorlist(editorList);
			}
			 else if(issueElement.getName().equals("series")) {
				 for(Element seriesElement : issueElement.getChildren()) {
					 if(seriesElement.getName().equals("volume-nr")) {
						 String volNo = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(seriesElement.getValue()));
						 obj.setVolume(volNo);
					 }
					 else if(seriesElement.getName().equals("title")) {
						 String seriesTitle = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(seriesElement.getValue()));
						 obj.setBookTitle(seriesTitle);
					 }
					 else if(seriesElement.getName().equals("issn")) {
						 String issn = seriesElement.getValue();
						 obj.setIssn(issn);
					 }
				 }
			 }
			 else if(issueElement.getName().equals("title")) {
				 String title = normalCharConvertor.normalCharConversion(issueElement.getValue());
				 obj.setBookTitle(title);
			 }
			 else if(issueElement.getName().equals("date")) {
				String date = issueElement.getValue();
				obj.setYear(date);
			 }
			 else if(issueElement.getName().equals("issue-nr")) {
				 String issueNr = issueElement.getValue();
				 obj.setIssue(issueNr);
			 }
		 }
	 }
	 
	 /*
	  * host edited-book element parsing
	  */
	 public void hostEditedBookParsing(Element hostElement, Artifact_Book obj,Author auInd,Publisher publ) {
		 List<Editor> editorList = null;
		 for(Element issueElement : hostElement.getChildren()) {
			if (issueElement.getName().equals("editors")) {
				editorList = new ArrayList<Editor>();
				for(Element editorElement : issueElement.getChildren()) {
					if(editorElement.getName().equals("editor")) {
						String givenName = "", surName = "", orcId = ""; 
						for(Element editorName : editorElement.getChildren()) {
							if(editorName.getName().equals("given-name")) {
								givenName = normalCharConvertor.normalCharConversion(editorName.getValue());
							}
							else if(editorName.getName().equals("surname")) {
								surName = normalCharConvertor.normalCharConversion(editorName.getValue());
							}
						}
						if(editorElement.getAttribute("orcid") != null) {
							log.debug("att orcid is-->"+orcId);
							orcId = editorElement.getAttribute("orcid").getValue(); 
						}
						editorList.add(new Editor(givenName, surName, orcId));
					}
				}
				//obj.setEditors(editorList);
				auInd.setEditorlist(editorList);
			}
			else if(issueElement.getName().equals("book-series")) {
				List<Editor> seriesEditorList = null;
				for(Element bookSeriesElement : issueElement.getChildren()) {
					if(bookSeriesElement.getName().equals("editors")) {
						seriesEditorList = new ArrayList<Editor>();
						for(Element editorElement : issueElement.getChildren()) {
							if(editorElement.getName().equals("editor")) {
								String givenName = "", surName = "", orcId = ""; 
								for(Element editorName : editorElement.getChildren()) {
									if(editorName.getName().equals("given-name")) {
										givenName = normalCharConvertor.normalCharConversion(editorName.getValue());
									}
									else if(editorName.getName().equals("surname")) {
										surName = normalCharConvertor.normalCharConversion(editorName.getValue());
									}
								}
								if(editorElement.getAttribute("orcid") != null) {
									log.debug("att orcid is-->"+orcId);
									orcId = editorElement.getAttribute("orcid").getValue(); 
								}
								seriesEditorList.add(new Editor(givenName, surName, orcId));
							}
						}
						auInd.setEditorlist(seriesEditorList);
					}
					if(bookSeriesElement.getName().equals("series")) {
						 for(Element seriesElement : bookSeriesElement.getChildren()) {
							 log.debug(seriesElement.getName()+"-->"+seriesElement.getValue());
							 if(seriesElement.getName().equals("volume-nr")) {
								 String volNo = seriesElement.getValue();
								 obj.setVolume(volNo);
							 }
							 else if(seriesElement.getName().equals("title")) {
								 String seriesTitle = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(seriesElement.getValue()));
								 obj.setBookTitle(seriesTitle);
							 }
							 else if(seriesElement.getName().equals("issn")) {
								 String issn = seriesElement.getValue();
								 obj.setIssn(issn);
							 }
						 }
					}
				}
			}
			else if(issueElement.getName().equals("isbn")) {
		 		obj.setIsbn(issueElement.getValue());
		 	}
			else if(issueElement.getName().equals("date")) {
				obj.setYear(issueElement.getValue());
			}
			else if(issueElement.getName().equals("title")) {
				obj.setEditedBookTitle(normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(issueElement.getValue())));
			}
			else if(issueElement.getName().equals("publisher")) {
				for(Element pubElement: issueElement.getChildren()) {
					String nodeValue = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(pubElement.getValue()));
					log.debug(pubElement.getName()+"-->"+nodeValue);
					if(pubElement.getName().equals("name")) {
						obj.setPublisherName(nodeValue);
						publ.setName(nodeValue);
						System.out.println("Pblisher Name"+ nodeValue);
					}
					else if(pubElement.getName().equals("location")) {
						obj.setPublisherLocation(nodeValue);
					     publ.setAddress(nodeValue);
					}
				}
			}
		 }
	 }
	 
	 /*
	  * host book element parsing
	  */
	 public void hostBookParsing(Element hostElement, Artifact_Book obj,Author auInd,Publisher publ) {
		 for(Element issueElement : hostElement.getChildren()) {
		 	if(issueElement.getName().equals("date")) {
				String date =issueElement.getValue();
				obj.setYear(date);
			}
		 	else if(issueElement.getName().equals("isbn")) {
		 		obj.setIsbn(issueElement.getValue());
		 	}
		 	else if(issueElement.getName().equals("edition")) {
		 		obj.setEdition(xmlExtractorObj.removeWhiteSpaces(issueElement.getValue()));
		 	}
		 	else if(issueElement.getName().equals("book-series")) {
				List<Editor> seriesEditorList = null;
				for(Element bookSeriesElement : issueElement.getChildren()) {
					if(bookSeriesElement.getName().equals("editors")) {
						seriesEditorList = new ArrayList<Editor>();
						for(Element editorElement : issueElement.getChildren()) {
							if(editorElement.getName().equals("editor")) {
								String givenName = "", surName = "", orcId = ""; 
								for(Element editorName : editorElement.getChildren()) {
									if(editorName.getName().equals("given-name")) {
										givenName = normalCharConvertor.normalCharConversion(editorName.getValue());
									}
									else if(editorName.getName().equals("surname")) {
										surName = normalCharConvertor.normalCharConversion(editorName.getValue());
									}
								}
								if(editorElement.getAttribute("orcid") != null) {
									log.debug("att orcid is-->"+orcId);
									orcId = editorElement.getAttribute("orcid").getValue(); 
								}
								seriesEditorList.add(new Editor(givenName, surName, orcId));
							}
						}
						//obj.setEditors(seriesEditorList);
						auInd.setEditorlist(seriesEditorList);
					}
					if(bookSeriesElement.getName().equals("series")) {
						 for(Element seriesElement : bookSeriesElement.getChildren()) {
							 if(seriesElement.getName().equals("volume-nr")) {
								 String volNo = seriesElement.getValue();
								 obj.setVolume(volNo);
							 }
							 else if(seriesElement.getName().equals("title")) {
								 String seriesTitle = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(seriesElement.getValue()));
								 obj.setBookTitle(seriesTitle);
							 }
							 else if(seriesElement.getName().equals("issn")) {
								 String issn = seriesElement.getValue();
								 obj.setIssn(issn);
							 }
						 }
					}
				}
			}
			else if(issueElement.getName().equals("title")) {
				obj.setBookTitle(normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(issueElement.getValue())));
			}
			else if(issueElement.getName().equals("publisher")) {
				for(Element pubElement: issueElement.getChildren()) {
					String nodeValue = normalCharConvertor.normalCharConversion(xmlExtractorObj.removeWhiteSpaces(pubElement.getValue()));
					log.debug(pubElement.getName()+"-->"+nodeValue);
					if(pubElement.getName().equals("name")) {
						obj.setPublisherName(nodeValue);
						publ.setName(nodeValue);
					}
					else if(pubElement.getName().equals("location")) {
						obj.setPublisherLocation(nodeValue);
						publ.setAddress(nodeValue);
					}
				}
			}
		 }
	 }
}

