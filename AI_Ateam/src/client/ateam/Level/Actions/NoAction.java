package client.ateam.Level.Actions;

import client.ateam.Free;
import client.ateam.projectEnum.Direction;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Lasse on 5/27/15.
 */
public class NoAction implements IAction {

    private int agentId;
    private Point currentCell;
    private ArrayList<Free> effects = new ArrayList<Free>();

    public NoAction(int agentId, Point currentCell){
        this.agentId = agentId;
        this.currentCell = currentCell;
    }

    @Override
    public Point getTargetLocation() {
        return currentCell;
    }

    @Override
    public Point getOriginLocation() {
        return currentCell;
    }

    @Override
    public boolean preconditions() {
        return true;
    }

    @Override
    public void executeAction() {
        //do nothing at all (this is intended!)
    }

    @Override
    public ArrayList<Free> getEffects() {
        return effects;
    }

    @Override
    public String toString(){
        return "NoOp";
    }

    private String dirToString(Direction dir)
    {
        if(dir==Direction.NORTH){
            return "N";
        }
        else if(dir==Direction.SOUTH){
            return "S";
        }
        else if(dir==Direction.EAST){
            return "E";
        }
        else{
            return "W";
        }
    }
}
