package org.complexsystems.tools;

public class WikidataSparqlResult {
	private String prop;
	private String object;
	private String objectLabel;
	private String statementURI;
	private String statQual;
	private String statQualLabel;
	
	public WikidataSparqlResult() {
		this.prop = "";
		this.object = "";
		this.objectLabel = "";
		this.statementURI = "";
		this.statQual = "";
		this.statQualLabel = "";
	}
	
	public WikidataSparqlResult(String prop, String object, String objectLabel, 
			String statementUri, String statQual, String statQualLabel) {
		this.prop = prop;
		this.object = object;
		this.objectLabel = objectLabel;
		this.statementURI = statementUri;
		this.statQual = statQual;
		this.statQualLabel = statQualLabel;
	}

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getObjectLabel() {
		return objectLabel;
	}

	public void setObjectLabel(String objectLabel) {
		this.objectLabel = objectLabel;
	}

	public String getStatementURI() {
		return statementURI;
	}

	public void setStatementURI(String statementURI) {
		this.statementURI = statementURI;
	}

	public String getStatQual() {
		return statQual;
	}

	public void setStatQual(String statQual) {
		this.statQual = statQual;
	}

	public String getStatQualLabel() {
		return statQualLabel;
	}

	public void setStatQualLabel(String statQualLabel) {
		this.statQualLabel = statQualLabel;
	}
	
}
