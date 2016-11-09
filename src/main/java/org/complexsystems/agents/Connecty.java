package org.complexsystems.agents;

import info.debatty.java.stringsimilarity.Cosine;
import info.debatty.java.stringsimilarity.Jaccard;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

import org.complexsystems.DBpediaWikiDataConnector;
import org.complexsystems.metrics.CustomMetrics;
import org.complexsystems.tools.Entity;
import org.complexsystems.tools.Pair;
import org.complexsystems.tools.Results;
import org.complexsystems.tools.Row;
import org.complexsystems.tools.StringStaticTools;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import java.util.Set;

public class Connecty extends Agent {

	private class Aggregator {
		private String wikiDataDescription;
		private String DBpediaDescription;
		private String dbpediaAbstract;

		private ArrayList<Pair<String, String>> wdProp;
		private ArrayList<Pair<String, String>> dbProp;

		private ArrayList<Row> data;

		public Aggregator() {
			setWdProp(new ArrayList<Pair<String, String>>());
			setDbProp(new ArrayList<Pair<String, String>>());
			setData(new ArrayList<Row>());
		}

		public String getWikiDataDescription() {
			return wikiDataDescription;
		}

		public void setWikiDataDescription(String wikiDataDescription) {
			this.wikiDataDescription = wikiDataDescription;
		}

		public String getDBpediaDescription() {
			return DBpediaDescription;
		}

		public void setDBpediaDescription(String dBpediaDescription) {
			DBpediaDescription = dBpediaDescription;
		}

		public ArrayList<Pair<String, String>> getWdProp() {
			return wdProp;
		}

		public void setWdProp(ArrayList<Pair<String, String>> wdProp) {
			this.wdProp = wdProp;
		}

		public ArrayList<Pair<String, String>> getDbProp() {
			return dbProp;
		}

		public void setDbProp(ArrayList<Pair<String, String>> dbProp) {
			this.dbProp = dbProp;
		}

		public ArrayList<Row> getData() {
			return data;
		}

		public void setData(ArrayList<Row> data) {
			this.data = data;
		}

		public String getDbpediaAbstract() {
			return dbpediaAbstract;
		}

		public void setDbpediaAbstract(String dbpediaAbstract) {
			this.dbpediaAbstract = dbpediaAbstract;
		}

		public void order() {

			ArrayList<Row> dataTemp = new ArrayList<Row>();

			for (Row row : data) {
				String propertyOne = row.getProperties().getProperty().get(0)
						.getProperty();
				if (!dataTemp.contains(row)) {
					dataTemp.add(row);

					for (Row row2 : data) {
						if (row != row2) {
							String propertyTwo = row2.getProperties()
									.getProperty().get(0).getProperty();
							if (propertyOne == propertyTwo) {
								dataTemp.add(row2);
							}
						}
					}
				}
			}
			data = dataTemp;
		}

		public void removeDuplicate() {
			for (int i = 0; i < data.size(); ++i) {
				for (int j = 0; j < data.size(); ++j) {
					if (i != j) {
						if (data.get(i).hasEqualProperties(data.get(j))) { // hanno
																			// la
																			// stessa
																			// property
							if (data.get(i).getSimilarity() > data.get(j)
									.getSimilarity()) { // similarity(i) >
														// similarity(j)
								data.remove(j);
							} else {
								data.remove(i);
							}
							i = 0;
							j = 0;
						}
					}
				}
			}
		}

	}

	private static final long serialVersionUID = 1L;

	private Aggregator agg;
	private String inputQuery;
	private InputStreamReader inputStream;
	private DataOutputStream response;
	private Socket clientSocket;
	private BufferedReader input;

	@Override
	protected void setup() {

		Object[] args = getArguments();
		this.clientSocket = (Socket) args[0];
		this.agg = new Aggregator();

		try {
			if (this.clientSocket != null)
				System.out.println("Sto servendo il client che ha indirizzo "
						+ this.clientSocket.getInetAddress());

			this.inputStream = new InputStreamReader(
					this.clientSocket.getInputStream());
			this.response = new DataOutputStream(
					this.clientSocket.getOutputStream());
			this.input = new BufferedReader(this.inputStream);
			this.inputQuery = input.readLine();

			System.out.println("The input query is: " + this.inputQuery);
		} catch (IOException e) {
			System.out.println("Accept failed.");
		}

		// Creo Winky e Debby
		String nameWinkyAgent = this.getLocalName()
				.replace("Connecty", "Winky");
		String nameDebbyAgent = this.getLocalName()
				.replace("Connecty", "Debby");
		AgentContainer c = getContainerController();
		try {
			AgentController a = c.createNewAgent(nameWinkyAgent,
					"org.complexsystems.agents.Winky", null);
			AgentController b = c.createNewAgent(nameDebbyAgent,
					"org.complexsystems.agents.Debby", null);
			a.start();
			b.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Contatto Winky
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(nameWinkyAgent, AID.ISLOCALNAME));
		msg.setLanguage("English");
		msg.setOntology("Weather-Forecast-Ontology");
		msg.setContent(this.inputQuery);
		send(msg);

		// Contatto Debby
		msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(nameDebbyAgent, AID.ISLOCALNAME));
		msg.setLanguage("English");
		msg.setOntology("Weather-Forecast-Ontology");
		msg.setContent(this.inputQuery);
		send(msg);

		addBehaviour(new CyclicBehaviour() {
			private static final long serialVersionUID = 1L;
			private boolean winkyReply = false;
			private boolean debbyReply = false;

			public void action() {
				// ricevo il messaggio
				ACLMessage msg = receive();

				if (msg != null)
					try {
						System.out.println(" - " + myAgent.getLocalName()
								+ " <- ha ricevuto un messaggio da "
								+ msg.getSender().getLocalName());
						String sender = msg.getSender().getName();

						if (sender.contains("Winky")) {
							Entity wiki = ((Entity) msg.getContentObject());
							agg.setWikiDataDescription(wiki.getDescription());
							agg.setWdProp(wiki.getListOfPropertiesAndPairs());
							winkyReply = true;
						} else if (sender.contains("Debby")) {
							Entity db = ((Entity) msg.getContentObject());
							agg.setDBpediaDescription(db.getDescription());
							agg.setDbProp(db.getListOfPropertiesAndPairs());
							agg.setDbpediaAbstract(db.getSummary());
							debbyReply = true;
						}

						if (winkyReply && debbyReply) {
							if (agg.getDbProp().size() != 0
									&& agg.getWdProp().size() != 0) {

								System.out.println("Check SameAs links");
								checkSameAsProperties();

								System.out.println("Check CustomSameAs links");
								checkCustomSameAsProperties();

								System.out.println("Check Cosine links");
								checkCosineSameAsProperties();

								System.out.println("Check Jaccard links");
								checkJaccardSameAsProperties();

								System.out.println("Removing duplicate....");
								agg.removeDuplicate();

								agg.order();

								Results res = new Results();
								res.setDbpediaDescription(agg
										.getDBpediaDescription());
								res.setWikidataDescription(agg
										.getWikiDataDescription());
								res.setPairs(agg.data);
								res.setEntity(inputQuery);
								res.setDbpediaAbstract(agg.getDbpediaAbstract());

								// add statistics
								Set<String> set = new HashSet<String>();
								for (Pair<String, String> pair : agg.dbProp) {
									set.add(pair.getUriProperty());
								}
								res.setTotalDbProperties(set.size());

								set.clear();

								for (Pair<String, String> pair : agg.wdProp) {
									set.add(pair.getUriProperty());
								}
								res.setTotalWdProperties(set.size());
								res.setSameProperties(agg.data.size());

								ObjectMapper mapper = new ObjectMapper();
								String jsonString = "";
								String jsonPrettyString = "";
								try {
									jsonString = mapper.writeValueAsString(res);
									jsonPrettyString = mapper
											.writerWithDefaultPrettyPrinter()
											.writeValueAsString(res);
								} catch (JsonGenerationException e) {
									e.printStackTrace();
								} catch (JsonMappingException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}

								System.out.println(jsonPrettyString);

								response.writeBytes(jsonString);
								response.flush();
								doDelete();
							} else {
								if (agg.getDbProp().size() == 0)
									System.out
											.println("La ricerca di Debby non ha prodotto risultati");
								if (agg.getWdProp().size() == 0)
									System.out
											.println("La ricerca di Winky non ha prodotto risultati");
								doDelete();
							}
						}
					} catch (UnreadableException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				block();
			}
		});
	}

	@Override
	public void doDelete() {
		super.doDelete();
		try {
			this.inputStream.close();
			this.response.close();
			this.input.close();
			this.clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkSameAsProperties() {
		DBpediaWikiDataConnector c = new DBpediaWikiDataConnector();

		for (Pair<String, String> pair : agg.dbProp) {
			if (pair.getUriProperty().isEmpty())
				continue;

			String wdPropertyUri = c.findSameAsDBpediaToWikiData(pair
					.getUriProperty());

			if (wdPropertyUri == null)
				continue;

			for (Pair<String, String> pair2 : agg.wdProp) {
				if (pair2.getUriProperty().equals(wdPropertyUri)) {

					ArrayList<Pair<String, String>> tempWd = new ArrayList<Pair<String, String>>();
					for (Pair<String, String> pair3 : agg.wdProp) {
						if (pair3.getUriProperty().equals(wdPropertyUri)) {
							tempWd.add(pair3);
						}
					}

					ArrayList<Pair<String, String>> tempDb = new ArrayList<Pair<String, String>>();
					for (Pair<String, String> pair3 : agg.dbProp) {
						if (pair3.getProperty().equals(pair.getProperty())) {
							tempDb.add(pair3);
						}
					}

					Row row = new Row(
							new Pair<ArrayList<Pair<String, String>>, ArrayList<Pair<String, String>>>(
									tempWd, tempDb), 1.0, "EP");
					agg.getData().add(row);
				}
			}
		}

		// Rimozione dei duplicati
		Set<Row> hs = new HashSet<Row>();
		hs.addAll(agg.getData());
		agg.getData().clear();
		agg.getData().addAll(hs);

		for (Row row : agg.getData()) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println("Similarity: " + row.getSimilarity());
			System.out.println(row.getProperties());
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<");
		}

	}

	private void checkCustomSameAsProperties() {

		for (Pair<String, String> wdPair : agg.wdProp) {
			for (Pair<String, String> dbPair : agg.dbProp) {
				String wdProperty = wdPair.getProperty();
				String dbProperty = cleanDBpediaProperty(dbPair
						.getUriProperty());

				if (CustomMetrics.customSameAs(wdProperty, dbProperty) == 1) {// ho
																				// le
																				// stesse
																				// parole,
																				// magari
																				// in
																				// ordine
																				// diverso

					ArrayList<Pair<String, String>> tempWd = new ArrayList<Pair<String, String>>();
					for (Pair<String, String> pair : agg.wdProp) {
						if (pair.getProperty().equals(wdProperty)) {
							tempWd.add(pair);
						}
					}

					ArrayList<Pair<String, String>> tempDb = new ArrayList<Pair<String, String>>();
					for (Pair<String, String> pair : agg.dbProp) {
						if (cleanDBpediaProperty(pair.getProperty()).equals(
								dbProperty)) {
							tempDb.add(pair);
						}
					}

					Row row = new Row(
							new Pair<ArrayList<Pair<String, String>>, ArrayList<Pair<String, String>>>(
									tempWd, tempDb), 1.0, "CC");
					agg.getData().add(row);
				}
			}
		}

		// Rimozione dei duplicati
		Set<Row> hs = new HashSet<Row>();
		hs.addAll(agg.getData());
		agg.getData().clear();
		agg.getData().addAll(hs);

		for (Row row : agg.getData()) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println("Similarity: " + row.getSimilarity());
			System.out.println(row.getProperties());
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<");
		}
	}

	private void checkCosineSameAsProperties() {

		Cosine c1 = new Cosine();

		for (Pair<String, String> wdPair : agg.wdProp) {
			for (Pair<String, String> dbPair : agg.dbProp) {
				String wdProperty = wdPair.getProperty();
				String dbProperty = cleanDBpediaProperty(dbPair
						.getUriProperty());

				double score = c1.similarity(
						StringStaticTools.splitCamelCase(wdProperty),
						StringStaticTools.splitCamelCase(dbProperty));
				if (score > 0.70) {// dammi tre paroleee

					ArrayList<Pair<String, String>> tempWd = new ArrayList<Pair<String, String>>();
					for (Pair<String, String> pair : agg.wdProp) {
						if (pair.getProperty().equals(wdProperty)) {
							tempWd.add(pair);
						}
					}

					ArrayList<Pair<String, String>> tempDb = new ArrayList<Pair<String, String>>();
					for (Pair<String, String> pair : agg.dbProp) {
						if (cleanDBpediaProperty(pair.getProperty()).equals(
								dbProperty)) {
							tempDb.add(pair);
						}
					}

					Row row = new Row(
							new Pair<ArrayList<Pair<String, String>>, ArrayList<Pair<String, String>>>(
									tempWd, tempDb), score, "CS");
					agg.getData().add(row);
				}
			}
		}

		// Rimozione dei duplicati
		Set<Row> hs = new HashSet<Row>();
		hs.addAll(agg.getData());
		agg.getData().clear();
		agg.getData().addAll(hs);

		for (Row row : agg.getData()) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println("Similarity: " + row.getSimilarity());
			System.out.println(row.getProperties());
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<");
		}
	}

	private void checkJaccardSameAsProperties() {

		Jaccard c1 = new Jaccard();

		for (Pair<String, String> wdPair : agg.wdProp) {
			for (Pair<String, String> dbPair : agg.dbProp) {
				String wdProperty = wdPair.getProperty();
				String dbProperty = cleanDBpediaProperty(dbPair
						.getUriProperty());

				double score = c1.similarity(
						StringStaticTools.splitCamelCase(wdProperty),
						StringStaticTools.splitCamelCase(dbProperty));
				if (score > 0.60) {// dammi tre paroleee

					ArrayList<Pair<String, String>> tempWd = new ArrayList<Pair<String, String>>();
					for (Pair<String, String> pair : agg.wdProp) {
						if (pair.getProperty().equals(wdProperty)) {
							tempWd.add(pair);
						}
					}

					ArrayList<Pair<String, String>> tempDb = new ArrayList<Pair<String, String>>();
					for (Pair<String, String> pair : agg.dbProp) {
						if (cleanDBpediaProperty(pair.getProperty()).equals(
								dbProperty)) {
							tempDb.add(pair);
						}
					}

					Row row = new Row(
							new Pair<ArrayList<Pair<String, String>>, ArrayList<Pair<String, String>>>(
									tempWd, tempDb), score, "JI");
					agg.getData().add(row);
				}
			}
		}

		// Rimozione dei duplicati
		Set<Row> hs = new HashSet<Row>();
		hs.addAll(agg.getData());
		agg.getData().clear();
		agg.getData().addAll(hs);

		for (Row row : agg.getData()) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println("Similarity: " + row.getSimilarity());
			System.out.println(row.getProperties());
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<");
		}
	}

	private String cleanDBpediaProperty(String property) {
		if (property.contains("/")) // es. http://dbpedia.org/ontology/team
			property = property.substring(property.lastIndexOf("/") + 1); // ottengo
																			// team
		if (property.contains("#")) // es.
									// http://www.w3.org/1999/02/22-rdf-syntax-ns#type
			property = property.substring(property.lastIndexOf("#") + 1); // ottengo
																			// type
		return property;
	}
}
