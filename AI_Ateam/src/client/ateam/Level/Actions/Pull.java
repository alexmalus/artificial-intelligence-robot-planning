package client.ateam.Level.Actions;

import client.ateam.Free;
import client.ateam.Level.ArrayLevel;
import client.ateam.projectEnum.Direction;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Lasse on 5/27/15.
 */
public class Pull implements IAction {
    private int agentId;
    private char boxLetter;
    private Direction dirAgent;
    private Direction dirBox;
    private Point currentCell;
    private Point boxCell;
    private Point tarCell;

    private ArrayList<Free> effects = new ArrayList<Free>();
    private ArrayLevel level = ArrayLevel.getSingleton();

    public Pull(int agentId, char boxLetter, Point currentCell, Point tarCell, Point boxCell){
        this.agentId = agentId;
        this.boxLetter = boxLetter;
        this.currentCell = currentCell;
        this.boxCell = boxCell;
        this.tarCell = tarCell;
        this.dirAgent = this.calculateDirection(currentCell,tarCell);
        this.dirBox = this.calculateDirection(boxCell,currentCell);

        effects.add(new Free(boxCell,true,agentId));
        effects.add(new Free(tarCell,false,agentId));

    }
    //TODO: naming might be bad here, adding extra methods may be needed.
    @Override
    public Point getTargetLocation() {
        return tarCell;
    }

    @Override
    public Direction calculateDirection(Point sourceCell, Point tarCell) {
        if(tarCell.y-sourceCell.y == 1)
        {
            return Direction.NORTH;
        }
        else if(tarCell.y-sourceCell.y == -1){
            return Direction.SOUTH;
        }
        else if(tarCell.x-sourceCell.x == 1){
            return Direction.WEST;
        }
        else if(tarCell.x-sourceCell.x == -1){
            return Direction.EAST;
        }
        else{
            System.err.println("Coordinates do not generate direction");
            return Direction.WEST;
        }
    }

    //TODO: again, technically this one should return the box location as origin....
    @Override
    public Point getOriginLocation() {
        return currentCell;
    }

    @Override
    public boolean preconditions() {
        return (level.isNeighbor(currentCell.y,currentCell.x,boxCell.y,boxCell.x) && level.isNeighbor(currentCell.y,currentCell.x,tarCell.y,tarCell.x) && level.isFree(tarCell.y,tarCell.x));
    }

    @Override
    public void executeAction() {
        this.level.executePullAction(this.agentId,this.boxLetter,this.currentCell,this.boxCell,this.tarCell);
    }

    @Override
    public ArrayList<Free> getEffects() {
        return effects;
    }

    @Override
    public String toString(){
        return "Pull("+dirToString(dirAgent)+","+dirToString(dirBox)+")";
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
