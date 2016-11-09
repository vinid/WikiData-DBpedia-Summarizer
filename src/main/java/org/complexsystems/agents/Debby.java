package org.complexsystems.agents;

import java.io.IOException;

import org.complexsystems.DBpediaGetModule;
import org.complexsystems.tools.Entity;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Debby extends Agent {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {

		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					System.out.println(" - " + myAgent.getLocalName() + " <- "
							+ "ha ricevuto un messaggio");

				    ACLMessage reply = new ACLMessage( ACLMessage.INFORM );
				    try {
						reply.setContentObject(askDBpediaEntity(msg.getContent()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				    reply.addReceiver( msg.getSender() );
				    send(reply);
				    doDelete();
				}
				block();
			}
		});
	}
	
	private Entity askDBpediaEntity(String query) {
		DBpediaGetModule dbd = new DBpediaGetModule();
		Entity db = dbd.getData(query);

		return db;
	}
}