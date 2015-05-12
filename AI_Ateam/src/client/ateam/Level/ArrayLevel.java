package client.ateam.Level;

import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;

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
}
