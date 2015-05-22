package client.ateam.Pathfinder.ExplorationTree;

import client.ateam.Level.State;

    public class Node<A, S extends Comparable<S>> implements Comparable<Node<A,S>> {
        public double h;
        public double g;
        public double f;
        public Node<A, S> parent;
        public S state;
        public A action;

        public Node (A a, S s, Node<A,S> parent){
            this.parent=parent;
            this.action=a;
            this.state=s;
        }

        @Override
        public String toString(){
            return "Node with state: " state + "which is reached by action: " + action + " having the parent: " + parent
                    + "with a cost of reaching: " + g + "has h with value: " + h + "complete value of: " + f;
        }
}

