package client.ateam.Level.Models;

import client.ateam.Task;

import java.util.List;

/**
 * Created by Lasse on 24-04-2015.
 */
public class Agent {

    public int id;
    public int color;
    public int row;
    public int column;
    //TODO: position connection with levels
    //private int pos
    public List<Task> tasks;

    public Agent(int id, int color, int row, int column){
        this.color = color;
       // this.position = Level.getIndexFromColoumnAndRow(column,row);
        this.id = id;
        this.row = row;
        this.column = column;

    }

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
