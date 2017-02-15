package com.td.mongodb.bean;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.TextScore;

@Document(collection = "artifact")
public class Artifact {
	@Id
	private ObjectId id;
	@Indexed(unique = true)
	private String articleTitle;
	
	@Field("journalTitle")
	private String journalTitle;
	
	@Field("language")
	private String language;
	
	@Field("alt-language")
	private String alt_language;
	
	@Field("url")
	private String url;
	
	@TextScore Float score;
	
	@Field("pii")
	private String pii;
	
	@Field("doi")
	private String doi;
	
	@Field("volume")
	private String volume;
	
	@Field("issue")
	private String issue;
	
	@Field("first-page")
	private String firstPage;
	
	@Field("last-page")
	private String lastPage;
	
	@Field("authors")
	private List<Author> authors;
	
	@Field("editors")
	private List<Editor> editors;
	
	@Field("type")
	private String type;
	
	@Field("year")
	private String year;
	
	@Field("issn")
	private String issn;
	
	@Field("createddate")
	private Date createddate;
	
	@Field("updateddate")
	private Date updateddate;


	


	public ObjectId getId() {
		return id;
	}





	public void setId(ObjectId id) {
		this.id = id;
	}





	public String getArticleTitle() {
		return articleTitle;
	}





	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}





	public String getJournalTitle() {
		return journalTitle;
	}





	public void setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
	}





	public String getLanguage() {
		return language;
	}





	public void setLanguage(String language) {
		this.language = language;
	}





	public String getAlt_language() {
		return alt_language;
	}





	public void setAlt_language(String alt_language) {
		this.alt_language = alt_language;
	}





	public String getUrl() {
		return url;
	}





	public void setUrl(String url) {
		this.url = url;
	}





	public Float getScore() {
		return score;
	}





	public void setScore(Float score) {
		this.score = score;
	}





	public String getPii() {
		return pii;
	}





	public void setPii(String pii) {
		this.pii = pii;
	}





	public String getDoi() {
		return doi;
	}





	public void setDoi(String doi) {
		this.doi = doi;
	}





	public String getVolume() {
		return volume;
	}





	public void setVolume(String volume) {
		this.volume = volume;
	}





	public String getIssue() {
		return issue;
	}





	public void setIssue(String issue) {
		this.issue = issue;
	}





	public String getFirstPage() {
		return firstPage;
	}





	public void setFirstPage(String firstPage) {
		this.firstPage = firstPage;
	}





	public String getLastPage() {
		return lastPage;
	}





	public void setLastPage(String lastPage) {
		this.lastPage = lastPage;
	}





	public List<Author> getAuthors() {
		return authors;
	}




/*
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}


*/


	public List<Editor> getEditors() {
		return editors;
	}





	public void setEditors(List<Editor> editors) {
		this.editors = editors;
	}





	public String getType() {
		return type;
	}





	public void setType(String type) {
		this.type = type;
	}





	public String getYear() {
		return year;
	}





	public void setYear(String year) {
		this.year = year;
	}





	public String getIssn() {
		return issn;
	}





	public void setIssn(String issn) {
		this.issn = issn;
	}





	public Date getCreateddate() {
		return createddate;
	}





	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}





	public Date getUpdateddate() {
		return updateddate;
	}





	public void setUpdateddate(Date updateddate) {
		this.updateddate = updateddate;
	}





	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return (articleTitle+"::"+language);
	}

	
	
}
