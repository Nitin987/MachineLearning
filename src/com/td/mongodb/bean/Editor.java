package com.td.mongodb.bean;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Editor {

	private String givenName;
	private String surName;
	private String orcid;
	
	
	
	public String getGivenName() {
		return givenName;
	}


	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}


	public String getSurName() {
		return surName;
	}


	public void setSurName(String surName) {
		this.surName = surName;
	}


	public String getOrcid() {
		return orcid;
	}


	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}


	@PersistenceConstructor
	public Editor(String givenName, String surName, String orcid) {
		this.givenName = givenName;
		this.surName = surName;
		this.orcid  = orcid;
	}
}
