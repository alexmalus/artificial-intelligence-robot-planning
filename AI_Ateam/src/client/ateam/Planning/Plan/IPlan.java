package client.ateam.Planning.Plan;

import java.util.ArrayList;
import java.util.HashMap;

public class IPlan extends ArrayList<Action> implements Comparable {
    /*
    find solution
    create list of actions in linked list
    replanning mechanism?
     */

    // The agent who created this plan
    protected Agent creator;

    // Create a new Plan with an agent as owner
    public IPlan(Agent own) {
        super();
        this.creator = own;
    }

    public Agent getCreator() {
        return this.creator;
    }

    @Override
    public int compareTo(Object arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

}
