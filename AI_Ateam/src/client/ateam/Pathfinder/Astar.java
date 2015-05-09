package client.ateam.Pathfinder;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import aims.Level;

public class AStar {

    /**
     * This method takes a problem instance, and return a solution to the
     * specified problem.
     * @param <A> Action
     * @param <S> State
     * @Problem instance An instance of the problem, which defines the
     * goal-states and heuristics.
     * @return The shortest path (if the heuristic is admissible) to solve the
     * given problem. Returns null if it can't find a plan.
     */
    public static <A, S extends Comparable<S>> Solution<A,S>
    solve(Problem<A,S> p, int maximum) {

        //TreeSet<S> closedSet = new TreeSet<S>();
        HashMap<Integer,TreeSet<S>> closedSet = new HashMap<Integer,TreeSet<S>>();

        TreeSet<Node<A,S>> frontSet = new TreeSet<Node<A,S>>();
        frontSet.add(p.init);
        p.init.g = 0;
        p.init.h = p.heuristic(p.init);
        p.init.f = p.init.g + p.init.h;

        int closedConf = 0;
        while(!frontSet.isEmpty() && closedSet.size() < maximum) {
            Node<A,S> n = frontSet.pollFirst();

            TreeSet<S> c = closedSet.get(n.state.hashCode());
            if (c != null && c.contains(n.state)) {
                //if (closedSet.contains(n.state)) {
                ++closedConf;
                continue;
            }

            if (c == null) {
                c = new TreeSet<S>();
                closedSet.put(n.state.hashCode(), c);
            }
            c.add(n.state);
            //closedSet.add(n.state);

            if(p.isGoal(n)) {
                return new Solution<A,S>(AStar.track(p.init, n), n.f, n.state);
            }

            if (false && closedSet.size() % 5000 == 0 /*|| closedSet.size() < 50*/) {
                System.err.println("A* search-depth: " + closedSet.size() + " front-set: "
                        + frontSet.size() + ":" /*+ frontMap.size()*/ + " g: " + n.g + " h: " + n.h + " f: " + n.f +
                        " closed-conflicts: " + closedConf);
                System.err.println(n.parent.state + " -> [" + n.action + "] -> " + n.state);
            }

            for (Node<A,S> suc: p.expand(n)) {
                suc.g = n.g + p.cost(n, suc);
                suc.h = p.heuristic(suc);
                suc.f = suc.h + suc.g;

                TreeSet<S> c2 = closedSet.get(suc.state.hashCode());
                if (c2 == null || c2.contains(suc.state)) {
                    //if(!closedSet.contains(suc.state)) {
                    frontSet.add(suc);
                }
                else
                    ++closedConf;
            }
        }

        return null;
    }

    /**
     * This method creates a list of actions to solve a problem, when given the
     * initial and final nodes in the solution.
     * @param <A> Action
     * @param <S> State
     * @param init The initial state
     * @param n The goal state
     * @return A path to solve the goal, in List<> format
     */
    public static <A, S extends Comparable<S>> List<Node<A,S>>
    track(Node<A,S> init, Node<A,S> n) {
        List<Node<A,S>> solution = new Stack<Node<A,S>>();
        Node<A,S> parent = n;

        while (parent != init) {
            solution.add(0, parent);
            parent = parent.parent;
        }

        solution.add(0,init);
        return solution;
    }
}
