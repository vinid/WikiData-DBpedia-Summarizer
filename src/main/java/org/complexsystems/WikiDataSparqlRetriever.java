package org.complexsystems;

import java.util.ArrayList;

import org.complexsystems.interfaces.Retriever;
import org.complexsystems.tools.Pair;
import org.complexsystems.tools.WikidataSparqlResult;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class WikiDataSparqlRetriever implements Retriever {

	private static final String RDFSCHEMAPREFIX = 
			"<http://www.w3.org/2000/01/rdf-schema#>";
	private static final String WDPREFIX ="<http://www.wikidata.org/entity/>";
	private static final String WDTPREFIX ="<http://www.wikidata.org/entity/>";
	private static final String WIKIBASEPEFIX ="<http://wikiba.se/ontology#>";
	private static final String SPARQLSERVICE = "http://wdqs-beta.wmflabs.org/bigdata/namespace/wdq/sparql/";

	public WikiDataSparqlRetriever() {
	}

	@Override
	public ArrayList<Pair<String, String>> getAllPairs(String searchString) {
		// ArrayList dove salvare le pair
		ArrayList<Pair<String, String>> list = 
				new ArrayList<Pair<String, String>>();

		ParameterizedSparqlString qs = new ParameterizedSparqlString(""
				+ "prefix rdfs:" + RDFSCHEMAPREFIX + "\n"
				+ "prefix wd:" + WDPREFIX + "\n"
				+ "prefix wdt:" + WDTPREFIX + "\n"
				+ "prefix wikibase:" + WIKIBASEPEFIX + "\n"
				+ "SELECT ?property ?statementURI ?statQual ?statQualLabel ?object ?objectLabel WHERE { "
				+ "<" + searchString + "> ?p ?statementURI ."
				+ "?property ?ref ?p ."
				+ "?property rdfs:label ?propertyLabel FILTER (lang(?propertyLabel) = \"en\") ."
				+ "FILTER regex(str(?statementURI), \"statement\") ."
					+ "OPTIONAL {"
						+ "?statementURI ?statQual ?object ."
						+ "FILTER( regex(str(?statQual), \"statement\") || regex(str(?statQual), \"qualifier\") ) ."
						+ "FILTER (!regex(str(?object), \"wikidata.org/value\")) ."
						+ "OPTIONAL {"
							+ "?object rdfs:label ?objectLabel ."
							+ "FILTER (lang(?objectLabel) = \"en\") ."
						+ "}"
						+ "OPTIONAL {"
							+ "?ak ?bk ?statQual ."
							+ "?ak rdfs:label ?statQualLabel FILTER (lang(?statQualLabel) = \"en\") ."
						+ "}"
					+ "} "
				+ "}");

		System.out.println(qs.asQuery());
		
		QueryExecution exec = QueryExecutionFactory.sparqlService(
				SPARQLSERVICE, qs.asQuery());
		ResultSet results = ResultSetFactory.copyResults(exec.execSelect());
		
		ArrayList<WikidataSparqlResult> wdResultStatement = new ArrayList<WikidataSparqlResult>();
		ArrayList<WikidataSparqlResult> wdResultQualifier = new ArrayList<WikidataSparqlResult>();
		
		while (results.hasNext()) {
			QuerySolution node = results.next();
			RDFNode propNode = node.get("property");
			RDFNode objectNode = node.get("object");			
			RDFNode objectLabelNode = node.get("objectLabel");
			RDFNode statementURINode = node.get("statementURI");
			RDFNode statQualNode = node.get("statQual");
			RDFNode statQualLabelNode = node.get("statQualLabel");
			
			String prop = propNode.toString();
			String object = objectNode.toString();
			String objectLabel = "";
			if (objectLabelNode != null)
				objectLabel = objectLabelNode.toString();
			String statementURI = statementURINode.toString();
			String statQual = statQualNode.toString();
			String statQualLabel = statQualLabelNode.toString().replace("@en", "");
			
			/*
			 * Se è uno statement lo salvo nella lista statement, se è un qualifier lo salvo nella lista qualifier
			 */
			if (statQual.contains("statement")) {
				wdResultStatement.add(new WikidataSparqlResult(prop, object, objectLabel, statementURI, statQual, statQualLabel));
			} else if (statQual.contains("qualifier")) {
				wdResultQualifier.add(new WikidataSparqlResult(prop, object, objectLabel, statementURI, statQual, statQualLabel));
			}
		}
				
		/*
		 * Processo gli statement. Per ognuno, cerco se esiste lo statement di riferimento nei qualifier
		 */
		for (WikidataSparqlResult r1 : wdResultStatement) {
			String uri = r1.getStatementURI();
			String label1 = r1.getObjectLabel();
			if (label1.equals(""))
				label1 = r1.getObject();
			
			Pair<String, String> pair = new Pair<String, String>(r1.getStatQualLabel(), label1);
			pair.setUriProperty(r1.getProp());
			pair.setUriObject(r1.getObject());
			
			for (WikidataSparqlResult r2 : wdResultQualifier) {
				if (r2.getStatementURI().equals(uri)) {
					String label2 = r2.getObjectLabel();
					if (label2.equals(""))
						label2 = r2.getObject();
					pair.addQualifier(new Pair<String, String>(r2.getStatQualLabel(), label2));
				}
			}
			
			list.add(pair);
		}
		
		return list;

	}

	@Override
	public String getDescription(String text) {
		ParameterizedSparqlString qs = new ParameterizedSparqlString("PREFIX wd: <http://www.wikidata.org/entity/>"
				+ "SELECT ?description WHERE {"
				+ "<" + text + "> <http://schema.org/description> ?description . "
				+ "FILTER (lang(?description) = \"en\") "
				+" }");

		QueryExecution exec = QueryExecutionFactory.sparqlService(
				SPARQLSERVICE, qs.asQuery());

		ResultSet results = ResultSetFactory.copyResults(exec.execSelect());

		String obj = "";
		while (results.hasNext()) {
			RDFNode objNode = results.next().get("description");
			obj += objNode.toString() + " ";

		}		
		return obj.replace("@en", "");
	}
	
}
