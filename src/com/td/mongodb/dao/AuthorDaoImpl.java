package com.td.mongodb.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.td.mongodb.bean.Author;
import com.td.mongodb.bean.Organization;

public  class AuthorDaoImpl implements AuthorsDao {

	private MongoOperations mongoOps;
	
	public AuthorDaoImpl(MongoOperations mongoOps) {
		 this.mongoOps=mongoOps;
	}
	
	public void create(Author o) {
		mongoOps.insert(o, "Authors");
	}



	public void update(Author o) {
		mongoOps.save(o, "Authors");
	}

	public Author readByName(String name) {
		 Query query = new Query(Criteria.where("fullName").is(name));
	     return mongoOps.findOne(query, Author.class, "author");
	}
	
}