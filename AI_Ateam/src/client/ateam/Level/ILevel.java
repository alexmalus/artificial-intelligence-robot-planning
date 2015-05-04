package client.ateam.Level;

import java.util.ArrayList;

/**
 * Created by Lasse on 24-04-2015.
 */
public interface ILevel {

    int getAgentID();
    /*

     */

    boolean isNeighbor();
    boolean isBoxAt();
    boolean isAgentAt();
    boolean isFree();
    boolean isGoalCompleted();
    boolean getBoxLetter();
    boolean getBoxColor();
    boolean getGoalLetter();
    public int[] loadFromString(String s);
    public ArrayList<Integer> getAgents();
    public ArrayList<Integer> getBoxes();
    public ArrayList<Integer> getGoals();


}
