package com.td.mongodb.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.td.mongodb.bean.Publisher;

public  class PublisherDaoImpl implements PublisherDao {

	private MongoOperations mongoOps;
	
	public PublisherDaoImpl(MongoOperations mongoOps) {
		 this.mongoOps=mongoOps;
	}
	
	public void create(Publisher o) {
		mongoOps.insert(o, "Publisher");
	}

	public void update(Publisher o) {
		mongoOps.save(o, "Publisher");
	}
	public Publisher searchByPName(String name) {
		Publisher publish = null;
		
		try {
//			title = title.replace("(", "\\(");
			if(!name.trim().equals("")) {
				Criteria searchCriteria = Criteria.where("name").is(name);
				Query query = new Query(searchCriteria);
				publish = mongoOps.findOne(query, Publisher.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return publish;
	}


}