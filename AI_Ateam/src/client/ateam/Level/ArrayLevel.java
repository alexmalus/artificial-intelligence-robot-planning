package client.ateam.Level;

import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.projectEnum.Direction;
import client.ateam.projectEnum.ActionType;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by joh on 5/10/15.
 */
public class ArrayLevel{ //implements ILevel {

    public static int MAX_ROW = 70;
    public static int MAX_COLUMN = 70;
    public int agentRow;
    public int agentCol;
    public boolean[][] walls = new boolean[MAX_ROW][MAX_COLUMN];
    public char[][] boxes = new char[MAX_ROW][MAX_COLUMN];
    public char[][] goals = new char[MAX_ROW][MAX_COLUMN];
    private static int height;
    private static int width;
    public static int[] realMap; //The current parsed map
    private static ArrayList<Agent> agentsArrayList = new ArrayList<Agent>();
    private static ArrayList<Box> boxesArrayList= new ArrayList<Box>();
    private static ArrayList<Goal> goalsArrayList= new ArrayList<Goal>();


    public static void error( String msg ) throws Exception {
        throw new Exception( "GSCError: " + msg );
    }
    /**
     *
     * @return height of level, will not be set before loadLevel
     */
    public static int getHeight(){
        return ArrayLevel.height;
    }
    /**
     *
     * @return width of level, will not be set before loadLevel
     */
    public static int getWidth(){
        return ArrayLevel.width;
    }

    @Override
    public int getAgentID() {
        return 0;
    }

    @Override
    public boolean isNeighbor(int curRow, int curCol,int neighborRow, int neighborCol) {
//        if (curPos >= Level.realMap.length || curPos < 0 || neighborPos < 0 || neighborPos >= Level.realMap.length)
//            return false; //outside bounds of map
        if ((curRow-1 == neighborRow) && (curCol-1 == neighborCol) || (curRow+1 == neighborRow) && (curCol+1 == neighborCol)) // right left neighbor
            return true;
        // if (curPos-Level.getWidth() == neighborPos || curPos+Level.getWidth() == neighborPos) // up down neighbor
        // *************************************** TO DO - Need height and width  **************************************
        //     return true;

        return false;
    }

    @Override
    public boolean isBoxAt(int row, int col) {
        return this.boxes[row][col] > 0;
    }

    @Override
    public boolean isAgentAt(int row, int col) {
        return (row == this.agentRow && col == this.agentCol);
    }

    @Override
    public boolean isFree( int row, int col ) {
        return ( !this.walls[row][col] && this.boxes[row][col] == 0 );
    }

    @Override
    public boolean isGoalCompleted() {
        for ( int row = 1; row < MAX_ROW - 1; row++ ) {
            for ( int col = 1; col < MAX_COLUMN - 1; col++ ) {
                char g = goals[row][col];
                char b = Character.toLowerCase( boxes[row][col] );
                if ( g > 0 && b != g) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean getBoxLetter() {
        return false;
        /*if (Level.isBox(field)) {
            return (char) (((field >> 4) & 0x1F) + 0x41);
        }
        else {
            System.err.println("getBoxLetter: provided field do not contain a box!");
            return '0';
        }*/
    }

    @Override
    public boolean getBoxColor() {
        return false;


        /*char boxLetter = Level.getBoxLetter(field);
        if (boxLetter == '0') return -1;
        return boxColors[boxLetter - 0x41];*/
    }

    @Override
    public boolean getGoalLetter() {
        return false;

    /*
        if (Level.isGoal(field)) {
            return (char) ((field >> 21)+0x61);
        }
        else {
            System.err.println("getGoalLetter: provided field do not contain a goal!");
            return '0';
        }

        return false;*/
    }




//    @Override
//    public int[] loadFromString(String s) {
//
//        Scanner scanner = new Scanner(s);
//        //for()
//
//        return new int[0];
//    }

    public void LoadFromString( BufferedReader serverMessages ) throws Exception {
        Map< Character, String > colors = new HashMap< Character, String >();
        String line, color;

        int agentCol = -1, agentRow = -1;
        int colorLines = 0, levelLines = 0;

        // Read lines specifying colors
        while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
            line = line.replaceAll( "\\s", "" );
            String[] colonSplit = line.split( ":" );
            color = colonSplit[0].trim();

            for ( String id : colonSplit[1].split( "," ) ) {
                colors.put( id.trim().charAt( 0 ), color );
            }
            colorLines++;
        }

        if ( colorLines > 0 ) {
            error( "Box colors not supported" );
        }

        //initialState = new Node( null );

        while ( !line.equals( "" ) ) {
            for ( int i = 0; i < line.length(); i++ ) {
                char chr = line.charAt( i );
                if ( '+' == chr ) { // Walls
                    initialState.walls[levelLines][i] = true;
                } else if ( '0' <= chr && chr <= '9' ) { // Agents
                    if ( agentCol != -1 || agentRow != -1 ) {
                        error( "Not a single agent level" );
                    }
                    initialState.agentRow = levelLines;
                    initialState.agentCol = i;
                } else if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
                    initialState.boxes[levelLines][i] = chr;
                } else if ( 'a' <= chr && chr <= 'z' ) { // Goal cells
                    initialState.goals[levelLines][i] = chr;
                }
            }
            line = serverMessages.readLine();
            levelLines++;
        }
    }

    @Override
    public ArrayList<Agent> getAgents() {
        return agentsArrayList;
    }

    @Override
    public ArrayList<Box> getBoxes() {
        return boxesArrayList;
    }

    @Override
    public ArrayList<Goal> getGoals() {
        return goalsArrayList;
    }

    //and it keeps going as one function relies on another..

//    public static int getRowFromIndex(int index) {return index / ArrayLevel.width;}
//
//    public static int getColumnFromIndex(int index) {
//        return index % ArrayLevel.width;
//    }
//
//    public static int getPosFromPosInDirection(int pos, Direction dir){
//        int position = 0;
//        switch (dir) {
//            case NORTH:
//                position = pos - width;
//                break;
//            case SOUTH:
//                position = pos + width;
//                break;
//            case EAST:
//                position = pos + 1;
//                break;
//            case WEST:
//                position = pos - 1;
//                break;
//
//            default:
//                break;
//        }
//        return 0 <= position && position < realMap.length ? position : -1;
//    }
//
//    public static boolean isActionApplicable(int[] map, Action action, int agentPos, boolean silent) {
//        return ArrayLevel.isActionApplicable(new Map(map,null), action, agentPos, silent);
//    }
//
//    public static boolean isActionApplicable(Map map, Action action, int agentPos, boolean silent){
//        int field;
//        int fieldPos;
//        int box;
//        int agent;
//        switch (action.type()) {
//
//            case MOVE:
//                fieldPos = getPosFromPosInDirection(agentPos, action.direction());
//                field = map.get(fieldPos);
//                if(isEmpty(field)){
//                    agent = map.get(agentPos);
//                    if((agent & 0x4) != 0x4)
//                        break;
//
//                    return true;
//                }
//                break;
//
//            case PULL:
//                fieldPos = getPosFromPosInDirection(agentPos, action.direction());
//                field = map.get(fieldPos);
//                //Box Dir, the direction of the box
//                //agentdirection the direction the agent moves
//                if(isEmpty(field)){
//                    int boxPos = getPosFromPosInDirection(agentPos, action.boxDirection());
//
//                    box = map.get(boxPos);
//                    agent = map.get(agentPos);
//
//                    if (!isBox(box) || !isAgent(agent)){
//                        if(!silent)
//                            System.err.println("PULL: box or agent not on right position - boxpos: "
//                                    + Level.coordsToString(boxPos)
//                                    + " agentpos: " + Level.coordsToString(agentPos));
//                        break;
//                    }
//
//                    if( getBoxColor(box) != getAgentColor(getAgentId(agent))){
//                        if(!silent)
//                            System.err.println("agent and box not same color");
//                        break;
//                    }
//
//                    return true;
//
//                }
//                break;
//            case PUSH:
//                //Box Dir, the direction the box moves
//                //agten dir, the direction of the box
//
//                //get the boc, should be in the agnet direction
//                int boxPos = getPosFromPosInDirection(agentPos, action.direction());
//
//                //Init box and agent
//                box = map.get(boxPos);
//                agent = map.get(agentPos);
//
//                //check if box and agent is what they say they are
//                if (!isBox(box) || !isAgent(agent)){
//                    if(!silent)
//                        System.err.println("PUSH: box or agent not on right position - boxpos: "
//                                + Level.coordsToString(boxPos)
//                                + " agentpos: " + Level.coordsToString(agentPos));
//                    break;
//                }
//                //Check if agent and box is same color
//                if( getBoxColor(box) != getAgentColor(getAgentId(agent))){
//                    if(!silent)
//                        System.err.println("agent and box not same color");
//                    break;
//                }
//
//                fieldPos = getPosFromPosInDirection(boxPos, action.boxDirection());
//                field = map.get(fieldPos);
//
//                //Check if  the field the box is moving to is empty
//                if(!isEmpty(field)){
//                    if(!silent)
//                        System.err.println("try to push box to non empty field");
//                    break;
//                }
//
//                return true;
//
//            case NOOP:
//                return true;
//
//            default:
//                break;
//        }
//
//        return false;
//    }
//
//    public static boolean applyAction(Map map, Action action, int agentPos, boolean silent) {
//        if (!ArrayLevel.isActionApplicable(map,action,agentPos,silent))
//            return false;
//
//        if (action.type() == ActionType.NOOP)
//            return true;
//
//        //If the action is applicable a new map is created and returned
//        int boxPos;
//        int fieldPos = getPosFromPosInDirection(agentPos, action.direction());
//
//        int box;
//        int agent = map.get(agentPos);
//
//        switch (action.type()) {
//            case MOVE:
//
//                //add agent to new field
//                map.set(fieldPos, map.get(fieldPos) | (agent & 0x1FE004));
//                map.set(agentPos, map.get(agentPos) & 0xFFE01FFB);
//                break;
//
//            case PULL:
//                boxPos = getPosFromPosInDirection(agentPos, action.boxDirection());
//                box = map.get(boxPos);
//
//                //addAgent
//                map.set(fieldPos, map.get(fieldPos) | (agent & 0x1FE004));
//                //Remove agent and add box
//                map.set(agentPos, map.get(agentPos) & 0xFFE01FFB);
//                map.set(agentPos, map.get(agentPos) | (box & 0x1FF2));
//                //remove box
//                map.set(boxPos, map.get(boxPos) & 0xFFFFE00D);
//
//                //If we are changing RealMap, update the boxesArrayList
//                if (ArrayLevel.realMap == map.map && map.isRoot()) {
//                    int bId = ArrayLevel.getBoxIdFromPosition(boxPos);
//                    ArrayLevel.boxesArrayList.set(bId, agentPos);
//                }
//                break;
//
//            case PUSH:
//                boxPos = getPosFromPosInDirection(agentPos, action.direction());
//                box = map.get(boxPos);
//
//                fieldPos = getPosFromPosInDirection(boxPos, action.boxDirection());
//                //addBox
//                map.set(fieldPos, map.get(fieldPos) | (box & 0x1FF2));
//                //Remove box and add Agent
//                map.set(boxPos, map.get(boxPos) & 0xFFFFE00D);
//                map.set(boxPos, map.get(boxPos) | (agent & 0x1FE004));
//                //remove Agent
//                map.set(agentPos, map.get(agentPos) & 0xFFE01FFB);
//
//                //If we are changing RealMap, update the boxesArrayList
//                if (ArrayLevel.realMap == map.map && map.isRoot()) {
//                    int bId = ArrayLevel.getBoxIdFromPosition(boxPos);
//                    ArrayLevel.boxesArrayList.set(bId, fieldPos);
//                }
//
//                break;
//
//            default:
//
//                break;
//        }
//        return true;
//    }
}
