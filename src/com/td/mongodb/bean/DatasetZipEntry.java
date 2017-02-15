package com.td.mongodb.bean;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "datasetZipEntry")
public class DatasetZipEntry {
	@Id
	private ObjectId id;
	
	@Indexed(unique = true)
	private String zipName;
	
	@Field("path")
	private String path;

	@Field("startTime")
	private Date startTime;
	
	@Field("endTime")
	private Date endTime;
	
	@Field("status")
	private String status;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getZipName() {
		return zipName;
	}

	public void setZipName(String zipName) {
		this.zipName = zipName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@PersistenceConstructor
	public DatasetZipEntry(String zipName, String path, Date startTime, Date endTime, String status) {
		this.zipName = zipName;
		this.path= path;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
	}
	
}
