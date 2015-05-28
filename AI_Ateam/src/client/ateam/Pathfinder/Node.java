package client.ateam.Pathfinder;

//TODO:commented out section

import client.ateam.Level.Cell;

public class Node {
        private Cell cell = null;
        public Node parent;

        public int h;
        public int g;
        public int f;

        public Node(Cell cell)
        {
            this.cell = cell;
        }

        // Sets the parent Cell
        public void setParent(Node parent) {
            this.parent = parent;
        }

        public void setCosts(int g, int h)
        {
            // Movement cost from parent Cell to this Cell
            this.g = g;

            // Estimated cost from this Cell to the goal Cell
            this.h = h;

            // F = G() + H() - Totalcost for this Cell
            this.f = g + h;
        }

        public int totalCost() {
            return f;
        }

        public int movementCost() {
            return g;
        }

        public int estimatedCost() {
            return h;
        }

        public Cell getCell() {
            return cell;
        }

        public Node getParent() {
            return parent;
        }

        @Override
        public String toString(){
//            return "Node with state: " state + "which is reached by action: " + action + " having the parent: " + parent
//                    + "with a cost of reaching: " + g + "has h with value: " + h + "complete value of: " + f;
            return "I am a cute string";
        }
}