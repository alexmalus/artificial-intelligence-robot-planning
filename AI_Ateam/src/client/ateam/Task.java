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

public class Task {
    public Agent agent;
    public Box box;
    public int box_id;
    public Goal goal;
    protected TaskType type;

    public Task(Agent agent, Box box, Goal goal, TaskType type){this.agent = agent;this.box=box;this.goal=goal;this.type=type;}
    public Task(Agent agent, int box_id, Goal goal, TaskType type){this.agent = agent;this.box_id=box_id;this.goal=goal;this.type=type;this.box = new Box();}
    public Task(Agent agent, int box_id, Box box, Goal goal, TaskType type){this.agent = agent;this.box_id=box_id;this.goal=goal;this.type=type;this.box = box;}

    public boolean isTaskCompleted(){
        Astar temp_path = new Astar(agent);
        ArrayLevel level = ArrayLevel.getSingleton();
        Box the_box = level.getBoxByID(box_id);
        Agent the_agent = level.getAgentByID(agent.id);

        switch (type){
            case MoveBoxToGoal:
                return (the_box.getColumn()==goal.getColumn()&& the_box.getRow()==goal.getRow());
            case FindBox:
                return (level.isNeighbor(box.getRow(), box.getColumn(), the_agent.row, the_agent.column));
            case Idle:
                return true;
            case NonObstructing:
                if(agent.tasks.get(0).isTaskCompleted()) {return true;}
                Cell startLocation = new Cell(agent.tasks.get(0).box.getRow(), agent.tasks.get(0).box.getColumn());
                startLocation.setLocation();
                Cell goalLocation = new Cell(agent.tasks.get(0).goal.getRow(), agent.tasks.get(0).goal.getColumn());
                goalLocation.setLocation();
                temp_path.newPath(startLocation, goalLocation);
                temp_path.findPath();
                return (temp_path.pathExists());
            case RemoveBox:
                Cell start_Location = ArrayLevel.getCellFromLocation(the_agent.row, the_agent.column);
                ArrayList<Cell> goal_location_neighbors = agent.find_neighbor(new Point(agent.tasks.get(0).box.getRow(), agent.tasks.get(0).box.getColumn()));
                for(Cell goal_location_neighbor : goal_location_neighbors)
                {
                    temp_path.newPath(start_Location, goal_location_neighbor);
                    temp_path.findPath();
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
