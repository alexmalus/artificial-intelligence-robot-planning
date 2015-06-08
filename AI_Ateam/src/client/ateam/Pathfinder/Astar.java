package client.ateam.Pathfinder;

import java.util.HashMap;
import java.util.ArrayList;
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

    public boolean preliminary_build_path = false; //made so that passing through agents and boxes is possible, but not through walls

    public Astar(Agent owner) {
        this.owner = owner;
    }

    public Astar(Agent owner, boolean preliminary_build_path)
    {
        this.owner = owner;
        this.preliminary_build_path = preliminary_build_path;
    }

    public ArrayList<Node> getPath() // Return the path
    {
        return pathList;
    }

    public void setPath(ArrayList<Node> pathList)
    {
        this.pathList = pathList;
    }

    public boolean pathExists()  // Whether or not we have a path to move along
    {
        return (pathList != null && pathList.size() > 0);
    }

    public void setStart(Cell start) // Store the starting point of the path and add it to openList
    {
        this.startNode = new Node(start);
        nodeList.put(start, startNode);
        openList.add(startNode);
    }

    public void setGoal(Cell goal) // Store the goal point of the path
    {
        this.goalNode = new Node(goal);
        nodeList.put(goal, goalNode);
    }

    public void newPath(Cell start, Cell goal) // Re-calculates the path with new start and goal nodes
    {
        initialize(); // Initialize variables

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

        // Loop through all possible nodes and find the best path to the goal
        while (openList.size() > 0)
        {
            // Set our currentNode to the node with the lowest totalCost
            currentNode = openList.pop();

            // Add currentNode to closedList (since we will be examining it)
            closedList.add(currentNode);
            // If we have found the goal, notify AStar that we no longer need a path
            if (currentNode.getCell().getX() == goalNode.getCell().getX() &&
                    currentNode.getCell().getY() == goalNode.getCell().getY())
            {
                foundGoal();
                // Otherwise, continue to search for next best move
            }
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
//                        System.err.println("Neighbor added: " + neighbor.getCell().toString());
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

        if (needPath && openList.size() == 0)
        {
            pathList = null; //agent can't move!
        }

        // Path is complete
        if (!needPath)
        {
            cleanUp();
        }
    }

    // Returns a list of Nodes surrounding parentNode
    public ArrayList<Node> neighbors(Node parentNode)
    {
        Node childNode;
        Cell parentCell;
        ArrayList<Node> tempList = new ArrayList<Node>(8);

        // Cell reference for parentNode
        parentCell = parentNode.getCell();
        ArrayList<Cell> childCells = new ArrayList<>();
        childCells.add(ArrayLevel.getCell(parentCell.getR() - 1, parentCell.getC()));
        childCells.add(ArrayLevel.getCell(parentCell.getR() + 1, parentCell.getC()));
        childCells.add(ArrayLevel.getCell(parentCell.getR(), parentCell.getC() - 1));
        childCells.add(ArrayLevel.getCell(parentCell.getR(), parentCell.getC() + 1));

        for(Cell childCell : childCells) // Search the surrounding 4 neighbour nodes for possible places to go
        {
            // Make sure this Cell exists and is not occupied
            if ((childCell != null) && !childCell.isOccupied(preliminary_build_path))
            {

                // Attempt to grab the Node for this Cell
                childNode = nodeList.get(childCell);

                // If this node is already on our nodeList...
                if (childNode != null)
                {
                    // if childNode is not parentNode, add it to our list
                    if (childNode != parentNode) tempList.add(childNode);
                    else continue; // Otherwise, skip it
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

        // Return a list of neighbor nodes
        return tempList;
    }

    // H() The estimate heuristic
    private int estimate(Node start, Node goal)
    {
        int straightCost = 10; // The movement cost for going straight (horizontal/vertical)

        // The Manhattan Distance from the start node to the goal node (horizontal/vertical)
        int straightSteps = (Math.abs(start.getCell().getR() - goal.getCell().getR()) + Math.abs(start.getCell().getC() - goal.getCell().getC()));

        // Return our estimate
        return straightCost * straightSteps;
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
        if (current != null){
            tempList.add(current);
        }
        // Loop through our generated path and add only the necessary points
        while ((next = current.getParent()) != null)
        {
            if (next != startNode) tempList.add(next);

            // Proceed to next point
            current = next;
//            System.err.println("Next node: " + current.getCell().toString());
        }

        // Return our smoothed path
        return tempList;
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
