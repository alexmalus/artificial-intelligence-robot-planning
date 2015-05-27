package client.ateam;

import java.awt.*;

/**
 * Created by Lasse on 5/21/15.
 */
public class Free {

    public Free(Point location, boolean truthvalue, int agentID)
    {
        this.location = location;
        this.truthvalue = truthvalue;
        this.agentID = agentID;
    }

    public Point location;
    public boolean truthvalue;
    public int agentID;
}
