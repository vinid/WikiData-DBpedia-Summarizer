package org.complexsystems;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;

/**
 * Classe che effettua i collegamenti tra le proprietà di DBpedia e WikiData
 * @author vinid
 */
public class DBpediaWikiDataConnector {

	/**
	 *  PREFIX owl: <http://www.w3.org/2002/07/owl#>
		PREFIX dbo: <http://dbpedia.org/ontology/>
		SELECT ?prop WHERE {
			dbo:team owl:equivalentProperty ?prop
		}
		risultato: http://www.wikidata.org/entity/P54	
	*/
	public String findSameAsDBpediaToWikiData(String dbProp)
	{
		/*
		 * Al momento solo alcune proprietà ontologiche hanno l'equivalent property.
		 * In questo modo evitiamo molte query sparql inutili (risultato sicuramente vuoto)
		 */
		if (dbProp.contains("http://dbpedia.org/ontology/")) {
			ParameterizedSparqlString qs = new ParameterizedSparqlString(
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
				+ "SELECT ?prop WHERE {"
				+ "<" + dbProp + "> owl:equivalentProperty ?prop "
				+ "FILTER regex(?prop, 'wikidata') }"
			);

			QueryExecution exec = QueryExecutionFactory.sparqlService(
					"http://dbpedia.org/sparql", qs.asQuery());
	
			ResultSet results = ResultSetFactory.copyResults(exec.execSelect());
	
			while (results.hasNext()) {
				return (results.next().get("prop").toString());
			}
		}
		
		return null;
	}
}
