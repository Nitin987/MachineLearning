package com.td.mongodb.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.td.mongodb.bean.Country;

public class CountryDaoImpl implements CountryDao {
	private MongoOperations mongoOps;
	public CountryDaoImpl(MongoOperations mongoOps) {
		 this.mongoOps=mongoOps;
	}

	
	public Country readBycountryName(String countryname) {
		Query query = new Query(Criteria.where("countryName").is(countryname));
	       return mongoOps.findOne(query, Country.class, "Country");
	}

	
	public void create(Country cc){
		  mongoOps.insert(cc, "Country"); 
	  }

		public void update(Country cc) {
		mongoOps.save(cc, "Country");
		
	}

}
