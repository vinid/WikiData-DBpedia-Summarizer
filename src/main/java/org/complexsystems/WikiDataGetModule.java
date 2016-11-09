package org.complexsystems;

import java.util.ArrayList;

import org.complexsystems.tools.Entity;
import org.complexsystems.tools.Pair;
import org.complexsystems.tools.WikiDataSparqlTextToEntity;

public class WikiDataGetModule {
	
	/**
	 * Metodo per restituire al chiamante i dati trovati
	 * @return
	 */
	public Entity getData(String query)
	{
		WikiDataSparqlRetriever wdRetriever = new WikiDataSparqlRetriever();
	
		String serachString = new WikiDataSparqlTextToEntity(query).getEntity();

		if (serachString.equals(""))
			return null;

		ArrayList<Pair<String, String>> wdPairs = wdRetriever
				.getAllPairs(serachString);
		String description = wdRetriever.getDescription(serachString);
		Entity ent = new Entity(description, "", wdPairs);
		
		return ent;
	}
}
