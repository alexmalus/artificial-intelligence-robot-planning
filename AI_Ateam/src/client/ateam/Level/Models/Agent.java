package client.ateam.Level.Models;

import client.ateam.Level.Actions.IAction;
import client.ateam.Task;
import client.ateam.projectEnum.Color;
import client.ateam.Level.ArrayLevel;
import client.ateam.Level.Cell;
import client.ateam.Pathfinder.Astar;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

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

    private Astar astar = new Astar(this);

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
//            System.err.println("My task now is to: " + currentTask.box.toString());
            Cell agentLocation = new Cell(row, column);
//            System.err.println("Temp 1: " + temp1.toString());
            Cell goalLocation = new Cell(currentTask.box.getRow(), currentTask.box.getColumn());
//            System.err.println("Temp 2: " + temp2.toString());

            astar.newPath(agentLocation,goalLocation);
//            System.err.println(astar.getPathSize());
            astar.findPath();
            //find plan (first plan or replanning)
        }
    }

    // Convenience method for canMove(int, int)
    public boolean canMove(Point p)
    {
        return canMove(p.x, p.y);
    }

    // Check to see whether or not this entity can move
    // FIXME
    public boolean canMove(int x, int y)
    {
        // Construct a bounding box for location x, y
        Rectangle me = new Rectangle(x, y);

        // Check top-left corner
        if (!ArrayLevel.getCellFromLocation(me.x, me.y).isOccupied())
            return false;

        // Check top-right corner
        if (!ArrayLevel.getCellFromLocation(me.x + me.width, me.y).isOccupied())
            return false;

        // Check bottom-left corner
        if (!ArrayLevel.getCellFromLocation(me.x, me.y + me.height).isOccupied())
            return false;

        // Check bottom-right corner
        if (!ArrayLevel.getCellFromLocation(me.x + me.width, me.y + me.height).isOccupied())
            return false;

        // Entity can move
        return true;
    }
}
