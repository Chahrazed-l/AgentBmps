# AgentBmps
The simulator contains three different agents:
- The user Agent: This agent imitates a normal user (restrictive manner for now). Such an agent can login to the bpms,retrieve tasks, auto-assign a task and execute a task.  
- The synchronizer per tenant: This agent is created per tenant. It synchronizes the reservation of available tasks between the agents (agents are parallel threads, so a concurrence is created when it comes to the reservation of tasks). 
- The Master Agent: responsible for managing the time within the simulation. It defines time intervals for executing the tasks within the BPMS. Once the period of execution is finish, the Master blocks the user agents. Latter, after a given time intervall the agents are deblocked, and so on. 

The simulator takes as input:
  - The name of the BPMS
  - The url of the BPMS
  - A config.txt file containing information about the Tenants names, IDs, and number of users per tenant
  - The execution Time: denotes the interval of time when the user agents are active before being blocked by the Master Agent.
  - The activation Time: denotes the period of time after which the Master Agent will deblock the user agents to execute the rest of the avialable tasks.  
