package client.ateam.Planning;

import client.ateam.Level.Models.Agent;

import java.util.ArrayList;

/**
 * Created by Lasse on 5/25/15.
 */
public class Planning {

    public void findPlan(Agent agent){
        //first check if goal is already completed (primary goal)
        if(agent.currentTask.goal.isComplete())
        {

        }

        //then check if box is adjacent or neighbor to agent (subgoal)

        //find MoveBoxToGoal path
            // find solution

        //else start with findbox (solution to subgoal)
            // recursion till solution has been found
        //if findbox fails

    }

    public void replan(Agent agent){
        //replan current procedure / plantype
    }

    public void replan(ArrayList<Agent> agents)
    {
        //replan current procedures / plantypes w.r.t. several agents
    }

}
