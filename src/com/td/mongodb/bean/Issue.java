package com.td.mongodb.bean;

import java.util.List;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class Issue {
	@Field("issue-nr")
	private Integer issue_nr;
	
	@Field("issuedate")
	private String issuedate;
	
	@Field("title")
	private String title;
	
	@Field("editors")
	private List<Editor> editors;
	
	@Field("series")
	private List<Series> series;
	
	@PersistenceConstructor
	public Issue(Integer issue_nr, String issuedate, String title, List<Editor> editors, List<Series> series) {
		this.issue_nr = issue_nr;
		this.issuedate = issuedate;
		this.title = title;
		this.editors = editors;
		this.series = series;
	}

	public Integer getIssue_nr() {
		return issue_nr;
	}

	public void setIssue_nr(Integer issue_nr) {
		this.issue_nr = issue_nr;
	}

	public String getIssuedate() {
		return issuedate;
	}

	public void setIssuedate(String issuedate) {
		this.issuedate = issuedate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Editor> getEditors() {
		return editors;
	}

	public void setEditors(List<Editor> editors) {
		this.editors = editors;
	}

	public List<Series> getSeries() {
		return series;
	}

	public void setSeries(List<Series> series) {
		this.series = series;
	}
	
}
