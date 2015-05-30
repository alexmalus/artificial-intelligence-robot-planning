package client.ateam;

/*
*Contents and functionality of this class is to contain the client programs main method.
* As well as startup:
*
* System IO
* Reading level from server
* Initialize Agents
* TaskDistributor
* */


import client.ateam.Level.ArrayLevel;
import client.ateam.Level.ILevel;
import client.ateam.Level.Models.Agent;
import client.ateam.conflictHandler.Conflict;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    protected ILevel level = null;
    public int[] realMap;
    private BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws Exception {

     Main main = new Main();
     main.run();
    }

    public void run() throws Exception {
        //arg parser?
        TaskDistributor tasker = new TaskDistributor(); // needs interface?

        // agents+colors, boxes+colors, goals
        this.level = ArrayLevel.getSingleton();
        this.level.ReadMap();

        //TODO: some sort of ordering in goals, doing this at replanning may be hard, chapter 12 in the book ?
        //serialize subgoals (we probably cannot do POP)

        //task distribution
        tasker.distributeTasks(level.getAgents(),level.getBoxes(),level.getGoals());
        //tasks are now located on each agent

        //planning for each individual agent (linked lists)
        //pathfinding
        for(Agent agent: level.getAgents()){
            //plan the initial tasks of each agent
            agent.planning();
        }
//        System.err.println("After converting path to actions, let's see how 0 should move: " + level.getAgents().get(0).actionList);

        StringJoiner strJoiner;
        String act;
        while (true) { // all this is possibly a jason area (along with planning) excluding pathfinding
            strJoiner = new StringJoiner(", ","[","]");
            // find next moves

            //create joint action (action merging)

            //check for conflicts ( use ILevel methods for literals/atoms etc )

            //add list
            //delete list
            // current state

            //resolve conflicts ( needs thinking ) + ActionHelper

            //IDEA: run through all actions and gather add / delete lists into key-value maps (with affiliated task/agent)
            //then run through said key-value maps to check for conflicts and replan accordingly
            //check both preconditions from level and preconditions from key-value maps

            //TODO: alternative approach is just to keep an ordering of who gets to go first (simpler)
            //ArrayList<Free> addEffects = new ArrayList<Free>();
            //ArrayList<Free> deleteEffects = new ArrayList<Free>();
            ArrayList<Free> effects;// = new ArrayList<Free>();
            ArrayList<Integer> agentIDs;
            ArrayList<Conflict> conflictList = new ArrayList<Conflict>();
            Map<Point,ArrayList<Free>> effectlist = new HashMap<Point,ArrayList<Free>>();
            Map<Point,Boolean> resolvedGhostFields = new HashMap<Point,Boolean>();

            //accumulate effects of each agent
            for(Agent agent : level.getAgents()){

                // first we check simple preconditions
                // these are identified as isAgent, isBox, isNeighbor and they concern the validity of agent and boxlocations

                // this part concerns the conflict detection, this is concerned with the isFree literal
                // The isFree literal is the main source of conflicts and limitation of movement
                // An agent should never attempt to move into a wall due to action planning

                // boxes in the way may have to be checked

                //addEffects = agent.getNextAction().getAddEffects();
                //deleteEffects = agent.getNextAction().getDeleteEffects();


                /*for(Free addEffect : addEffects){
                    //add effect to key value set
                }
                for(Free deleteEffect : deleteEffects){
                    //add effect to key value set
                }*/
                for(Free effect : agent.getCurrentAction().getEffects()){
                    //add effect to key value set

                    //check if location has already been created in map
                    if(effectlist.containsKey(effect.location))
                    {
                        effectlist.get(effect.location).add(effect);
                    }
                    // add new location to map
                    else
                    {
                        effectlist.put(effect.location,new ArrayList<Free>());
                        effectlist.get(effect.location).add(effect);
                    }

                }
            }
            // now we have the current state and the 'ghost' state of the level

            // we now check for conflicts in the states

            // First we match preconditions and effects, adding conflicts to a conflict list - everything concerns the isFree() literal
            // these will be flagged for replanning
            int counter;
            //System.err.println("EntrySet size: "+effectlist.entrySet().size());
            for(Map.Entry<Point,ArrayList<Free>> entry : effectlist.entrySet()){
                // check add and delete lists against eachother
                // add conflict with affiliated agents all linked to the conflict
                // conflict will be solved by replanning after other actions have been performed.
                //System.err.println("Entry: "+ entry.getKey());
                //System.err.println("Value:" + entry.getValue().get(0).truthvalue);
                agentIDs = new ArrayList<Integer>();
                counter = 0;
                for(Free effect : entry.getValue()){
                    if(!effect.truthvalue)
                    {
                        counter+=1;

                        //add list of effects containing the agent IDs
                        agentIDs.add(effect.agentID);

                    }
                    else
                    {
                        counter-=1;
                    }
                }

                // if counter is greater than zero then a conflict will exist (more agents accessing field than leaving)
                if(counter>1)
                {
                    // create conflict
                    // add affiliated agents
                    conflictList.add(new Conflict(entry.getKey(),agentIDs));

                    resolvedGhostFields.put(entry.getKey(),false);
                }
                else{
                    resolvedGhostFields.put(entry.getKey(),true);
                }
                // if
            }

            // Agents not flagged will go through a last precondition check in order to check if any stationary boxes are in the way
            for(Agent agent : level.getAgents()){

                /*TODO: this if-loop will not work if an agent is moving out of the field another agent is trying to move into
                  TODO: since preconditions will fail but resolvedGhostFields will be true. A splitting of checks is needed
                  */
                if((agent.getCurrentAction().preconditions() && resolvedGhostFields.getOrDefault(agent.getCurrentAction().getTargetLocation(), false)))
                {
                    //simulate next moves? or simply perform them
                    //if no next moves exist, check for goal & create next plan
                    //TODO: executing actions may need ordering (otherwise execution will fail), maybe make queue list for every action that fails and keep re-attempting?
                    //only if the agent's task is not completed. we only checked in the planning phase
//                    if(!agent.currentTask.isTaskCompleted()){
//                        System.err.println("I execute this here inside the main's for");
//                        agent.executeCurrentAction();
//                    }
                }
                else
                {
                    //check if agent is noted in conflict list, otherwise add as conflict for replanning
                    //agent.getNextAction().getConflicts();
                    //add conflict
                    conflictList.add(new Conflict(agent.getCurrentAction().getTargetLocation(),agent.id));
                    // find conflicting objects/agents
                    System.err.println("Conflict found");
                    System.err.println("Preconditions:"+agent.getCurrentAction().preconditions());
                    System.err.println("Resolvedfields: "+ resolvedGhostFields.getOrDefault(agent.getCurrentAction().getTargetLocation(),true));
                    //replan (online replanning)
                    //agent.replanTask();
                    //if(agent.getNextAction().preconditions()){
                    //    agent.executeCurrentAction();
                    //}
                }
            }

            //TODO: resolve conflicts from conflict list -> replan w.r.t. multiple agents
            //TODO: is feasible due to assumption of conflicts being scarce/few.
            //TODO: no. of agents is upper bounded by 4 since there are only 4 directions of movement.
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


            //TODO: get help, move boxes out of the way

            //System.err.println(strLevel.get(1));

            //TODO: think about future online planning
            //future planning, avoiding conflicts

            //send action

            for(Agent agent : level.getAgents()){
//                System.err.println("Reached Str Joiner step, currentAction for 0 is: " + agent.getCurrentAction().toString());
                strJoiner.add(agent.getCurrentAction().toString());
//                System.err.println
            }

            act = strJoiner.toString();
//            strJoiner = null;
//            System.err.println("This is strjoiner's elements:" + act);
            System.err.println("Agent 0' tasks are: " + "size : " + level.getAgents().get(0).tasks.size() + " " + level.getAgents().get(0).tasks.toString());
            System.out.println( act );
            String response = serverMessages.readLine();
            if ( response.contains( "false" ) ) {
                System.err.println("Server responded with false");
//                System.err.format( "Server responded with %s to the inapplicable action: %s\n", response, act );
                //System.err.format( "%s was attempted in \n%s\n", act );

                //retry or something...
            }
            else if(response.contains("true"))
            {
                System.err.println("Server responded with true");
                for(Agent agent : level.getAgents()){
                    // execute actions on local level, if empty do next plan
                    agent.executeCurrentAction();
                }
            }
            else{
                System.err.println("something went wrong..");

            }

        }
    }
}
