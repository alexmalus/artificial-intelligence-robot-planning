package client.ateam.Level;

/**
 * Created by Lasse on 24-04-2015.
 */
public interface ILevel {

    int getAgentID();

    void createLevel(String lvlString);

    boolean isNeighbor();
    boolean isBoxAt();
    boolean isAgentAt();
    boolean isFree();
    boolean isGoalCompleted();
    boolean getBoxLetter();
    boolean getBoxColor();
    boolean getGoalLetter();

}
