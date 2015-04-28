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
import client.ateam.Level.BitBoardLevel;
import client.ateam.Level.ILevel;
import client.ateam.LvlReader.FileLvlReader;
import client.ateam.LvlReader.ILvlReader;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    protected ArrayLevel arraylevel = null;
    public int[] realMap;

    public static void main(String[] args) throws Exception {

     Main main = new Main();
     main.run();

    }

    public void run() throws IOException {
        // write your code here

        //arg parser?
        ILvlReader reader = new FileLvlReader();
        TaskDistributor tasker = new TaskDistributor(); // needs interface?
        //load level

        String strLevel = reader.readLevel();

        // should agents, colors, goals, boxes be read inside the level class or outside ?

        // create level format, or make level singleton object?
        // agents+colors, boxes+colors, goals
        //ILevel level = new BitBoardLevel(strLevel);
        this.arraylevel = ArrayLevel.getSingletonObject();
        this.realMap = this.arraylevel.loadFromString(strLevel);
        //task distribution
        tasker.distributeTasks(level.getAgents,level.getBoxes,level.getGoals,level.getColors);
        //planning for each individual agent (linked lists)

        //pathfinding

        while (true) { // all this is possibly a jason area (along with planning) excluding pathfinding
            // find next moves

            System.err.println(strLevel.get(1));
            //create joint action (action merging)

            //check for conflicts ( use ILevel methods for literals/atoms etc )

            //resolve conflicts ( needs thinking ) + ActionHelper

            //future planning, avoiding conflicts

            //send action
            return;
        }

    }


}
