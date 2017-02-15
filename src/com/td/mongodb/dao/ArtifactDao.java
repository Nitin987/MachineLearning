package com.td.mongodb.dao;

import java.util.List;

import com.td.mongodb.bean.Artifact;
import com.td.mongodb.bean.Artifact_Book;


public interface ArtifactDao {
	public void create_Article(Artifact a);
	public void create_Book(Artifact_Book a);
    
    public Artifact readById(String id);
     
    public void update(Artifact a);
    
    public void update_Book(Artifact_Book a);
     
    public int deleteById(String id);
    
    public List<Artifact> fullTextSearchOnArtilcle(String title);
    
    public Artifact searchByArticleTitle(String title, String criteria);
    
    public Artifact_Book searchByChapterTitle(String title, String criteria);
}
