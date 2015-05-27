package client.ateam.Level;

import client.ateam.Free;
import client.ateam.projectEnum.ActionType;
import client.ateam.projectEnum.Direction;

import java.awt.*;
import java.util.ArrayList;

public class Action {
    /*
    Legacy class, see Actions folder.
    */
    //TODO: remove legacy class
    public Point targetLocation;

    public boolean preconditions(){

        return true;
    }

    private ActionType type;
    private Direction direction;
    private Direction boxDirection;
    private ArrayList<Free> effects;

    public ArrayList<Free> getEffects() {
        return effects;
    }
}