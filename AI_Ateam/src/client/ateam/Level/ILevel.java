package client.ateam.Level;

import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lasse on 24-04-2015.
 */
public interface ILevel {

    int getAgentID();
    /*

     */

    boolean isNeighbor(int curRow, int curCol,int neighborRow, int neighborCol);
    boolean isBoxAt(int row, int col);
    boolean isAgentAt(int row, int col);
    boolean isFree(int row, int col);
    boolean isGoalCompleted();
    public boolean getBoxLetter(int row, int col);
    boolean getBoxColor();
    boolean getGoalLetter();
    public void LoadFromString(BufferedReader serverMessages) throws Exception;
    public ArrayList<Agent> getAgents();
    public ArrayList<Box> getBoxes();
    public ArrayList<Goal> getGoals();
    public void setFileLength(Scanner input);


}
