package com.td.mongodb.dao;

import com.td.mongodb.bean.Artifact;
import com.td.mongodb.bean.Publisher;


public interface PublisherDao{

	public void create(Publisher o);
	public void update(Publisher o);
	public Publisher searchByPName(String name);
}
