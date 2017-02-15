package com.td.mongodb.dao;

import com.td.mongodb.bean.Country;
public interface CountryDao {

	 public Country readBycountryName(String countryname);
	 public void create(Country cc);
	 public void update(Country cc);

}
