package client.ateam.Level.Models;

import client.ateam.Level.Actions.*;
import client.ateam.Task;
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
    public List<Task> tasks = new ArrayList<Task>(); //TODO: list of tasks could be priority queue
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
        if(!actionList.isEmpty())
        {
            currentAction = actionList.remove(0);
        }
    }

//    Getter for the currentaction
    public IAction getCurrentAction(){
        if(currentAction == null)
        {
            if(!actionList.isEmpty()) {
                currentAction = actionList.remove(0);
            }
        }
        return currentAction;
    }

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
        currentAction.executeAction();
        currentAction = null;
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
                        tasks.add(completed_task);
                        completed_tasks_check = false;
                    }
                }
                if(completed_tasks_check)
                {
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
                if(currentTask.getTaskType() == TaskType.MoveBoxToGoal)
                {
                    completed_tasks.add(currentTask);
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
                    if(hasBox)
                    {
                        if (!astar.pathExists())
                        {
                            Cell startLocation = new Cell(currentTask.box.getRow(), currentTask.box.getColumn());
                            startLocation.setLocation();
                            Cell goalLocation = new Cell(currentTask.goal.getRow(), currentTask.goal.getColumn());
                            goalLocation.setLocation();
                            astar.newPath(startLocation, goalLocation);
                            astar.findPath();
                            if(astar.pathExists())
                            {
                                convert_path_to_actions();
                                currentAction = actionList.remove(0);
                                currentTask.box.toggleisTaken();
                            }
                            else // something either is blocking the way or there is no path available
                            {
                                findAlternative(startLocation, goalLocation);
                            }
                        }
                    }
                    else
                    {
                        tasks.add(0, currentTask);
                        currentTask = new Task(this, currentTask.box.getId(), currentTask.box, new Goal(), TaskType.FindBox);
                        planning();
                    }
                    break;
                case FindBox:
                find_box:
                {
                    ArrayList<Cell> goal_neighbours = find_neighbor(new Point(currentTask.box.getRow(), currentTask.box.getColumn()));
                    for (Cell goal_neighbour : goal_neighbours) {
                        goal_neighbour.setLocation();
                        ArrayLevel level = ArrayLevel.getSingleton();
                        Agent our_agent = level.getAgentByID(currentTask.agent.id);
                        Cell our_agent_cell = ArrayLevel.getCellFromLocation(our_agent.row, our_agent.column);
                        astar.newPath(our_agent_cell, goal_neighbour);
                        astar.findPath();
                        if (astar.pathExists()) {
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
                                        break if_cond; //path bigger than the one we already selected? We skip through it
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
                        actionList.add(new_action);
                        break find_box;
                    }
                    //End of obstacle avoidance

                    // something either is blocking the way or there is no path available
                    //at this point, we know that we have the minimum amount of obstacles(if we moved or not previously from our original location)
                    //remove obstacles, or tell agents to move out of the way, or stay idle until another agent moves a box/themselves
                    preliminary_astar.newPath(ArrayLevel.getCellFromLocation(row, column),
                            ArrayLevel.getCellFromLocation(currentTask.box.getRow(), currentTask.box.getColumn()));
                    preliminary_astar.findPath();
                    if (preliminary_astar.pathExists())
                    {
                        findAlternative(ArrayLevel.getCellFromLocation(row, column),
                                ArrayLevel.getCellFromLocation(currentTask.box.getRow(), currentTask.box.getColumn()));
                    } else {
                        tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                        tasks.add(1, currentTask);
                        currentAction = new NoAction(id, new Point(row, column));
                    }
                }
                    break;
                case Idle:
                    break;
                case NonObstructing:
                    astar_path = preliminary_astar.getPath();
                    if (currentTask.agent.id == id) //pinch case where the agent needs to pull a box from a spot
                    {
                        the_for:
                        for(Node path_element : astar_path)
                        {
                            ArrayList<Cell> neighbors = new ArrayList<>();
                            neighbors = find_neighbor(new Point(path_element.getCell().getR(), path_element.getCell().getC()));

                            for(Cell neighbor : neighbors)
                            {
                                if (!(neighbor.getR() == path_element.getCell().getR() && neighbor.getC() == path_element.getCell().getC()) &&
                                        (neighbor.getCell_type() == CellType.EMPTY))
                                {
                                  Box task_box = currentTask.box;
                                    Point curAgent =  new Point(row, column);
                                    IAction new_action = new Pull(id, task_box.getBoxLetter(), curAgent,
                                            new Point(neighbor.getR(), neighbor.getC()),
                                            new Point(task_box.getRow(), task_box.getColumn()));
                                    actionList.add(0, new_action);
                                    path_element_to_remove = path_element;
                                    Box goal_box = new Box();
                                    goal_box.setColor(tasks.get(0).box.getColor());
                                    goal_box.setBoxLetter(tasks.get(0).box.getBoxLetter());
                                    goal_box.setRow(row);
                                    goal_box.setColumn(column);
                                    tasks.get(0).setBox(goal_box);
                                    astar.setPath(new ArrayList<Node>());
                                    break the_for;
                                }
                            }
                        }
                        astar_path.remove(path_element_to_remove);
                    }
                    else
                    {
                        //agent just needs to move out of the path's way
                    }
                    break;
                case RemoveBox:
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

                                new_action = new Push(id, using_box.getBoxLetter(), new Point(row, column),
                                        new Point(using_box.getRow(), using_box.getColumn()),
                                        new Point(neighbor.getR(), neighbor.getC()));
                                if (!new_action.preconditions())
                                {
                                    new_action = new Pull(id, currentTask.box.getBoxLetter(), new Point(row, column),
                                            new Point(neighbor.getR(), neighbor.getC()),
                                            new Point(using_box.getRow(), using_box.getColumn()));
                                }
                                actionList.add(0, new_action);
                                currentTask.box.toggleisTaken();
                                break bigger_for;
                            }
                        }
                    }
                    break;
                case AskForHelp:
                    break;
                case HelpOther:
                    break;
                default:
                    System.err.println("No Task Type assigned..be careful!");
                    break;
            }
        }
    }

    public void findAlternative(Cell startLocation, Cell goalLocation)
    {
        preliminary_astar.newPath(startLocation, goalLocation);
        preliminary_astar.findPath();
        if(preliminary_astar.pathExists())
        {
            ArrayList<Node> astar_path = preliminary_astar.getPath();
            Collections.reverse(astar_path);
            path_element_for:
            for(Node path_element : astar_path)
            {
                Cell path_cell = ArrayLevel.getCell(path_element.getCell().getR(), path_element.getCell().getC());
                if (path_cell.isOccupied()) //we found an element which is blocking the way..
                {
                    ArrayLevel level = ArrayLevel.getSingleton();
                    if (path_cell.getCell_type() == CellType.BOX)
                    {
                        Box path_box = level.getSpecificBox(path_cell);
                        if (path_box.isTaken())
                        {
                            tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                            currentAction = new NoAction(id, new Point(row, column));
                            currentTask = null;
                        }
                        else
                        {
                            if(this.color == path_box.getColor())
                            {
                                //if yes, try to move it somewhere which is NOT part of the preemptive_path
                                //get legit path to nearest box to remove
                                ArrayList<Cell> goal_box_neighbors = find_neighbor(new Point(path_box.getRow(), path_box.getColumn()));
                                Cell agent_Location = new Cell(row, column);
                                agent_Location.setLocation();
                                small_for:
                                {
                                    for (Cell goal_box_neighbor : goal_box_neighbors) {
                                        astar.newPath(startLocation, goal_box_neighbor);
                                        astar.findPath();
                                        if (astar.pathExists()) {
                                            break small_for;
                                        }
                                    }
                                }
                                tasks.add(0, new Task(this, path_box, currentTask.goal, TaskType.FindBox));
                                //as goal for removebox is a path element which is part of the path..it needs to be different than it
                                tasks.add(1, new Task(this, path_box.getId(),
                                        new Goal(' ', astar.getPath().get(1).getCell().getR(), astar.getPath().get(1).getCell().getC()),
                                        TaskType.RemoveBox));
                                tasks.add(2, currentTask);
                                currentTask = null;
                                break path_element_for;
                            }
                            else
                            {
                                ArrayList<Agent> agents = level.getAgents();
                                //just gonna assume that the box has a color identical to at least one of the agents
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
                        Agent path_agent = level.getSpecificAgent(path_cell);
                        if (path_agent.id != id)
                        {
                            if(path_agent.currentTask != null)
                            {
                                tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                                tasks.add(1, currentTask);
                                currentAction = new NoAction(id, new Point(row, column));
                                currentTask = null;
                            }
                            else
                            {
                                path_agent.tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.NonObstructing));
                                tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
                                tasks.add(1, currentTask);
                                currentAction = new NoAction(id, new Point(row, column));
                                currentTask = null;
                            }
                        }
                        else //agent blocking his own path
                        {
                            tasks.add(0, new Task(this, currentTask.box.getId(), currentTask.box, currentTask.goal, TaskType.NonObstructing));
                            tasks.add(1, currentTask);
                            currentTask = null;
                        }
                        break path_element_for;
                    }
                }
            }
            planning();
        }
        else
        {
            tasks.add(0, new Task(this, new Box(), new Goal(), TaskType.Idle));
            tasks.add(1, currentTask);
            currentAction = new NoAction(id, new Point(row, column));
        }
    }

    public void convert_path_to_actions(){
        ArrayList<Node> astar_path = astar.getPath();
        Collections.reverse(astar_path); //to make it ordered
        int astar_path_size = astar_path.size();

        if(!hasBox) //agent needs to move next to the box
        {
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
                IAction new_action = new Move(id, current, next);
                actionList.add(new_action);
                current = next;
                if(astar_path.size() == 0) break;
                next = astar_path.remove(0).getCell().getLocation();
            }while(astar_path.size() >= 0);
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
                        curAgent.getX() == tarCell.getX() && curAgent.getY() == tarCell.getY())
                {
                    //remove it from the path, we only need to do one push or pull action
                    tarCell = astar_path.remove(0).getCell().getLocation();
                }
            }
            do{
                new_action = new Push(id, curBox.getBoxLetter(), curAgent,
                        new Point(curBox.getRow(), curBox.getColumn()), new Point((int)tarCell.getX(), (int)tarCell.getY()));
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
            }while(astar_path.size() >= 0);

            Collections.reverse(actionList); //to make it ordered
        }
    }

    public boolean canMove(Point p)
    {
        return canMove(p.x, p.y);
    }

    public boolean canMove(int x, int y)
    {
        Rectangle me = new Rectangle(x, y);

        if (!ArrayLevel.getCellFromLocation(me.x, me.y).isOccupied())
            return false;

        if (!ArrayLevel.getCellFromLocation(me.x + me.width, me.y).isOccupied())
            return false;

        if (!ArrayLevel.getCellFromLocation(me.x, me.y + me.height).isOccupied())
            return false;

        if (!ArrayLevel.getCellFromLocation(me.x + me.width, me.y + me.height).isOccupied())
            return false;

        return true; // Entity can move
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

    public ArrayList<Node> find_alternative_path(Cell start_location, Cell goal_location) //first find the path
    {
        preliminary_astar.newPath(start_location, goal_location);
        preliminary_astar.findPath();
        return preliminary_astar.getPath();
    }

    public int count_alternative_path_obstacles() //second count the obstacles
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
