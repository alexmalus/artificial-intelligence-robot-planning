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
import client.ateam.LvlReader.FileLvlReader;
import client.ateam.LvlReader.ILvlReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.StringJoiner;

public class Main {

    protected ILevel level = null;
    public int[] realMap;
    private BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws Exception {

     Main main = new Main();
     main.run();

    }

    public void run() throws IOException {
        // write your code here

        //arg parser?
        ILvlReader reader = new FileLvlReader(serverMessages);
        TaskDistributor tasker = new TaskDistributor(); // needs interface?
        //load level

        String strLevel = reader.readLevel();

        // should agents, colors, goals, boxes be read inside the level class or outside ?

        // create level format, or make level singleton object?
        // agents+colors, boxes+colors, goals
        //ILevel level = new BitBoardLevel(strLevel);
        //this.level = ArrayLevel.getSingletonObject();
        this.level = ArrayLevel.getSingleton();
        this.realMap = this.level.LoadFromString(strLevel);
        level.setFileLength(new Scanner(strLevel));


        //TODO: some sort of ordering in goals, doing this at replanning may be hard, chapter 12 in the book ?
        //serialize subgoals (we probably cannot do POP)


        //task distribution
        tasker.distributeTasks(level.getAgents(),level.getBoxes(),level.getGoals(),level.getColors());
        //tasks are now located on each agent



        //planning for each individual agent (linked lists)
        System.err.println("hej");

        //pathfinding


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

            for(Agent agent : level.getAgents()){
                if(agent.getNextAction().preconditions())
                {
                    //simulate next moves? or simply perform them
                    //if no next moves exist, check for goal & create next plan

                }
                else
                {
                    //add conflict

                    // find conflicting objects/agents

                    //replan (online replanning)
                }


            }
            //System.err.println(strLevel.get(1));

            //TODO: think about future online planning
            //future planning, avoiding conflicts



            //send action

            for(Agent agent : level.getAgents()){
                strJoiner.add(agent.getNextAction().toString());
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
                for(Agent agent : level.getAgents()){
                    // execute actions on local level, if empty do next plan
                }


            }



        }
    }
}
