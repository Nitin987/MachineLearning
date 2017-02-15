package com.td.mongodb.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBObject;
import com.td.mongodb.bean.DivisionName;
import com.td.mongodb.bean.Organization;

public class OrganizationDaoImpl implements OrganizationDao {

	private MongoOperations mongoOps;
	
	public OrganizationDaoImpl(MongoOperations mongoOps) {
		 this.mongoOps=mongoOps;
	}
	
	public void create(Organization o) {
		mongoOps.insert(o, "organization");
		/*BasicDBObject doc = getBasicDbObject(o);
		mongoOps.insert( doc );
		return (ObjectId)doc.get( "_id" );*/
		
	}

	/*private BasicDBObject getBasicDbObject(Organization o) {
		BasicDBObject doc = new BasicDBObject();
		doc.put(key, val)
		private String name;
		private List<DivisionName> division_name;
		private String abbrev_name;
		private String address_line;
		private String city;
		private String state;
		private String state_code;
		private String country;
		private String country_code;
		private String postal_code;
		private String tel;
		private String fax;
		
		return null;
	}*/

	public Organization readByName(String name) {
		 Query query = new Query(Criteria.where("name").is(name));
	     return mongoOps.findOne(query, Organization.class, "organization");
	}

	public void update(Organization o) {
		mongoOps.save(o, "organization");
	}

}
