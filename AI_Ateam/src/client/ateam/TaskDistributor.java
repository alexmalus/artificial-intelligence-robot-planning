package client.ateam;

import client.ateam.Level.ArrayLevel;
import client.ateam.Level.Cell;
import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.projectEnum.Color;
import client.ateam.projectEnum.TaskType;

import java.util.*;

public class TaskDistributor {
    private ArrayLevel level = ArrayLevel.getSingleton();

    void distributeTasks(ArrayList<Agent> agents, ArrayList<Box> boxes, ArrayList<Goal> goals){
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

            valSet = new_boxletters.remove(box.getBoxLetter());
            if (valSet == null){
                valSet = new ArrayList<Box>();
            }
            valSet.add(box);
            new_boxletters.put(box.getBoxLetter(), valSet);
        }

        for(Agent agent:agents){
            agentcolors.get(agent.color).add(agent);
            freeAgents.add(agent);
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
                boolean is_a_match = false; //sees if the agent is free
                for(Agent matching_agent: matchingAgents)
                {
                    for (Agent free_agent : freeAgents)
                    {
                        if (free_agent.id == matching_agent.id)
                        {
                            is_a_match = true;
                        }
                    }
                    if (!is_a_match) //if the agent already has a task assigned to it
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
                for (Box box : matchingBoxes) {
                    for (Agent agent : matchingAgents) { // manhattan distance is used
                        dist1 = Math.abs(agent.row - box.getRow()) + Math.abs(agent.column - box.getColumn());
                        dist2 = Math.abs(goal.getRow() - box.getRow()) + Math.abs(goal.getColumn() - box.getColumn());
                        dist1 += dist2;
                        if (smallest_dist == 0 || dist1 < smallest_dist) {
                            smallest_dist = dist1;
                            matchingAgent = agent;
                            matchingBox = level.getBoxByID(box.getId());
                        }
                    }
                }
                matchingAgent.tasks.add(new Task(matchingAgent, matchingBox.getId(), matchingBox, goal, TaskType.MoveBoxToGoal));
                level.getTasks().add(new Task(matchingAgent, matchingBox.getId(), matchingBox, goal, TaskType.MoveBoxToGoal));
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
            }

            for(Goal goal : ordered_goals) {
                matchingBoxes = new_boxletters.get(Character.toUpperCase(goal.getGoalLetter()));
                matchingAgent = agentcolors.get(matchingBoxes.get(0).getColor()).get(0); //only one color box per agent

                for (Box box : matchingBoxes) {
                    dist1 = Math.abs(goal.getRow() - box.getRow()) + Math.abs(goal.getColumn() - box.getColumn());
                    if (smallest_dist == 0 || dist1 < smallest_dist) {
                        smallest_dist = dist1;
                        matchingBox = level.getBoxByID(box.getId());
                    }
                }

                matchingAgent.tasks.add(new Task(matchingAgent, matchingBox.getId(), matchingBox, goal, TaskType.MoveBoxToGoal));
                level.getTasks().add(new Task(matchingAgent, matchingBox.getId(), matchingBox, goal, TaskType.MoveBoxToGoal));
                smallest_dist = 0;
                matchingBoxes.remove(matchingBox); //remove from the list once you assign it..
                Character goal_letter = Character.toUpperCase(goal.getGoalLetter());
                new_boxletters.put(goal_letter, matchingBoxes);
            }
        }
    }
}