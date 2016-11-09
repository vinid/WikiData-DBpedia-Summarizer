package org.complexsystems.tools;

import org.complexsystems.interfaces.TextToEntity;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

public class WikiDataSparqlTextToEntity implements TextToEntity{

	private String string;
	private WikibaseDataFetcher wbdf;
	
	public WikiDataSparqlTextToEntity(String text)
	{
		this.wbdf = new WikibaseDataFetcher();
		EntityDocument document = wbdf.getEntityDocumentByTitle("enwiki",
				text);
		if (document != null)
			this.string = (document.getEntityId()).toString().replace(" (item)", "");
		else
			this.string = "";
	}
	
	@Override
	public String getEntity() {
		return this.string;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
}