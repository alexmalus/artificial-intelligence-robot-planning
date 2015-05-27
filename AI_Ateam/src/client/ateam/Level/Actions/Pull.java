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

    public Pull(int agentId, Direction dirAgent, Direction dirBox, char boxLetter, Point currentCell, Point tarCell, Point boxCell){
        this.agentId = agentId;
        this.dirAgent = dirAgent;
        this.dirBox = dirBox;
        this.boxLetter = boxLetter;
        this.currentCell = currentCell;
        this.boxCell = boxCell;
        this.tarCell = tarCell;

        effects.add(new Free(boxCell,true,agentId));
        effects.add(new Free(tarCell,false,agentId));
    }
    //TODO: naming might be bad here, adding extra methods may be needed.
    @Override
    public Point getTargetLocation() {
        return tarCell;
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
        //TODO: this needs to be done
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
