package com.agents;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.acl.Acl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;

public class UserAgent extends Agent {

	private String login;
	private String password;
	private String platform_URI;
	private String tenantId;
	private String containerName;
	private Class<?> con;
	private String token;
	private String className;
	private int nbpage = 0;
	private int nbproc = 10;
	Object conn1;
	private long userId;
	private AID synchroagentID;
	private int k;
	private int p;
	private ArrayList<Long> listofPendingTasks;
	private Method getNameMethod1;

	// Initilize the agent
	public void setup() {
		Object[] args = getArguments();
		platform_URI = args[0].toString();
		login = args[1].toString();
		password = args[2].toString();
		tenantId = args[3].toString();
		className = args[4].toString();
		try {
			con = Class.forName(className);
			Object conn = con.newInstance();
			Method getNameMethod = conn.getClass().getMethod("getConnectionManager");
			PoolingHttpClientConnectionManager pool = (PoolingHttpClientConnectionManager) getNameMethod.invoke(conn);
			Constructor<?> myConstructor = con.getConstructor(CloseableHttpClient.class, String.class);
			conn1 = myConstructor.newInstance(HttpClients.custom().setConnectionManager(pool).build(), platform_URI);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		registerUserAgent();

		this.addBehaviour(new UserBehavior());
	}

	// A cyclic behavior
	public class UserBehavior extends Behaviour {
		int step = 1;
		MessageTemplate mt;

		@Override
		public void action() {

			switch (step) {

			case 1:
				// Connect to the portal
				Class<?>[] paramTypes = { String.class, String.class, String.class };
				try {
					getNameMethod1 = conn1.getClass().getMethod("doLogin", paramTypes);
					token = (String) getNameMethod1.invoke(conn1, login, password, tenantId);
					Class<?>[] paramTypess = { String.class, String.class };
					getNameMethod1 = conn1.getClass().getMethod("getactorID", paramTypess);
					userId = (Long) getNameMethod1.invoke(conn1, token, login);
				} catch (NoSuchMethodException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} catch (SecurityException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					containerName = myAgent.getContainerController().getContainerName();
					synchroagentID = synchroId(containerName);
				} catch (ControllerException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				// Consulter la liste des taches a executer
				listofPendingTasks = new ArrayList<Long>();

				try {
					Class<?>[] paramType = { int.class, int.class, String.class, long.class };
					getNameMethod1 = conn1.getClass().getMethod("retreiveTask", paramType);
					listofPendingTasks = (ArrayList<Long>) getNameMethod1.invoke(conn1, nbpage, nbproc, token, userId);
					// System.out.println("I m the agent " + myAgent.getLocalName()
					// + " La taille de la liste des pending tasks is " +
					// listofPendingTasks.size());
				} catch (NoSuchMethodException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} catch (SecurityException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (listofPendingTasks.size() > 0) {
					k = listofPendingTasks.size();
					Random r = new Random();

					p = r.nextInt(k);
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					msg.addReceiver(synchroagentID);
					try {
						msg.setContentObject(listofPendingTasks.get(p));
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					send(msg);
					mt = MessageTemplate.MatchAll();
					ACLMessage mg = myAgent.receive(mt);
					if (mg != null) {
						if (mg.getPerformative() == ACLMessage.INFORM) {
							try {
								try {
									Class<?>[] paramTypess = { long.class, long.class, String.class };
									getNameMethod1 = conn1.getClass().getMethod("autoAssign", paramTypess);
									getNameMethod1.invoke(conn1, listofPendingTasks.get(p), userId, token);
								} catch (NoSuchMethodException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								Class<?>[] paramTyp = { long.class, long.class, String.class };
								getNameMethod1 = conn1.getClass().getMethod("executeTask", paramTyp);
								getNameMethod1.invoke(conn1, listofPendingTasks.get(p), userId, token);
							} catch (NoSuchMethodException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SecurityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							step = 2;
						} else if (mg.getPerformative() == ACLMessage.REFUSE) {
							step = 2;
						} else {
							ACLMessage msg1 = new ACLMessage(ACLMessage.DISCONFIRM);
							msg1.addReceiver(synchroagentID);
							send(msg1);
							step = 2;
						}
					}
					block();
				} else {
					step = 2;
				}
				break;
			case 2:
				mt = MessageTemplate.MatchAll();
				ACLMessage mg1 = myAgent.receive(mt);
				if (mg1 != null) {
					if (mg1.getPerformative() == ACLMessage.CFP) {
						step = 3;
					}
				} else {
					step = 1;
				}
				break;
			case 3:
				mt = MessageTemplate.MatchAll();
				ACLMessage mg2 = myAgent.receive(mt);
				if (mg2 != null) {
					if (mg2.getPerformative() == ACLMessage.CANCEL) {
						step = 1;
					}
				}
				block();
				break;
			}
		}

		@Override
		public boolean done() {
			return step == -1;
		}

	}

	// Register the Technical Agent within the DF: Directory Facilitator
	private void registerUserAgent() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(this.getLocalName());
		sd.setType("UserAgent");
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

	}

	private AID synchroId(String containerName) {
		AID agentsynchro = null;
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("synchro" + containerName);
		template.addServices(sd);
		boolean found = false;
		try {
			do {

				DFAgentDescription[] resultList = DFService.search(this, template);
				if (resultList != null && resultList.length > 0) {
					agentsynchro = resultList[0].getName();
					found = true;
				}
			} while (!found);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return agentsynchro;
	}

}
