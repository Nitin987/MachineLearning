package com.td.mongodb.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.td.mongodb.bean.GrantSponsor;

public class GrantDaoImpl implements GrantDao{

private MongoOperations mongoOps;
	
	public GrantDaoImpl(MongoOperations mongoOps) {
		 this.mongoOps=mongoOps;
	}
	
	public void create(GrantSponsor o) {
		mongoOps.insert(o, "Grant");
	}



	public void update(GrantSponsor o) {
		mongoOps.save(o, "Grant");
	}

	public GrantSponsor readByGrant(String name) {
		 Query query = new Query(Criteria.where("grantSponsors").is(name));
	     return mongoOps.findOne(query, GrantSponsor.class, "Grant");
	}

	

	

}
