package com.td.mongodb.bean;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.td.mongodb.bean.DivisionName;

@Document(collection = "organization")
public class Organization {
	@Id
	private ObjectId id;
//	@Indexed(unique = true)
	private String name;
	
	private List<DivisionName> division_name;
	
	@Field("abbrev-name")
	private String abbrev_name;
	
	@Field("address-line")
	private String address_line;
	
	@Field("city")
	private String city;
	
	
	@Field("state-code")
	private String state_code;
	
	@Field("country")
	private String country;
	
	@Field("country-code")
	private String country_code;
	
	@Field("postal-code")
	private String postal_code;
	
	@Field("tel")
	private String tel;
	
	@Field("fax")
	private String fax;
	private String state;
	
	// public ObjectId getId() {
		   // return id;
	// }
	 
	 @PersistenceConstructor
	 public Organization(String name, List<DivisionName> division_name, String abbrev_name,String address_line, /*String city, String state, String state_code,String country ,String country_code, String postal_code,*/ String tel, String fax) {
		 this.name = name;
		 this.division_name = division_name;
		 this.abbrev_name = abbrev_name;
		 this.address_line = address_line;
		 /*this.city = city;
		 this.state = state;
		 this.state_code = state_code;
		 this.country = country;
		 this.country_code = country_code;
		 this.postal_code = postal_code;*/
		 this.tel = tel;
		 this.fax = fax;
	 }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DivisionName> getDivision_name() {
		return division_name;
	}

	public void setDivision_name(List<DivisionName> division_name) {
		this.division_name = division_name;
	}

	public String getAbbrev_name() {
		return abbrev_name;
	}

	public void setAbbrev_name(String abbrev_name) {
		this.abbrev_name = abbrev_name;
	}

	public String getAddress_line() {
		return address_line;
	}

	public void setAddress_line(String address_line) {
		this.address_line = address_line;
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

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	
	 
	 
}

