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
                System.err.println("is MoveBoxToGoal completed? " + (box.getx()==goal.getx()) + " " + (box.gety()==goal.gety()));
                return (box.getx()==goal.getx())&&(box.gety()==goal.gety());
            case FindBox:
                ArrayLevel level = ArrayLevel.getSingleton();
//                System.err.println("box: " + box.toString() + agent.assigned_goal_neighbour.toString());
                System.err.println("is FindBox completed? " + level.isNeighbor(box.gety(), agent.assigned_goal_neighbour.getR(), box.getx(),
                        agent.assigned_goal_neighbour.getC()));
                return (level.isNeighbor(box.gety(), agent.assigned_goal_neighbour.getR(), box.getx(), agent.assigned_goal_neighbour.getC()));
            case Idle:
                return true;
            case NonObstructing:
                return true;
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
        return ("agentID: " + agent.id + ",box: " + box.getBoxLetter() + ",goal: " + goal + ",taskType: " + type);
    }
}
