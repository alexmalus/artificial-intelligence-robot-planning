package client.ateam;

import client.ateam.Level.Cell;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.Level.Models.Agent;
import client.ateam.Pathfinder.Astar;
import client.ateam.projectEnum.TaskType;
import client.ateam.Level.ArrayLevel;

import java.awt.*;

/**
 * Created by Lasse on 4/28/15.
 */
public class Task {
    //TODO:(reminder) tasks can currently be set to non-goal fields, e.g. a task could be to move a box to the free cell (1,1) or likewise.
    public Agent agent;
    public Box box;
    public Goal goal;
    protected TaskType type;

    public Task(Agent agent, Box box, Goal goal, TaskType type){this.agent = agent;this.box=box;this.goal=goal;this.type=type;}

    public boolean isTaskCompleted(){
        //this can allow goals to be empty cells (helping other agents or themselves)
        switch (type){
            case MoveBoxToGoal:
//                System.err.println("current task box and goal: " + box.toString() + ", " + goal.toString());
                System.err.println("is MoveBoxToGoal completed? " + (box.getColumn()==goal.getColumn()) + " " + (box.getRow()==goal.getRow()));
                return (box.getColumn()==goal.getColumn())&&(box.getRow()==goal.getRow());
            case FindBox:
                ArrayLevel level = ArrayLevel.getSingleton();
//                System.err.println("box: " + box.toString() + " " + "assigned goal neighbor: " + agent.assigned_goal_neighbour.toString());
//                System.err.println("box: " + box.toString() + " " + "agent: " + agent.toString());
//                System.err.println("is FindBox completed? " + level.isNeighbor(box.getRow(), box.getColumn(), agent.assigned_goal_neighbour.getX(),
//                        agent.assigned_goal_neighbour.getY()));
                System.err.println("is FindBox completed? " + level.isNeighbor(box.getRow(), box.getColumn(), agent.row,
                        agent.column));
                System.err.println("Agent coords: "+agent.row+","+agent.column);
                return (level.isNeighbor(box.getRow(), box.getColumn(), agent.row, agent.column));
            case Idle:
                return true;
            case NonObstructing:
//                System.err.println("Task from which I am seeing stuff: " + agent.tasks.get(0).toString());
//                System.err.println("Trying to add box as startLoc: " + agent.tasks.get(0).box.toString());
                Cell startLocation = new Cell(agent.tasks.get(0).box.getRow(), agent.tasks.get(0).box.getColumn());
                startLocation.setLocation();
                Cell goalLocation = new Cell(agent.tasks.get(0).goal.getRow(), agent.tasks.get(0).goal.getColumn());
                goalLocation.setLocation();
//                System.err.println("start loc:" + startLocation.toString() + "goal loc: " + goalLocation.toString());
                Astar temp_path = new Astar(agent);
                temp_path.newPath(startLocation, goalLocation);
                temp_path.findPath();
                System.err.println("Is non-obstructing complete? " + temp_path.pathExists());
                return (temp_path.pathExists());
            case RemoveBox:
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
        return ("agentID: " + agent.id + ",box: " + box.getRow() + ", " + box.getColumn() + ",goal: " + goal + ",taskType: " + type);
    }
}
