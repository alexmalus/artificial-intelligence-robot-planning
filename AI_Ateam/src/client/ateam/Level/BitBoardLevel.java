package client.ateam.Level;

import java.util.ArrayList;

/**
 * Created by Lasse on 24-04-2015.
 */
public class BitBoardLevel implements ILevel {

    public BitBoardLevel(ArrayList<String> strLevel){

    }

    @Override
    public int getAgentID() {
        return 0;
    }


    @Override
    public boolean isNeighbor() {
        return false;
    }

    @Override
    public boolean isBoxAt() {
        return false;
    }

    @Override
    public boolean isAgentAt() {
        return false;
    }

    @Override
    public boolean isFree() {
        return false;
    }

    @Override
    public boolean isGoalCompleted() {
        return false;
    }

    @Override
    public boolean getBoxLetter() {
        return false;
    }

    @Override
    public boolean getBoxColor() {
        return false;
    }

    @Override
    public boolean getGoalLetter() {
        return false;
    }
}
