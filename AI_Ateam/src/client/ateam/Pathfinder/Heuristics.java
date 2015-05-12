package client.ateam.Pathfinder;

import client.ateam.Level.ArrayLevel;
import client.ateam.Task;
import client.ateam.projectEnum.Direction;

public class Heuristics {
    private int curPlayerLocation;
    private int targetPlayerLocation;
    private int targetBoxLocation;
    private int curBoxLocation;
    private int goalLocation;
    protected Task parentTask;

    public static double manhattanDistance(int i, int j) {
        int dx = Math.abs(ArrayLevel.getRowFromIndex(i) - ArrayLevel.getRowFromIndex(j));
        int dy = Math.abs(ArrayLevel.getColumnFromIndex(i) - ArrayLevel.getColumnFromIndex(j));
        return dx + dy;
    }

    public double heuristic(Node<Action,State> n) {
        double h = 0;
        switch (this.parentTask.type) {
            case GOAL:
                h += Heuristics.manhattanDistance(n.state.boxPosition, targetBoxLocation);
                       /* + shortestDistanceOnMap(n.state.boxPosition,parentTask.targetBoxPosition)
                        + shortestDistanceOnMap(n.state.playerLocation, n.state.boxPosition) - 1; */
                break;
            case BOX:
                System.err.println("Cannot determine Heuristic for BOX case");
                break;
            case AGENT:
                h += Heuristics.manhattanDistance(n.state.playerLocation, targetPlayerLocation);
                break;
            case AGENTAPPROX:
                int pl = n.state.playerLocation;
                int gl = parentTask.targetAgentPosition;
                //h += manhattanDistance(pl,gl) - 1;
                h += Heuristics.shortestDistanceOnMap(pl, gl) - 1;
                break;
            default:
                break;
        }

        return 1 * h /*+ n.state.boxesMovedFromGoal*/;
    }

    private static class BFS_Distance {
        private int[] distancesToGoal;
        private LinkedList<Integer> frontSet = new LinkedList<Integer>();
        private int goal;

        public BFS_Distance(int goal) {
            this.goal = goal;
            distancesToGoal = new int[realMap.length];
            frontSet.add(goal);
        }

        public int shortestDistanceOnMap(int from) {
            if (from == goal)
                return 0;

            int dist = distancesToGoal[from];
            while (dist == 0 && frontSet.size() > 0) {
                dist = distancesToGoal[from];

                int next = frontSet.poll();
                int thisDist = distancesToGoal[next];

                List<Integer> neighbors = Arrays.asList(
                        ArrayLevel.getPosFromPosInDirection(next, Direction.EAST),
                        ArrayLevel.getPosFromPosInDirection(next, Direction.WEST),
                        ArrayLevel.getPosFromPosInDirection(next, Direction.NORTH),
                        ArrayLevel.getPosFromPosInDirection(next, Direction.SOUTH));

                for (int i : neighbors) {
                    if (i > 0 && !ArrayLevel.isWall(realMap[i]) && distancesToGoal[i] == 0 && i != goal) {
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

    private static TreeMap<Integer,BFS_Distance> distances = new TreeMap<Integer,BFS_Distance>();

    public static int shortestDistanceOnMap(int from, int to) {
        if (from == to)
            return 0;

        BFS_Distance dist = distances.get(to);
        if (dist == null) {
            dist = new BFS_Distance(to);
            distances.put(to, dist);
        }

        return dist.shortestDistanceOnMap(from);
    }

    public static void resetShortestDistances() {
        distances = new TreeMap<Integer,BFS_Distance>();
    }

    // shortest distance list contains the positions that agents and boxes pass to the goal
    // these positions had better not be used as contingency positions
    public static ArrayList<Integer> shortestPathList(int from, int to) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(from);

        if (from == to)
            return path;

        int dist = Heuristics.shortestDistanceOnMap(from, to);

        List<Integer> neighbors = Arrays.asList(
                ArrayLevel.getPosFromPosInDirection(from, Direction.EAST),
                ArrayLevel.getPosFromPosInDirection(from, Direction.WEST),
                ArrayLevel.getPosFromPosInDirection(from, Direction.NORTH),
                ArrayLevel.getPosFromPosInDirection(from, Direction.SOUTH));

        for (Integer i : neighbors) {
            if (Heuristics.shortestDistanceOnMap(i, to) == dist-1) {
                path.addAll(shortestPathList(i, to));
                break;
            }
        }

        return path;
    }
}
