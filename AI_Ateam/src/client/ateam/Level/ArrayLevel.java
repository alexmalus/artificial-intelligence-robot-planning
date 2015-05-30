package client.ateam.Level;

import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.Task;
import client.ateam.projectEnum.Color;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by joh on 5/10/15.
 */
public class ArrayLevel implements ILevel {

    private BufferedReader serverMessages = new BufferedReader( new InputStreamReader( System.in ) );

    public static int MAX_y = 70;
    public static int MAX_x = 70;


    //    public ArrayList<ArrayList<Integer>> agentLocation = new ArrayList<>()
    public int agenty;
    public int agentx;

    public boolean[][] walls = new boolean[MAX_y][MAX_x];
    private char[][] boxes = new char[MAX_y][MAX_x];
    private char[][] goals = new char[MAX_y][MAX_x];
    private int[][] agents = new int[MAX_y][MAX_x];

    private static int height;
    private static int width;
    // Our list of each cell in this ArrayLevel
    private static HashMap<Point, Cell> cells = null;

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
    public boolean isNeighbor(int cury, int curx,int neighbory, int neighborx) {
//        if (curPos >= Level.realMap.length || curPos < 0 || neighborPos < 0 || neighborPos >= Level.realMap.length)
//            return false; //outside bounds of map
        // right left neighbor
        System.err.println("CUR: Y and X: " + cury + " " + curx + " Neighbor: " + neighbory + " " + neighborx);
        if (((cury-1 == neighbory) && (curx == neighborx)) || ((cury+1 == neighbory) && (curx == neighborx))||
        ((cury==neighbory)&&(curx+1==neighborx)) || ((cury==neighbory)&&(curx-1==neighborx)))
            return true;
        // up down neighbor
//        if ((cury-ArrayLevel.getWidth() == neighbory) && (curx-ArrayLevel.getWidth() == neighborx) || (cury+ArrayLevel.getWidth() == neighbory) && (curx+ArrayLevel.getWidth() == neighborx))
//            return true;

        return false;
    }

    @Override
    public boolean isBoxAt(char boxLetter, int y, int x) {
        return this.boxes[y][x] == boxLetter;
    }

    @Override
    public boolean isAgentAt(int agentId, int y, int x) {
        return this.agents[y][x]==agentId;
    }

    @Override
    public boolean isFree( int y, int x ) {
        System.err.println("X: "+x+" Y: "+y);
        System.err.println("Walls: "+this.walls[y][x]);
        System.err.println("Boxes: "+this.boxes[y][x]);
        System.err.println("Boxes inverted: "+this.walls[x][y]);
        System.err.println("Agents: "+this.agents[y][x]);
        return ( !(this.walls[y][x]) && (this.boxes[y][x] == 0 || this.boxes[y][x]==' ') && this.agents[y][x]==-1 );
    }

    @Override
    public boolean isGoalCompleted() {
        for ( int y = 1; y < MAX_y - 1; y++ ) {
            for ( int x = 1; x < MAX_x - 1; x++ ) {
                char g = goals[y][x];
                char b = Character.toLowerCase( boxes[y][x] );
                if ( g > 0 && b != g) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public char getBoxLetter(int y, int x) {
        for(int i = 0; i == boxesArrayList.size(); i++)
        {
            Box tempbox = boxesArrayList.get(i);
            if((tempbox.gety() == y) && (tempbox.getx() == x)){
                return tempbox.getBoxLetter();
            }

        }
        System.err.println("ERROR: could not find any boxletters");
        return 0;
    }

    @Override
    public Color getBoxColor(int y, int x) {
        for(int i = 0; i == boxesArrayList.size(); i++)
        {
            Box tempbox = boxesArrayList.get(i);
            if((tempbox.gety() == y) && (tempbox.getx() == x)){
                return tempbox.getColor();
            }

        }
        System.err.println("ERROR: could not find any box colors");
        return null;
    }

    @Override
    public char getGoalLetter(int y, int x) {
        for(int i = 0; i == goalsArrayList.size(); i++)
        {
            Goal tempGoal = goalsArrayList.get(i);
            if((tempGoal.gety() == y) && (tempGoal.getx() == x)){
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
        Cell tempCell;
        // Create our cellList
        cells = new HashMap<Point, Cell>();

//        System.err.println(walls.length);
//        System.err.println(goals.length);
        for(int y=0;y<agents.length;y++)
        for(int x=0;x<agents[y].length;x++)
                agents[y][x] = -1;
        for(int y=0;y<walls.length;y++)
            for(int x=0;x<walls[y].length;x++)
                walls[y][x] = false;
        for(int y=0;y<goals.length;y++) {
//            System.err.println(goals[y].length);
            for(int x=0;x<goals[y].length;x++)
            {
                goals[y][x] = ' ';
            }
        }

        int agentx = -1, agenty = -1;
        int colorLines = 0, levelLines = 0;

        // Read lines specifying colors
        while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
            line = line.replaceAll( "\\s", "" );
            String[] colorSplit = line.split( ":" );
            //color = colorSplit[0].trim();
            switch(colorSplit[0].trim()){
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

            for ( String id : colorSplit[1].split( "," ) ) {
                colors.put( id.trim().charAt( 0 ), color );
            }
            colorLines++;
        }

        if ( colorLines > 0 ) {
            //which means that for the moment we are only considering SA cases
            error( "Box colors not supported" );
        }

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
                    walls[i][levelLines] = true;
                    tempCell = new Cell(levelLines, i);
                    tempCell.toggleOccupied(); //the cell is Occupied
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
                } else if ( '0' <= chr && chr <= '9' ) { // Agents
                    if ( agentx != -1 || agenty != -1) {
                        error( "Not a single agent level" );
                    }
                    agentsArrayList.add(new Agent((int)chr,colors.get(chr),levelLines,i));
                    tempCell = new Cell(levelLines, i);
                    tempCell.toggleOccupied(); //the cell is Occupied
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
                    agents[i][levelLines] = (int)chr;
                    System.err.println("int chr" + (int)chr);
                    System.err.println("Agent location Y: "+levelLines+", X: "+i+", value: "+agents[levelLines][i]);


                } else if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
                    boxesArrayList.add(new Box(chr,colors.get(chr),levelLines,i));
                    tempCell = new Cell(levelLines, i);
                    tempCell.toggleOccupied(); //the cell is Occupied
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
                    boxes[i][levelLines]=chr;
                    System.err.println("Box coords: "+levelLines+","+i);

                } else if ( 'a' <= chr && chr <= 'z' ) { // Goal cells
                    goalsArrayList.add(new Goal(chr,levelLines,i));
                    tempCell = new Cell(levelLines, i);
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
                    goals[levelLines][i] = chr;
                }
                else{ //it means this is an empty cell
                    tempCell = new Cell(levelLines, i);
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
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
//        System.err.println("Height and width: " + height + " " +  width);
        for(Map.Entry<Point, Cell> temp_Cell : cells.entrySet()){
            temp_Cell.getValue().setLocation();
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

    @Override
    public ArrayList<Task> getTasks() {return taskList;}

    private void moveAgentTo(int agentId, Point currentCell, Point tarCell){
        //change agenty and agentx for agent
        System.err.println("Curr Cell: " + currentCell.toString());
        System.err.println("TArget Cell: " + tarCell.toString());
        for(Agent agent : this.getAgents()){
            if(agent.id == agentId)
            {
                agent.y = tarCell.y;
                agent.x = tarCell.x;
                break;
            }
        }
        System.err.println("moveAgentTo - currentCell: "+agents[currentCell.y][currentCell.x]);
        if(agents[currentCell.y][currentCell.x]==agentId) {
            agents[currentCell.y][currentCell.x] = -1;
            agents[tarCell.y][tarCell.x] = agentId;
        }
        System.err.println("moveAgentTo - agentlist after move: "+agents[tarCell.y][tarCell.x]);
        System.err.println("moveAgentTo - agentID: " + agentId);
    }
    private void moveBoxTo(char boxLetter, Point boxCell, Point boxTarCell){
        for(Box box : this.getBoxes()){
            if (box.getBoxLetter()==boxLetter) {
                box.setx(boxTarCell.x);
                box.sety(boxTarCell.y);
                break;
            }
        }
        if(boxes[boxCell.y][boxCell.x]==boxLetter) {
            boxes[boxCell.y][boxCell.x] = ' ';
            boxes[boxTarCell.y][boxTarCell.x] = boxLetter;
        }
    }

    @Override
    public void executeMoveAction(int agentId, Point currentCell, Point tarCell) {
        System.err.println("executeMoveAction - isNeighor: "+this.isNeighbor(currentCell.y,currentCell.x,tarCell.y,tarCell.x));
        System.err.println("executeMoveAction - isFree: "+this.isFree(tarCell.y, tarCell.x));
        if(this.isNeighbor(currentCell.y,currentCell.x,tarCell.y,tarCell.x) && this.isFree(tarCell.y, tarCell.x)){
            this.moveAgentTo(agentId,currentCell,tarCell);
        }
    }

    @Override
    public void executePushAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point boxTarCell) {
        if(this.isNeighbor(currentCell.y,currentCell.x,boxCell.y,boxCell.x) && this.isNeighbor(boxCell.y,boxCell.x,boxTarCell.y,boxTarCell.x)
                && this.isFree(boxTarCell.y,boxTarCell.x)) {
            //change boxy and boxx for box
            //move box on level
            this.moveBoxTo(boxLetter, boxCell, boxTarCell);
            //change agenty and agentx for agent
            // move agent on level
            this.moveAgentTo(agentId, currentCell, boxCell);
        }
    }

    @Override
    public void executePullAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point tarCell) {
        if(this.isNeighbor(currentCell.y,currentCell.x,tarCell.y,tarCell.x) && this.isNeighbor(boxCell.y,boxCell.x,currentCell.y,currentCell.x)
                && this.isFree(tarCell.y,tarCell.x)){
            //change agenty and agentx for agent
            // move agent on level
            this.moveAgentTo(agentId,currentCell,tarCell);
            //change boxy and boxx for box
            //move box on level
            this.moveBoxTo(boxLetter,boxCell,currentCell);
        }
    }

    // Return a cell from the ArrayLevel (Point cell)
    public static Cell getCell(Point cell) {
//        System.err.println("Look what cell i'm fetching to be a childCell: " + cell.toString());
//        System.err.println("trying to get from cells hashmap: " + cells.get(cell).toString());
        return cells.get(cell);
    }

    // Return a cell from the ArrayLevel (int y, int x)
    public static Cell getCell(int r, int c) {
        return getCell(new Point(r, c));
    }

    // Return a cell from the ArrayLevel (int x, int y)
    public static Cell getCellFromLocation(int x, int y) {
        return getCell(cellFromLocation(x, y));
    }

    //    // Return Point(x, y) from Point(r, c)
    public static Point locationFromCell(Point cell) {
        return locationFromCell(cell.x, cell.y);
    }

    // Return Point(x, y) from (r, c)
    public static Point locationFromCell(int r, int c)
    {
//        System.err.println("R: " + r + " C: " + c);
        // Make sure this point is within our grid
        if (r <= height && c <= width)
        {
            return new Point(r, c);
        }

        // Otherwise, return Point(0, 0)
        return new Point();
    }

    // Return Point(r, c) from (x, y)
    public static Point cellFromLocation(int x, int y)
    {
        // Make sure this point is within our grid
        if ((x <= width) && (y <= height))
        {
            return new Point(x, y);
        }

        // Otherwise, return Point(0, 0)
        return new Point();
    }

    // Returns points along a line spaced apart by int spacing length
    public static Point[] pointsAlongLine(Point start, Point end, int spacing)
    {
        // Find the difference between the points
        int xDif = (end.x - start.x);
        int yDif = (end.y - start.y);

        // Find the length of the line [sqrt(x^2 + y^2)]
        int lineLength = (int)Math.sqrt((Math.pow(xDif, 2) + Math.pow(yDif, 2)));

        // The number of steps is equal to the length of the line divided by the spacing
        int steps = lineLength / spacing;

        // The number of x and y steps is equal to the x and y difference divided by the number of steps needed
        int xStep = xDif / steps;
        int yStep = yDif / steps;

        // Store our result in an array of points
        Point[] result = new Point[steps];

        // Return the points on a line
        for (int i = 0; i < steps; i++)
        {
            int x = start.x + (xStep * i);
            int y = start.y + (yStep * i);
            result[i] = new Point(x, y);
        }

        // Return the points on a line
        return result;
    }
}