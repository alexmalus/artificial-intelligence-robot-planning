package client.ateam.Pathfinder.ExplorationTree;

import client.ateam.Level.State;

/**
 * Created by Lasse on 25-04-2015.
 */
    /*
    the tree for searching through the level
     */

    public class Node<A, S extends Comparable<S>> implements Comparable<Node<A,S>> {
        public double h;
        public double g;
        public double f;
        public Node<A, S> parent;
        public S state;
        public A action;

        /**
         * Constructor.
         * @param a Action to reach the given state
         * @param s State
         * @param parent Parent node
         */
        public Node (A a, S s, Node<A,S> parent){
            this.parent=parent;
            this.action=a;
            this.state=s;
        }

        /**
         * Override
         */
        public int compareTo(Node<A, S> other) {
            if (this.f != other.f) {
                return Double.compare(this.f,other.f);
            }

            else if(this.g!=other.g){
                return Double.compare(this.g, other.g);
            }
            else if(this.h != other.h)
                return Double.compare(this.h, other.h);

            return this.state.compareTo(other.state);
        }


        /**
         * Override
         */
        @Override
        public String toString(){
            return "State " + state + " Reached by action "+ action + " parent " +
                    parent + "cost of reaching "+ g + " h-value" + h + " complete" +
                    " value " + f;
        }

}

