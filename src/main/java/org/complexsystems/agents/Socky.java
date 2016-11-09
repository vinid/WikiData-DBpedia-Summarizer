package org.complexsystems.agents;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Socky extends Agent {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setup() {
		
		addBehaviour(new CyclicBehaviour(this) 
        {

			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				try {
					ServerSocket server = new ServerSocket(4309);
					System.out.println("In attesa di richieste...");
					while(true)
					{
						Socket client = server.accept();

						String nameAgent = "Connecty-" + String.valueOf((new Date()).hashCode()) ;
						AgentContainer c = getContainerController();
						try {
							AgentController a = c.createNewAgent(nameAgent, "org.complexsystems.agents.Connecty", new Object[]{client});
							a.start();
						}
						catch (Exception e) {
							server.close();
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
        });
	}	
}
