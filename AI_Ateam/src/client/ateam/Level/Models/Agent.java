package client.ateam.Level.Models;

import client.ateam.Level.Actions.*;
import client.ateam.Task;
import client.ateam.projectEnum.ActionType;
import client.ateam.projectEnum.CellType;
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

public class Agent {
    public int id;
    public Color color;
    public int row;
    public int column;
    //TODO: list of tasks could be priority queue
    public List<Task> tasks = new ArrayList<Task>();
    public List<Task> completed_tasks = new ArrayList<Task>();
    public boolean completed_tasks_check = true;
    public Task currentTask;
    private IAction currentAction;
    public List<IAction> actionList = new ArrayList<IAction>();
    public boolean hasBox;

    private Astar astar = new Astar(this);
    public Astar preliminary_astar = new Astar(this, true);

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

//    Gets next action in list and loads it to the current action
    public void NextAction(){
        if(actionList.isEmpty())
        {
            System.err.println("Action List is empty now");
//            planning();
        }
        else
        {
            currentAction = actionList.remove(0);
            System.err.println("Removed action from actionlist. Actionlist size now is : " + actionList.size());
        }
    }

//    Getter for the currentaction
    public IAction getCurrentAction(){
        if(currentAction == null)
        {
            if(actionList.isEmpty()) {
                System.err.println("Empty actionList when tried to get the Current Action");
            }
            else{
                currentAction = actionList.remove(0);
                System.err.println("I just removed action from action list. It is: " + currentAction.toString());
            }
        }
        return currentAction;
    }

//    Execute current action
    public void executeCurrentAction() {
        System.err.println("Calling ExecuteAction()");
        currentAction.executeAction();
        System.err.println("Calling NextAction()");
        NextAction();
    }

    public void planning()
    {
        currentAction = null;    // clean remnants from last plan
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
                completed_tasks_check = true;
                if (currentTask.getTaskType() == TaskType.MoveBoxToGoal)
                {
                    currentTask.box.toggleisTaken();
                }

                for(Task completed_task : completed_tasks)   //for each compleetd task, check again if they're completed
                {
                    if(!completed_task.isTaskCompleted())
                    {
                        System.err.println("Considering redoing some tasks..");
                        tasks.add(completed_task);
                        completed_tasks_check = false;
                    }
                }
                if(completed_tasks_check)
                {
                    System.err.println("Do not have any tasks assigned to me. I can help somebody");
                    tasks.add(new Task(this, new Box(), new Goal(), TaskType.Idle));
                    currentAction = new NoAction(id, new Point(row, column));
                }
                else
                {
                    planning();
                }
            }
            else {
                switch (currentTask.getTaskType()) {
                    case MoveBoxToGoal:
                        hasBox = false;
                        currentTask.box.toggleisTaken();
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
                //at some point we make a preliminary path which may cancel this task(moving a box out of the way which was already in a goal location)
                //we just add the current task to completed tasks
                //works well with SA, not sure about MA
                completed_tasks.add(currentTask);
                currentTask = tasks.remove(0);
                planning();
            }
        }
        else
        {
            Cell agentLocation = new Cell(row, column);
            agentLocation.setLocation();
            ArrayList<Node> astar_path;
            Node path_element_to_remove = new Node();

            switch (currentTask.getTaskType()) {
                case MoveBoxToGoal:
                    System.err.println("Case MoveBoxToGoal with hasBox: " + hasBox);
                    if(hasBox)
                    {
//                        System.err.println("I have a box..Do i have a path? " + astar.pathExists());
                        if (!astar.pathExists())
                        {
                            System.err.println("Current Task box and goal: " + currentTask.box.toString() + " , " + currentTask.goal.toString());
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
                                currentTask.box.toggleisTaken();
                                System.err.println("Action removed from list: " + currentAction.toString());
//                                System.err.println("Action list: " + actionList.size() + ", " + actionList.get(0));
                            }
                            else // something either is blocking the way or there is no path available
                            {
//                                System.err.println("didn't get any path from the box to the goal");
                                findAlternative(startLocation, goalLocation);
                            }
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
                find_box:
                {
                    System.err.println("Case FindBox");
                    ArrayList<Cell> goal_neighbours = new ArrayList<>();
                    goal_neighbours = find_neighbor(new Point(currentTask.box.getRow(), currentTask.box.getColumn()));

                    for (Cell goal_neighbour : goal_neighbours) {
                        goal_neighbour.setLocation();
                        astar.newPath(agentLocation, goal_neighbour);
                        astar.findPath();
                        if (astar.pathExists()) {
                            System.err.println("Managed without having anything blocking my way!");
                            convert_path_to_actions();
//                            System.err.println("Just converted the Pathfinding path to actions. Has Box? " + hasBox);
                            break find_box;
                        }
                    }
                    //OBSTACLE AVOIDANCE
                    ArrayList<Cell> agent_neighbours = new ArrayList<>();
                    agent_neighbours = find_neighbor(new Point(row, column));

                    for (Cell nei : agent_neighbours)
                    {
                        System.err.println("agent neighbors: " + nei.getRowColumn());
                    }
                    for(Cell agent_neighbor : agent_neighbours)
                    {
                        preliminary_astar.newPath(agent_neighbor, ArrayLevel.getCellFromLocation(currentTask.box.getRow(), currentTask.box.getColumn()));
                        preliminary_astar.findPath();
                        if (preliminary_astar.pathExists()) {
                            System.err.println("By moving to this neighbor first, I can findthebox");
                            IAction new_action = new Move(id, new Point(row, column), new Point(currentTask.box.getRow(), currentTask.box.getColumn()));
                            actionList.add(new_action);
//                            System.err.println("Just converted the Pathfinding path to actions. Has Box? " + hasBox);
                            break find_box;
                        }
                    }
                    Cell goal_neighbour = new Cell();
                    System.err.println("houston we have a problem"); // something either is blocking the way or there is no path available
                    //trying to get the neighbor which is closest to the agent in the preemptive path which may be generated
                    int path_size = 300; //just to get at least one neighbour
                    Cell start_location = new Cell(row, column);
                    Astar temp_path = new Astar(this, true);
                    for (int i = 0; i < goal_neighbours.size(); i++) {
                        Cell goal_neighbor_cell = goal_neighbours.get(i);
                        goal_neighbor_cell.setLocation();
                        start_location.setLocation();
//                        System.err.println("start,goal: " + start_location.toString() + ", " + goal_neighbor_cell.toString());
                        temp_path.newPath(start_location, goal_neighbor_cell);
                        temp_path.findPath();
                        int astar_size = temp_path.getPathSize();
                        if (astar_size != -1 && astar_size < path_size) {
                            goal_neighbour = goal_neighbours.remove(goal_neighbours.indexOf(goal_neighbor_cell));
//                                    System.err.println("we have a new goal_neighbor: " + goal_neighbor_cell.toString());
                            path_size = astar_size;
                            preliminary_astar = temp_path;
                            i--; //to avoid skipping of shifted element
                        }
                    }
                    if (preliminary_astar.pathExists()) //found a path, then something is blocking us!
                    {
                        System.err.println("Preliminary astar path: " + preliminary_astar.getPath());
                        findAlternative(start_location, goal_neighbour);
//                        System.err.println("task which I just added: " + tasks.get(0).toString());
//                        currentTask = null;
//                        planning();
                    } else {
                        System.err.println("Unsolvable Task, gonna chicken out for a while..maybe another agent may do a miracle");
                        tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                        tasks.add(1, currentTask);
                        currentAction = new NoAction(id, new Point(row, column));
                    }
                }
                    break;
                case Idle:
                    System.err.println("Case where agent needs to stay put");
                    break;
                case NonObstructing:
                    astar_path = preliminary_astar.getPath();
                    System.err.println("NonObstructing astar_path: " + preliminary_astar.toString());

                    if (currentTask.agent.id == id) //pinch case where the agent needs to pull a box from a spot
                    {
                        System.err.println("Trying to remove myself!");
                        the_for:
                        for(Node path_element : astar_path)
                        {
//                            System.err.println("Path element which I am about to make neighbors: " + path_element.getCell().getRowColumn());
                            ArrayList<Cell> neighbors = new ArrayList<>();
                            neighbors = find_neighbor(new Point(path_element.getCell().getR(), path_element.getCell().getC()));

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
                                        (neighbor.getCell_type() == CellType.EMPTY))
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
                    System.err.println("This is me, saying HI from RemoveBox");
                    astar_path = preliminary_astar.getPath();
                    System.err.println("NonObstructing astar_path: " + preliminary_astar.toString());

                    ArrayList<Cell> neighbors = new ArrayList<>();
                    neighbors = find_neighbor(new Point(currentTask.box.getRow(), currentTask.box.getColumn()));

//                    for (Cell nei : neighbors)
//                    {
//                        System.err.println("Added neighbor: " + nei.getRowColumn());
//                    }
                    for(Cell neighbor : neighbors)
                    {
                        if (!(neighbor.getR() == row && neighbor.getC() == column) && (neighbor.getCell_type() == CellType.EMPTY))
                        {
                            Box task_box = currentTask.box;
                            Point curAgent =  new Point(row, column);
                            //push or pull depending on the situation
//                            IAction new_action = new Push(id, task_box.getBoxLetter(), curAgent,
//                                    new Point(task_box.getRow(), task_box.getColumn()),
//                                    new Point(neighbor.getR(), neighbor.getC()));
//                            actionList.add(0, new_action);
//                            Box goal_box = new Box();
//                            goal_box.setColor(tasks.get(0).box.getColor());
//                            goal_box.setBoxLetter(tasks.get(0).box.getBoxLetter());
//                            goal_box.setRow(row);
//                            goal_box.setColumn(column);
//                            System.err.println("Goal box: " + goal_box.toString());
//                            tasks.get(0).setBox(goal_box);
//                            astar.setPath(new ArrayList<Node>());
                        }
                    }
//                    astar_path.remove(path_element_to_remove);

//                    Collections.reverse(astar_path);
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

    public void convert_path_to_actions(){
        ArrayList<Node> astar_path = astar.getPath();
        System.err.println("Before converting path to actions, let's see agent 0 path list: " + astar.getPath());
        Collections.reverse(astar_path); //to make it ordered
        int astar_path_size = astar_path.size();
        System.err.println("Agent 0 path list reversed: " + astar_path);

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

    public void findAlternative(Cell startLocation, Cell goalLocation)
    {
        preliminary_astar.newPath(startLocation, goalLocation);
        preliminary_astar.findPath();
        if(preliminary_astar.pathExists())
        {
            System.err.println("Unfortunately just gonna use a preliminary build path!");
            System.err.println("Something is blocking me, but I found a temporary path!");
            ArrayList<Node> astar_path = preliminary_astar.getPath();
            Collections.reverse(astar_path);
            path_element_for:
            for(Node path_element : astar_path)
            {
                Cell path_cell = ArrayLevel.getCell(path_element.getCell().getR(), path_element.getCell().getC());
                if (path_cell.isOccupied()) //we found an element which is blocking the way..
                {
//                    System.err.println("Element location which is blocking the way: " + path_cell.getR() + ", " + path_cell.getC());
                    ArrayLevel level = ArrayLevel.getSingleton();
                    if (path_cell.getCell_type() == CellType.BOX)
                    {
                        System.err.println("A box is blocking the path");
                        Box path_box = level.getSpecificBox(path_cell);
                        if (path_box.isTaken())
                        {
                            System.err.println("box is taken, gonna wait for it to disappear out of my(current agent) way!");
                            tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                            currentAction = new NoAction(id, new Point(row, column));
                            currentTask = null;
                        }
                        else
                        {
                            System.err.println("box is not taken, it may be removed!");
                            if(this.color == path_box.getColor())
                            {
                                //if yes, try to move it somewhere which is NOT part of the preemptive_path
                                System.err.println("Preemptive path: " + preliminary_astar.getPath()); // from moveboxtogoal task
                                System.err.println("Box is the same color as me; I can remove it!");
                                System.err.println("The box hero: " + path_box.getRow() + ", " + path_box.getColumn());
                                Box goal_box = new Box(' ', null, path_box.getRow(), path_box.getColumn());
                                tasks.add(0, new Task(this, goal_box, currentTask.goal, TaskType.FindBox));
                                //as goal for removebox is a path element which is part of the path..it needs to be different than it
                                tasks.add(1, new Task(this, goal_box,
                                        new Goal(' ', preliminary_astar.getPath().get(1).getCell().getR(), preliminary_astar.getPath().get(1).getCell().getC()),
                                        TaskType.RemoveBox));
                                tasks.add(2, currentTask);
                                System.err.println(tasks.get(0).toString());
                                System.err.println(tasks.get(1).toString());
                                System.err.println(tasks.get(2).toString());
                                currentTask = null;
                                break path_element_for;
                            }
                            else
                            {
                                ArrayList<Agent> agents = level.getAgents();
                                //just gonna assume that the box has a color identical to at least one of the agents
                                System.err.println("Box is not the same color as me; Somebody else can remove it!");
                                for(Agent agent : agents)
                                {
                                    if (agent.id != id)
                                    {
                                        agent.tasks.add(0, new Task(agent, new Box(' ', null, path_box.getRow(), path_box.getColumn()), new Goal(), TaskType.RemoveBox));
                                    }
                                }
                                tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                                tasks.add(1, currentTask);
                                currentAction = new NoAction(id, new Point(row, column));
                                currentTask = null;
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
                                currentAction = new NoAction(id, new Point(row, column));
                                currentTask = null;
                            }
                            else
                            {
                                System.err.println("Move out of the way!");
                                path_agent.tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.NonObstructing));
                                System.err.println("Gonna wait");
                                tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                                currentAction = new NoAction(id, new Point(row, column));
                                currentTask = null;
                            }
                        }
                        else
                        {
                            System.err.println("I am blocking the path!");
                            System.err.println("Current path: " + astar_path);
                            tasks.add(0, new Task(this, currentTask.box, currentTask.goal, TaskType.NonObstructing));
                            tasks.add(1, currentTask);
                            currentTask = null;
                        }
                        break path_element_for;
                    }
                }
            }
            planning();
            //TODO: remember to reverse at some points as we do for NonObstructing!!! in below's code
//            Collections.reverse(astar_path);
        }
        else
        {
            System.err.println("Unsolvable Task, gonna chicken out for a while..maybe another agent may do a miracle");
            tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
            tasks.add(1, currentTask);
            currentAction = new NoAction(id, new Point(row, column));
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

    public ArrayList<Cell> find_neighbor(Point cell)
    {
        boolean[][] walls = ArrayLevel.getSingleton().walls;
        ArrayList<Cell> neighbors = new ArrayList<>();

        Cell temp_cell = ArrayLevel.getCellFromLocation(cell.x - 1, cell.y);
        neighbors.add(temp_cell);
        temp_cell = ArrayLevel.getCellFromLocation(cell.x + 1, cell.y);
        neighbors.add(temp_cell);
        temp_cell = ArrayLevel.getCellFromLocation(cell.x, cell.y -1);
        neighbors.add(temp_cell);
        temp_cell = ArrayLevel.getCellFromLocation(cell.x, cell.y -1);
        neighbors.add(temp_cell);

        for (int i = 0; i < neighbors.size(); i++) {
            Cell goal_neighbor_cell = neighbors.get(i);
            if (walls[goal_neighbor_cell.getR()][goal_neighbor_cell.getC()]) {
                neighbors.remove(neighbors.indexOf(goal_neighbor_cell));
                i--; //to avoid skipping of shifted element
            }
        }

        return neighbors;
    }

    @Override
    public String toString(){
        return "row: " + row + " column: " + column;
    }
}
