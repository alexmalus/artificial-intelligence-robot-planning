package client.ateam;

import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;

/**
 * Created by Lasse on 4/28/15.
 */
public class Task {

    public int agentID;
    public Box box;
    public Goal goal;

    Task(int agentid,Box box, Goal goal){this.agentID=agentid;this.box=box;this.goal=goal;}
}
