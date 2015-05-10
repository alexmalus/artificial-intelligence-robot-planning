package client.ateam.Level;

import java.util.List;

/**
 * Created by Lasse on 5/8/15.
 */
public class JointAction {

    private List<Action> actions;

    public JointAction(List<Action> actions){
        this.actions = actions;

        for(Action action : actions){
            action.preconditions();
        }
    }
}
