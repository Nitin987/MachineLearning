package com.td.mongodb.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="Author")
public class Author {

	private String initials;
	private List<String> degrees;
	private String prefix;
	
	@Field("corresp_author")
	private Boolean correspAuthor;
	
	@Field("given-name")
	private String given_name;
	
	@Field("family-name")
	private String family_name;
	
	@Field("alt-name")
	private String alt_name;
	
	@Field("suffix")
	private String suffix;
	
	@Field("fullName")
	private String fullName;
	
	@Field("orcid")
	private String orcid;
	
	@Field("email")
	private String email;
	
	@Field("url")
	private String url;
	
	@Field("organization")
	private List<Organization> organizationList;
	
	private List<Editor> editorlist;
	public List<Editor> getEditorlist() {
		return editorlist;
	}

	public void setEditorlist(List<Editor> editorlist) {
		this.editorlist = editorlist;
	}


	@Field("address")
	private Address address;
	
	private String supValue;
	
	public Author() {
		
	}
	
	@PersistenceConstructor
	public Author(Boolean correspAuthor,String initials, List<String> degrees,String prefix,String given_name,String family_name,String alt_name,String suffix,String fullName,String orcid,String email,String url,@Value("#root.Editor")List<Editor> editor,Address address) {
		this.correspAuthor = correspAuthor;
		this.initials = initials;
		this.degrees = degrees;
		this.prefix = prefix;
		this.given_name = given_name;
		this.family_name = family_name;
		this.alt_name = alt_name;
		this.suffix = suffix;
		this.fullName = fullName;
		this.orcid = orcid;
		this.email = email;
		this.url = url;
		this.address = address;
		this.editorlist=editor;
	}

	
	public Boolean getCorrespAuthor() {
		return correspAuthor;
	}


	public void setCorrespAuthor(Boolean correspAuthor) {
		this.correspAuthor = correspAuthor;
	}


	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public List<String> getDegrees() {
		return degrees;
	}

	public void setDegrees(List<String> degrees) {
		this.degrees = degrees;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getGiven_name() {
		return given_name;
	}

	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}

	public String getFamily_name() {
		return family_name;
	}

	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}

	public String getAlt_name() {
		return alt_name;
	}

	public void setAlt_name(String alt_name) {
		this.alt_name = alt_name;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getOrcid() {
		return orcid;
	}

	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Organization> getOrganization() {
		return organizationList;
	}

	public void setOrganization(List<Organization> organization) {
		this.organizationList = organization;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}


	public String getSupValue() {
		return supValue;
	}


	public void setSupValue(String supValue) {
		this.supValue = supValue;
	}
	
}
