package org.complexsystems;

import java.util.ArrayList;

import org.complexsystems.tools.DBpediaTextToEntity;
import org.complexsystems.tools.Entity;
import org.complexsystems.tools.Pair;

/**
 * Modulo che effettua l'estrazione dei dati da DBpedia
 * @author vinid
 */
public class DBpediaGetModule {
	
	public Entity getData(String query)
	{
		
		String searchString = new DBpediaTextToEntity(query).getEntity();

		
		DBpediaRetriever dbRetriever = new DBpediaRetriever();
		ArrayList<Pair<String, String>> dbPairs = dbRetriever
				.getAllPairs(searchString);
		String description = dbRetriever.getDescription(searchString);
		String summary = dbRetriever.getSummary(searchString);
		
		Entity ent = new Entity(description, summary, dbPairs);

		return ent;
	}	
}
