package client.ateam.Level.Actions;

import client.ateam.Free;
import client.ateam.Level.ArrayLevel;
import client.ateam.projectEnum.Direction;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Lasse on 5/27/15.
 */
public class Push implements IAction {
    //TODO: Direction can be found from cells and thus can be calculated inside the action instead of passing it as a parameter
    private int agentId;
    private char boxLetter;
    private Direction dirAgent;
    private Direction dirBox;
    private Point currentCell;
    private Point boxCell;
    private Point boxTarCell;

    private ArrayList<Free> effects = new ArrayList<Free>();
    private ArrayLevel level = ArrayLevel.getSingleton();

    public Push(int agentId, Direction dirAgent, Direction dirBox, char boxLetter, Point currentCell, Point boxCell, Point boxTarCell){
        this.agentId = agentId;
        this.dirAgent = dirAgent;
        this.dirBox = dirBox;
        this.boxLetter = boxLetter;
        this.currentCell = currentCell;
        this.boxCell = boxCell;
        this.boxTarCell = boxTarCell;

        effects.add(new Free(currentCell,true,agentId));
        effects.add(new Free(boxTarCell,false,agentId));
    }

    @Override
    public Point getTargetLocation() {
        return boxTarCell;
    }

    @Override
    public Point getOriginLocation() {
        return currentCell;
    }

    @Override
    public boolean preconditions() {
        return (level.isNeighbor(currentCell.y,currentCell.x,boxCell.y,boxCell.x) && level.isNeighbor(boxCell.y, boxCell.x,boxTarCell.y,boxTarCell.x) && level.isFree(boxTarCell.y,boxTarCell.x));
    }

    @Override
    public void executeAction() {
        this.level.executePushAction(this.agentId,this.boxLetter,this.currentCell,this.boxCell,this.boxTarCell);
    }

    @Override
    public ArrayList<Free> getEffects() {
        return effects;
    }

    @Override
    public String toString(){
        return "Push("+dirToString(dirAgent)+","+dirToString(dirBox)+")";
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