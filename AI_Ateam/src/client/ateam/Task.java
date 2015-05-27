package client.ateam;

import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.projectEnum.TaskType;

/**
 * Created by Lasse on 4/28/15.
 */
public class Task {

    public int agentID;
    public Box box;
    public Goal goal;
    protected TaskType type;

    public Task(int agentid,Box box, Goal goal){this.agentID=agentid;this.box=box;this.goal=goal;}

    public boolean isTaskCompleted(){
        //this can allow goals to be empty cells (helping other agents or themselves)
        return (box.getColumn()==goal.getColumn())&&(box.getRow()==goal.getRow());
    }
}
