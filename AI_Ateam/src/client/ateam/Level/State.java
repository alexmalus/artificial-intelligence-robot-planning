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
    public State() {
        // TODO Auto-generated constructor stub
    }

    //compare two states
    public int compareTo(State other) {
        int r = map.compareTo(other.map);
        return r;
    }

    //converter a.newX ved lejlighed
    public State applyAction(Action a){
        Map newMap = new Map(map.map, map);
        if (!Level.applyAction(newMap, a, playerLocation, true))
            return null;
        int nBoxLocation= a.getBoxLocation(playerLocation);
        int oldBoxLocation = a.getOldBoxLocation(playerLocation);
        int deltaBoxesMovedFromGoal = 0;

        if (nBoxLocation > 0 && oldBoxLocation > 0) {
            boolean boxOnGoalBefore = Level.boxIsOnItsGoal(map.get(oldBoxLocation));
            boolean boxOnGoalAfter = Level.boxIsOnItsGoal(newMap.get(nBoxLocation));

            if (boxOnGoalBefore && !boxOnGoalAfter)
                deltaBoxesMovedFromGoal = 1;
            else if (!boxOnGoalBefore && boxOnGoalAfter)
                deltaBoxesMovedFromGoal = -1;
        }

        if (nBoxLocation > 0 && boxPosition > 0) {
            int oldBox = map.get(boxPosition);
            int newBox = newMap.get(nBoxLocation);

            if (Level.isBox(oldBox) && Level.isBox(newBox) &&
                    Level.getBoxLetter(oldBox) != Level.getBoxLetter(newBox))
                nBoxLocation = this.boxPosition;
        }

        if(nBoxLocation==0)
            nBoxLocation=this.boxPosition;

        State newState = new State(newMap, a.newAgentPosition(playerLocation),nBoxLocation);
        newState.boxesMovedFromGoal = this.boxesMovedFromGoal + deltaBoxesMovedFromGoal;
        newState.time = this.time + 1;
        return newState;
    }

    public String toString(){
        int x = Level.getColumnFromIndex(playerLocation);
        int y = Level.getRowFromIndex(playerLocation);
        return "("+x+","+y+")";
    }

    public int hashCode() {
        return map.getHashValue();
    }

    public int getPlayerlocation(){
        return this.playerLocation;
    }
}
