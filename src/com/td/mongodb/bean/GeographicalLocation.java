package com.td.mongodb.bean;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="geoloc")
public class GeographicalLocation {
	@Id
	private ObjectId id;
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	private String label;
	@Indexed(unique = true)
	private String city;
	@Indexed(unique = true)
	private String state;
	
	@Field("state-code")
	private String state_code;
	@Indexed(unique = true)
	private String country;
	
	@Field("country-code")
	private String country_code;
	
	@Indexed(unique = true)
	private String postal_code;
	
	
	
	@PersistenceConstructor
	public GeographicalLocation(){}
	/*public GeographicalLocation(String label, String address_line,String city, String state, String state_code, String country, String country_code, String postal_code) {
		this.label = label;
		//this.address_line = address_line;
		this.city = city;
		this.state = state;
		this.state_code = state_code;
		this.country = country;
		this.country_code = country_code;
		this.postal_code = postal_code;
		
	}
*/
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState_code() {
		return state_code;
	}

	public void setState_code(String state_code) {
		this.state_code = state_code;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	public double getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
}
