package com.td.mongodb.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;

import com.mongodb.WriteResult;

import com.td.mongodb.bean.Country;
import com.td.mongodb.bean.GeographicalLocation;

public  class GeographicalDaoImpl implements GeographicalDao {



	private static Logger log = Logger.getLogger(ArtifactDaoImpl.class);

	private MongoOperations mongoOps;
	
	public GeographicalDaoImpl(MongoOperations mongoOps) {
		 this.mongoOps=mongoOps;
	}
	public void create(GeographicalLocation a) {
		mongoOps.insert(a, "geoloc");
	}
	
	/*public Country readBycountriesName(String countryname) {
		Query query = new Query(Criteria.where("country").is(countryname));
	       return mongoOps.findOne(query, Country.class, "geoloc");
	}

	public void create(Country cc){
		  mongoOps.insert(cc, "geoloc"); 
	  }

	public void update(Country cc) {
		mongoOps.save(cc, "geoloc");
		
	}
*/  
	public void update(GeographicalLocation a) {
		mongoOps.save(a, "geoloc");
	}

	public int deleteById(String id) {
		 Query query = new Query(Criteria.where("_id").is(id));
		 WriteResult result = this.mongoOps.remove(query, GeographicalLocation.class, "geoloc");
		 return result.getN();
	}
	public List<GeographicalLocation> fullTextSearchOnLocation(String country,String pincode) {
		List<GeographicalLocation> geoList = null;
		List<GeographicalLocation> modifiedList = null;
		try {
			if(!country.trim().equals("")) {
				Query query = TextQuery.queryText(new TextCriteria().matching(country).matching(pincode)).sortByScore().limit(5);
				geoList = mongoOps.find(query, GeographicalLocation.class);
				if(geoList != null && geoList.size() > 0) {
					modifiedList = new ArrayList<GeographicalLocation>();
					for(GeographicalLocation artifact: geoList ) {
						if(artifact.getScore() >= 13.0)
							modifiedList.add(artifact);
					}
				}
			}
			
		} catch (Exception e) {
			log.debug("title-->"+country);
			e.printStackTrace();
		}
		
		return modifiedList;
	}
	/*public GeographicalLocation searchByCountry(String id) {
		GeographicalLocation geoartifact = null;
		
		try {
//			title = title.replace("(", "\\(");
			if(!id.trim().equals("")) {
				Criteria searchCriteria = Criteria.where("id").is(id);
				Query query = new Query(searchCriteria);
				geoartifact = mongoOps.findOne(query, GeographicalLocation.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return geoartifact;
	}*/
	@Override
	public GeographicalLocation readBycityName(String cityname) {
		 Query query = new Query(Criteria.where("city").is(cityname));
	       return mongoOps.findOne(query, GeographicalLocation.class, "geoloc");
	}
	@Override
	public GeographicalLocation readBycountryName(String countryname) {
		 Query query = new Query(Criteria.where("country").is(countryname));
	       return mongoOps.findOne(query, GeographicalLocation.class, "geoloc");
	}
	@Override
	public GeographicalLocation readBystateName(String statename) {
		Query query = new Query(Criteria.where("state").is(statename));
	       return mongoOps.findOne(query, GeographicalLocation.class, "geoloc");
	}
	@Override
	public GeographicalLocation readBypostalcode(String postalcode) {
		Query query = new Query(Criteria.where("postal_code").is(postalcode));
	       return mongoOps.findOne(query, GeographicalLocation.class, "geoloc");
	}
	@Override
	public GeographicalLocation readBystatecode(String statecode) {
		Query query = new Query(Criteria.where("statecode").is(statecode));
	       return mongoOps.findOne(query, GeographicalLocation.class, "geoloc");
	}
	@Override
	public GeographicalLocation readBycountryCode(String countryCode) {
		Query query = new Query(Criteria.where("countryCode").is(countryCode));
	       return mongoOps.findOne(query, GeographicalLocation.class, "geoloc");
	}
	

}
