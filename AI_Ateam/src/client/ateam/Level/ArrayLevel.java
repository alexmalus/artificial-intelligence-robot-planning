package client.ateam.Level;

import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.Task;
import client.ateam.projectEnum.Color;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by joh on 5/10/15.
 */
public class ArrayLevel implements ILevel {

    private BufferedReader serverMessages = new BufferedReader( new InputStreamReader( System.in ) );

    public static int MAX_ROW = 70;
    public static int MAX_COLUMN = 70;

    public int agentRow;
    public int agentCol;

    public boolean[][] walls = new boolean[MAX_ROW][MAX_COLUMN];
    public char[][] boxes = new char[MAX_ROW][MAX_COLUMN];
    public char[][] goals = new char[MAX_ROW][MAX_COLUMN];

    private static int height;
    private static int width;
//    public static int[] realMap; //The current parsed map
    // Our list of each cell in this ArrayLevel
    //TODO: commented out line
    private static HashMap<Point, Cell> cells = null;
    // Minimum and maximum X and Y coordinates
    private static int minX, minY, maxX, maxY;
    private static int cellSize;
    // The rectangular clip for the gameplay region
    private static Rectangle clip = null;

    private static ArrayList<Agent> agentsArrayList = new ArrayList<Agent>();
    private static ArrayList<Box> boxesArrayList= new ArrayList<Box>();
    private static ArrayList<Goal> goalsArrayList= new ArrayList<Goal>();
    private static ArrayList<Task> taskList = new ArrayList<>();
    private static ArrayLevel level;

    public static ArrayLevel getSingleton(){
        if(level == null){
            level = new ArrayLevel();
        }
        return level;
    }

    public static void error( String msg ) throws Exception {
        throw new Exception( "GSCError: " + msg );
    }

    public static int getHeight(){
        return ArrayLevel.height;
    }

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
        // right left neighbor
        if ((curRow-1 == neighborRow) && (curCol-1 == neighborCol) || (curRow+1 == neighborRow) && (curCol+1 == neighborCol))
            return true;
        // up down neighbor
        if ((curRow-ArrayLevel.getWidth() == neighborRow) && (curCol-ArrayLevel.getWidth() == neighborCol) || (curRow+ArrayLevel.getWidth() == neighborRow) && (curCol+ArrayLevel.getWidth() == neighborCol))
            return true;

        return false;
    }

    //TODO: will return true on agents as well
    @Override
    public boolean isBoxAt(int row, int col) {
        return this.boxes[row][col] > 0;
    }

    //TODO: this checks only the agentrow and agentcol located on the ArrayLevel class??
    @Override
    public boolean isAgentAt(int row, int col) {
        return (row == this.agentRow && col == this.agentCol);
    }

    //TODO: this does not check for agents
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
    public char getBoxLetter(int row, int col) {
        for(int i = 0; i == boxesArrayList.size(); i++)
        {
            Box tempbox = boxesArrayList.get(i);
            if((tempbox.getRow() == row) && (tempbox.getColumn() == col)){
                return tempbox.getBoxLetter();
            }

        }
        System.err.println("ERROR: could not find any boxletters");
        return 0;
    }

    @Override
    public Color getBoxColor(int row, int col) {
        for(int i = 0; i == boxesArrayList.size(); i++)
        {
            Box tempbox = boxesArrayList.get(i);
            if((tempbox.getRow() == row) && (tempbox.getColumn() == col)){
                return tempbox.getColor();
            }

        }
        System.err.println("ERROR: could not find any box colors");
        return null;
    }

    @Override
    public char getGoalLetter(int row, int col) {
        for(int i = 0; i == goalsArrayList.size(); i++)
        {
            Goal tempGoal = goalsArrayList.get(i);
            if((tempGoal.getRow() == row) && (tempGoal.getColumn() == col)){
                return tempGoal.getGoalLetter();
            }

        }
        System.err.println("ERROR: could not find any Goal letters");
        return 0;

    }

    public void ReadMap() throws Exception {
        Map< Character, Color > colors = new HashMap< Character, Color >();
        String line;
        Color color;

        int agentCol = -1, agentRow = -1;
        int colorLines = 0, levelLines = 0;

        // Read lines specifying colors
        while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
            line = line.replaceAll( "\\s", "" );
            String[] colonSplit = line.split( ":" );
            //color = colonSplit[0].trim();
            switch(colonSplit[0].trim()){
                case("red"):
                    color = Color.RED;
                    break;
                case("green"):
                    color = Color.GREEN;
                    break;
                case("cyan"):
                    color = Color.CYAN;
                    break;
                case("blue"):
                    color = Color.BLUE;
                    break;
                case("magenta"):
                    color = Color.MAGENTA;
                    break;
                case("orange"):
                    color = Color.ORANGE;
                    break;
                case("pink"):
                    color = Color.PINK;
                    break;
                case("yellow"):
                    color = Color.YELLOW;
                    break;
                default:
                    color = Color.BLUE;
                    break;
            }

            for ( String id : colonSplit[1].split( "," ) ) {
                colors.put( id.trim().charAt( 0 ), color );
            }
            colorLines++;
        }

        if ( colorLines > 0 ) {
            //which means that for the moment we are only considering SA cases
            error( "Box colors not supported" );
        }

        //initialState = new Node( null );

        int[] widths = new int[70];
        for (int i=0; i<70; ++i)
        {
            widths[i] = 0;
        }

        //it means we are dealing with SA case where we only have on the .lvl file the map and not the declarations red: 0, etc etc
        //so just add default case for one agent, the color blue
        //for the moment we're considering the case for the SA to just have one box in the map called A
        if (colors.isEmpty())
        {
            colors.put('0', Color.BLUE);
            colors.put('A', Color.BLUE);
        }

        while ( !line.equals( "" ) ) {
            for ( int i = 0; i < line.length(); i++ ) {
                char chr = line.charAt( i );
                if ( '+' == chr ) { // Walls
                    walls[levelLines][i] = true;
                } else if ( '0' <= chr && chr <= '9' ) { // Agents
                    if ( agentCol != -1 || agentRow != -1 ) {
                        error( "Not a single agent level" );
                    }
                    agentsArrayList.add(new Agent((int)chr,colors.get(chr),levelLines,i));
                    //initialState.agentRow = levelLines;
                    //initialState.agentCol = i;
                } else if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
                    boxesArrayList.add(new Box(chr,colors.get(chr),levelLines,i));
                    //initialState.boxes[levelLines][i] = chr;
                } else if ( 'a' <= chr && chr <= 'z' ) { // Goal cells
                    goalsArrayList.add(new Goal(chr,levelLines,i));
                    //initialState.goals[levelLines][i] = chr;
                }
                widths[height]++;
            }
            line = serverMessages.readLine();
            levelLines++;
            height++;
        }
        for(int i=0; i<70; ++i)
        {
            if (widths[i] > width)
            {
                width = widths[i];
            }
        }
        //TODO: commented out section

        // Create our cellList
        cells = new HashMap<Point, Cell>(width * height);

        // Set the minX and minY coordinates
        minX = (height * cellSize) / 2;
        minY = (width * cellSize) / 2;

        // Set the maxX and maxY coordinates based on arrayLevel and cell size
        maxX = (minX + (height * cellSize));
        maxY = (minY + (width * cellSize));

        // Set clip rectangle
        clip = new Rectangle(getMinX(), getMinY(), (height * getCellSize()) + 1, (width * getCellSize()) + 1);

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

    @Override
    public ArrayList<Task> getTasks() {return taskList;}

    private void moveAgentTo(int agentId, Point currentCell, Point tarCell){
        //change agentrow and agentcol for agent
        for(Agent agent : this.getAgents()){
            if(agent.id == agentId)
            {
                agent.row = tarCell.y;
                agent.column = tarCell.x;
                break;
            }
        }
        //TODO:change agent location on agent array

    }
    private void moveBoxTo(char boxLetter, Point boxCell, Point boxTarCell){
        for(Box box : this.getBoxes()){
            if(box.getBoxLetter()==boxLetter){
                box.setColumn(boxTarCell.x);
                box.setRow(boxTarCell.y);
                break;
            }
        }
        //TODO:change box location on box array
    }

    @Override
    public void executeMoveAction(int agentId, Point currentCell, Point tarCell) {
        //TODO: agentlocation on ArrayLevel is missing (no array for it)
        if(this.isNeighbor(currentCell.y,currentCell.x,tarCell.y,tarCell.x) && isFree(tarCell.y,tarCell.x)){
            this.moveAgentTo(agentId,currentCell,tarCell);
        }
    }

    @Override
    public void executePushAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point boxTarCell) {
        //TODO: preconditions in if loop
        if(true) {
            //change boxrow and boxcol for box
            //move box on level
            this.moveBoxTo(boxLetter, boxCell, boxTarCell);
            //change agentrow and agentcol for agent
            // move agent on level
            this.moveAgentTo(agentId, currentCell, boxCell);
        }
    }

    @Override
    public void executePullAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point tarCell) {
        //TODO: preconditions in if loop
        if(true) {
            //change agentrow and agentcol for agent
            // move agent on level
            this.moveAgentTo(agentId,currentCell,tarCell);
            //change boxrow and boxcol for box
            //move box on level
            this.moveBoxTo(boxLetter,boxCell,currentCell);
        }
    }
    //TODO: commented out section

    // Return the cell list
    public static HashMap<Point, Cell> getCells()
    {
        return cells;
    }

    // Return a cell from the ArrayLevel (Point cell)
    public static Cell getCell(Point cell) {
        return cells.get(cell);
    }

    // Return a cell from the ArrayLevel (int row, int column)
    public static Cell getCell(int r, int c) {
        return getCell(new Point(r, c));
    }


    // Return a cell from the ArrayLevel (int x, int y)
    public static Cell getCellFromLocation(int x, int y) {
        return getCell(cellFromLocation(x, y));
    }

    // Return a cell from the ArrayLevel (Point loc)
    public static Cell getCellFromLocation(Point loc) {
        return getCell(cellFromLocation(loc.x, loc.y));
    }

    // Return this ArrayLevel's cell size
    public static int getCellSize() {
        return cellSize;
    }

    // Return the minX coordinate
    public static int getMinX() {
        return minX;
    }

    // Return the minY coordinate
    public static int getMinY() {
        return minY;
    }

    // Return the maxX coordinate
    public static int getMaxX() {
        return maxX;
    }

    // Return the maxY coordinate
    public static int getMaxY() {
        return maxY;
    }

    // Return x from r
    public static int XFromRow(int r)
    {
        return (minX + ((r - 1) * cellSize));
    }

    // Return y from c
    public static int YFromColumn(int c)
    {
        return (minY + ((c - 1) * cellSize));
    }

    // Return r from x
    public static int rowFromX(int x)
    {
        return ((x - minX + cellSize) / cellSize);
    }

    // Return c from y
    public static int columnFromY(int y)
    {
        return ((y - minY + cellSize) / cellSize);
    }

    // Return Point(x, y) from Point(r, c)
    public static Point locationFromCell(Point cell) {
        return locationFromCell(cell.x, cell.y);
    }

    // Return Point(x, y) from (r, c)
    public static Point locationFromCell(int r, int c)
    {
        // Make sure this point is within our grid
        if (r <= width && c <= height)
        {
            return new Point(XFromRow(r), YFromColumn(c));
        }

        // Otherwise, return Point(0, 0)
        return new Point();
    }

    // Return Point(r, c) from Point(x, y)
    public static Point cellFromLocation(Point loc) {
        return cellFromLocation(loc.x, loc.y);
    }

    // Return Point(r, c) from (x, y)
    public static Point cellFromLocation(int x, int y)
    {
        // Make sure this point is within our grid
        if ((x <= maxX && x >= minX) && (y <= maxY && y >= minY))
        {
            return new Point(rowFromX(x), columnFromY(y));
        }

        // Otherwise, return Point(0, 0)
        return new Point();
    }
}