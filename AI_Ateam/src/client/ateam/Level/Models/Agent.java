package client.ateam.Level.Models;

import client.ateam.Level.Actions.IAction;
import client.ateam.Level.Actions.Move;
import client.ateam.Task;
import client.ateam.projectEnum.Color;
import client.ateam.Level.ArrayLevel;
import client.ateam.Level.Cell;
import client.ateam.Pathfinder.Astar;
import client.ateam.Pathfinder.Node;
import client.ateam.projectEnum.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.Collections;

//TODO: might be an idea to merge Agent and Planning classes since each Agent plans its own plan.

public class Agent {
    public int id;
    public Color color;
    public int row;
    public int column;
    //TODO: list of tasks could be priority queue
    public List<Task> tasks = new ArrayList<Task>();
    public Task currentTask;
    private IAction currentAction;
    public List<IAction> actionList = new ArrayList<IAction>();
    public boolean hasBox = false;

    private Astar astar = new Astar(this);

    public Agent(int id, Color color, int row, int column){
        this.color = color;
        this.id = id;
        this.row = row;
        this.column = column;
    }

    /*
    Gets next action in list and loads it to the current action
     */
    /*public void NextAction(){
        if(actionList.isEmpty())
        {
            //do planning if list is empty
            //but this should never occur to
        }

    }*/
    /*
    Getter for the currentaction
     */
    public IAction getCurrentAction(){
        if(currentAction == null)
        {
            //TODO: empty list checks
            if(actionList.isEmpty()) {

            }
            else{
                currentAction = actionList.remove(0);
            }
        }
        return currentAction;
    }

    /*
    Execute current action
     */
    public void executeCurrentAction() {

        //do execute
        currentAction.executeAction();


        //check for goal
        if(actionList.isEmpty())
        {
            planning();
        }
        else{
            currentAction = actionList.remove(0);
        }
    }
    public void planning()
    {
        // clean remnants from last plan
        currentAction = null;
        actionList.clear();

        if (currentTask == null) {
            if(tasks.isEmpty()) {
                return;
            }
            else {
                currentTask = tasks.remove(0);
            }
        }

        if(currentTask.isTaskCompleted())
        {
            //TODO: empty list checks
            if(tasks.isEmpty())
            {
                //idle or help others

            }
            else {
                currentTask = tasks.remove(0);
//                planning();
            }
        }
        else
        {
            Cell agentLocation = new Cell(row, column);
            agentLocation.setLocation();

            switch (currentTask.getTaskType()) {
                case GOAL:
                    System.err.println("Case where agent needs first move near box and then move box into goal");
                    if(hasBox)
                    {
                        System.err.println("move the box to the goal wise ass");
                    }
                    else
                    {
                        tasks.add(0, new Task(id, currentTask.box, new Goal(), TaskType.BOX));
                        tasks.add(1, currentTask);
                        currentTask = null;
//                        for (int i = 0; i < tasks.size(); i++) {
//                            System.err.println(tasks.get(i).toString());
//                        }
                        planning();
                    }
                    break;
                case BOX:
                    System.err.println("Case where agent needs to move towards box");
                    Cell goalLocation = new Cell(currentTask.box.getRow(), currentTask.box.getColumn());
                    ArrayList<Cell> goal_neighbours = new ArrayList<>(4);
                    goal_neighbours.add(new Cell(goalLocation.getR()-1, goalLocation.getC()));
                    goal_neighbours.add(new Cell(goalLocation.getR()+1, goalLocation.getC()));
                    goal_neighbours.add(new Cell(goalLocation.getR(), goalLocation.getC()-1));
                    goal_neighbours.add(new Cell(goalLocation.getR(), goalLocation.getC()+1));
                    Cell goal_neighbour;
                    for(int i = 0; i < 4; ++i)
                    {
                        goal_neighbour = goal_neighbours.remove(i);
                        goal_neighbour.setLocation();
                        astar.newPath(agentLocation, goal_neighbour);
                        astar.findPath();
                        if(astar.pathExists()) break;
                    }
//                    hasBox = true;
                    //find plan (first plan or replan)
                    convert_path_to_actions();
                    break;
                case AGENT:
                    System.err.println("Case where agent needs to help other agent");
                    break;
                case AGENTAPPROX:
                    System.err.println("Do not know what this is");
                    break;
                case NONOBSTRUCTING:
                    System.err.println("Case where agent needs to move out of the way");
                    break;
                case REMOVEBOX:
                    System.err.println("Case where agent needs to remove box");
                    break;
                default:
                    System.err.println("No Task Type assigned..be careful!");
                    break;
            }

        }
    }

    // Convenience method for canMove(int, int)
    public boolean canMove(Point p)
    {
        return canMove(p.x, p.y);
    }

    // Check to see whether or not this entity can move
    // FIXME
    public boolean canMove(int x, int y)
    {
        // Construct a bounding box for location x, y
        Rectangle me = new Rectangle(x, y);

        // Check top-left corner
        if (!ArrayLevel.getCellFromLocation(me.x, me.y).isOccupied())
            return false;

        // Check top-right corner
        if (!ArrayLevel.getCellFromLocation(me.x + me.width, me.y).isOccupied())
            return false;

        // Check bottom-left corner
        if (!ArrayLevel.getCellFromLocation(me.x, me.y + me.height).isOccupied())
            return false;

        // Check bottom-right corner
        if (!ArrayLevel.getCellFromLocation(me.x + me.width, me.y + me.height).isOccupied())
            return false;

        // Entity can move
        return true;
    }

    public void convert_path_to_actions(){
        ArrayList<Node> astar_path = astar.getPath();
        Node path_node;
        //first step is to reverse points from the pathlist to make the actions which the agent do ordered
        Collections.reverse(astar_path);
//        for (int i = 0; i < astar.getPath().size(); i++) {
//            System.err.println(astar.getPath().get(i));
//        }

        if(!hasBox)
        //move the agent next to the box
        {
            System.err.println("Before converting path to actions, let's see agent 0 path list: "+ astar.getPath());
            while(astar_path.size() != 0){
                path_node = astar_path.remove(0);
                IAction new_action = new Move(id, new Point(row, column), path_node.getCell().getLocation());
                actionList.add(new_action);
            }
            hasBox = true;
        }
        else
        {
            //move with box to the goal
            hasBox = false;
        }

    }

    public Astar get_astar(){
        return astar;
    }
}
