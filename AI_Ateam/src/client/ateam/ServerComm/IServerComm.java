package client.ateam.ServerComm;

import client.ateam.Level.Action;

import java.util.ArrayList;

/**
 * convert joint actions to string format
 */
public interface IServerComm {

    void sendJointAction(ArrayList<Action> actions);
}
