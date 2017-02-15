package com.td.mongodb.dao;

import org.bson.types.ObjectId;

import com.td.mongodb.bean.Organization;

public interface OrganizationDao{

	public void create(Organization o);
	public Organization readByName(String name);
	public void update(Organization o);
}
