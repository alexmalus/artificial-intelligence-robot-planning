package client.ateam.Pathfinder;

/**
 * Created by joh on 21/04/15.
 */

/*
*
* */

public class Heuristics {

    public static double manhattanDistance(int i, int j) {
        int dx = Math.abs(Level.getRowFromIndex(i) - Level.getRowFromIndex(j));
        int dy = Math.abs(Level.getColumnFromIndex(i) - Level.getColumnFromIndex(j));
        return dx + dy;
    }

    public double heuristic(Node<Action,State> n) {
        double h = 0;
        switch (this.parentTask.type) {
            case GOAL:
                h += /*AstarProblem.manhattanDistance(n.state.boxPosition, targetBoxLocation)
			+*/ Level.shortestDistanceOnMap(n.state.boxPosition,parentTask.targetBoxPosition)
                        + Level.shortestDistanceOnMap(n.state.playerLocation, n.state.boxPosition) - 1;
                break;
            case BOX:
                System.err.println("UNKNOWN HEURISTIC FOR BOX-TYPE TASK");
                break;
            case AGENT:
                h += AstarProblem.manhattanDistance(n.state.playerLocation, targetPlayerLocation);
                break;
            case AGENTAPPROX:
                int pl = n.state.playerLocation;
                int gl = parentTask.targetAgentPosition;
                //h += AstarProblem.manhattanDistance(pl,gl) - 1;
                h += Level.shortestDistanceOnMap(pl, gl) - 1;
                break;
            default:
                break;
        }

        return 1 * h /*+ n.state.boxesMovedFromGoal*/;
    }
}
