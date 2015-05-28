package client.ateam;

import client.ateam.Level.ArrayLevel;
import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.projectEnum.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
* This is supposed to handle
* Find Tasks
* Find Boxes
* Find Agents
* Combine the 3 objectives above.
* */

public class TaskDistributor {
	/*
    Break down tasks and create high level tasks, which boxes to move where - this can be changed to be computed dynamically as well,
    but currently working with static tasks is favorable
     */
    private ArrayLevel level = ArrayLevel.getSingleton();

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
            boxcolors.get(box.getColor()).add(box);
            boxletters.put(box.getBoxLetter(),box); // bad use of hashmap :b
        }
        for(Agent agent:agents){
            agentcolors.get(agent.color).add(agent);
        }
        for(Goal goal:goals){
            goalletters.put(goal.getGoalLetter(),goal);
        }

        Box matchingBox;
        Agent matchingAgent = null;
        int dist=-1;
        if(identicalColorAgents){
            for(Goal goal : goals){
                matchingBox = boxletters.get(Character.toUpperCase(goal.getGoalLetter()));
                matchingAgents = agentcolors.get(matchingBox.getColor());

                //find best matching agent
                //TODO:Round robin implementation
                // otherwise an implementation where agents ask for next may be more feasible.
                for(Agent agent:matchingAgents){
                    // manhattan distance is used
                    if(matchingAgent==null || Math.abs(agent.row-matchingBox.getRow())+Math.abs(agent.column-matchingBox.getColumn()) < dist){
                        matchingAgent = agent;
                        dist = Math.abs(agent.row-matchingBox.getRow())+Math.abs(agent.column-matchingBox.getColumn());
                    }
                }
                matchingAgent.tasks.add(new Task(matchingAgent.id, matchingBox, goal));
                level.getTasks().add(new Task(matchingAgent.id, matchingBox, goal));
            }
        }
        else{
            for(Goal goal : goals) {
                matchingBox = boxletters.get(Character.toUpperCase(goal.getGoalLetter()));
                matchingAgent = agentcolors.get(matchingBox.getColor()).get(0);//only one color box per agent
                matchingAgent.tasks.add(new Task(matchingAgent.id,matchingBox,goal));
//                System.err.println("Matching ID: "+matchingAgent.tasks.get(0).box.getBoxLetter());
                level.getTasks().add(new Task(matchingAgent.id, matchingBox, goal));
            }
        }
//        System.err.println(agents.get(0).tasks.get(0).box.getBoxLetter());
//        System.err.println(ArrayLevel.getSingleton().getAgents().get(0).tasks.isEmpty());
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