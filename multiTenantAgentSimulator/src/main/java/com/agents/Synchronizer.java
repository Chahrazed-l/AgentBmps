package com.agents;

import java.util.ArrayList;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class Synchronizer extends Agent {
	private ArrayList<Long> listtakenTasks = new ArrayList<Long>();
	private int nbusers;
	private int i = 0;

	public void setup() {
		Object[] args = getArguments();
		nbusers = Integer.parseInt(args[0].toString());
		registerUserAgent();
		this.addBehaviour(new synchBehavior());
	}

	public class synchBehavior extends CyclicBehaviour {
		MessageTemplate mt;
		int step = 1;

		@Override
		public void action() {
			// TODO Auto-generated method stub
			switch (step) {
			case 1:
				mt = MessageTemplate.MatchAll();
				ACLMessage mg = myAgent.receive(mt);

				if (mg != null) {
					if (mg.getPerformative() == ACLMessage.REQUEST) {
						// Check if the agent user can take the task
						try {
							Long taskid = (Long) mg.getContentObject();
							// search in the list of hold tasks
							if (!takenTasks(taskid, listtakenTasks)) {
								listtakenTasks.add(taskid);
								// affiche(listtakenTasks);
								ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
								msg.addReceiver(mg.getSender());
								send(msg);
							} else {
								// task already taken by another agent
								ACLMessage msg1 = new ACLMessage(ACLMessage.REFUSE);
								msg1.addReceiver(mg.getSender());
								send(msg1);
							}
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						step = 1;
					}
					if (mg.getPerformative() == ACLMessage.DISCONFIRM) {
						i = i + 1;
						if (nbusers == i) {
							listtakenTasks = new ArrayList<Long>();
							i = 0;
						}
						step = 1;
					}

				} else {
					step = 1;
				}
				break;

			}

		}

	}

	private void registerUserAgent() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(this.getLocalName());
		sd.setType(this.getLocalName());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

	}

	private boolean takenTasks(long taskId, ArrayList<Long> list) {
		int i = 0;
		boolean found = false;
		int sizelist = list.size();
		while (!found && i < sizelist) {
			if (list.get(i) == taskId) {
				found = true;
			} else {
				i++;
			}

		}
		return found;
	}

}
