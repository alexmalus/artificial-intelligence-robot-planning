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



    void distributeTasks(ArrayList<Agent> agents, ArrayList<Box> boxes, ArrayList<Goal> goals, HashMap<String,Character> colors){
        Character letter;
        Box selectedBox;
        Agent selectedAgent;
        List<Box> matchingBoxes;
        List<Agent> matchingAgents;

        for(Goal goal : goals) {
            //find box to match goal along with agent
            // filter box by letter
            matchingBoxes = boxes.stream().filter(x -> x.letter == goal.letter).collect(Collectors.toList());

            //TODO: more reliable selecting of most relevant box (distance based?) (assuming several boxes are applicable)
            selectedBox = this.selectBox(matchingBoxes);

            //filter agents by color
            matchingAgents = agents.stream().filter(x -> x.color == selectedBox.color).collect(Collectors.toList());

            //TODO: selecting based on distance to box
            selectedAgent = this.selectAgent(matchingAgents);

            // create class Task, to match Agent with box/goal
            selectedAgent.tasks.add(new Task(selectedAgent.id,selectedBox,goal));
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
