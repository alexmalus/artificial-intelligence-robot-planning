package client.ateam.Level.Models;

import client.ateam.Level.Action;
import client.ateam.Level.Actions.IAction;
import client.ateam.Task;
import client.ateam.projectEnum.Color;

import java.util.ArrayList;
import java.util.List;

//TODO: might be an idea to merge Agent and Planning classes since each Agent plans its own plan.

public class Agent {
    public int id;
    public Color color;
    public int row;
    public int column;
    //TODO: list of tasks could be priority queue
    public List<Task> tasks = new ArrayList<Task>();
    public Task currentTask;
    private IAction currentAction;
    public List<IAction> actionList = new ArrayList<IAction>();

    public Agent(int id, Color color, int row, int column){
        this.color = color;
        this.id = id;
        this.row = row;
        this.column = column;
    }

    /*
    Gets next action in list and loads it to the current action
     */
    /*public void NextAction(){
        if(actionList.isEmpty())
        {
            //do planning if list is empty
            //but this should never occur to
        }

    }*/
    /*
    Getter for the currentaction
     */
    public IAction getCurrentAction(){
        if(currentAction == null)
        {
            //TODO: empty list checks
            if(actionList.isEmpty()) {

            }
            else{
                currentAction = actionList.remove(0);
            }
        }
        return currentAction;
    }

    /*
    Execute current action
     */
    public void executeCurrentAction() {

        //do execute
        currentAction.executeAction();


        //check for goal
        if(actionList.isEmpty())
        {
            planning();
        }
        else{
            currentAction = actionList.remove(0);
        }
    }
    public void planning()
    {
        // clean remnants from last plan
        currentAction = null;
        actionList.clear();

        if (currentTask == null) {
            if(tasks.isEmpty()) {
                return;
            }
            else {
                currentTask = tasks.remove(0);
            }
        }
        if(currentTask.isTaskCompleted())
        {
            //TODO: empty list checks
            if(tasks.isEmpty())
            {
                //idle or help others
            }
            else {
                currentTask = tasks.remove(0);
                // find plan (plan new task)

            }
        }
        else
        {
            //find plan (first plan or replanning)
        }
    }



}
