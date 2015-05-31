package client.ateam.Planning;

import client.ateam.Level.Models.Agent;

import java.util.ArrayList;

/**
 * Created by Lasse on 5/25/15.
 */
public class Planning {

    /*
    All this code could be located in the agent class
     */

    public void findPlan(Agent agent){
        //first check if goal is already completed (primary goal)
        // NOTE: FIRST IF SHOULD NEVER BE ENTERED, BUT IT'S THERE AS A SAFETY CHECK
        if(agent.currentTask.isTaskCompleted())
        {
            agent.planning();

            //recursion stuff
            this.findPlan(agent);
        }
        //then check if box is adjacent or neighbor to agent (subgoal)
        else if(this.isBoxNeighbor(agent)) {
            //find MoveBoxToGoal path
            // find solution
            if(!findBox2Goal(agent)){
                // if solution can't be found
                //TODO: this is not done (link to pathfinding)
                //ask for help or attempt other task
            }
        }
        // see if other agents request help
        /*else if(){

        }*/
        //else start with findbox (solution to subgoal)
        else {
            // recursion till solution has been found
            if (!this.findBox(agent)) {
                // if solution can't be found -> ask for help or attempt other task
                //TODO: this is not done (link to pathfinding)
            }

        }
    }
    private boolean isBoxNeighbor(Agent agent){
        return ((Math.abs(agent.currentTask.box.getRow()-agent.row)+Math.abs(agent.currentTask.box.getColumn()-agent.column))== 2);
    }

    // NOTE THAT THESE CAN BE USED WITH HELPING OTHER AGENTS AS WELL BY SETTING GOAL TO EMPTY CELL
    private boolean findBox2Goal(Agent agent){
        // A* search with agent as starting point and goal being box at goal
        return false;
    }
    private boolean findBox(Agent agent){
        // A* search with agent as starting point and goal as being next to box
        return false;
    }

    public void replan(Agent agent){
        //replan current procedure / plantype
    }

    public void replan(ArrayList<Agent> agents)
    {
        //replan current procedures / plantypes w.r.t. several agents
    }

}
