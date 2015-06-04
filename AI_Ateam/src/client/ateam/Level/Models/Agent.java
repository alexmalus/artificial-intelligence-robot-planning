package client.ateam.Level.Models;

import client.ateam.Level.Actions.IAction;
import client.ateam.Level.Actions.Move;
import client.ateam.Task;
import client.ateam.projectEnum.CellType;
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
    private IAction currentAction;
    public List<IAction> actionList = new ArrayList<IAction>();
    public boolean hasBox;
    //made so that in task distributor, you can find the path, passing through agents and boxes, but not through walls
    //in order to order goals
    public boolean preliminary_build_path = true;

    private Astar astar = new Astar(this);
    public Astar preliminary_astar = new Astar(this);

    public Agent()
    {
        id = -1;
        row = -200;
        column = -200;
        color = null;
    }

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
//                        System.err.println("I have a box..");
//                        System.err.println("Do i have a path? " + astar.pathExists());
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
//                            System.err.println("I have a box..tried to make a path from the box to the goal");
                            if(astar.pathExists())
                            {
                                convert_path_to_actions();
                                currentAction = actionList.remove(0);
                                System.err.println("Action removed from list: " + currentAction.toString());
//                                System.err.println("Action list: " + actionList.size() + ", " + actionList.get(0));
                            }
                            else // something either is blocking the way or there is no path available
                            {
//                                System.err.println("didn't get any path from the box to the goal");
                                //try to find out if there's a possible path available
                                preliminary_build_path = true;
                                astar.newPath(startLocation, goalLocation);
                                astar.findPath();
                                if(astar.pathExists())
                                {
                                    System.err.println("Something is blocking me, but I found a temporary path!");
                                    ArrayList<Node> astar_path = astar.getPath();
                                    Collections.reverse(astar_path);
                                    path_element_for:
                                    for(Node path_element : astar_path)
                                    {
                                        Cell path_cell = ArrayLevel.getCell(path_element.getCell().getR(), path_element.getCell().getC());
                                        if (path_cell.isOccupied()) //we found the element which is blocking the way..
                                        {
//                                            System.err.println("Element location which is blocking the way: " + path_cell.getR() + ", " + path_cell.getC());
                                            ArrayLevel level = ArrayLevel.getSingleton();
                                            if (path_cell.getCell_type() == CellType.BOX)
                                            {
                                                System.err.println("A box is blocking the path");
                                                Box path_box = level.getSpecificBox(path_cell);
                                                if (path_box.isTaken())
                                                {
                                                    //gonna wait for it to dissapear out of my(current agent) way
                                                    tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                                                }
                                                else
                                                {
                                                    if(this.color == path_box.getColor())
                                                    {
                                                        //if yes, try to move it somewhere which is NOT part of the preemptive_path
                                                        tasks.add(0, new Task(this, path_box, new Goal(), TaskType.RemoveBox));
                                                    }
                                                    else
                                                    {
                                                        ArrayList<Agent> agents = level.getAgents();
                                                        //just gonna assume that the box has a color identical to at least one of the agents
                                                        for(Agent agent : agents)
                                                        {
                                                            if (agent.id != id)
                                                            {
                                                                agent.tasks.add(0, new Task(agent, path_box, new Goal(), TaskType.RemoveBox));
                                                            }
                                                            else
                                                            {

                                                            }
                                                        }
                                                        tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                                                        tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                                                    }
                                                }
                                                break path_element_for;
                                            }
                                            else //it's an agent
                                            {
                                                System.err.println("An agent is blocking the path");
                                                Agent path_agent = level.getSpecificAgent(path_cell);
                                                if (path_agent.id != id)
                                                {
                                                    if(path_agent.currentTask != null)
                                                    {
                                                        System.err.println("Gonna wait");
                                                        tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                                                    }
                                                    else
                                                    {
                                                        System.err.println("Move out of the way!");
                                                        path_agent.tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.NonObstructing));
                                                    }
                                                }
                                                else
                                                {
                                                    System.err.println("I am blocking the path!");
//                                                    System.err.println("Current path: " + astar_path);
                                                    tasks.add(0, new Task(this, currentTask.box, currentTask.goal, TaskType.NonObstructing));
                                                    tasks.add(1, currentTask);
                                                    currentTask = null;
                                                    preliminary_build_path = false;
                                                    planning();
                                                }
                                                break path_element_for;
                                            }
                                        }
                                    }
                                    //TODO: remember to reverse at some points as we do for NonObstructing!!! in below's code
//                                    Collections.reverse(astar_path);
                                }
                                else
                                {
                                    System.err.println("Unsolvable Task");
                                }
                            }
//                            System.err.println("Do I have a path? " + astar.pathExists());
//                            Cell agent_location = ArrayLevel.getCell(row, column);
//                            System.err.println("Cell where agent is currently at is occupied? : " +
//                                    agent_location.getX() + ", " + agent_location.getY() + ", " + agent_location.isOccupied());

                        }
                    }
                    else
                    {
                        tasks.add(0, new Task(this, currentTask.box, new Goal(), TaskType.FindBox));
                        tasks.add(1, new Task(this, currentTask.box, currentTask.goal, TaskType.MoveBoxToGoal));
                        currentTask = null;
                        planning();
                    }
                    break;
                case FindBox:
                    System.err.println("Case FindBox");
                    ArrayList<Node> astar_path;
//                    Cell goalLocation = new Cell(currentTask.box.getRow(), currentTask.box.getColumn());
//                    goalLocation.setLocation();
                    ArrayList<Cell> goal_neighbours = new ArrayList<>();
                    goal_neighbours.add(new Cell(currentTask.box.getRow()-1, currentTask.box.getColumn()));
                    goal_neighbours.add(new Cell(currentTask.box.getRow()+1, currentTask.box.getColumn()));
                    goal_neighbours.add(new Cell(currentTask.box.getRow(), currentTask.box.getColumn()-1));
                    goal_neighbours.add(new Cell(currentTask.box.getRow(), currentTask.box.getColumn() + 1));
                    boolean[][] walls = ArrayLevel.getSingleton().walls;

                    for (int i = 0; i < goal_neighbours.size(); i++) {
                        Cell goal_neighbor_cell = goal_neighbours.get(i);
                        if (walls[goal_neighbor_cell.getR()][goal_neighbor_cell.getC()]){
                            goal_neighbours.remove(goal_neighbours.indexOf(goal_neighbor_cell));
                            i--; //to avoid skipping of shifted element
                        }
                    }
                    Cell goal_neighbour;

                    for(int i = 0; i <= goal_neighbours.size(); ++i)
                    {
                        goal_neighbour = goal_neighbours.remove(0);
                        goal_neighbour.setLocation();
                        System.err.println((i + 1) + " neighbor which I'm trying to TRY to get to:" +
                                goal_neighbour.toString());
                        astar.newPath(agentLocation, goal_neighbour);
                        astar.findPath();
//                        System.err.println("astar path size after find attempt: " + astar.pathExists());
                        if(astar.pathExists())
                        {
                            System.err.println("Managed without having anything blocking my way!");
//                            System.err.println("I have a path!" + astar.getPath());
                            //find plan (first plan or replan)
                            convert_path_to_actions();
//                            System.err.println("Just converted the Pathfinding path to actions. Has Box? " + hasBox);
                            break;
                        }
                        else // something either is blocking the way or there is no path available
                        {
                            //trying to get the neighbor which is closest to the agent in the preemptive path which may be generated
                            //in order to simplify the future computations
                            int path_size = 300; //just to get at least one neighbour
                            preliminary_build_path = true;
                            for (i = 0; i < goal_neighbours.size(); i++) {
                                Cell goal_neighbor_cell = goal_neighbours.get(i);
                                Cell start_location = new Cell(row, column);
                                start_location.setLocation();
                                preliminary_astar.newPath(start_location, goal_neighbor_cell);
                                preliminary_astar.findPath();
                                int astar_size = preliminary_astar.getPathSize();
                                if (astar_size != -1 && astar_size < path_size){
                                    goal_neighbour = goal_neighbours.remove(goal_neighbours.indexOf(goal_neighbor_cell));
                                    path_size = astar_size;
                                    i--; //to avoid skipping of shifted element
                                }
                            }
//                            preliminary_build_path = !preliminary_build_path;

                            System.err.println("Unfortunately just gonna use a preliminary build path!");
//                            preliminary_build_path = true;
//                            astar.newPath(agentLocation, goal_neighbour);
//                            astar.findPath();
                            if(astar.pathExists()) //found a path, then something is blocking us!
                            {
                                preliminary_build_path = false;
//                                System.err.println("Astar's attempt to see path: " + astar.getPath());
                                tasks.add(0, new Task(this, currentTask.box, new Goal(' ', goal_neighbour.getR(), goal_neighbour.getC()), TaskType.NonObstructing));
                                System.err.println("task which I just added NONOBS: " + tasks.get(0).toString());
                                tasks.add(1, currentTask);
                                currentTask = null;
                                planning();
                            }
                            else
                            {
                                System.err.println("Unsolvable Task");
                            }
                        }
                    }
                    break;
                case Idle:
                    System.err.println("Case where agent needs to stay put");
                    break;
                case NonObstructing:
                    astar_path = astar.getPath();
                    System.err.println("NonObstructing astar_path: " + astar_path.toString());
                    boolean[][] the_walls = ArrayLevel.getSingleton().walls;

                    Node path_element_to_remove = new Node();
                    if (currentTask.agent.id == id) //pinch case where the agent needs to pull a box from a spot
                    {
                        System.err.println("Trying to remove myself!");
                        the_for:
                        for(Node path_element : astar_path)
                        {
//                            System.err.println("Path element which I am about to make neighbors: " + path_element.getCell().getRowColumn());
                            ArrayList<Cell> neighbors = new ArrayList<>();
                            Cell temp_cell = ArrayLevel.getCellFromLocation(path_element.getCell().getR() - 1, path_element.getCell().getC());
                            neighbors.add(temp_cell);
                            temp_cell = ArrayLevel.getCellFromLocation(path_element.getCell().getR() + 1, path_element.getCell().getC());
                            neighbors.add(temp_cell);
                            temp_cell = ArrayLevel.getCellFromLocation(path_element.getCell().getR(), path_element.getCell().getC()-1);
                            neighbors.add(temp_cell);
                            temp_cell = ArrayLevel.getCellFromLocation(path_element.getCell().getR(), path_element.getCell().getC()+1);
                            neighbors.add(temp_cell);

                            for (Cell nei : neighbors)
                            {
                                System.err.println("Added neighbor: " + nei.getRowColumn());
                            }
                            for(Cell neighbor : neighbors)
                            {
//                                System.err.println("neigbor row: " + neighbor.getR() + ", col: " + neighbor.getC());
//                                System.err.println("Same as element within path?" + (neighbor.getR() == path_element.getCell().getR()));
//                                System.err.println("is it a wall?" + the_walls[neighbor.getR()][neighbor.getC()]);
//                                System.err.println("is it a cell type empty?" + (neighbor.getCell_type() == CellType.EMPTY));
                                if (!(neighbor.getR() == path_element.getCell().getR() && neighbor.getC() == path_element.getCell().getC()) &&
                                        !(the_walls[neighbor.getR()][neighbor.getC()]) && (neighbor.getCell_type() == CellType.EMPTY))
                                {
                                  Box task_box = currentTask.box;
                                    Point curAgent =  new Point(row, column);
                                    IAction new_action = new Pull(id, task_box.getBoxLetter(), curAgent,
                                            new Point(neighbor.getR(), neighbor.getC()),
                                            new Point(task_box.getRow(), task_box.getColumn()));
//                                    System.err.println("Agent location: " + curAgent.toString());
//                                    System.err.println("Box location: " + task_box.toString());
//                                    System.err.println("Tar cell: " + neighbor.toString());
//                                    System.err.println("Action trying to perform: " + new_action.toString());
//                                    }
                                    actionList.add(0, new_action);
                                    path_element_to_remove = path_element;
                                    Box goal_box = new Box();
                                    goal_box.setColor(tasks.get(0).box.getColor());
                                    goal_box.setBoxLetter(tasks.get(0).box.getBoxLetter());
                                    goal_box.setRow(row);
                                    goal_box.setColumn(column);
                                    System.err.println("Goal box: " + goal_box.toString());
                                    tasks.get(0).setBox(goal_box);
                                    astar.setPath(new ArrayList<Node>());
//                                    System.err.println("Will add these to the new box location of moveboxtogoal: " + row + ", " + column);
//                                    System.err.println("Box before adding from movebox: " + tasks.get(0).box.toString());
//                                    tasks.get(0).box.setRow(row);
//                                    System.err.println("Box row after adding from movebox: " + tasks.get(0).box.getRow());
//                                    tasks.get(0).box.setRow(column);
//                                    System.err.println("Box column after adding from movebox: " + tasks.get(0).box.getColumn());
//                                    System.err.println("Box from movebox: " + tasks.get(0).box.toString());
//                                    System.err.println("The movebox looks as follows: " + tasks.get(0).toString());
//                                    System.err.println("Found neighbor for the agent to move in: " + neighbor.getR() + ", " + neighbor.getC());
//                                    System.err.println("Prepared this action for you to execute first: " + new_action.toString());
                                    break the_for;
                                }
                            }
                        }
                        astar_path.remove(path_element_to_remove);
                    }
                    else //agent just needs to move out of the path's way
                    {

                    }
                    Collections.reverse(astar_path);
//                    convert_path_to_actions();
//                    currentAction = actionList.remove(0);

//                    Cell startLocation = new Cell(currentTask.box.getRow(), currentTask.box.getColumn());
//                    startLocation.setLocation();
//                    Cell goalLocation = new Cell(currentTask.goal.getRow(), currentTask.goal.getColumn());
//                    goalLocation.setLocation();
//                    astar.newPath(startLocation, goalLocation);
//                    astar.findPath();
//                    currentTask = null;
                    break;
                case RemoveBox:
                    //we have previously calculated a preemptive path. we need to move the box out of box in cause of this path
                    astar_path = astar.getPath();
                    System.err.println("RemoveBox astar_path: " + astar_path.toString());
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
        System.err.println("Before converting path to actions, let's see agent 0 path list: "+ astar.getPath());
        Collections.reverse(astar_path); //to make it ordered
        int astar_path_size = astar_path.size();
        System.err.println("Same as upp, agent 0 path list: "+ astar_path);

        if(!hasBox) //agent needs to move next to the box
        {
//            System.err.println("Before converting path to actions, let's see agent 0 path list: "+ astar.getPath());
            Point current =  new Point(row, column);
            Point next = astar_path.remove(0).getCell().getLocation();
            //fix in Astar's pathfinding two points path problem, occurs when current Agent's location is equal to startLocation of the pathfinder
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
            Box curBox = new Box(currentTask.box.getBoxLetter(), currentTask.box.getColor() ,currentTask.box.getRow(), currentTask.box.getColumn());
            //fix in Astar's pathfinding two points path problem, occurs when curAgent's or box's Location is equal to startLocation of the pathfinder
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
