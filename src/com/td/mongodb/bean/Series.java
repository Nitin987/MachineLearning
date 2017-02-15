package com.td.mongodb.bean;

import java.util.List;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class Series {
	private String title;
	private String issn;
	
	@Field("volume-nr")
	private Integer volume_nr;
	
	private List<String> editors;
	
	@PersistenceConstructor
	public Series(String title, String issn, Integer volume_nr, List<String> editors) {
		this.title = title;
		this.issn = issn;
		this.editors = editors;
		this.volume_nr = volume_nr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIssn() {
		return issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}

	public Integer getVolume_nr() {
		return volume_nr;
	}

	public void setVolume_nr(Integer volume_nr) {
		this.volume_nr = volume_nr;
	}

	public List<String> getEditors() {
		return editors;
	}

	public void setEditors(List<String> editors) {
		this.editors = editors;
	}
	
}
