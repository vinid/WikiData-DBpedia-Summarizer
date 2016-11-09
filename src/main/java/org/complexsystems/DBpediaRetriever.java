package org.complexsystems;

import java.util.ArrayList;

import org.complexsystems.interfaces.Retriever;
import org.complexsystems.tools.DBpediaTextToEntity;
import org.complexsystems.tools.Pair;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Classe che implementa l'interfaccia Retriever, per recuperare i dati dalla
 * base di conoscenza DBpedia.
 */
public class DBpediaRetriever implements Retriever {

	private static final String RDFSCHEMAPREFIX = 
			"<http://www.w3.org/2000/01/rdf-schema#>";
	private static final String SPARQLSERVICE = "http://dbpedia.org/sparql";

	public DBpediaRetriever() {
	}

	@Override
	public ArrayList<Pair<String, String>> getAllPairs(String searchString) {
		// ArrayList dove salvare le pair
		ArrayList<Pair<String, String>> list = 
				new ArrayList<Pair<String, String>>();

		ParameterizedSparqlString qs = new ParameterizedSparqlString(""
				+ "prefix rdfs:" + RDFSCHEMAPREFIX + "\n\n"
				+ "select ?prop ?obj where {\n" 
				+ "  " + searchString
				+ " ?prop ?obj\n"
				+ " }");

		QueryExecution exec = QueryExecutionFactory.sparqlService(
				SPARQLSERVICE, qs.asQuery());

		ResultSet results = ResultSetFactory.copyResults(exec.execSelect());

		while (results.hasNext()) {
			
			QuerySolution node = results.next();
			RDFNode propNode = node.get("prop");
			RDFNode objNode = node.get("obj");
			
			String prop = propNode.toString();
			String obj = objNode.toString();
			
			//string senza l'http:ecc...
			String propString = cleanDBpediaProperty(prop);
			
			Pair<String, String> pair = new Pair<String, String>(propString, obj);
			pair.setUriProperty(prop);
			list.add(pair);
		}
		return list;

	}
	

	private String cleanDBpediaProperty(String property){
		if (property.contains("/")) //es. http://dbpedia.org/ontology/team
			property = property.substring(property.lastIndexOf("/")+1); //ottengo team
		if (property.contains("#")) //es. http://www.w3.org/1999/02/22-rdf-syntax-ns#type
			property = property.substring(property.lastIndexOf("#")+1); //ottengo type
		return property;
	}
	
	public String getSummary(String text) {
		ParameterizedSparqlString qs = new ParameterizedSparqlString("PREFIX dbo:<http://dbpedia.org/ontology/> \n"
				+ "select ?obj where {\n" 
				+ "  " + text
				+ " dbo:abstract ?obj\n"
				+ " FILTER(langMatches(lang(?obj), \"en\")) }");

		QueryExecution exec = QueryExecutionFactory.sparqlService(
				SPARQLSERVICE, qs.asQuery());

		ResultSet results = ResultSetFactory.copyResults(exec.execSelect());

		String obj = "";
		while (results.hasNext()) {
			RDFNode objNode = results.next().get("obj");
			obj = objNode.toString();

		}
		return obj.replace("@en", "");
	}
	
	@Override
	public String getDescription(String text) {
		String shortDescr = getShortDescription(text);
		if (getShortDescription(text).equals(""))
			shortDescr = "Keywords: " + getUmbelKeywords(text);
		return shortDescr;
	}
	
	private String getShortDescription(String text) {
		ParameterizedSparqlString qs = new ParameterizedSparqlString(
				"SELECT ?description WHERE {"
				+ text + " <http://dbpedia.org/property/shortDescription> ?description . "
				+ "FILTER (lang(?description) = \"en\") "
				+" }");

		QueryExecution exec = QueryExecutionFactory.sparqlService(
				SPARQLSERVICE, qs.asQuery());

		ResultSet results = ResultSetFactory.copyResults(exec.execSelect());

		String obj = "";
		while (results.hasNext()) {
			RDFNode objNode = results.next().get("description");
			if (objNode != null)
				obj += objNode.toString() + " ";

		}		
		return obj.replace("@en", "");
	}
	
	private String getUmbelKeywords(String text) {
		ParameterizedSparqlString qs = new ParameterizedSparqlString("PREFIX dbo:<http://dbpedia.org/ontology/> \n"
				+ "select ?obj where {\n" 
				+ "  " + text
				+ " a ?obj\n"
				+ " FILTER regex(?obj, 'umbel') }");

		QueryExecution exec = QueryExecutionFactory.sparqlService(
				SPARQLSERVICE, qs.asQuery());

		ResultSet results = ResultSetFactory.copyResults(exec.execSelect());

		String obj = "";
		while (results.hasNext()) {
			RDFNode objNode = results.next().get("obj");
			if (objNode != null)
				obj += objNode.toString() + " ";

		}
		obj = obj.replace("http://umbel.org/umbel/rc/", "");
		return obj;
	}
	
	
	public static void main(String args[])
	{
		DBpediaRetriever dn = new DBpediaRetriever();
		DBpediaTextToEntity dte = new DBpediaTextToEntity("Barack Obama");
		System.out.println(dn.getSummary(dte.getEntity()));

	}
}
