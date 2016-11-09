package org.complexsystems.tools;

import java.util.ArrayList;

public class Results {
	private String entity;
	private String dbpediaAbstract;
	private String wikidataDescription;
	private String dbpediaDescription;
	private int totalDbProperties;
	private int totalWdProperties;
	private int sameProperties;
	private ArrayList<Row> pairs;
	
	public String getWikidataDescription() {
		return wikidataDescription;
	}
	
	public void setWikidataDescription(String wikidataDescription) {
		this.wikidataDescription = wikidataDescription;
	}
	
	public String getDbpediaDescription() {
		return dbpediaDescription;
	}
	
	public void setDbpediaDescription(String dbpediaDescription) {
		this.dbpediaDescription = dbpediaDescription;
	}
	
	public ArrayList<Row> getPairs() {
		return pairs;
	}
	
	public void setPairs(ArrayList<Row> pairs) {
		this.pairs = pairs;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getDbpediaAbstract() {
		return dbpediaAbstract;
	}

	public void setDbpediaAbstract(String dbpediaAbstract) {
		this.dbpediaAbstract = dbpediaAbstract;
	}

	public int getTotalDbProperties() {
		return totalDbProperties;
	}

	public void setTotalDbProperties(int totalDbProperties) {
		this.totalDbProperties = totalDbProperties;
	}

	public int getTotalWdProperties() {
		return totalWdProperties;
	}

	public void setTotalWdProperties(int totalWdProperties) {
		this.totalWdProperties = totalWdProperties;
	}

	public int getSameProperties() {
		return sameProperties;
	}

	public void setSameProperties(int sameProperties) {
		this.sameProperties = sameProperties;
	}
	
}
