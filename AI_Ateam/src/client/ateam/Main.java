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

import client.ateam.Level.Action;
import client.ateam.Level.ArrayLevel;
import client.ateam.Level.ILevel;
import client.ateam.Level.Models.Agent;
import client.ateam.LvlReader.FileLvlReader;
import client.ateam.LvlReader.ILvlReader;
import client.ateam.conflictHandler.Conflict;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
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
        ILvlReader reader = new FileLvlReader(serverMessages);
        TaskDistributor tasker = new TaskDistributor(); // needs interface?

        //load level
//        String strLevel = reader.readLevel();

        // should agents, colors, goals, boxes be read inside the level class or outside ?

        // create level format, or make level singleton object?
        // agents+colors, boxes+colors, goals
        //ILevel level = new BitBoardLevel(strLevel);
        //this.level = ArrayLevel.getSingletonObject();
        this.level = ArrayLevel.getSingleton();
        this.level = ArrayLevel.ReadMap(serverMessages);

        //TODO: some sort of ordering in goals, doing this at replanning may be hard, chapter 12 in the book ?
        //serialize subgoals (we probably cannot do POP)

        //task distribution
        //tasker.distributeTasks(level.getAgents(),level.getBoxes(),level.getGoals());
        //tasks are now located on each agent


        //planning for each individual agent (linked lists)

        //pathfinding
        //TODO: planning and pathfinding for each agent
        for(Agent agent: level.getAgents()){
            //plan the initial tasks of each agent

        }

        StringJoiner strJoiner = new StringJoiner(", ","[","]");
        String act;
        while (true) { // all this is possibly a jason area (along with planning) excluding pathfinding
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
            //ArrayList<Literal> addEffects = new ArrayList<Literal>();
            //ArrayList<Literal> deleteEffects = new ArrayList<Literal>();
            ArrayList<Literal> effects;// = new ArrayList<Literal>();
            ArrayList<Integer> agentIDs;
            ArrayList<Conflict> conflictList = new ArrayList<Conflict>();
            Map<Point,ArrayList<Literal>> effectlist = new HashMap<Point,ArrayList<Literal>>();
            Map<Point,Boolean> resolvedGhostFields = new HashMap<Point,Boolean>();
            Action action;

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
                action = agent.getNextAction();
                effects = action.getEffects();
                /*for(Literal addEffect : addEffects){
                    //add effect to key value set
                }
                for(Literal deleteEffect : deleteEffects){
                    //add effect to key value set
                }*/
                for(Literal effect : effects){
                    //add effect to key value set

                    //check if location has already been created in map
                    if(effectlist.containsKey(effect.location))
                    {
                        effectlist.get(effect.location).add(effect);
                    }
                    // add new location to map
                    else
                    {
                        effectlist.put(effect.location,new ArrayList<Literal>());
                        effectlist.get(effect.location).add(effect);
                    }

                }
            }
            // now we have the current state and the 'ghost' state of the level

            // we now check for conflicts in the states

            // First we match preconditions and effects, adding conflicts to a conflict list - everything concerns the isFree() literal
            // these will be flagged for replanning
            int counter;
            for(Map.Entry<Point,ArrayList<Literal>> entry : effectlist.entrySet()){
                // check add and delete lists against eachother
                // add conflict with affiliated agents all linked to the conflict
                // conflict will be solved by replanning after other actions have been performed.
                agentIDs = new ArrayList<Integer>();
                counter = 0;
                for(Literal effect : entry.getValue()){
                    if(effect.truthvalue)
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
                if(counter>0)
                {
                    // create conflict
                    // add affiliated agents
                    //TODO: run through agent IDs in order to create conflict object, which will be resolved later
                    // is somewhat done..
                    conflictList.add(new Conflict(entry.getKey(),agentIDs));

                    resolvedGhostFields.put(entry.getKey(),false);
                }
                else{
                    resolvedGhostFields.put(entry.getKey(),true);
                }
                // if
            }

            //TODO: (this seems done but needs to be rechecked)
            // Agents not flagged will go through a last precondition check in order to check if any stationary boxes are in the way
            for(Agent agent : level.getAgents()){
                if(agent.getNextAction().preconditions() && resolvedGhostFields.getOrDefault(agent.getNextAction().targetLocation, true))
                {
                    //simulate next moves? or simply perform them
                    //if no next moves exist, check for goal & create next plan
                    agent.executeCurrentAction();
                }
                else
                {
                    //check if agent is noted in conflict list, otherwise add as conflict for replanning
                    //agent.getNextAction().getConflicts();
                    //add conflict
                    conflictList.add(new Conflict(agent.getNextAction().targetLocation,agent.id));
                    // find conflicting objects/agents

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
            for(Conflict conflict: conflictList){

            }


            //TODO: get help, move boxes out of the way

            //System.err.println(strLevel.get(1));

            //TODO: think about future online planning
            //future planning, avoiding conflicts

            //send action

            for(Agent agent : level.getAgents()){
                strJoiner.add(agent.getCurrentAction().toString());
            }

            act = strJoiner.toString();
            System.out.println( act );
            String response = serverMessages.readLine();
            if ( response.contains( "false" ) ) {
                System.err.format( "Server responsed with %s to the inapplicable action: %s\n", response, act );
                //System.err.format( "%s was attempted in \n%s\n", act );

                //retry or something...
            }
            else{
                //for(Agent agent : level.getAgents()){
                    // execute actions on local level, if empty do next plan
                //}
            }

        }
    }
}
