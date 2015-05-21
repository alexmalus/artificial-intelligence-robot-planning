package client.ateam.Level.Models;

import client.ateam.Level.Action;
import client.ateam.Task;
import client.ateam.projectEnum.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lasse on 24-04-2015.
 */
public class Agent {

    public int id;
    public Color color;
    public int row;
    public int column;
    //TODO: position connection with levels
    //private int pos
    public List<Task> tasks = new ArrayList<Task>();
    public Task currentTask;
    private Action currentAction;
    public List<Action> actionList = new ArrayList<Action>();

    public Agent(int id, Color color, int row, int column){
        this.color = color;
       // this.position = Level.getIndexFromColoumnAndRow(column,row);
        this.id = id;
        this.row = row;
        this.column = column;

    }

    public Action getNextAction(){
        currentAction = actionList.remove(0);
        return currentAction;
    }

    public Action getCurrentAction(){
        return currentAction;
    }

    public void executeCurrentAction() {

        //do execute

        //check for goal
        if(actionList.isEmpty())
        {
            if(currentTask.isComplete()){
                //select next task
                currentTask = tasks.remove(0);
            }
            else
            {
                //replan
            }


        }
    }

    public



//    /**
//     * Get the agent id from the the agent on the provided field
//     * @param field
//     * @return An integer with the agent id (not ascii or utf-8)
//     */
//    public static int getAgentId(int field) {
//        if (Level.isAgent(field)) {
//            return (int) (field >> 13) & 0xF;
//        }
//        else {
//            System.err.println("getAgentId: provided field do not contain an agent!");
//            return -1;
//        }
//    }



}
