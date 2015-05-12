package client.ateam.Pathfinder.ExplorationTree;

import client.ateam.Level.State;
    //the tree for searching through the level

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


        @Override
        public String toString(){
            return "Node with state: " state + "which is reached by action: " + action + " having the parent: " + parent
                    + "with a cost of reaching: " + g + "has h with value: " + h + "complete value of: " + f;
        }

}

