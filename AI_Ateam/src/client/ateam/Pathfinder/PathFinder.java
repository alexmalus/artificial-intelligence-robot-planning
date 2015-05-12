package client.ateam.Pathfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import client.ateam.Pathfinder.ExplorationTree.Node;
import client.ateam.Pathfinder.AstarProblem;
import client.ateam.Level.Map;
import client.ateam.Task;
import client.ateam.Planning.Plan.Iplan;
import client.ateam.projectEnum.Direction;
import client.ateam.projectEnum.ActionType;
import client.ateam.Level.Action;

/*
* This class is supposed to find the path from A to B for an agent
*
*  This class can possibly contain
*  Astar
*  ShortestPath
*  Some heuristics
*
* */

public class PathFinder extends AstarProblem<Action,State>{			//
    HashMap<State, HashMap<Action,State>> successor;
    Map map;
    private int curPlayerLocation;
    private int targetPlayerLocation;
    private int targetBoxLocation;
    private int curBoxLocation;
    private int goalLocation;
    protected boolean shouldMoveBoxesBack = true;
    protected int moveCost = 1; // set from task
    protected int pushCost = 1; // set from task
    protected int pullCost = 1; // set from task
    protected Task parentTask;
    protected int agentColor = 0;
    private ArrayList<Plan> otherPlans;
    private ArrayList<Integer> otherPositions;

    public PathFinder(Map m, int curPlayerLocation, int curBoxLocation, int targetPlayerLocation, int targetBoxLocation,
                        int goalLocation, Task task, ArrayList<Plan> otherPlans, ArrayList<Integer> otherPositions){
        this.map = m;
        this.curPlayerLocation = curPlayerLocation;
        this.curBoxLocation = curBoxLocation;
        this.targetPlayerLocation = targetPlayerLocation;
        this.targetBoxLocation = targetBoxLocation;
        this.goalLocation = goalLocation;
        this.parentTask = task;
        this.otherPlans = otherPlans;
        this.otherPositions = otherPositions;
    }

    public boolean isFree(int field){
        return (field & 7)==0;
    }
    final static int  colorFilter = 15<<9;
    public boolean boxApplicable(int playerfield, int boxfield){
        return (boxfield & 2) == 2 && (playerfield & (colorFilter)) == (boxfield & (colorFilter));
    }

    private static List<Action> actions = new LinkedList<Action>();

    public static List<Action> getActions(){
        if(actions.size()==0){
            //move actions
            for(Direction d: Direction.values()){
                actions.add(new Action(d));
            }
            //push actions
            for(Direction d: Direction.values()){
                for(Direction e: Direction.values()){
                    if(Action.opposite(d) != e )
                        actions.add(new Action(ActionType.PUSH,d,e));
                }
            }
            //pull actions
            for(Direction d: Direction.values()){
                for(Direction e: Direction.values()){
                    if(d !=e )
                        actions.add(new Action(ActionType.PULL,d,e));
                }
            }
            actions.add(new Action(ActionType.NOOP));
        }
        return actions;
    }
    private static List<Action> moveActions = new LinkedList<Action>();

    public static List<Action> getMoveActions(){
        if(moveActions.size()==0){
            for(ActionDirection d: ActionDirection.values()){
                moveActions.add(new Action(d));
            }
        }
        return moveActions;
    }

    public TreeSet<Node<Action,State>> expand(Node<Action,State> n){//successor function for
        TreeSet<Node<Action,State>> successors = new TreeSet<Node<Action,State>>();

        ArrayList<Action> otherActions = new ArrayList<Action>();
        for (int i = 0; i < otherPlans.size(); i++) {
            Plan p = otherPlans.get(i);
            Action a = n.state.time < p.size() ? p.get(n.state.time) : null;
            otherActions.add(a);
        }

        for(Action a : AstarProblem.getActions()){
            if (a.conflicts(n.state.playerLocation, n.state.otherAgentPositions, otherActions))
                continue;

            State newstate = n.state.applyAction(a);

            if (newstate != null) {
                boolean shouldContinue = false;
                for (int i = 0; i < otherActions.size(); i++) {
                    Action newA = otherActions.get(i);
                    if (newA != null) {
                        if (!Level.applyAction(newstate.map, newA, n.state.otherAgentPositions.get(i), true)) {
                            shouldContinue = true;
                            break;
                        }
                    }
                }

                if (shouldContinue) continue;

                newstate.otherAgentPositions = Action.getUpdatedPositions(n.state.otherAgentPositions, otherActions);
                successors.add(new Node<Action,State>(a,newstate,n));
            }
        }

        return successors;
    }

    public void setInit(){
        Map initMap = new Map(map.map,map);
        this.init= new Node<Action,State>(null, new State(initMap,curPlayerLocation,curBoxLocation), null);
        this.init.state.otherAgentPositions = this.otherPositions;
    }


    public double cost(astar.Node<Action,State> n, astar.Node<Action,State> suc){
        //If a parent task is defined, we delegate the cost handling to the task
        if (this.parentTask != null)
            return (double) this.parentTask.getCost(n.state.playerLocation, n.state.map, suc.action);

        int stepCost=0;
        int boxPos = -1;
        switch (suc.action.type()) {
            case MOVE:
                stepCost += this.moveCost;
                break;
            case PUSH:

                boxPos = Level.getPosFromPosInDirection(n.state.playerLocation, suc.action.direction());
                //Are we moving a box on its goal
                if (this.boxIsOnGoal(n.state.map.get(boxPos)))
                    stepCost += 20;
                else
                    stepCost += this.pushCost;
                break;
            case PULL:
                boxPos = Level.getPosFromPosInDirection(n.state.playerLocation, suc.action.boxDirection());
                //Are we moving a box on its goal
                if (this.boxIsOnGoal(n.state.map.get(boxPos))) {
                    stepCost += 20;
                }
                else
                    stepCost += this.pullCost;
                break;
            default:
                break;
        }

        return stepCost;
    }

    /**
     * Check if a field is a box on its goal */
    protected boolean boxIsOnGoal(int field) {
        if (Level.isGoal(field)
                && Character.toLowerCase(Level.getBoxLetter(field))
                == Character.toLowerCase(Level.getGoalLetter(field))
                ) {
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean isGoal(Node<Action,State> n){
        //If we have a parent task, delegate the goal check to that
        if (this.parentTask != null)
            return this.parentTask.isGoal(n, this.shouldMoveBoxesBack, this.agentColor);

        //test the case where we have an agentTask
        if(targetBoxLocation==-1){
            if(n.state.playerLocation==targetPlayerLocation)
                return true;
        }
        //if we have a box to goal task or
        else if(goalLocation !=-1){
            if (n.state.boxPosition==targetBoxLocation && Level.getBoxLetter(n.state.map.get(n.state.boxPosition))==Character.toUpperCase(Level.getGoalLetter(n.state.map.get(goalLocation))))
            {return true;}
        }

        //if we have a box to a field different than a goal
        else if(targetBoxLocation!=-1){
            if(n.state.boxPosition==targetBoxLocation ){
                return true;
            }
        }

        return false;

    }
}
