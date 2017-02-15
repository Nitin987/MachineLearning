package com.td.mongodb.dao;

import com.td.mongodb.bean.GrantSponsor;

public interface GrantDao {

		public void create(GrantSponsor o);
		public void update(GrantSponsor o);
		public GrantSponsor readByGrant(String name);
		
	}

