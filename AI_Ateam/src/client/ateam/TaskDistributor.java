package client.ateam;

import client.ateam.Level.ArrayLevel;
import client.ateam.Level.Cell;
import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.projectEnum.Color;
import client.ateam.projectEnum.TaskType;

import java.util.*;

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
//        Character letter;
//        Box selectedBox;
//        Agent selectedAgent;
        List<Agent> matchingAgents;
        List<Color> colors = new ArrayList<Color>();
        boolean identicalColorAgents = false;
        HashMap<Color, ArrayList<Box>> boxcolors = new HashMap<Color, ArrayList<Box>>();
        Map<Character, List<Box>> new_boxletters = new HashMap<Character, List<Box>>();
        List<Box> valSet;
        HashMap<Color, ArrayList<Agent>> agentcolors = new HashMap<Color, ArrayList<Agent>>();
        List<Agent> freeAgents = new ArrayList<>();
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
//            boxletters.put(box.getBoxLetter(),box); // bad use of hashmap :b

            valSet = new_boxletters.remove(box.getBoxLetter());
            if (valSet == null){
                valSet = new ArrayList<Box>();
            }
            valSet.add(box);
            new_boxletters.put(box.getBoxLetter(), valSet);
//            new_boxletters.get(box.getBoxLetter()).add(box);
        }

//        for(Character key: new_boxletters.keySet()){
//            System.err.println("key part of boxcolors: " + key);
//            for(Box value: new_boxletters.get(key)) {
//                System.err.println("value/s assigned to key: " + value); //none
//            }
//        }

        for(Agent agent:agents){
            agentcolors.get(agent.color).add(agent);
            freeAgents.add(agent); //filling up agents who do not have any tasks assigned to them yet
        }
        for(Goal goal:goals){
            goalletters.put(goal.getGoalLetter(),goal);
        }

        List<Box> matchingBoxes;
        Agent matchingAgent = null;
        Box matchingBox = null;
        int dist1, dist2;
        int smallest_dist = 0;

        if(identicalColorAgents) {
            for (Goal goal : goals) {
                matchingBoxes = new_boxletters.get(Character.toUpperCase(goal.getGoalLetter()));
                matchingAgents = agentcolors.get(matchingBoxes.get(0).getColor());
                //gonna select only free agents though even though they fulfill the same color as the boxes which have the same letter as the goal
                boolean is_a_match = false; //sees if a free
                for(Agent matching_agent: matchingAgents)
                {
                    for (Agent free_agent : freeAgents)
                    {
                        if (free_agent.id == matching_agent.id)
                        {
                            is_a_match = true;
                        }
                    }
                    if (!is_a_match) //if he already has a task assigned to it
                    {
                        matchingAgents.remove(matching_agent);
                    }
                }
                //if every agent have a task assigned to it, then we have to add more tasks to them
                if (matchingAgents.size() == 0)
                {
                    matchingAgents = agentcolors.get(matchingBoxes.get(0).getColor());
                }

                //find best matching agent
                //TODO:Round robin implementation
                // otherwise an implementation where agents ask for next may be more feasible.
                for (Box box : matchingBoxes) {
                    for (Agent agent : matchingAgents) { // manhattan distance is used
                        dist1 = Math.abs(agent.row - box.getRow()) + Math.abs(agent.column - box.getColumn());
                        dist2 = Math.abs(goal.getRow() - box.getRow()) + Math.abs(goal.getColumn() - box.getColumn());
                        dist1 += dist2;
                        if (smallest_dist == 0 || dist1 < smallest_dist) {
                            smallest_dist = dist1;
                            matchingAgent = agent;
                            matchingBox = box;
                        }
                    }
                }
                matchingAgent.tasks.add(new Task(matchingAgent, matchingBox, goal, TaskType.MoveBoxToGoal));
                level.getTasks().add(new Task(matchingAgent, matchingBox, goal, TaskType.MoveBoxToGoal));
                smallest_dist = 0;
                matchingAgents.remove(matchingAgent);
            }
        }
        else{
            List<Goal> ordered_goals = new ArrayList<>();
            ordered_goals.add(goals.remove(0));
            Agent sole_agent = agents.get(0);
            boolean is_set = false;
            int goal_path_size, ordered_goal_path_size;
//            System.err.println("Initially ordered_goals size and look:" + ordered_goals.size() + " , " + ordered_goals);
            for (Goal goal: goals)
            {
                inner_for:
                for(int i = 0; i< ordered_goals.size(); i++){

                    Goal ordered_goal = ordered_goals.get(i);
                    Cell agent_cell = new Cell(sole_agent.row, sole_agent.column);
                    agent_cell.setLocation();
                    Cell goal_cell = new Cell(ordered_goal.getRow(), ordered_goal.getColumn());
                    goal_cell.setLocation();
                    sole_agent.preliminary_astar.newPath(agent_cell, goal_cell);
                    sole_agent.preliminary_astar.findPath();
                    ordered_goal_path_size = sole_agent.preliminary_astar.getPath().size();

                    goal_cell = new Cell(goal.getRow(), goal.getColumn());
                    goal_cell.setLocation();
                    sole_agent.preliminary_astar.newPath(agent_cell, goal_cell);
                    sole_agent.preliminary_astar.findPath();
                    goal_path_size = sole_agent.preliminary_astar.getPath().size();
//                    System.err.println("ordered goal_path size: " + ordered_goal_path_size + " goal_path_size: " + goal_path_size);

                    if (goal_path_size <= ordered_goal_path_size)
                    {
                        ordered_goals.add(ordered_goals.indexOf(ordered_goal), goal);
                        is_set = true;
                        break inner_for;
                    }
                }
                if (!is_set)
                {
                    ordered_goals.add(goal);
                }
                is_set = false;
//                System.err.println("After adding a goal, it looks: " + ordered_goals.size() + " , " + ordered_goals);
            }

//            System.err.println("ordered goals related, size: " + ordered_goals.size() + " , " +ordered_goals);

            for(Goal goal : ordered_goals) {
                matchingBoxes = new_boxletters.get(Character.toUpperCase(goal.getGoalLetter()));
                matchingAgent = agentcolors.get(matchingBoxes.get(0).getColor()).get(0); //only one color box per agent

                for (Box box : matchingBoxes) {
                    dist1 = Math.abs(goal.getRow() - box.getRow()) + Math.abs(goal.getColumn() - box.getColumn());
                    if (smallest_dist == 0 || dist1 < smallest_dist) {
                        smallest_dist = dist1;
                        matchingBox = box;
                    }
                }

                matchingAgent.tasks.add(new Task(matchingAgent, matchingBox, goal, TaskType.MoveBoxToGoal));
                level.getTasks().add(new Task(matchingAgent, matchingBox, goal, TaskType.MoveBoxToGoal));
                smallest_dist = 0;
            }
        }
        List<Task> temp_tasks = agents.get(0).tasks;
        for (int i = 0; i < temp_tasks.size(); i++) {
            System.err.println("Task for agent 0: " + temp_tasks.get(i));
        }
        for(Agent agent : agents){
            agent.preliminary_build_path = false;
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