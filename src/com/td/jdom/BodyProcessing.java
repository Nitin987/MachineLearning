package com.td.jdom;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.Node;

import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.td.mongodb.bean.Artifact;
import com.td.mongodb.bean.Artifact_Book;
import com.td.mongodb.bean.Author;
import com.td.mongodb.bean.GrantSponsor;
import com.td.mongodb.bean.Publisher;
import com.td.mongodb.dao.ArtifactDao;
import com.td.mongodb.dao.AuthorsDao;
import com.td.mongodb.dao.GrantDao;
import com.td.mongodb.dao.PublisherDao;
import com.td.util.NormalCharConvertor;

public class BodyProcessing {
	private static Logger log = Logger.getLogger(BodyProcessing.class);
	XMLExtractionJob xmlExtractorObj;
	NormalCharConvertor normalCharConvertor;
	List<Element> acknowledgement;
	List<Element> ceSection;
	List<Element> cePara;
	List<Element> ceGrants;
	public BodyProcessing(Element element, XMLExtractionJob xmlExtractorObj, ClassPathXmlApplicationContext ctx) {
		GrantDao ga = ctx.getBean("grantDao", GrantDao.class);
		GrantSponsor grantSponsor=null;
		this.xmlExtractorObj = xmlExtractorObj;
		normalCharConvertor = xmlExtractorObj.normalCharConvertor;
		acknowledgement = new ArrayList<Element>();
		ceSection = new ArrayList<Element>();
		cePara = new ArrayList<Element>();
		ceGrants = new ArrayList<Element>();
		
		if(element != null)
			getRefList("sections", element);
		log.debug("ref size-->"+acknowledgement.size());
		XMLExtractionJob.refCount += acknowledgement.size();
		for(Element ackElement : acknowledgement) {
			
			try {
				if(ackElement!=null){
					getcesectionList("section", ackElement);
					for(Element ceSec : ceSection) {
						getcepara("para", ceSec);
						for(Element ceGrant : cePara) {
							getgrantList("grant-sponsor", ceGrant);
 							for(Element grantItera : ceGrants) {
								if(grantItera.getText()!=null){
									grantSponsor=new GrantSponsor(grantItera.getText());
									//System.out.println(grantItera.getText());
								}
								if(grantSponsor!=null){
									GrantSponsor grsponsor=null;
									if(grantSponsor!=null){
										grsponsor=ga.readByGrant(grantSponsor.getGrantSponsors());
										
									}
									if(grsponsor==null){
										System.out.println(grsponsor);
										System.out.println(grantItera.getText());
										ga.create(grantSponsor);
										
									}
									else{
										
										ga.update(grantSponsor);
										
									}
									
									
								}
							}
					}
					
					
					
				}
				}	
			//	PublisherDao pd = ctx.getBean("publishDao", PublisherDao.class);
				
			}
			catch(Exception e){
				log.info("Grant failed to insert into Repository");
			}
		}
			
			
			
			
		}
		

	private void getgrantList(String which, Element startElem) {
		 List<Element> nodes = startElem.getChildren();
		    Iterator<Element> iter = nodes.iterator();

		    if(startElem.getName().equals(which))
		    	ceGrants.add(startElem);
		    
			while (iter.hasNext()) {
				Element elem = iter.next();
				getgrantList(which, elem);
			}
		
	}

	private void getcepara(String which, Element startElem) {
		 List<Element> nodes = startElem.getChildren();
		    Iterator<Element> iter = nodes.iterator();

		    if(startElem.getName().equals(which))
		    	cePara.add(startElem);
		    
			while (iter.hasNext()) {
				Element elem = iter.next();
				getcepara(which, elem);
			}
		
	}



	public void getcesectionList(String which, Element startElem) {
		 List<Element> nodes = startElem.getChildren();
		    Iterator<Element> iter = nodes.iterator();

		    if(startElem.getName().equals(which))
		    	ceSection.add(startElem);
		    
			while (iter.hasNext()) {
				Element elem = iter.next();
				getcesectionList(which, elem);
			}
	}


	public void getRefList(String which,Element startElem) {
	    List<Element> nodes = startElem.getChildren();
	    Iterator<Element> iter = nodes.iterator();

	    if(startElem.getName().equals(which))
	    	acknowledgement.add(startElem);
	    
		while (iter.hasNext()) {
			Element elem = iter.next();
			getRefList(which, elem);
		}
	 }
	
	
	
}
