package client.ateam.Level.Actions;

import client.ateam.Free;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Lasse on 5/27/15.
 */
public interface IAction {

    public Point getTargetLocation();

    public Point getOriginLocation();

    public boolean preconditions();

    public void executeAction();

    public ArrayList<Free> getEffects();

    public String toString();
}
