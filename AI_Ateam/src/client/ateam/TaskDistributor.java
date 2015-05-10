package client.ateam;

import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Integer> colors = new ArrayList<Integer>();
        boolean identicalColorAgents = false;
        HashMap<Integer,ArrayList<Box>> boxcolors = new HashMap<Integer,ArrayList<Box>>();
        HashMap<Character,Box> boxletters = new HashMap<Character,Box>();
        HashMap<Integer,ArrayList<Agent>> agentcolors = new HashMap<Integer,ArrayList<Agent>>();
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

        for(Integer color: colors){
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
        Agent matchingAgent;

        if(identicalColorAgents){
            for(Goal goal : goals){
                matchingBox = boxletters.get(goal.letter);
                matchingAgents = agentcolors.get(matchingBox.color);

                //find best matching agent

                //TODO:BETTER IMPLEMENTATION AND DISTRIBUTION SELECT BEST OPTION IN MATCHING AGENTS
                matchingAgent = matchingAgents.get(0);
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
