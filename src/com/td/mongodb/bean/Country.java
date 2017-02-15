package com.td.mongodb.bean;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection ="Country")
public class Country {
	@Id
	private ObjectId id;

	@Field("countryName")
	private String countryName;
	
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getTwolettercountryCode() {
		return twolettercountryCode;
	}
	public void setTwolettercountryCode(String twolettercountryCode) {
		this.twolettercountryCode = twolettercountryCode;
	}
	public String getthreelettercountryCode() {
		return threelettercountryCode;
	}
	public void setthreelettercountryCode(String threelettercountryCode) {
		this.threelettercountryCode = threelettercountryCode;
	}
	@Field("twolettercountryCode")
	private String twolettercountryCode;
	@Field("threelettercountryCode")
	private String threelettercountryCode;
	@Field("city")
	private List<City> city;

	public List<City> getCity() {
		return city;
	}
	public void setCity(List<City> city) {
		this.city = city;
	}
	@PersistenceConstructor
	public Country(){
		
	}
	

}
