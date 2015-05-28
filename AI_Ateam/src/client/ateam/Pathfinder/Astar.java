package client.ateam.Pathfinder;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.awt.*;

 import client.ateam.Level.ArrayLevel;
import client.ateam.Level.Cell;
import client.ateam.Level.Models.Agent;

public class Astar {
    private Agent owner;

    private int expanded = 0;
    private int limit = 0;

    private Node goalNode = null;
    private Node startNode = null;

    private Heap openList = null;
    private Heap closedList = null;

    private boolean needPath = false;
    private boolean isFinished = false;

    private ArrayList<Node> pathList = null;		// The list containing our path
    private HashMap<Cell, Node> nodeList = null;	// The list containing Cell costs

    public Astar(Agent owner) {
        this.owner = owner;
    }

    // Return the path
    public ArrayList<Node> getPath()
    {
        return pathList;
    }

    // Return the size of the path
    public int getPathSize() {
        return pathList.size();
    }

    // Return Node at location n on pathList
    public Node getNode(int n)
    {
        // Since we follow the list from back to front, the index is reversed
        // IE: if getSize() = 25 and n = 5, we are really getting Node 20 (not 5)
        return pathList.get(getPathSize() - n);
    }

    // Whether or not we have a path to move along
    public boolean pathExists()
    {
        if (pathList == null) return false;
        return ((pathList.size() > 0) ? true : false);
    }

    // Whether or not AStar is building a path
    public boolean buildingPath() {
        return needPath;
    }

    public boolean pathIsFinished() {
        return isFinished;
    }

    // Number of steps to take per loop
    public void setStepLimit(int steps) {
        limit = steps;
    }

    // Store the starting point of the path and add it to openList
    public void setStart(Cell start)
    {
        System.err.println("Here's the start node: " + start.toString());
        // Create a Node for this Cell
        this.startNode = new Node(start);

        // Add it to the nodeList
        nodeList.put(start, startNode);

        // Add it to the openList
        openList.add(startNode);
    }

    // Store the goal point of the path
    public void setGoal(Cell goal)
    {
        System.err.println("Here's the goal node: " + goal.toString());
        // Create a Node for this Cell
        this.goalNode = new Node(goal);

        // Add it to the nodeList
        nodeList.put(goal, goalNode);
    }

    // Re-calculates the path with new start and goal nodes
    public void newPath(Cell start, Cell goal)
    {
        // Initialize variables
        initialize();

        // Set our new starting point and goal point
        setStart(start);
        setGoal(goal);
    }

    // Calculating the best path based on the start and goal nodes given in the constructor.
    public void findPath()
    {
        // Make sure we have starting and ending points and that we don't already have a path
        if (!needPath || startNode == null || goalNode == null) return;

        int steps            		= 0;    // Used to count the number of steps taken per method call
        int movementCost      		= 0;    // Stores the calculated cost of the current node
        boolean needUpdate			= true; // Whether or not the neighbor node needs to be updated
        Node currentNode     		= null; // the node we are currently working on
        ArrayList<Node> neighbors 	= null; // currentNode's neighbors

        System.err.println("First print inside Astar; openlist size: " + openList.size());
        // Loop through all possible nodes and find the best path to the goal
        while (openList.size() > 0)
        {
            // Set our currentNode to the node with the lowest totalCost
            currentNode = openList.pop();
            System.err.println("Current Node is:  " + currentNode.getCell().toString());

            // Add currentNode to closedList (since we will be examining it)
            closedList.add(currentNode);

            // If we have found the goal, notify AStar that we no longer need a path
            if (currentNode == goalNode) foundGoal();
                // Otherwise, continue to search for next best move
            else
            {
                // Gather a list of neighbors to the currentNode
                neighbors = neighbors(currentNode);

                // Loop through neighbors
                for (Node neighbor : neighbors)
                {
                    // The estimated cost if we were to move through this neighbor node
                    movementCost = currentNode.movementCost() + estimate(currentNode, neighbor);

                    // If neighbor is on closedList...
                    if (openList.contains(neighbor))
                    {
                        // If this move is better, remove neighbor from openList for re-evaluation
                        if (movementCost < neighbor.movementCost())
                            openList.remove(neighbor);

                            // Otherwise, don't update it
                        else needUpdate = false;
                    }

                    // If neighbor is on openList...
                    else if (closedList.contains(neighbor))
                    {
                        // If this move is better, remove neighbor from closedList for re-evaluation
                        if (movementCost < neighbor.movementCost())
                            closedList.remove(neighbor);

                            // Otherwise, don't update it
                        else needUpdate = false;
                    }

                    // If this neighbor needs to be updated...
                    if (needUpdate)
                    {
                        // Set its parent to currentNode
                        neighbor.setParent(currentNode);

                        // Calculate new movementCost, estimatedCost and totalCost
                        neighbor.setCosts(movementCost, estimate(neighbor, goalNode));

                        // And add it to openList for future searching
                        openList.push(neighbor);
                    }

                    // Reset needUpdate
                    needUpdate = true;
                }

                // Increase steps taken on this loop
                steps++;
            }

            // If we are finished or have reached our limit for this loop, build best path to this point and exit
            if (!needPath || ((limit > 0) && (steps > 0) && (steps % limit == 0)))
            {
                // Set the new capacity of our pathList
                pathList.ensureCapacity(pathList.size() + steps);

                // Store our best path up to this point in our pathList
                pathList = buildPath(currentNode);

                // Break out of loop
                break;
            }
        }

        System.err.println(getPathSize());
        // Agent is unable to move to goal (path blocked)
        if (needPath && openList.size() == 0)
        {
            System.err.println("Agent #" + owner.id + ": can't move!");
        }

        // Path is complete
        if (!needPath)
        {
            cleanUp();
        }
    }

    /**
     *
     * Private Functions
     *
     **/

    // Returns a list of Nodes surrounding parentNode
    private ArrayList<Node> neighbors(Node parentNode)
    {
        int r, c;
        Node childNode;
        Cell childCell;
        Cell parentCell;
        ArrayList<Node> tempList = new ArrayList<Node>(8);

        // Cell reference for parentNode
        parentCell = parentNode.getCell();

        // Search the surrounding 8 nodes for possible places to go
        for (r = (parentCell.getR() - 1); r <= (parentCell.getR() + 1); r++)
        {
            for (c = (parentCell.getC() - 1); c <= (parentCell.getC() + 1); c++)
            {
                // Grab the Cell at location (r, c)
                childCell = ArrayLevel.getCell(r, c);

                // Make sure this Cell exists and is playable
                if ((childCell != null) && childCell.isPlayable())
                {
                    // Attempt to grab the Node for this Cell
                    childNode = nodeList.get(childCell);

                    // If this node is already on our nodeList...
                    if (childNode != null)
                    {
                        // if childNode is not parentNode, add it to our list
                        if (childNode != parentNode) tempList.add(childNode);

                            // Otherwise, skip it
                        else continue;
                    }

                    // If this node is not on our nodeList...
                    else
                    {
                        // Create a new Node
                        childNode = new Node(childCell);

                        // Set this Node's parent as currentNode
                        childNode.setParent(parentNode);

                        // Calculate F(), G() and H() for this Node
                        childNode.setCosts((parentNode.movementCost() + estimate(parentNode, childNode)), estimate(childNode, goalNode));

                        // Add Node to templist
                        tempList.add(childNode);

                        // Add Node to nodeList
                        nodeList.put(childCell, childNode);

                        // Increase nodes expanded
                        expanded++;
                    }
                }
            }
        }

        // Return a list of neighbor nodes
        return tempList;
    }

    // H() The estimate heuristic
    private int estimate(Node start, Node goal)
    {
        int straightCost = 10; // The movement cost for going straight (horizontal/vertical)

        // The Manhattan Distance from the start node to the goal node (horizontal/vertical)
        int straightSteps = (Math.abs(start.getCell().getR() - goal.getCell().getR()) + Math.abs(start.getCell().getC() - goal.getCell().getC()));

        // The actual heuristic for moving horizontally or vertically
        int estimate = straightCost * straightSteps;

        // Return our estimate
        return estimate;
    }

    // Build the best path up to this point
    private ArrayList<Node> buildPath(Node start)
    {
        Node current, next;
        ArrayList<Node> tempList = new ArrayList<Node>();

        // Add the first point to our list
        tempList.add(start);

        // Grab the next point in line
        current = start.getParent();

        // Loop through our generated path and add only the necessary points
        while ((next = current.getParent()) != null)
        {
            // If we can't skip the point, add it to our list and
            // set our new starting point to our current location
            if (!walkable(start.getCell(), next.getCell()))
                tempList.add(start = current);

            // Proceed to next point
            current = next;
        }

        // Add the last point to our list
        tempList.add(current);

        // Return our smoothed path
        return tempList;
    }

    // Samples points along a line from point A to point B at a certain granularity
    // checking at each point whether the unit overlaps any neighboring blocked tile.
    // This function returns true if it encounters no blocked tiles and false otherwise.
    private boolean walkable(Cell a, Cell b)
    {
        // We are using half cell width, anything smaller than 1/4 cell width results in error
        Point[] points = ArrayLevel.pointsAlongLine(a.getLocation(), b.getLocation(), (ArrayLevel.getCellSize() / 2));

        // Sample points along a line from Cell a to Cell b using on-fifth cell width
        for (Point p : points)
        {
            // Check to see if the unit would overlap into an unplayable cell
            // by checking the four points of its bounding box
            if (!owner.canMove(p)) return false;
        }

        // No obstructions found
        return true;
    }

    // Path is finished
    private void foundGoal()
    {
        // Tell AStar we are done
        needPath = false;
        isFinished = true;
    }

    // Perform clean-up operations when the final path is built
    private void cleanUp()
    {
        // Clean up data arrays
        openList 	= null;
        closedList 	= null;
        nodeList	= null;

        // Clean up Node variables
        startNode 	= null;
        goalNode 	= null;
    }

    // Initialize pathFinder arrays
    private void initialize()
    {
        // Initialize arrays
        openList 	= new Heap();
        closedList 	= new Heap();
        pathList 	= new ArrayList<Node>();
        nodeList	= new HashMap<Cell, Node>();

        // Initialize variables
        expanded 	= 0;
        needPath 	= true;
    }

}
