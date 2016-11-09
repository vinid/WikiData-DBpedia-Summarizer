package org.complexsystems.tools;

import org.complexsystems.interfaces.TextToEntity;

public class DBpediaTextToEntity implements TextToEntity{

	private String string;
	private final String DBPEDIAPREFIX = "http://dbpedia.org/resource/";
	
	public DBpediaTextToEntity(String text)
	{
		this.setString(text);
	}
	
	@Override
	public String getEntity() {
		return stringToResource(this.string);
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
	private String stringToResource (String searchString) {
		String resource = "<"
				+ DBPEDIAPREFIX
				+ searchString.replaceAll(" ", "_")
				+ ">";
		return resource;
	}

}
