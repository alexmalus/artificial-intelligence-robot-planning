package client.ateam.Level;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lasse on 5/10/15.
 */
public class ArrayLevel2 implements ILevel {

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

    @Override
    public int[] loadFromString(String s) {

        Scanner scanner = new Scanner(s);
        //for()

        return new int[0];
    }

    @Override
    public ArrayList<Integer> getAgents() {
        return null;
    }

    @Override
    public ArrayList<Integer> getBoxes() {
        return null;
    }

    @Override
    public ArrayList<Integer> getGoals() {
        return null;
    }
}

