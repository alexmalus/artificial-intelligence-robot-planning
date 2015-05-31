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
    public Cell assigned_goal_neighbour = new Cell();
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
    public void NextAction(){
        if(actionList.isEmpty())
        {
            //do planning if list is empty
            //but this should never occur to
            System.err.println("Action List is empty now");
//            planning();
        }
        else
        {
            currentAction = actionList.remove(0);
            System.err.println("Removed action from actionlist. Actionlist size now is : " + actionList.size());
        }
    }
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
        System.err.println("Calling ExecuteAction()");
        currentAction.executeAction();
        System.err.println("Calling NextAction()");
        NextAction();
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
            if(tasks.isEmpty())
            {
                System.err.println("Do not have any tasks assigned to me. I can help somebody");
                tasks.add(new Task(this, new Box(), new Goal(), TaskType.Idle));
            }
            else {
                currentTask = tasks.remove(0);
                switch (currentTask.getTaskType()) {
                    case MoveBoxToGoal:
                        break;
                    case FindBox:
                        hasBox = true;
                        break;
                    case NonObstructing:
                        break;
                    case RemoveBox:
                        break;
                    case AskForHelp:
                        break;
                    case HelpOther:
                        break;
                    default:
                        System.err.println("I should not see this print on the console!");
                        break;
                }
                planning();
            }
        }
        else
        {
            Cell agentLocation = new Cell(row, column);
            agentLocation.setLocation();

            switch (currentTask.getTaskType()) {
                case MoveBoxToGoal:
                    System.err.println("Case MoveBoxToGoal");
                    if(hasBox)
                    {
                        System.err.println("Plan so you move box to goal");
                    }
                    else
                    {
                        tasks.add(0, new Task(this, currentTask.box, new Goal(), TaskType.FindBox));
                        tasks.add(1, new Task(this, currentTask.box, currentTask.goal, TaskType.MoveBoxToGoal));
                        currentTask = null;
//                        for (int i = 0; i < tasks.size(); i++) {
//                            System.err.println(tasks.get(i).toString());
//                        }
                        planning();
                    }
                    break;
                case FindBox:
                    System.err.println("Case FindBox");
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
                        if(astar.pathExists())
                        {
//                            assigned_goal_neighbour = goal_neighbour;
//                            assigned_goal_neighbour.setLocation();
                            break;
                        }
                    }
//                    hasBox = true;
                    //find plan (first plan or replan)
                    convert_path_to_actions();
                    break;
                case Idle:
                    System.err.println("Case where agent needs to stay put");
                    break;
                case NonObstructing:
                    System.err.println("Case where agent needs to move out of the way");
                    break;
                case RemoveBox:
                    System.err.println("Case where agent needs to remove box");
                    break;
                case AskForHelp:
                    System.err.println("Case where agent asks for help");
                    break;
                case HelpOther:
                    System.err.println("Case where agent needs to help other agent");
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
        //first step is to reverse points from the pathlist to make the actions which the agent do ordered
        Collections.reverse(astar_path);
//        for (int i = 0; i < astar.getPath().size(); i++) {
//            System.err.println(astar.getPath().get(i));
//        }

        if(!hasBox)
        //move the agent next to the box
        {
//            System.err.println("Before converting path to actions, let's see agent 0 path list: "+ astar.getPath());
            Point current =  new Point(row, column);
            Point next = astar_path.remove(0).getCell().getLocation();
            do{
//                System.err.println("Current R AND C: " + current.getX() + " " + current.getY());
//                System.err.println("Next R AND C: " + next.getX() + " " + next.getY());
                IAction new_action = new Move(id, current, next);
                actionList.add(new_action);
                current = next;
                if(astar_path.size() == 0) break;
                next = astar_path.remove(0).getCell().getLocation();

            }while(astar_path.size() >= 0);
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

    @Override
    public String toString(){
        return "row: " + row + " column: " + column;
    }
}
