package com.td.mongodb.bean;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import com.td.mongodb.bean.Pages;

public class Chapter {
	@Field("title")
	private String title;
	
	@Field("pages")
	private Pages pages;

	@Field("pii")
	
	private String pii;
	
	@Field("doi") 
	private String doi;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Pages getPages() {
		return pages;
	}

	public void setPages(Pages pages) {
		this.pages = pages;
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

	@PersistenceConstructor
	public Chapter(String title, Pages pages, String pii, String doi){
		this.title = title;
		this.pages = pages;
		this.pii = pii;
		this.doi = doi;
	}
	
}
