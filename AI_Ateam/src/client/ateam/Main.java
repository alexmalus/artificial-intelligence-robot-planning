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
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.conflictHandler.Conflict;
import client.ateam.projectEnum.TaskType;

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
        TaskDistributor tasker = new TaskDistributor(); // needs interface?

        // agents+colors, boxes+colors, goals
        this.level = ArrayLevel.getSingleton();
        this.level.ReadMap();

        //TODO: some sort of ordering in goals, doing this at replanning may be hard, chapter 12 in the book ?
        //serialize subgoals (we probably cannot do POP)

        //task distribution
//        System.err.println("BoxArr size: " + level.getBoxes().size());
        tasker.distributeTasks(level.getAgents(),level.getBoxes(),level.getGoals());
        //tasks are now located on each agent

        //planning for each individual agent (linked lists)
        //pathfinding
        for(Agent agent: level.getAgents()){
            //plan the initial tasks of each agent
            agent.planning();
        }
        System.err.println("After converting path to actions, let's see how 0 should move: " + level.getAgents().get(0).actionList);

        StringJoiner strJoiner;
        String act;
        while (true) { // all this is possibly a jason area (along with planning) excluding pathfinding
            System.err.println(" ");
            System.err.println("Next iteration in endless while loop(main)");
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
            ArrayList<Free> addEffects = new ArrayList<Free>();
            ArrayList<Free> deleteEffects = new ArrayList<Free>();
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

//                addEffects = agent.getNextAction().getAddEffects();
//                deleteEffects = agent.getNextAction().getDeleteEffects();


                /*for(Free addEffect : addEffects){
                    //add effect to key value set
                }
                for(Free deleteEffect : deleteEffects){
                    //add effect to key value set
                }*/
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
            }
            // now we have the current state and the 'ghost' state of the level

            // we now check for conflicts in the states

            // First we match preconditions and effects, adding conflicts to a conflict list - everything concerns the isFree() literal
            // these will be flagged for replanning
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
//                /*TODO: this if-loop will not work if an agent is moving out of the field another agent is trying to move into
//                  TODO: since preconditions will fail but resolvedGhostFields will be true. A splitting of checks is needed
//                  */
//                if((agent.getCurrentAction().preconditions() && resolvedGhostFields.getOrDefault(agent.getCurrentAction().getTargetLocation(), true)) ||
//                        (resolvedGhostFields.getOrDefault(agent.getCurrentAction().getTargetLocation(), false)))
//                {
//                    //simulate next moves? or simply perform them
//                    //if no next moves exist, check for goal & create next plan
//                    //TODO: executing actions may need ordering (otherwise execution will fail), maybe make queue list for every action that fails and keep re-attempting?
//                    //only if the agent's task is not completed. we only checked in the planning phase
////                    if(!agent.currentTask.isTaskCompleted()){
////                        agent.executeCurrentAction();
////                    }
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
//                    //replan (online replanning)
//                    //agent.replanTask();
//                    //if(agent.getNextAction().preconditions()){
//                    //    agent.executeCurrentAction();
//                    //}
//                }
//            }

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

//            for(Agent agent : level.getAgents())
//            {
//                System.err.println("Box's id of the current task: " + agent.currentTask.box.getId());
//                int xx = 0;
//                for(Task task : agent.tasks)
//                {
//                    System.err.println("Box's id of task " + xx + " is: " + agent.tasks.get(xx).box.getId());
//                    xx++;
//                }
//            }

            //send action
            for(Agent agent : level.getAgents()){
//                System.err.println("Reached Str Joiner step, currentAction for 0 is: " + agent.getCurrentAction().toString());
//                if(agent.currentTask == null && agent.tasks.size() >0)
//                {
//                    agent.currentTask = agent.tasks.remove(0);
//                }
//                else
//                {
//                    agent.currentTask = new Task(agent, new Box(), new Goal(), TaskType.Idle);
//                    agent.setCurrentAction_ToNoOp();
//                }

                System.err.println("Current task before seeing if it's completed or not: " + agent.currentTask.toString());
//                System.err.println("Box's id of the current task: " + agent.currentTask.box.getId());
                if (!agent.currentTask.isTaskCompleted()){
                    System.err.println("Current Task is not completed");
                    System.err.println("action list size: " + agent.actionList.size());
                    if (agent.getFirstAction() == null)
                    {
                        agent.planning();
                        strJoiner.add(agent.getCurrentAction().toString());
                    }
                    else
                    {
                        System.err.println("Current action: " + agent.getCurrentAction().toString());
                        strJoiner.add(agent.getCurrentAction().toString());
                    }
                } else {
                    System.err.println("Current Task is completed");
                    agent.planning();
                    strJoiner.add(agent.getCurrentAction().toString());
                }
            }

            act = strJoiner.toString();
            System.err.println("This is strjoiner's element/s:" + act);
//            System.err.println("Agent 0' tasks are: " + "size : " + level.getAgents().get(0).tasks.size() + " " + level.getAgents().get(0).tasks.toString());

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
                        System.err.println("Server responded with false for agent: " + agent.id);
                        System.err.println("retry the agent's plan");
                        agent.tasks.add(0, agent.currentTask);
                        agent.currentTask = null;
                        agent.planning();
                    }
                    the_agent++;
                }
            }
            else //every response is true
            {
                System.err.println("Server responded with true for all agents");
                for(Agent agent : level.getAgents()){
                    // execute actions on local level, if empty do next plan
                    agent.executeCurrentAction();
                    System.err.println("New position for agent: " + agent.row + ", " + agent.column);
                }
            }
        }
    }
}
