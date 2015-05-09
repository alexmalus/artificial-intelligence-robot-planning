package client.ateam.Pathfinder;

import java.util.TreeSet;

/**
 * This abstract class contains the methods needed by the A* implementation.
 *
 *
 * @param <A> Action
 * @param <S> State
 */
public abstract class Problem<A, S extends Comparable<S>> {

    public Node<A,S> init;

    /**
     * Test if a given node contains a goal state.
     * @param n Matching node.
     * @return True if the state in n is a goal state, false otherwise.
     */
    public abstract boolean isGoal(Node<A,S> n);

    /**
     * Calculate all the neighbors/successors of a given node, i.e. expand the
     * node.
     * @param n The node to expand.
     * @return All neighbor-nodes of n.
     */
    public abstract TreeSet<Node<A,S>> expand(Node<A,S> n);

    /**
     * Calculate the cost of traveling from one node to another. It is assumed
     * that the two given nodes are actually adjacent.
     * @param n Traveling from.
     * @param suc Traveling to.
     * @return Cost of traveling from n to suc.
     */
    public abstract double cost(Node<A,S> n, Node<A,S> suc);

    /**
     * Heuristic value of traveling from the state in a given node, to a goal
     * state. This may or may not be the same value as the cost() method.
     * @param n Traveling from.
     * @return Value of traveling from n to a goal state.
     */
    public abstract double heuristic(Node<A,S> n);
}
