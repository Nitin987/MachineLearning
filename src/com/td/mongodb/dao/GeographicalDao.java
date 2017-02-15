package com.td.mongodb.dao;

import java.util.List;


import com.td.mongodb.bean.Country;
import com.td.mongodb.bean.GeographicalLocation;


public interface GeographicalDao{
	public void create(GeographicalLocation a);
    
    public GeographicalLocation readBycityName(String cityname);
    public GeographicalLocation readBycountryName(String countryname);
    public GeographicalLocation readBystateName(String statename);
    
    public GeographicalLocation readBypostalcode(String postalcode);
    public GeographicalLocation readBystatecode(String statecode);
    public GeographicalLocation readBycountryCode(String countryCode);
     
    public void update(GeographicalLocation a);
     
    public int deleteById(String id);
    
    public List<GeographicalLocation> fullTextSearchOnLocation(String title,String pincode);
    /*public Country readBycountriesName(String countryname);
	 public void create(Country cc);
	 public void update(Country cc);
	*/
    
   // public List<GeographicalLocation> searchByCountry(String country);
}
