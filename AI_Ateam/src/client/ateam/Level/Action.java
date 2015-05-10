package client.ateam.Level;

import client.ateam.projectEnum.ActionType;
import client.ateam.projectEnum.Direction;

import java.util.ArrayList;

/**
 *
 */
public class Action {

    public boolean preconditions(){

        return true;
    }

    /**
     * Action type definition
     */
	/*static public enum ActionType {
		MOVE, PUSH, PULL
	}*/

    /**
     * Action direction definition
     */
	/*static public enum ActionDirection {
		NORTH, EAST, SOUTH, WEST
	}*/

    private ActionType type;
    private Direction direction;
    private Direction boxDirection;
    //protected boolean committed = false;
    //protected boolean succeeded = false;
    //protected Plan owner;
    //protected Plan owner;

	/*private final static ActionDirection e = ActionDirection.EAST;
	private final static ActionDirection n = ActionDirection.NORTH;
	private final static ActionDirection w = ActionDirection.WEST;
	private final static ActionDirection s = ActionDirection.SOUTH;*/

    /**
     * Constructor for a NoOp action
     * @param type
     */
    Action(ActionType type) {
        if (type != ActionType.NOOP)
            System.err.println("Action constructor for NoOp did not get a NoOp ActionType");
        this.type = type;
    }

    /**
     * Initialize the action a MOVE action.
     * @param agentDirection
     */
    public Action(Direction agentDirection) {
        this.type = ActionType.MOVE;
        this.direction = agentDirection;
    }

    /**
     * Initialize the action as either a push or pull action. Parameter depend of the type of action.
     * @param type Either PUSH or PULL
     * @param agentDirection The direction the agent moves
     * @param boxDirection Either the direction the box moves (if PUSH action), or if PULL the position of the box relative to the agent.
     */
    public Action(ActionType type, Direction agentDirection, Direction boxDirection) {
        this.type = type;
        if (this.type == ActionType.MOVE) {
            System.err.println("Invalid action initialized! Move action do not have two direction arguments");
        }
        this.direction = agentDirection;
        this.boxDirection = boxDirection;

        //Check for invalid action
        if (this.type == ActionType.PUSH && this.direction == Direction.NORTH && this.boxDirection == Direction.SOUTH
                || this.type == ActionType.PUSH && this.direction == Direction.SOUTH && this.boxDirection == Direction.NORTH)
            System.err.println("Invalid PUSH action initialized. Box cannot move in oppersite direction of the agent!");
    }

    public static Direction opposite(Direction dir) {
        switch (dir) {
            case EAST: return Direction.WEST;
            case WEST: return Direction.EAST;
            case NORTH: return Direction.SOUTH;
            case SOUTH: return Direction.NORTH;
            default: return Direction.NORTH;
        }
    }

    public int newAgentPosition(int agentPosition) {
        if (this.type == ActionType.NOOP)
            return agentPosition;
        int newPos = agentPosition;
        switch (this.direction) {
            case NORTH:
                newPos = agentPosition - BitBoardLevel.getWidth();
                break;
            case SOUTH:
                newPos = agentPosition + BitBoardLevel.getWidth();
                break;
            case EAST:
                newPos = agentPosition + 1;
                break;
            case WEST:
                newPos = agentPosition - 1;
                break;

            default:
                break;
        }
        return newPos;
    }

    /**
     * Give this action a reference to the plan it is part of
     * @param own
     */
    /*public void assign(Plan own) {
        this.owner = own;
    }*/
    /**
     * Return the Plan which this action is part of
     * @return
     */
    /*public Plan getOwner() {
       return this.owner;
    }*/

    /**
     * <p>The the committed property when the action is sent to the server.</p>
     * <strong>NOTE: This method should ONLY be used by the coordinator!</strong>
     */
	/*public void commitAction() {
		this.committed = true;
	}*/

    /**
     * <p>After an action has been committed to the server,
     * use this method to set the return status of the committed action.</p>
     * <strong>NOTE: This method should ONLY be used by the coordinator!</strong>
     * @param a Whether or not the action was successful
     */
	/*public void commitSuccess(boolean success) {
		if (this.committed == true) this.succeeded = success;
		else System.err.println("Cannot set succeeded property on uncommitted action!");
	}*/

    public static String toString(Action a) {
        return a == null ? "NoOp" : a.toString();
    }

    /**
     * Return a string representation of the action,
     * in a format that can be understood by the server
     * @return The action string in the server format
     */
    public String toString() {
        String str = this.type2string(this.type);
        if (this.type == ActionType.NOOP)
            return str;
        else if (this.type == ActionType.MOVE)
            return str+"("+this.direction2String(this.direction)+")";
        else
            return str+"("+this.direction2String(this.direction)+","+this.direction2String(this.boxDirection)+")";
    }

    /**
     * Translate a direction to a string value
     * @param dir the direction
     * @return string representation of the direction
     */
    public String direction2String(Direction dir) {
        switch (dir) {
            case NORTH:
                return "N";
            case EAST:
                return "E";
            case SOUTH:
                return "S";
            case WEST:
                return "W";
            default:
                return "UNKOWN-DIRECTION";
        }
    }

    /**
     * Translate a action type into a string
     * @param typ the action type
     * @return the string representation of the type
     */
    public String type2string(ActionType typ) {
        switch (typ) {
            case MOVE:
                return "Move";
            case PUSH:
                return "Push";
            case PULL:
                return "Pull";
            case NOOP:
                return "NoOp";
            default:
                return "UNKNOWN-ACTION";
        }
    }

    public int getOldBoxLocation(int agentPosition) {
        switch (this.type) {
            case PUSH:
                return BitBoardLevel.getPosFromPosInDirection(agentPosition, direction);
            case PULL:
                return BitBoardLevel.getPosFromPosInDirection(agentPosition, boxDirection);
            default: return 0;
        }
    }

    public int getBoxLocation(int agentPosition) {
        switch (this.type) {
            case MOVE:
                return 0;
            case PUSH:
                switch (this.direction) {
                    //lav om til kun at bruge agentposition.
                    case WEST:
                        switch(this.boxDirection) {
                            case WEST:
                                return agentPosition-2;
                            case NORTH:
                                return (agentPosition-BitBoardLevel.getWidth())-1;
                            case SOUTH:
                                return (agentPosition+BitBoardLevel.getWidth())-1;
                        }
                    case EAST:
                        switch(this.boxDirection) {
                            case EAST:
                                return agentPosition+2;
                            case NORTH:
                                return (agentPosition-BitBoardLevel.getWidth())+1;
                            case SOUTH:
                                return agentPosition+BitBoardLevel.getWidth()+1;
                        }
                    case NORTH:
                        switch(this.boxDirection) {
                            case EAST:
                                return (agentPosition-BitBoardLevel.getWidth())+1;
                            case WEST:
                                return (agentPosition-BitBoardLevel.getWidth())-1;
                            case NORTH:
                                return agentPosition-2*BitBoardLevel.getWidth();
                        }
                    case SOUTH:
                        switch(this.boxDirection) {
                            case EAST:
                                return agentPosition+BitBoardLevel.getWidth()+1;
                            case WEST:
                                return (agentPosition+BitBoardLevel.getWidth())-1;
                            case SOUTH:
                                return agentPosition+2*BitBoardLevel.getWidth();
                        }
                }
            case PULL:
                return agentPosition;

            default:
                return 0;
        }

    }

	/*private int getPosFromPosInDirection(int pos, ActionDirection dir){
		int position = -1;
		switch (dir) {
		case NORTH:
			position = pos - Level.getWidth();
			break;
		case SOUTH:
			position = pos + Level.getWidth();
			break;
		case EAST:
			position = pos + 1;
			break;
		case WEST:
			position = pos - 1;
			break;

		default:
			break;
		}
		return position;
	}*/


    public ArrayList<Integer> addToArraylist(int curPosI){
        ArrayList<Integer> positions = new ArrayList<Integer>();
        //Add the current position of the agent
        positions.add(curPosI);
        switch (this.type) {
            //Moved adds the new position of the agent
            case MOVE:
                positions.add(this.newAgentPosition(curPosI));
                break;
            //PULL Adds the new agent position and the start position of the box
            case PULL:
                positions.add(this.newAgentPosition(curPosI));
                positions.add(BitBoardLevel.getPosFromPosInDirection(curPosI, this.boxDirection));
                break;
            //PUSH Adds the new position of the agent(starting position of box) and the new position of the box
            case PUSH:
                positions.add(this.newAgentPosition(curPosI));
                positions.add(this.getBoxLocation(curPosI));
                break;
            default:
                break;
        }
        return positions;
    }

    /**
     * Checks if to actions for two agents will conflict
     * @param curPosI
     * @param action
     * @param curPosJ
     * @param action2
     * @return
     */
    public static boolean conflict(int curPosI, Action action, int curPosJ,
                                   Action action2) {

        ArrayList<Integer> positions = new ArrayList<Integer>();

        //Fills the arrayList wit all the potential places the agent and its box are and will be
        positions.addAll(action.addToArraylist(curPosI));
        positions.addAll(action2.addToArraylist(curPosJ));

        //checks if any off these places are the same
        for(int i=0; i<positions.size();i++){
            for(int j=i+1;j<positions.size();j++){
                int p1 = positions.get(i);
                int p2 = positions.get(j);
                if(p1 == p2)
                    return true;
            }
        }
        return false;
    }

    /**
     * Determine if there is a conflict between this Action-object and another
     * given action object.
     * @param curPosI The position this action is performed from.
     * @param otherPos The position the other action is performed from.
     * @param other The other action
     * @return True if there is a conflict between this action and the other given
     */
    public boolean conflicts(int curPosI, int otherPos, Action other) {
        if (other == null)
            return false;
        return Action.conflict(curPosI, this, otherPos, other);
    }

    /**
     * Determine if there is a conflict between this Action-object and any action
     * from a given list of other actions.
     * @param curPosI The position this action is performed from.
     * @param otherPositions The positions the other actions are performed from.
     * @param otherActions The other actions
     * @return True if there is a conflict between this action and any of the other given actions
     */
    public boolean conflicts(int curPosI, ArrayList<Integer> otherPositions, ArrayList<Action> otherActions) {
        for (int i = 0; i < otherPositions.size(); i++) {
            if (this.conflicts(curPosI, otherPositions.get(i), otherActions.get(i)))
                return true;
        }

        return false;
    }

    /**
     * From a given list of actions and agent positions, calculate the new positions
     * after performing the matching actions
     * @param curPositions Current positions of the agents, such that curPositions[i] matches actions[i]
     * @param actions Action to perform for agent i
     * @return Updated list of positions for all agents
     */
    public static ArrayList<Integer> getUpdatedPositions(ArrayList<Integer> curPositions, ArrayList<Action> actions) {
        ArrayList<Integer> newPositions = new ArrayList<Integer>(10);

        for (int i = 0; i < curPositions.size(); i++) {
            Action a = actions.get(i);
            int pos = curPositions.get(i);
            if (a != null && a.type() != ActionType.NOOP)
                pos = BitBoardLevel.getPosFromPosInDirection(pos, a.direction());
            newPositions.add(pos);
        }

        return newPositions;
    }

    /**
     * Get the target position where a agent end up if MOVE action,
     * If PUSH action target is where box is going to be,
     * If PULL aciton agent is where the agent is going to be.
     * @param currentPos
     * @return
     */
    public int getTargetPositionFromPosition(int currentPos) {
        switch (this.type) {
            case MOVE:
            case PULL:
                return BitBoardLevel.getPosFromPosInDirection(currentPos, this.direction);
            case PUSH:
                return BitBoardLevel.getPosFromPosInDirection(
                        BitBoardLevel.getPosFromPosInDirection(currentPos, direction), this.boxDirection);
            case NOOP:
                return currentPos;
            default:
                return -1;
        }


    }

    public ActionType type() {
        return this.type;
    }

    public Direction direction() {
        return this.direction;
    }

    public Direction boxDirection() {
        return this.boxDirection;
    }

    /*
     * private ActionType type;
	private ActionDirection direction;
	private ActionDirection boxDirection;
	*/
}
