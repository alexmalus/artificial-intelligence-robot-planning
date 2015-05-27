package client.ateam.Level;

import client.ateam.Literal;
import client.ateam.projectEnum.ActionType;
import client.ateam.projectEnum.Direction;

import java.awt.*;
import java.util.ArrayList;

public class Action {

    public Point targetLocation;

    public boolean preconditions(){

        return true;
    }

    private ActionType type;
    private Direction direction;
    private Direction boxDirection;
    private ArrayList<Literal> effects;

    public ArrayList<Literal> getEffects() {
        return effects;
    }
}