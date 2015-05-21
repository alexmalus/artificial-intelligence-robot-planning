package client.ateam;

import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.projectEnum.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import static javafx.beans.binding.Bindings.select;

/**
 * Created by joh on 21/04/15.
 */

/*
* This is suppose to handle
*
* Find Tasks
* Find Boxes
* Find Agents
* Combine the 3 objectives above.
*
* */


public class TaskDistributor {

    /*
    Break down tasks and create high level tasks, which boxes to move where - this can be changed to be computed dynamically as well,
    but currently working with static tasks is favorable
     */

    void distributeTasks(ArrayList<Agent> agents, ArrayList<Box> boxes, ArrayList<Goal> goals){
        Character letter;
        Box selectedBox;
        Agent selectedAgent;
        List<Box> matchingBoxes;
        List<Agent> matchingAgents;
        List<Color> colors = new ArrayList<Color>();
        boolean identicalColorAgents = false;
        HashMap<Color, ArrayList<Box>> boxcolors = new HashMap<Color, ArrayList<Box>>();
        HashMap<Character,Box> boxletters = new HashMap<Character,Box>();
        HashMap<Color, ArrayList<Agent>> agentcolors = new HashMap<Color, ArrayList<Agent>>();
        HashMap<Character,Goal> goalletters = new HashMap<Character,Goal>();


        for(Agent agent : agents){
            if(!colors.contains(agent.color)){
                colors.add(agent.color);
            }
            else{
                identicalColorAgents = true;
                break;
            }
        }

        for(Color color: colors){
            boxcolors.put(color, new ArrayList<Box>());
            agentcolors.put(color, new ArrayList<Agent>());
        }
        for(Box box:boxes){
            boxcolors.get(box.color).add(box);
            boxletters.put(box.boxLetter,box); // bad use of hashmap :b
        }
        for(Agent agent:agents){
            agentcolors.get(agent.color).add(agent);
        }
        for(Goal goal:goals){
            goalletters.put(goal.letter,goal);
        }

        Box matchingBox;
        Agent matchingAgent = null;
        int dist=-1;
        if(identicalColorAgents){
            for(Goal goal : goals){
                matchingBox = boxletters.get(goal.letter);
                matchingAgents = agentcolors.get(matchingBox.color);

                //find best matching agent
                //TODO:Round robin implementation
                // otherwise an implementation where agents ask for next may be more feasible.
                for(Agent agent:matchingAgents){
                    // manhattan distance is used
                    if(matchingAgent==null || Math.abs(agent.row-matchingBox.row)+Math.abs(agent.column-matchingBox.column) < dist){
                        matchingAgent = agent;
                        dist = Math.abs(agent.row-matchingBox.row)+Math.abs(agent.column-matchingBox.column);
                    }
                }

                matchingAgent.tasks.add(new Task(matchingAgent.id,matchingBox,goal));
            }

        }
        else{
            for(Goal goal : goals){
                matchingBox = boxletters.get(goal.letter);
                matchingAgent = agentcolors.get(matchingBox.color).get(0);//only one color box per agent
                matchingAgent.tasks.add(new Task(matchingAgent.id,matchingBox,goal));
            }

        }
    }
    void addGoal(Goal goal){

    }

    void removeGoal(Goal goal){

    }
    private Box selectBox(List<Box> boxes){
        return boxes.get(0);
    }
    private Agent selectAgent(List<Agent> agents){
        return agents.get(0);
    }
}
