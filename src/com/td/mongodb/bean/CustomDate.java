package com.td.mongodb.bean;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class CustomDate {

	private Integer year;
	private Integer month;
	private Integer date;
	
	@PersistenceConstructor
	public CustomDate(Integer year, Integer month, Integer date) {
		this.year = year;
		this.month = month;
		this.date = date;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDate() {
		return date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}
	
}
