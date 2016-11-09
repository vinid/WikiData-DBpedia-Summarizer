package org.complexsystems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.complexsystems.interfaces.Retriever;
import org.complexsystems.tools.Pair;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

/**
 * Classe che implementa l'interfaccia Retriever, per recuperare i dati
 * dalla base di conoscenza WikiData.
 */
public class WikiDataRetriever implements Retriever {

	private WikibaseDataFetcher wbdf;

	public WikiDataRetriever() {
		this.wbdf = new WikibaseDataFetcher();
	}

	@Override
	public ArrayList<Pair<String, String>> getAllPairs(String searchString) {
		
		EntityDocument document = wbdf.getEntityDocumentByTitle("enwiki",
				searchString);
			
	
		ArrayList<Pair<String, String>> list = 
				new ArrayList<Pair<String, String>>();

		if (document instanceof ItemDocument) {

			Iterator<Statement> it = ((ItemDocument) document)
					.getAllStatements();

			while (it.hasNext()) {
				Statement st = it.next();
				Claim claim = st.getClaim();


				String[] property = getProperty(claim);
				Pair<String, String> pair = null;
				try{
					pair = new Pair<String, String>(
							property[1], getObject(claim));
					pair.setQualifiers(getQualifiers(claim));
					pair.setUriProperty(property[0]);
					
					list.add(pair);
					
				} catch (NullPointerException e) {
					System.out.println(property);
				}

			}
		}

		return list;
	}

	private String[] getProperty(Claim claim) {

		if (claim.getMainSnak() instanceof ValueSnak) {
			ValueSnak snk = (ValueSnak) claim.getMainSnak();
			PropertyIdValue prp = snk.getPropertyId();

			PropertyDocument property = (PropertyDocument) wbdf
					.getEntityDocument(prp.getId());
			
			return new String[]{prp.getIri(), 
					property.getLabels().get("en").getText()};

		} else {
			System.out.println(claim.getMainSnak());
			return null;
		}

	}

	private String getObject(Claim claim) {

		if (claim.getMainSnak() instanceof ValueSnak) {
			
			ValueSnak snk = (ValueSnak) claim.getMainSnak();
			
			Value v = snk.getValue();
			String objValue = "";
			
			if (v instanceof ItemIdValue) {
				EntityDocument et = wbdf.getEntityDocument(((ItemIdValue) v)
						.getId()); 
				objValue = (((ItemDocument) et).getLabels().get("en")
						.getText());
			
			} else {
				objValue = v.toString();
			}

			return objValue;

		} else {
			System.out.println(claim.getMainSnak());
			return "";
		}
	}

	private ArrayList<Pair<String, String>> getQualifiers(Claim claim) {

		ArrayList<Pair<String, String>> qualifiers = 
				new ArrayList<Pair<String, String>>();

		ArrayList<SnakGroup> list = (ArrayList<SnakGroup>) claim
				.getQualifiers();
		
		for (SnakGroup snakGroup : list) {
			List<Snak> snaks = snakGroup.getSnaks();
			
			for (Snak snak : snaks) {
				PropertyIdValue prp = snak.getPropertyId();
				PropertyDocument property = (PropertyDocument) wbdf
						.getEntityDocument(prp.getId());
				String propertyValue = property.getLabels().get("en").getText();
					if(snak instanceof ValueSnak)
					{
						Value v = ((ValueSnak) (snak)).getValue();
						String objValue = "";
						
						if (v instanceof ItemIdValue) {
							EntityDocument et = wbdf.getEntityDocument(((ItemIdValue) v)
									.getId());
							if (((ItemDocument) et).getLabels().get("en") != null)
								objValue = (((ItemDocument) et).getLabels().get("en").getText());
							else
								objValue = "No label defined";
						} else {
							objValue = v.toString();
						}						
						qualifiers.add(new Pair<String, String>(propertyValue, objValue));
					} else {
						qualifiers.add(new Pair<String, String>(propertyValue, snak
								.toString()));
					}
			}
		}
		return qualifiers;
	}

	@Override
	public String getDescription(String text) {
		EntityDocument document = wbdf.getEntityDocumentByTitle("enwiki", text);
			
		if (document instanceof ItemDocument) {
			return ((ItemDocument)document).getDescriptions().get("en").getText();
		
		}
		return "No description";
	}
}
