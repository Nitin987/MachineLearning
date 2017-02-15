package com.td.mongodb.dao;

import java.util.Date;

import com.td.mongodb.bean.DatasetZipEntry;

public interface DatasetZipEntryDao {

	public void create(DatasetZipEntry entry);
     
    public void update(DatasetZipEntry entry);
     
    public DatasetZipEntry searchByName(String zipName);
    
    public void updateByZipName(String zipName, String status, Date endDate);

}
