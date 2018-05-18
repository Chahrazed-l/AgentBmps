package com.master;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


public class AgentMaster extends Agent {
	long execTime;
	long deblockTime;
	ArrayList<AID> userAID = new ArrayList<AID>();
	
	public void setup() {
		Object[] args = getArguments();
		execTime = Long.valueOf(args[0].toString()).longValue();
		deblockTime = Long.valueOf(args[1].toString()).longValue();
		userAID = userId();
	

		this.addBehaviour(new MasterBehavior());
	}

	public class MasterBehavior extends CyclicBehaviour {

		@Override
		public void action() {
			
			try {
				Thread.sleep(execTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ACLMessage msg1 = new ACLMessage(ACLMessage.CFP);
			for(int i=0;i<userAID.size();i++) 	{
			msg1.addReceiver(userAID.get(i));
			}
			send(msg1);
			//Deblock()
			try {
				Thread.sleep(deblockTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			ACLMessage msg112 = new ACLMessage(ACLMessage.CANCEL);
			for(int i=0;i<userAID.size();i++) 
			msg112.addReceiver(userAID.get(i));
			
			send(msg112);

		}

	}
	
	// Get the IDs of all the user agents
	private ArrayList<AID> userId() {
		ArrayList<AID> agentsynchro = new ArrayList<AID>();
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("UserAgent");
		template.addServices(sd);
		boolean found = false;
		try {
			do {

				DFAgentDescription[] resultList = DFService.search(this, template);
				if (resultList != null && resultList.length > 0) {
					for (int i = 0; i < resultList.length; i++) {
						agentsynchro.add(resultList[i].getName());
					}
					found = true;
				} // System.out.println("not found yet");
			} while (!found);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return agentsynchro;
	}

}
