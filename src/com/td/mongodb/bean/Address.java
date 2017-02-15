package com.td.mongodb.bean;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class Address {
	private String label;
	
	@Field("address-line")
	private String address_line;
	
	private String city;
	private String state;
	
	@Field("state-code")
	private String state_code;
	
	private String country;
	
	@Field("country-code")
	private String country_code;
	
	@Field("postal-code")
	private String postal_code;
	
	private String tel;
	private String fax;
	
	@PersistenceConstructor
	public Address(String label, String address_line,String city, String state, String state_code, String country, String country_code, String postal_code, String tel, String fax) {
		this.label = label;
		this.address_line = address_line;
		this.city = city;
		this.state = state;
		this.state_code = state_code;
		this.country = country;
		this.country_code = country_code;
		this.postal_code = postal_code;
		this.tel = tel;
		this.fax = fax;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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
	
}
