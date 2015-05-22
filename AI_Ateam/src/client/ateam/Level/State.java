package client.ateam.Level;

import java.util.ArrayList;

public class State implements Comparable<State> {
    public Map map;
    public int playerLocation,boxPosition;
    public int boxesMovedFromGoal = 0;
    public int time = 0;
    public ArrayList<Integer> otherAgentPositions = new ArrayList<Integer>();

    public State(Map map, int playerLocation, int boxPosition) {
        this.map=map;
        this.playerLocation=playerLocation;
        this.boxPosition=boxPosition;
    }
    public State() {}

    public String toString(){
        int x = ArrayLevel.getColumnFromIndex(playerLocation);
        int y = ArrayLevel.getRowFromIndex(playerLocation);
        return "("+x+","+y+")";
    }
}
