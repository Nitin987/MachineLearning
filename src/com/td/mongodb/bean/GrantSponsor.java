package com.td.mongodb.bean;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="Grant")
public class GrantSponsor {
	@Id
	private ObjectId id;
	@Indexed(unique = true)
	private String grantSponsors;

	public String getGrantSponsors() {
		return grantSponsors;
	}

	public void setGrantSponsors(String grantSponsors) {
		this.grantSponsors = grantSponsors;
	}

	@PersistenceConstructor
	public GrantSponsor(String GrantSponsors ) {
		
		this.grantSponsors = GrantSponsors;
	}
	
	
}
