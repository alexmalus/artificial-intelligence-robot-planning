package client.ateam.Level.Actions;

import client.ateam.Level.ArrayLevel;
import client.ateam.Free;
import client.ateam.projectEnum.Direction;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Lasse on 5/27/15.
 */
public class Move implements IAction {

    private int agentId;
    private Direction dirAgent;
    private Point currentCell;
    private Point tarCell;

    private ArrayList<Free> effects = new ArrayList<Free>();
    private ArrayLevel level = ArrayLevel.getSingleton();

    public Move(int agentId, Direction dirAgent, Point currentCell, Point tarCell )
    {
        this.agentId = agentId;
        this.dirAgent = dirAgent;
        this.currentCell = currentCell;
        this.tarCell = tarCell;
        effects.add(new Free(currentCell,true,agentId));
        effects.add(new Free(tarCell,false,agentId));

    }
    @Override
    public Point getTargetLocation() {
        return this.tarCell;
    }

    @Override
    public Point getOriginLocation() {
        return this.currentCell;
    }


    @Override
    public boolean preconditions() {
        return (level.isFree(this.tarCell.y,this.tarCell.x) && level.isNeighbor(currentCell.y,currentCell.x,tarCell.y,tarCell.x));
    }

    @Override
    public void executeAction() {
        //TODO: this stuff, yo.
    }

    @Override
    public ArrayList<Free> getEffects() {
        return effects;
    }

    @Override
    public String toString(){
        return "Move("+dirToString(dirAgent)+")";
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
