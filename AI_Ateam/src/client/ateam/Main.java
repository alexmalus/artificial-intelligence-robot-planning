package client.ateam;

import client.ateam.Level.ArrayLevel;
import client.ateam.Level.ILevel;
import client.ateam.Level.Models.Agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    protected ILevel level = null;
    private BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws Exception {

     Main main = new Main();
     main.run();
    }

    public void run() throws Exception {
        TaskDistributor tasker = new TaskDistributor();

        this.level = ArrayLevel.getSingleton();
        this.level.ReadMap();

        tasker.distributeTasks(level.getAgents(),level.getBoxes(),level.getGoals()); //tasks are now located on each agent

        for(Agent agent: level.getAgents()){
            agent.planning();  //plan the initial tasks of each agent
        }

        StringJoiner strJoiner;
        String act;
        while (true) {
            strJoiner = new StringJoiner(", ","[","]");

//            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//            Ideas, future work:
//            find next moves
//            create joint action (action merging)
//            check for conflicts ( use ILevel methods for literals/atoms etc )
//            add list
//            delete list
//            current state
//            resolve conflicts
//            IDEA: run through all actions and gather add / delete lists into key-value maps (with affiliated task/agent)
//            then run through said key-value maps to check for conflicts and replan accordingly
//            check both preconditions from level and preconditions from key-value maps
//            ArrayList<Free> addEffects = new ArrayList<Free>();
//            ArrayList<Free> deleteEffects = new ArrayList<Free>();
//            ArrayList<Free> effects;// = new ArrayList<Free>();
//            ArrayList<Integer> agentIDs;
//            ArrayList<Conflict> conflictList = new ArrayList<Conflict>();
//            Map<Point,ArrayList<Free>> effectlist = new HashMap<Point,ArrayList<Free>>();
//            Map<Point,Boolean> resolvedGhostFields = new HashMap<Point,Boolean>();

//            accumulate effects of each agent
//            for(Agent agent : level.getAgents()){
//                 first we check simple preconditions
//                 these are identified as isAgent, isBox, isNeighbor and they concern the validity of agent and boxlocations

//                 this part concerns the conflict detection, this is concerned with the isFree literal
//                 The isFree literal is the main source of conflicts and limitation of movement
//                 An agent should never attempt to move into a wall due to action planning
//                 boxes in the way may have to be checked

//                addEffects = agent.getNextAction().getAddEffects();
//                deleteEffects = agent.getNextAction().getDeleteEffects();

//                for(Free addEffect : addEffects){
//                    add effect to key value set
//                }
//                for(Free deleteEffect : deleteEffects){
//                    add effect to key value set
//                }
//                for(Free effect : agent.getCurrentAction().getEffects()){
//                    //add effect to key value set
//
//                    //check if location has already been created in map
//                    if(effectlist.containsKey(effect.location))
//                    {
//                        effectlist.get(effect.location).add(effect);
//                    }
//                    // add new location to map
//                    else
//                    {
//                        effectlist.put(effect.location,new ArrayList<Free>());
//                        effectlist.get(effect.location).add(effect);
//                    }
//
//                }
//            }
//             now we have the current state and the 'ghost' state of the level
//
//             we now check for conflicts in the states
//
//             First we match preconditions and effects, adding conflicts to a conflict list - everything concerns the isFree() literal
//             these will be flagged for replanning
//            int counter;
//            for(Map.Entry<Point,ArrayList<Free>> entry : effectlist.entrySet()){
//                // check add and delete lists against each other
//                // add conflict with affiliated agents all linked to the conflict
//                // conflict will be solved by replanning after other actions have been performed.
//                agentIDs = new ArrayList<Integer>();
//                counter = 0;
//                for(Free effect : entry.getValue()){
//                    if(!effect.truthvalue)
//                    {
//                        counter+=1;
//
//                        //add list of effects containing the agent IDs
//                        agentIDs.add(effect.agentID);
//                    }
//                    else
//                    {
//                        counter-=1;
//                    }
//                }
//
//                // if counter is greater than zero then a conflict will exist (more agents accessing field than leaving)
//                if(counter>1)
//                {
//                    // create conflict
//                    // add affiliated agents
//                    conflictList.add(new Conflict(entry.getKey(),agentIDs));
//
//                    resolvedGhostFields.put(entry.getKey(),false);
//                }
//                else{
//                    resolvedGhostFields.put(entry.getKey(),true);
//                }
//            }
//
//            // Agents not flagged will go through a last precondition check in order to check if any stationary boxes are in the way
//            for(Agent agent : level.getAgents()){
//
//                TODO: this if-loop will not work if an agent is moving out of the field another agent is trying to move into
//                TODO: since preconditions will fail but resolvedGhostFields will be true. A splitting of checks is needed
//
//                if((agent.getCurrentAction().preconditions() && resolvedGhostFields.getOrDefault(agent.getCurrentAction().getTargetLocation(), true)) ||
//                        (resolvedGhostFields.getOrDefault(agent.getCurrentAction().getTargetLocation(), false)))
//                {
//                    simulate next moves? or simply perform them
//                    if no next moves exist, check for goal & create next plan
//                    TODO: executing actions may need ordering (otherwise execution will fail), maybe make queue list for every action that fails and keep re-attempting?
//                    only if the agent's task is not completed. we only checked in the planning phase
//                    if(!agent.currentTask.isTaskCompleted()){
//                        agent.executeCurrentAction();
//                    }
//                }
//                else
//                {
//                    //check if agent is noted in conflict list, otherwise add as conflict for replanning
//                    //agent.getNextAction().getConflicts();
//                    //add conflict
//                    conflictList.add(new Conflict(agent.getCurrentAction().getTargetLocation(), agent.id));
//                    // find conflicting objects/agents
//                    System.err.println("Conflict found");
//                    System.err.println("Preconditions:"+agent.getCurrentAction().preconditions());
//                    System.err.println("Resolvedfields: " + resolvedGhostFields.getOrDefault(agent.getCurrentAction().getTargetLocation(), true));
//
//                    replan (online replanning)
//                    agent.replanTask();
//                    if(agent.getNextAction().preconditions()){
//                        agent.executeCurrentAction();
//                    }
//                }
//            }

//            TODO: resolve conflicts from conflict list -> replan w.r.t. multiple agents
//            TODO: is feasible due to assumption of conflicts being scarce/few.
//            TODO: no. of agents is upper bounded by 4 since there are only 4 directions of movement.
//            for(Conflict conflict: conflictList){
//                if(conflict.getSingleAgentConflict())
//                {
//                    for(Agent agent : level.getAgents()){
//                        if(agent.id == conflict.getAgentIDs().get(0))
//                        {
//                            agent.planning();
//                            break;
//                        }
//                    }
//
//                }
//                else
//                {
//                    //TODO: multi-agent planning has to be done by and external class handing out individual plans to each agent, if we want this
//                }
//
//            }

//            TODO: get help, move boxes out of the way
//            TODO: think about future online planning
//            future planning, avoiding conflicts
//            End of Ideas section
//            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

            for(Agent agent : level.getAgents()){ //send action

                if (!agent.currentTask.isTaskCompleted()){
                    if (agent.getFirstAction() == null)
                    {
                        agent.planning();
                        strJoiner.add(agent.getCurrentAction().toString());
                    }
                    else
                    {
                        strJoiner.add(agent.getCurrentAction().toString());
                    }
                } else {
                    agent.planning();
                    strJoiner.add(agent.getCurrentAction().toString());
                }
            }

            act = strJoiner.toString();
            System.out.println( act );
            String response = serverMessages.readLine();
            if ( response.contains( "false" ) ) {
                String line = response.replaceAll("\\[", "").replaceAll("\\]", "");
                String[] colonSplit = line.split(",");
                int the_agent = 0;
                for(Agent agent : level.getAgents())
                {
                    if(colonSplit[the_agent].equals("false"))
                    {
                        agent.tasks.add(0, agent.currentTask);
                        agent.currentTask = null;
                        agent.planning();
                    }
                    the_agent++;
                }
            }
            else //every response is true
            {
                for(Agent agent : level.getAgents()){
                    agent.executeCurrentAction();
                }
            }
        }
    }
}
