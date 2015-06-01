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
    private int agentId;
    private char boxLetter;
    private Direction dirAgent;
    private Direction dirBox;
    private Point currentCell;
    private Point boxCell;
    private Point boxTarCell;

    private ArrayList<Free> effects = new ArrayList<Free>();
    private ArrayLevel level = ArrayLevel.getSingleton();

    public Push(int agentId, char boxLetter, Point currentCell, Point boxCell, Point boxTarCell){
        this.agentId = agentId;
        this.dirAgent = this.calculateDirection(currentCell,boxCell);
        this.dirBox = this.calculateDirection(boxCell,boxTarCell);
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
    public Direction calculateDirection(Point sourceCell, Point tarCell) {
        if(tarCell.y-sourceCell.y == 1)
        {
            return Direction.EAST;
        }
        else if(tarCell.y-sourceCell.y == -1){
            return Direction.WEST;
        }
        else if(tarCell.x-sourceCell.x == -1){
            return Direction.NORTH;
        }
        else if(tarCell.x-sourceCell.x == 1){
            return Direction.SOUTH;
        }
        else{
            System.err.println("Coordinates do not generate direction");
            return Direction.WEST;
        }
    }
    @Override
    public boolean preconditions() {
        System.err.println("Push preconditions");
        return (level.isNeighbor(currentCell.x,currentCell.y,boxCell.x,boxCell.y) && level.isNeighbor(boxCell.x, boxCell.y,boxTarCell.x,boxTarCell.y) && level.isFree(boxTarCell.x,boxTarCell.y));
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
