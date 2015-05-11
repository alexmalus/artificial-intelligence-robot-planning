package client.ateam.Level;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lasse on 5/10/15.
 */
public class ArrayLevel2 implements ILevel {
    private static int height;

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


    public static int getRowFromIndex(int index) {return index / Level.width;}

    public static int getColumnFromIndex(int index) {
        return index % Level.width;
    }

    //BFS
    public int shortestDistanceOnMap(int from) {
        if (from == goal)
            return 0;

        int dist = distancesToGoal[from];
        while (dist == 0 && frontSet.size() > 0) {
            dist = distancesToGoal[from];
				/*if (dist != 0)
					break;*/

            int next = frontSet.poll();
            int thisDist = distancesToGoal[next];

            List<Integer> neighbors = Arrays.asList(
                    Level.getPosFromPosInDirection(next, ActionDirection.EAST),
                    Level.getPosFromPosInDirection(next, ActionDirection.WEST),
                    Level.getPosFromPosInDirection(next, ActionDirection.NORTH),
                    Level.getPosFromPosInDirection(next, ActionDirection.SOUTH));

            for (int i : neighbors) {
                if (i > 0 && !Level.isWall(realMap[i]) && distancesToGoal[i] == 0 && i != goal) {
                    frontSet.add(i);
                    distancesToGoal[i] = thisDist+1;
                }
            }
        }

        if (dist == 0)
            System.err.print("");

        return dist != 0 ? dist : Integer.MAX_VALUE;
    }
}
}
