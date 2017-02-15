package com.td.mongodb.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import com.mongodb.WriteResult;
import com.td.mongodb.bean.Artifact;
import com.td.mongodb.bean.Artifact_Book;


public class ArtifactDaoImpl implements ArtifactDao{
	private static Logger log = Logger.getLogger(ArtifactDaoImpl.class);

	private MongoOperations mongoOps;
	
	public ArtifactDaoImpl(MongoOperations mongoOps) {
		 this.mongoOps=mongoOps;
	}
	public void create_Article(Artifact journalArtifact) {
		mongoOps.insert(journalArtifact, "artifact");
	}

	public void create_Book(Artifact_Book bookArtifact) {
		mongoOps.insert(bookArtifact, "artifactBook");
	}

	public Artifact readById(String id) {
		 Query query = new Query(Criteria.where("_id").is(id));
	       return mongoOps.findOne(query, Artifact.class, "artifact");
	}

	public void update(Artifact a) {
		mongoOps.save(a, "artifact");
	}
	
	public void update_Book(Artifact_Book a) {
		mongoOps.save(a, "artifactBook");
	}

	public int deleteById(String id) {
		 Query query = new Query(Criteria.where("_id").is(id));
		 WriteResult result = this.mongoOps.remove(query, Artifact.class, "artifact");
		 return result.getN();
	}
	public List<Artifact> fullTextSearchOnArtilcle(String title) {
		List<Artifact> artifactList = null;
		List<Artifact> modifiedList = null;
		try {
			if(!title.trim().equals("")) {
				Query query = TextQuery.queryText(new TextCriteria().matching(title)).sortByScore().limit(5);
				artifactList = mongoOps.find(query, Artifact.class);
				if(artifactList != null && artifactList.size() > 0) {
					modifiedList = new ArrayList<Artifact>();
					for(Artifact artifact: artifactList ) {
						if(artifact.getScore() >= 13.0)
							modifiedList.add(artifact);
					}
				}
			}
			
		} catch (Exception e) {
			log.debug("title-->"+title);
			e.printStackTrace();
		}
		
		return modifiedList;
	}
	public Artifact searchByArticleTitle(String title, String criteria) {
		Artifact artifact = null;
		
		try {
			if(!title.trim().equals("")) {
				Criteria searchCriteria = Criteria.where(criteria).is(title);
				Query query = new Query(searchCriteria);
				artifact = mongoOps.findOne(query, Artifact.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return artifact;
	}
	
	public Artifact_Book searchByChapterTitle(String title, String criteria) {
		Artifact_Book artifact = null;
		
		try {
			if(!title.trim().equals("")) {
				Criteria searchCriteria = Criteria.where(criteria).is(title);
				Query query = new Query(searchCriteria);
				artifact = mongoOps.findOne(query, Artifact_Book.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return artifact;
	}

}
