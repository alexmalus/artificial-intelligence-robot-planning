package client.ateam.Pathfinder;

import java.util.List;

public class Solution<A, S extends Comparable<S>> {
    public S goalState;
    double cost;
    List<Node<A,S>> path;

    /**
     * Constructor.
     * @param path Path that solves the problem.
     * @param cost Cost of solving the problem using the given path.
     */
    public Solution(List<Node<A,S>> path, double cost, S s) {
        this.path = path;
        this.cost = cost;
        goalState = s;
    }

    public List<Node<A, S>> getPath() {
        return path;
    }
}
