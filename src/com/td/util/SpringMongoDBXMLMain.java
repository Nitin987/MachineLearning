package com.td.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.InputSource;

import com.mongodb.MongoURI;
import com.td.jdom.CustomEntityResolver;
import com.td.mongodb.bean.Address;
import com.td.mongodb.bean.Artifact;
import com.td.mongodb.bean.Author;
import com.td.mongodb.bean.Chapter;
import com.td.mongodb.bean.CustomDate;
import com.td.mongodb.bean.DivisionName;
import com.td.mongodb.bean.Editor;
import com.td.mongodb.bean.Issue;
import com.td.mongodb.bean.Organization;
import com.td.mongodb.bean.Pages;
import com.td.mongodb.bean.Publisher;
import com.td.mongodb.bean.Series;
import com.td.mongodb.dao.ArtifactDao;
import com.td.mongodb.dao.DatasetZipEntryDao;
import com.td.mongodb.dao.OrganizationDao;

public class SpringMongoDBXMLMain {
	public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("Spring-Mongo-Config.xml");
        SpringMongoDBXMLMain obj = new SpringMongoDBXMLMain();
        
//        obj.insertArtifactThroughDaoImp(ctx);
//		obj.setDatasetZipValues(ctx);
//        System.out.println(SpringMongoDBXMLMain.class.getResourceAsStream("././art520.dtd"));
//        	InputStream dtdStream = CustomEntityResolver.class.getResourceAsStream("/art520.dtd");
//			System.out.println(dtdStream);
        ctx.close();
			
    }
	
	public void setDatasetZipValues(ClassPathXmlApplicationContext ctx) {
		DatasetZipEntryDao dd = ctx.getBean("datasetZipEntryDao", DatasetZipEntryDao.class);
		dd.updateByZipName("02618292.zip", "SUCCESS", new Date());
	}
	/*public void insertArtifactThroughDaoImp(ClassPathXmlApplicationContext ctx) {
		ArtifactDao ad = ctx.getBean("artifactDao",ArtifactDao.class);
		 //insert artifact object
		String title = "Modelling the hierarchical structure of road ";
        Artifact artifactObj = ad.searchByTitle(title);
        if(artifactObj == null) {
        	artifactObj = new Artifact();
        }
		artifactObj.setTitle(title);
		artifactObj.setTitle_abbrev("");
		artifactObj.setSub_title("");
		artifactObj.setAlt_title("");
		artifactObj.setTrans_title("");
		artifactObj.setLanguage("");
		artifactObj.setAlt_language("");
		artifactObj.setUrl("");
		artifactObj.setPii("");
		artifactObj.setDoi("");
		artifactObj.setIsbn("");
		artifactObj.setIssn("");
		artifactObj.setVolume(38);
		
		List<Chapter> chapters;
		Chapter chapter = new Chapter("chp2", new Pages(36, 40), "67555", "10.10.675675/r43573");
		if(artifactObj.getChapters() != null) {
			chapters = artifactObj.getChapters();
			boolean chapterExist = false;
			for(Chapter c: chapters) {
				if(chapter.getTitle().equals(c.getTitle())){
					chapterExist = true;
					break;
				}
			}
			if(!chapterExist) {
				chapters.add(chapter);
			}
		}
		else {
			chapters = new ArrayList<Chapter>();
			chapters.add(chapter);
		}
		
		artifactObj.setChapters(chapters);
		
		if(artifactObj.getIssue() == null) {
		List<Issue> issueList = new ArrayList<Issue>();
			List<Editor> editorList  = new ArrayList<Editor>();
			editorList.add(new Editor("1234","", null));
			
			List<Series> seriesList = new ArrayList<Series>();
//				List<Editor> editorList1  = new ArrayList<Editor>();
			
//				editorList1.add(new Editor("", ""));
			seriesList.add(new Series("Crash Analysis", "", null, null));
			
		issueList.add(new Issue(0, null, "", editorList, seriesList));
		artifactObj.setIssue(issueList);
		}
		
		artifactObj.setPages(new Pages(43,53));
		artifactObj.setEdition("");
		
		if(artifactObj.getPublisher() != null)
			artifactObj.setPublisher(new Publisher("", "", new CustomDate(2006, 11, 28)));
		
		artifactObj.setType("Article");
		List<String> cat = new ArrayList<String>();
		artifactObj.setCategories(cat);
		
//		List<String> srcList = new ArrayList<String>();
////		srcList.add("pubmed");
////		srcList.add("crossref");
////		srcList.add("worldcat");
//		srcList.add("");
//		artifactObj.setSource(srcList);
		
		OrganizationDao od = ctx.getBean("organizationDao", OrganizationDao.class);
		Organization orgObj  = od.readByName("Guy's Hospital");
		if(orgObj == null) {
			List<DivisionName> divisionList = new ArrayList<DivisionName>();
//				divisionList.add(new DivisionName("IT Development"));
//				divisionList.add(new DivisionName("IT Maintenance"));
			
			orgObj = new Organization("Universitat Leipzig", divisionList, "UL", "", "Leipzig", "", "", "Germany", "GER", "", "", "");
			
			od.create(orgObj);
		}
		
		ArrayList<Organization> org = new ArrayList<Organization>();
		org.add(orgObj);
		List<String> degrees = new ArrayList<String>();
		degrees.add("MA");
		Author authorInfo2 = new Author(true,"Mr.",degrees , "", "T. B.", "Dick", "", "", "Dick, T. B.", "","", "",org, new Address("", "", "", "", "", "", "", "", "", ""));
//			Author authorInfo1 = new Author(true,"", degrees, "", "", "E.", "Lenguerrand", "", "Lenguerrand, E.", "", "", "", null, new Address("", "", "", "", "", "", "", "", "", ""));
		List<Author> authors = null;
		if(artifactObj.getAuthors() == null) {
			authors = new ArrayList<Author>();
			authors.add(authorInfo2);
		}
		else {
			authors = artifactObj.getAuthors();
			for(Author auth : authors) {
				if(!auth.getFamily_name().equalsIgnoreCase(authorInfo2.getFamily_name())) {
					authors.add(authorInfo2);
				}
				else {
					System.out.println("author exist");
					if(auth.getOrganization() == null && authorInfo2.getOrganization() != null) {
						auth.setOrganization(authorInfo2.getOrganization());
					}
					if(auth.getAddress() == null && authorInfo2.getAddress() != null) {
						auth.setAddress(authorInfo2.getAddress());
					}
					if(auth.getDegrees() == null && authorInfo2.getDegrees() != null) {
						auth.setDegrees(authorInfo2.getDegrees());
					}
					if(auth.getPrefix() == null || auth.getPrefix().equals("")){
						auth.setPrefix(authorInfo2.getPrefix());
					}
					if(auth.getAlt_name() == null || auth.getAlt_name().equals("")){
						auth.setAlt_name(authorInfo2.getAlt_name());
					}
					if(auth.getInitials() == null || auth.getInitials().equals("")){
						auth.setInitials(authorInfo2.getInitials());
					}
					if(auth.getOrcid() == null || auth.getOrcid().equals("")){
						auth.setOrcid(authorInfo2.getOrcid());
					}
					if(auth.getSuffix() == null || auth.getSuffix().equals("")){
						auth.setSuffix(authorInfo2.getSuffix());
					}
					if(auth.getUrl() == null || auth.getUrl().equals("")){
						auth.setUrl(authorInfo2.getUrl());
					}
				}
			}	
		}
			
			
			
//		authors.add(authorInfo1);
		artifactObj.setAuthors(authors);
		
		if(artifactObj.getCreateddate() == null) {
		artifactObj.setCreateddate(new Date()); //"MM/dd/yyyy"
		}
		else {
			artifactObj.setUpdateddate(new Date());
		}
		
		ad.update(artifactObj);
		
		ctx.close();
	}*/
}
	