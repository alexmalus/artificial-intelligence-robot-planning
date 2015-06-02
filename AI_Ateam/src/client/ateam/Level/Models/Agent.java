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
import client.ateam.Level.Actions.Push;
import client.ateam.Level.Actions.Pull;

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
//    public Cell assigned_goal_neighbour = new Cell();
    private IAction currentAction;
    public List<IAction> actionList = new ArrayList<IAction>();
    public boolean hasBox;
    //made so that in task distributor, you can find the path, passing through agents and boxes, but not through walls
    //in order to order goals
    public boolean preliminary_build_path = true;

    private Astar astar = new Astar(this);

    public Agent(int id, Color color, int row, int column){
        this.color = color;
        this.id = id;
        this.row = row;
        this.column = column;
        hasBox = false;
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
                System.err.println("I just removed action from action list. It is: " + currentAction.toString());
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
                switch (currentTask.getTaskType()) {
                    case MoveBoxToGoal:
                        hasBox = false;
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
                currentTask = tasks.remove(0);
//                System.err.println("replanning 1");
                planning();
            }
        }
        else
        {
            Cell agentLocation = new Cell(row, column);
            agentLocation.setLocation();

            switch (currentTask.getTaskType()) {
                case MoveBoxToGoal:
                    System.err.println("Case MoveBoxToGoal with hasBox: " + hasBox);
                    if(hasBox)
                    {
                        if (!astar.pathExists())
                        {
                            System.err.println("Current Task box and goal: " + currentTask.box.toString() + " , " + currentTask.goal.toString());
//                          System.err.println("Plan so you move box to goal");
                            Cell startLocation = new Cell(currentTask.box.getRow(), currentTask.box.getColumn());
                            startLocation.setLocation();
                            Cell goalLocation = new Cell(currentTask.goal.getRow(), currentTask.goal.getColumn());
                            goalLocation.setLocation();
                            System.err.println("start: " + startLocation.toString() + ", goal: " + goalLocation.toString());
                            astar.newPath(startLocation, goalLocation);
                            astar.findPath();
                            convert_path_to_actions();

                            System.err.println("Action list: " + actionList.size() + ", " + actionList.get(0));
                        }
                        currentAction = actionList.remove(0);
                        System.err.println("Action removed from list: "+currentAction.toString());
                    }
                    else
                    {
                        tasks.add(0, new Task(this, currentTask.box, new Goal(), TaskType.FindBox));
                        tasks.add(1, new Task(this, currentTask.box, currentTask.goal, TaskType.MoveBoxToGoal));
                        currentTask = null;
//                        System.err.println("replanning 2");
                        planning();
                    }
                    break;
                case FindBox:
                    System.err.println("Case FindBox");
                    Cell goalLocation = new Cell(currentTask.box.getRow(), currentTask.box.getColumn());
                    goalLocation.setLocation();
//                    System.err.println("box: " + goalLocation.toString());
                    ArrayList<Cell> goal_neighbours = new ArrayList<>();
                    boolean[][] walls = ArrayLevel.getSingleton().walls;
//                    System.err.println("First neighbor: " + ArrayLevel.cellFromLocation(goalLocation.getR()-1,goalLocation.getC()));
                    if(!walls[goalLocation.getR()-1][goalLocation.getC()])
                    {
                        goal_neighbours.add(new Cell(goalLocation.getR()-1, goalLocation.getC()));
                    }
                    if(!walls[goalLocation.getR()+1][goalLocation.getC()])
                    {
                        goal_neighbours.add(new Cell(goalLocation.getR()+1, goalLocation.getC()));
                    }
                    if(!walls[goalLocation.getR()][goalLocation.getC()-1])
                    {
                        goal_neighbours.add(new Cell(goalLocation.getR(), goalLocation.getC()-1));
                    }
                    if(!walls[goalLocation.getR()][goalLocation.getC()+1])
                    {
                        goal_neighbours.add(new Cell(goalLocation.getR(), goalLocation.getC()+1));
                    }
                    Cell goal_neighbour;
//                    System.err.println("Goal neighbor sizes: " + goal_neighbours.size());
                    for(int i = 0; i <= goal_neighbours.size(); ++i)
                    {
//                        System.err.println("my :" + (i+1) + "attempt at trying to find a path");
                        goal_neighbour = goal_neighbours.remove(0);
                        goal_neighbour.setLocation();
                        System.err.println((i + 1) + " neighbor which I'm trying to TRY to get to:" +
                                goal_neighbour.toString());
                        astar.newPath(agentLocation, goal_neighbour);
                        astar.findPath();
//                        System.err.println("astar path size after find attempt: " + astar.pathExists());
                        if(astar.pathExists())
                        {
//                            System.err.println("I have a path!" + astar.getPath());
//                            assigned_goal_neighbour = goal_neighbour;
//                            assigned_goal_neighbour.setLocation();
                            //find plan (first plan or replan)
                            convert_path_to_actions();
//                            System.err.println("Just converted the Pathfinding path to actions. Has Box? " + hasBox);
                            break;
                        }
                    }
//                    hasBox = true;

                    break;
                case Idle:
                    System.err.println("Case where agent needs to stay put");
                    break;
                case NonObstructing:
                    System.err.println("Case where agent needs to move out of the way");
//                    Cell startLocation = new Cell(currentTask.box.getRow(), currentTask.box.getColumn());
//                    Cell goalLocation = new Cell(currentTask.goal.getRow(), currentTask.goal.getColumn());
//                    startLocation.setLocation();
//                    goalLocation.setLocation();
//                    System.err.println(startLocation.toString() + " " + goalLocation.toString());
//                    System.err.println("astar: " + astar.getPath());
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

    //TODO: figure out better naming for function
    public void convert_path_to_actions(){
        ArrayList<Node> astar_path = astar.getPath();
        Collections.reverse(astar_path); //to make it ordered
        int astar_path_size = astar_path.size();
        System.err.println("Before converting path to actions, let's see agent 0 path list: "+ astar.getPath());
        System.err.println("Same as upp, agent 0 path list: "+ astar_path);

        if(!hasBox) //agent needs to move next to the box
        {
//            System.err.println("Before converting path to actions, let's see agent 0 path list: "+ astar.getPath());
            Point current =  new Point(row, column);
            Point next = astar_path.remove(0).getCell().getLocation();
            //fix in Astar's pathfinding two points path problem, occurs when boxLocation is equal to startLocation of the pathfinder
            if(astar_path_size == 2)
            {
                if(current.getX() == next.getX() && current.getY() == next.getY())
                {
                    //remove it from the path, we only need to do one move action
                    next = astar_path.remove(0).getCell().getLocation();
                }
            }
            do{
//                System.err.println("Current R AND C: " + current.getX() + " " + current.getY());
//                System.err.println("Next R AND C: " + next.getX() + " " + next.getY());
                IAction new_action = new Move(id, current, next);
                actionList.add(new_action);
                current = next;
                if(astar_path.size() == 0) break;
                next = astar_path.remove(0).getCell().getLocation();
            }while(astar_path.size() >= 0);
//            System.err.println("Before converting path to actions, let's see agent 0 path list: " + astar_path);
//            System.err.println("path list size " + astar_path.size());
            System.err.println("Finished converting path to actions:" + actionList);
            System.err.println("action list size:" + actionList.size());
        }
        else //agent has to get the box in the goal
        {
//            System.err.println("Before converting path to actions, let's see agent 0 path list: "+ astar.getPath());
            IAction new_action;

            Point curAgent =  new Point(row, column);
            Box curBox = currentTask.box;
            //fix in Astar's pathfinding two points path problem
            //it only occurs when boxLocation is equal to startLocation of the pathfinder
            Point tarCell = astar_path.remove(0).getCell().getLocation();
            if(astar_path_size == 2)
            {
                if((curBox.getRow() == tarCell.getX() && curBox.getColumn() == tarCell.getY()) ||
                        curAgent.getX() == tarCell.getX() && curAgent.getY() == curAgent.getY())
                {
                    //remove it from the path, we only need to do one push or pull action
                    tarCell = astar_path.remove(0).getCell().getLocation();
                }
            }
//            System.err.println("tarCell: " + tarCell.toString());
            do{
                //    public Push(int agentId, char boxLetter, Point currentCell, Point boxCell, Point boxTarCell){
                //    public Pull(int agentId, char boxLetter, Point currentCell, Point tarCell, Point boxCell){
                new_action = new Push(id, curBox.getBoxLetter(), curAgent,
                        new Point(curBox.getRow(), curBox.getColumn()), new Point((int)tarCell.getX(), (int)tarCell.getY()));
//                System.err.println("c");
                if (new_action.preconditions()) //try to see if you can push the box
                {
                    actionList.add(0, new_action);

                    curAgent = new Point(curBox.getRow(), curBox.getColumn());
                    curBox.setRow((int) tarCell.getX());
                    curBox.setColumn((int) tarCell.getY());
                }
                else // then it means you need to pull it
                {
                    new_action = new Pull(id, curBox.getBoxLetter(), curAgent, new Point((int)tarCell.getX(), (int)tarCell.getY()),
                            new Point(curBox.getRow(), curBox.getColumn()));
                    actionList.add(0, new_action);

                    curAgent = tarCell;
                    curBox.setRow(row);
                    curBox.setColumn(column);
                }
                if(astar_path.size() == 0) break;
                tarCell = astar_path.remove(0).getCell().getLocation();
//                System.err.println("look at the action list after I've tried adding push or pull to it: " + actionList.get(0));

            }while(astar_path.size() >= 0);


            Collections.reverse(actionList); //to make it ordered
//            System.err.println("Before converting path to actions, let's see agent 0 path list: " + astar.getPath());
//            System.err.println("path list size " + astar.getPath().size());
            System.err.println("Finished converting path to actions:" + actionList);
            System.err.println("action list size:" + actionList.size());
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
