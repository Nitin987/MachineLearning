package com.td.mongodb.dao;

import com.td.mongodb.bean.Author;


public interface AuthorsDao{

	public void create(Author o);
	public void update(Author o);
	public Author readByName(String name);
}
