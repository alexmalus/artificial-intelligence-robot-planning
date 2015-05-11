package client.ateam.Pathfinder;

import client.ateam.Level.ArrayLevel2;
import client.ateam.Task;

/**
 * Created by joh on 21/04/15.
 */

public class Heuristics {
    private int curPlayerLocation;
    private int targetPlayerLocation;
    private int targetBoxLocation;
    private int curBoxLocation;
    private int goalLocation;
    protected Task parentTask;

    public static double manhattanDistance(int i, int j) {
        int dx = Math.abs(ArrayLevel2.getRowFromIndex(i) - ArrayLevel2.getRowFromIndex(j));
        int dy = Math.abs(ArrayLevel2.getColumnFromIndex(i) - ArrayLevel2.getColumnFromIndex(j));
        return dx + dy;
    }

    public double heuristic(Node<Action,State> n) {
        double h = 0;
        switch (this.parentTask.type) {
            case GOAL:
                h += manhattanDistance(n.state.boxPosition, targetBoxLocation);
                       /* + ArrayLevel2.shortestDistanceOnMap(n.state.boxPosition,parentTask.targetBoxPosition)
                        + ArrayLevel2.shortestDistanceOnMap(n.state.playerLocation, n.state.boxPosition) - 1; */
                break;
            case BOX:
                System.err.println("Cannot determine Heuristic for BOX case");
                break;
            case AGENT:
                h += manhattanDistance(n.state.playerLocation, targetPlayerLocation);
                break;
            case AGENTAPPROX:
                int pl = n.state.playerLocation;
                int gl = parentTask.targetAgentPosition;
                //h += manhattanDistance(pl,gl) - 1;
                h += ArrayLevel2.shortestDistanceOnMap(pl, gl) - 1;
                break;
            default:
                break;
        }

        return 1 * h /*+ n.state.boxesMovedFromGoal*/;
    }
}
