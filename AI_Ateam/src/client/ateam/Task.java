package client.ateam;

import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.Level.Models.Agent;
import client.ateam.projectEnum.TaskType;
import client.ateam.Level.ArrayLevel;

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
//                return (level.isNeighbor(box.getRow(), box.getColumn(), agent.assigned_goal_neighbour.getX(), agent.assigned_goal_neighbour.getY()));
                return (level.isNeighbor(box.getRow(), box.getColumn(), agent.row, agent.column));
            case Idle:
                return true;
            case NonObstructing:
                return false;
            case RemoveBox:
                return true;
            case AskForHelp:
                return true;
            case HelpOther:
                return true;
            default:
                System.err.println("Careful, you do not have any tasks assigned yet you're checking if an assigned task is completed");
                return false;
        }
    }

    public TaskType getTaskType(){
        return type;
    }
    @Override
    public String toString(){
        return ("agentID: " + agent.id + ",box: " + box.getRow() + ", " + box.getColumn() + ",goal: " + goal + ",taskType: " + type);
    }
}
