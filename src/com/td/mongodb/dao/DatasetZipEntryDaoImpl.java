package com.td.mongodb.dao;

import java.util.Date;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.td.mongodb.bean.DatasetZipEntry;

class DatasetZipEntryDaoImpl implements DatasetZipEntryDao {

	private MongoOperations mongoOps;
	
	public DatasetZipEntryDaoImpl(MongoOperations mongoOps) {
		 this.mongoOps=mongoOps;
	}
	public void create(DatasetZipEntry entry) {
		mongoOps.insert(entry, "datasetZipEntry");
	}

	@Override
	public void update(DatasetZipEntry entry) {
		mongoOps.save(entry, "datasetZipEntry");
	}

	@Override
	public DatasetZipEntry searchByName(String zipName) {
		DatasetZipEntry datasetZipEntry = null;
		
		try {
//			title = title.replace("(", "\\(");
			if(!zipName.trim().equals("")) {
				Criteria searchCriteria = Criteria.where("zipName").is(zipName);
				Query query = new Query(searchCriteria);
				datasetZipEntry = mongoOps.findOne(query, DatasetZipEntry.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datasetZipEntry;
	}
	@Override
	public void updateByZipName(String zipName, String status, Date endDate) {
		 mongoOps.updateFirst(new Query(Criteria.where("zipName").is(zipName)),new Update().set("status", status).set("endTime", endDate), DatasetZipEntry.class); 
	}

}
