package client.ateam;

import client.ateam.Level.Cell;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.Level.Models.Agent;
import client.ateam.Pathfinder.Astar;
import client.ateam.projectEnum.TaskType;
import client.ateam.Level.ArrayLevel;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Lasse on 4/28/15.
 */
public class Task {
    //TODO:(reminder) tasks can currently be set to non-goal fields, e.g. a task could be to move a box to the free cell (1,1) or likewise.
    public Agent agent;
    public Box box;
    public int box_id;
    public Goal goal;
    protected TaskType type;

    //TODO: change so that we have just 1 constructor for all task types!
    public Task(Agent agent, Box box, Goal goal, TaskType type){this.agent = agent;this.box=box;this.goal=goal;this.type=type;}
    public Task(Agent agent, int box_id, Goal goal, TaskType type){this.agent = agent;this.box_id=box_id;this.goal=goal;this.type=type;this.box = new Box();}
    public Task(Agent agent, int box_id, Box box, Goal goal, TaskType type){this.agent = agent;this.box_id=box_id;this.goal=goal;this.type=type;this.box = box;}

    public boolean isTaskCompleted(){
        Astar temp_path = new Astar(agent);
        ArrayLevel level = ArrayLevel.getSingleton();
        Box the_box = level.getBoxByID(box_id);
//        System.err.println("What do we have at cell pos 2 9? " + ArrayLevel.getCellFromLocation(2, 9).getCell_type());
//        if(the_box.getId() == -1)
//        {
//            System.err.println("Lost the box..");
//        }
        Agent the_agent = level.getAgentByID(agent.id);
        switch (type){
            case MoveBoxToGoal:
                System.err.println("Box in cause: " + the_box.toString());
                System.err.println("is MoveBoxToGoal completed? " + (the_box.getColumn() == goal.getColumn()) + " " + (the_box.getRow() == goal.getRow()));
                return (the_box.getColumn()==goal.getColumn()&& the_box.getRow()==goal.getRow());
            case FindBox:
                System.err.println("is FindBox completed? " + level.isNeighbor(the_box.getRow(), the_box.getColumn(), the_agent.row,
                        the_agent.column));
                return (level.isNeighbor(box.getRow(), box.getColumn(), the_agent.row, the_agent.column));
            case Idle:
                return true;
            case NonObstructing:
                if(agent.tasks.get(0).isTaskCompleted()) {return true;}
                Cell startLocation = new Cell(agent.tasks.get(0).box.getRow(), agent.tasks.get(0).box.getColumn());
                startLocation.setLocation();
                Cell goalLocation = new Cell(agent.tasks.get(0).goal.getRow(), agent.tasks.get(0).goal.getColumn());
                goalLocation.setLocation();
//                System.err.println("start loc:" + startLocation.toString() + "goal loc: " + goalLocation.toString());
                temp_path.newPath(startLocation, goalLocation);
                temp_path.findPath();
                System.err.println("Is non-obstructing complete? " + temp_path.pathExists());
//                System.err.println("Show me task 0:" + agent.tasks.get(0).toString());
                return (temp_path.pathExists());
            case RemoveBox:
                Cell start_Location = ArrayLevel.getCellFromLocation(the_agent.row, the_agent.column);
//                Cell goal_Location = ArrayLevel.getCellFromLocation(agent.tasks.get(0).box.getRow(), agent.tasks.get(0).box.getColumn());
                ArrayList<Cell> goal_location_neighbors = agent.find_neighbor(new Point(agent.tasks.get(0).box.getRow(), agent.tasks.get(0).box.getColumn()));
                for(Cell goal_location_neighbor : goal_location_neighbors)
                {
                    temp_path.newPath(start_Location, goal_location_neighbor);
                    temp_path.findPath();
                    System.err.println("Agent currently is: " + start_Location.toString() + "Box which you're trying to find " + goal_location_neighbor.toString());
                    if(temp_path.pathExists())
                    {
                        return true;
                    }
                }
                return false;
            case AskForHelp:
                return true;
            case HelpOther:
                return true;
            default:
                System.err.println("Careful, you do not have any tasks assigned yet you're checking if an assigned task is completed");
                return false;
        }
    }

    public void setBox(Box box){
        this.box = box;
    }

    public TaskType getTaskType(){
        return type;
    }

    @Override
    public String toString(){
        if(box.getColor() == null)
        {
            return ("agentID: " + agent.id + ",box_id " + box_id + ",goal: " + goal + ",taskType: " + type);
        }
        else
        {
            return ("agentID: " + agent.id + ",box: " + box.getRow() + ", " + box.getColumn() + ",goal: " + goal + ",taskType: " + type);
        }
    }
}
