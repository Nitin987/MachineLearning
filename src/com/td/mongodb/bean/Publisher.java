package com.td.mongodb.bean;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="Publisher")
public class Publisher {
	@Id
	private ObjectId id;
	@Field("publisherAbr")
	private List<String>publisherAbr;
	@Indexed(unique = true)
	private String name;
	@Indexed(unique = true)
	private String address;
	
	@Field("publicationDate")
	private CustomDate publicationDate;
	
	public List<String> getPublisherAbr() {
		return publisherAbr;
	}

	public void setPublisherAbr(List<String> publisherAbr) {
		this.publisherAbr = publisherAbr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public CustomDate getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(CustomDate publicationDate) {
		this.publicationDate = publicationDate;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return (name);
	}
	
}
