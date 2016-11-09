package org.complexsystems.tools;


import java.util.ArrayList;

public class Entity implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String description;
	private String summary;
	private ArrayList<Pair<String, String>> listOfPropertiesAndPairs;
	
	public Entity(String description, String summary, ArrayList <Pair<String, String>> list) {
		this.description = description;
		this.summary = summary;
		this.listOfPropertiesAndPairs = list;
	}
	
	public ArrayList<Pair<String, String>> getListOfPropertiesAndPairs() {
		return listOfPropertiesAndPairs;
	}
	
	public void setListOfPropertiesAndPairs(ArrayList<Pair<String, String>> listOfPropertiesAndPairs) {
		this.listOfPropertiesAndPairs = listOfPropertiesAndPairs;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
}
