package com.td.mongodb.bean;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Pages {
	private Integer firstpage;
	private Integer lastpage;
	
	
	public Integer getFirstpage() {
		return firstpage;
	}


	public void setFirstpage(Integer firstpage) {
		this.firstpage = firstpage;
	}


	public Integer getLastpage() {
		return lastpage;
	}


	public void setLastpage(Integer lastpage) {
		this.lastpage = lastpage;
	}


	@PersistenceConstructor
	public Pages(Integer firstpage, Integer lastpage) {
		this.firstpage = firstpage;
		this.lastpage = lastpage;
	}
	
}
