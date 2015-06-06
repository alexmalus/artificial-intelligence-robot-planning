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

//    public void setCurrentAction_ToNoOp()
//    {
//        currentAction = new NoAction(id, new Point(row, column));
//    }

    public IAction getFirstAction(){
        if (!actionList.isEmpty())
        {
            return actionList.get(0);
        }
        else if(currentAction != null)
        {
            return currentAction;
        }
        else
        {
            return null;
        }
    }

//    Execute current action
    public void executeCurrentAction() {
        System.err.println("Calling ExecuteAction()");
        currentAction.executeAction();
        currentAction = null;
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
                else if(currentTask.getTaskType() == TaskType.RemoveBox)
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
                System.err.println("Show me all the remaining tasks: " + tasks.toString());
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
                        hasBox = false;
                        currentTask.box.toggleisTaken();
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
                if(currentTask.getTaskType() == TaskType.MoveBoxToGoal)
                {
                    completed_tasks.add(currentTask);
                    System.err.println("Show me all the completed tasks: " + completed_tasks.toString());
                }
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
//                        tasks.add(0, new Task(this, currentTask.box, new Goal(), TaskType.FindBox));
                        tasks.add(0, currentTask);
                        currentTask = new Task(this, currentTask.box, new Goal(), TaskType.FindBox);
                        planning();
                    }
                    break;
                case FindBox:
                find_box:
                {
                    System.err.println("Case FindBox");
                    ArrayList<Cell> goal_neighbours = find_neighbor(new Point(currentTask.box.getRow(), currentTask.box.getColumn()));
                    for (Cell goal_neighbour : goal_neighbours) {
                        goal_neighbour.setLocation();
                        ArrayLevel level = ArrayLevel.getSingleton();
                        Agent our_agent = level.getAgentByID(currentTask.agent.id);
                        Cell our_agent_cell = ArrayLevel.getCellFromLocation(our_agent.row, our_agent.column);
                        System.err.println("Tell me the agent: " + our_agent_cell.toString());
//                        System.err.println("What Cell Type do we have at 4 12: " + ArrayLevel.getCellFromLocation(4,12).getCell_type());
                        System.err.println("At this point of time, do I have a box? " + hasBox);
                        astar.newPath(our_agent_cell, goal_neighbour);
                        astar.findPath();
                        if (astar.pathExists()) {
                            System.err.println("Managed without having anything blocking my way!");
                            convert_path_to_actions();
                            break find_box;
                        }
                    }
                    //OBSTACLE AVOIDANCE
                    int alternative_path_obstacle_size = 300;
                    boolean should_move_to_neighbor = false;
                    int temp_obstacle_size;
                    IAction new_action = new NoAction(id, new Point(row, column));
                    find_alternative_path(ArrayLevel.getCellFromLocation(row, column),
                            ArrayLevel.getCellFromLocation(currentTask.box.getRow(), currentTask.box.getColumn()));
                    if(preliminary_astar.pathExists())
                    {
                        alternative_path_obstacle_size = count_alternative_path_obstacles();//first I count no. obstacles from agent pos to box which I try to find
                    }
                    ArrayList<Cell> agent_neighbours = find_neighbor(new Point(row, column));//then obstacles from agent's neighbors
                    ArrayList<Node> temp_path_list;
                    for(Cell agent_neighbor : agent_neighbours)
                    {
                        temp_path_list = find_alternative_path(agent_neighbor,
                                ArrayLevel.getCellFromLocation(currentTask.box.getRow(), currentTask.box.getColumn()));
                        if (temp_path_list != null && temp_path_list.size() > 0)
                        if_cond:
                            {
                                temp_obstacle_size = count_alternative_path_obstacles();
                                if (temp_obstacle_size != 0 && temp_obstacle_size < alternative_path_obstacle_size)
                                {
                                    //we already selected a neighbor which has less obstacles than the one we are checking
                                    if(should_move_to_neighbor && temp_path_list.size() > preliminary_astar.getPath().size()){
                                        break if_cond; //path bigger than the one we already selected? Neeext..
                                    }
                                    alternative_path_obstacle_size = temp_obstacle_size;
                                    should_move_to_neighbor = true;
                                    new_action = new Move(id, new Point(row, column), new Point(agent_neighbor.getR(), agent_neighbor.getC()));
                                    preliminary_astar.setPath(temp_path_list);
                                }
                            }
                    }
                    if(should_move_to_neighbor)
                    {
                        System.err.println("Adding a Move action");
                        actionList.add(new_action);
                        break find_box;
                    }
                    //End of obstacle avoidance

                    System.err.println("houston we have a problem"); // something either is blocking the way or there is no path available
                    //at this point, we know that we have the minimum amount of obstacles(if we moved or not previously from our original location)
                    //remove obstacles, or tell agents to move out of the way, or stay idle until another agent moves a box/themselves
                    preliminary_astar.newPath(ArrayLevel.getCellFromLocation(row, column),
                            ArrayLevel.getCellFromLocation(currentTask.box.getRow(), currentTask.box.getColumn()));
                    preliminary_astar.findPath();
                    if (preliminary_astar.pathExists())
                    {
                        System.err.println("Preliminary astar path: " + preliminary_astar.getPath());
                        findAlternative(ArrayLevel.getCellFromLocation(row, column),
                                ArrayLevel.getCellFromLocation(currentTask.box.getRow(), currentTask.box.getColumn()));
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
                    System.err.println("NonObstructing astar_path: " + preliminary_astar.getPath().toString());

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
//                                    ArrayLevel level = ArrayLevel.getSingleton();
//                                    level.moveBoxTo(tasks.get(0).box.getBoxLetter(), new Point(tasks.get(0).box.getRow(), tasks.get(0).box.getColumn()),
//                                            new Point(row, column));
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
                    System.err.println("This is me, saying HI from RemoveBox");
                    System.err.println("prel astar: " + preliminary_astar.getPath().toString());
                    ArrayList<Cell> agent_neighbors = find_neighbor(new Point(row, column));
                    bigger_for:
                    {
                        for(Cell neighbor: agent_neighbors)
                        {
                            small_for:
                            {
                                //check if agent's neighbor is not on the path
                                for(Node path_list_member : preliminary_astar.getPath())
                                {
                                    if((neighbor.getR() == path_list_member.getCell().getR() && neighbor.getC() == path_list_member.getCell().getC()) &&
                                            (neighbor.getR() == currentTask.box.getRow() && neighbor.getC() == currentTask.box.getColumn()))
                                    {
                                        break small_for;
                                    }
                                }
                                IAction new_action;

                                ArrayLevel level = ArrayLevel.getSingleton();
                                Box using_box = level.getBoxByID(currentTask.box_id);
                                System.err.println("Curr task: " + currentTask.toString());
                                System.err.println("Agent R AND C: " + row + ", " + column);
                                System.err.println("Box R AND C: " + using_box.getRow() + ", " +  using_box.getColumn());
                                System.err.println("neighbor which is OK: " + neighbor.toString());

                                new_action = new Push(id, using_box.getBoxLetter(), new Point(row, column),
                                        new Point(using_box.getRow(), using_box.getColumn()),
                                        new Point(neighbor.getR(), neighbor.getC()));
                                if (!new_action.preconditions())
                                {
                                    new_action = new Pull(id, currentTask.box.getBoxLetter(), new Point(row, column),
                                            new Point(neighbor.getR(), neighbor.getC()),
                                            new Point(using_box.getRow(), using_box.getColumn()));
                                }
                                System.err.println("new Action: " + new_action.toString());
                                actionList.add(0, new_action);
                                currentTask.box.toggleisTaken();
                                break bigger_for;
                            }
                        }
                    }
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
            Box curBox = new Box(0, currentTask.box.getBoxLetter(), currentTask.box.getColor() ,currentTask.box.getRow(), currentTask.box.getColumn());
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
                                Box goal_box = new Box(0, ' ', null, path_box.getRow(), path_box.getColumn());

                                //get legit path to nearest box to remove
                                ArrayList<Cell> goal_box_neighbors = find_neighbor(new Point(path_box.getRow(), path_box.getColumn()));
                                Cell agent_Location = new Cell(row, column);
                                agent_Location.setLocation();
                                small_for:
                                {
                                    for (Cell goal_box_neighbor : goal_box_neighbors) {
                                        System.err.println("start: " + startLocation.toString() + ", goal: " + goal_box_neighbor.toString());
                                        astar.newPath(startLocation, goal_box_neighbor);
                                        astar.findPath();
                                        if (astar.pathExists()) {
//                                        System.err.println("Found path mofo!");
//                                        System.err.println("Path: " + astar.getPath().toString());
                                            break small_for;
                                        }
                                    }
                                }

                                tasks.add(0, new Task(this, goal_box, currentTask.goal, TaskType.FindBox));
                                //as goal for removebox is a path element which is part of the path..it needs to be different than it
//                                tasks.add(1, new Task(this, goal_box,
//                                        new Goal(' ', astar.getPath().get(1).getCell().getR(), astar.getPath().get(1).getCell().getC()),
//                                        TaskType.RemoveBox));
                                tasks.add(1, new Task(this, path_box.getId(),
                                        new Goal(' ', astar.getPath().get(1).getCell().getR(), astar.getPath().get(1).getCell().getC()),
                                        TaskType.RemoveBox));
                                tasks.add(2, currentTask);
                                System.err.println("path_box:" + path_box.toString() + " " + path_box.getId());
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
                                        agent.tasks.add(0, new Task(agent, path_box.getId(), new Goal(), TaskType.RemoveBox));
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
//                            System.err.println("Current path: " + astar_path);
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
        temp_cell = ArrayLevel.getCellFromLocation(cell.x, cell.y +1);
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

    //first find the path
    public ArrayList<Node> find_alternative_path(Cell start_location, Cell goal_location)
    {
        preliminary_astar.newPath(start_location, goal_location);
        preliminary_astar.findPath();
        return preliminary_astar.getPath();
    }
    //second count the obstacles
    public int count_alternative_path_obstacles()
    {
        int no_obstacles = 0;
        ArrayList<Node> astar_path = preliminary_astar.getPath();
            for(Node path_element : astar_path)
            {
                Cell path_cell = ArrayLevel.getCell(path_element.getCell().getR(), path_element.getCell().getC());
                if (path_cell.isOccupied()) //we found an element which is blocking the way..
                {
                    no_obstacles++;
                }
            }
        return no_obstacles;
    }

    @Override
    public String toString(){
        return "row: " + row + " column: " + column;
    }
}
