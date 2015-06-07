package client.ateam.Level;

import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.Task;
import client.ateam.projectEnum.CellType;
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

    public static int MAX_ROW = 70;
    public static int MAX_COLUMN = 70;

    public boolean[][] walls = new boolean[MAX_ROW][MAX_COLUMN];
    private char[][] boxes = new char[MAX_ROW][MAX_COLUMN];
    private char[][] goals = new char[MAX_ROW][MAX_COLUMN];
    private int[][] agents = new int[MAX_ROW][MAX_COLUMN];

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
    public boolean isNeighbor(int curRow, int curCol,int neighborRow, int neighborCol) {
//        System.err.println("Cur: " + curRow + " " + curCol + " Neighbor: " + neighborRow + " " + neighborCol);
        if (((curRow-1 == neighborRow) && (curCol == neighborCol)) || ((curRow+1 == neighborRow) && (curCol == neighborCol))||
                ((curRow==neighborRow)&&(curCol+1==neighborCol)) || ((curRow==neighborRow)&&(curCol-1==neighborCol))) return true;
        return false;
    }

    @Override
    public boolean isBoxAt(char boxLetter, int row, int col) {
        return this.boxes[row][col] == boxLetter;
    }

    @Override
    public boolean isAgentAt(int agentId, int row, int col) {
        return this.agents[row][col]==agentId;
    }

    @Override
    public boolean isFree(Point cell){
        return !getCell(cell).isOccupied();
    }

    @Override
    public boolean isGoalCompleted() {
        for ( int row = 1; row < MAX_ROW - 1; row++ ) {
            for ( int col = 1; col < MAX_COLUMN - 1; col++ ) {
                char g = goals[row][col];
                char b = Character.toLowerCase( boxes[row][col] );
                if (g > 0 && b != g) {
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
        Cell tempCell;
        cells = new HashMap<Point, Cell>();         // Create our cellList
        int cell_id = 0; //unique id for every agent & box. will get incremented every time we find one of them
        boolean is_multi_agent_system = false;

        for(int x=0;x<agents.length;x++)
            for(int y=0;y<agents[x].length;y++)
                agents[x][y] = -1;
        for(int x=0;x<walls.length;x++)
            for(int y=0;y<walls[x].length;y++)
                walls[x][y] = false;
        for(int x=0;x<goals.length;x++)
            for(int y=0;y<goals[x].length;y++)
                goals[x][y] = ' ';

        int levelLines = 0;

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
                colors.put(id.trim().charAt(0), color);
            }
        }

        if (!colors.isEmpty()) is_multi_agent_system = true;

        int[] widths = new int[70];
        for (int i=0; i<70; ++i)
        {
            widths[i] = 0;
        }

        while ( !line.equals( "" ) ) {
            for ( int i = 0; i < line.length(); i++ ) {
                char chr = line.charAt( i );
//                System.err.println("row: " + levelLines + "column: " + i);
                if ( '+' == chr ) { // Walls
                    walls[levelLines][i] = true;
                    tempCell = new Cell(levelLines, i, CellType.WALL);
                    tempCell.toggleOccupied(); //the cell is Occupied
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
                } else if ( '0' <= chr && chr <= '9' ) { // Agents
                    if (is_multi_agent_system)
                    {
                        agentsArrayList.add(new Agent(cell_id, colors.get(chr), levelLines, i));
                    }
                    else{
                        agentsArrayList.add(new Agent(cell_id, Color.BLUE, levelLines, i));
                    }
                    cell_id++;
                    tempCell = new Cell(levelLines, i, CellType.AGENT);
                    tempCell.toggleOccupied(); //the cell is Occupied
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
                    agents[levelLines][i] = (int)chr;
//                    System.err.println("int chr" + (int)chr);
//                    System.err.println("Agent location row: "+levelLines+", col: "+i+", value: "+agents[levelLines][i]);
                } else if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
                    if (is_multi_agent_system)
                    {
                        boxesArrayList.add(new Box(cell_id, chr, colors.get(chr), levelLines, i));
                    }
                    else{
                        boxesArrayList.add(new Box(cell_id, chr, Color.BLUE, levelLines, i));
//                        System.err.println("Box I am trying to add: " + new Box(chr, colors.get(chr), levelLines, i).toString());
                    }
                    cell_id++;
                    tempCell = new Cell(levelLines, i, CellType.BOX);
                    tempCell.toggleOccupied(); //the cell is Occupied
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
                    boxes[levelLines][i]=chr;
//                    System.err.println("Box coords: "+levelLines+","+i);
                } else if ( 'a' <= chr && chr <= 'z' ) { // Goal cells
                    goalsArrayList.add(new Goal(chr,levelLines,i));
                    tempCell = new Cell(levelLines, i, CellType.GOAL);
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
                    goals[levelLines][i] = chr;
                }
                else{ //it means this is an empty cell
                    tempCell = new Cell(levelLines, i, CellType.EMPTY);
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
//            if(temp_Cell.getValue().getCell_type() == CellType.BOX){
//                System.err.println("a box within cells: " + temp_Cell.getValue().toString());
//            }
//            System.err.println("Cell: " + temp_Cell.toString());
        }

//        for (Box box : boxesArrayList)
//        {
//            System.err.println("Box location : " + box.getRow() +", " +  box.getColumn());
//            System.err.println("Is it occupied? : " + getCell(box.getRow(), box.getColumn()).isOccupied());
//            System.err.println("Box id: " + box.getId());
//        }
//        for (Agent agent : agentsArrayList)
//        {
//            System.err.println("Agent id: " + agent.id);
//        }

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
    public Box getSpecificBox(Cell cell) {
        Box box_to_return = new Box();
        for(Box box : boxesArrayList)
        {
            if((box.getRow() == cell.getR()) && (box.getColumn() == cell.getC())){
                return box;
            }
        }
        return box_to_return;
    }

    @Override
    public Agent getSpecificAgent(Cell cell) {
        Agent agent_to_return = new Agent();
        for(Agent agent : agentsArrayList)
        {
            if((agent.row == cell.getR()) && (agent.column == cell.getC())){
                return agent;
            }
        }
        return agent_to_return;
    }

    @Override
    public ArrayList<Goal> getGoals() {
        return goalsArrayList;
    }

    @Override
    public ArrayList<Task> getTasks() {return taskList;}

    private void moveAgentTo(int agentId, Point currentCell, Point tarCell){
        //change agentrow and agentcol for agent
        System.err.println("Curr Cell: " + currentCell.toString());
        System.err.println("Target Cell: " + tarCell.toString());
        System.err.println("Current location agents array: "+agents[currentCell.x][currentCell.y]);
        for(Agent agent : this.getAgents()){
            if(agent.id == agentId)
            {
//                agent.row = tarCell.y;
//                agent.column = tarCell.x;
                agent.row = tarCell.x;
                agent.column = tarCell.y;
                break;
            }
        }
        if(agents[currentCell.x][currentCell.y] == agentId) {
            agents[currentCell.x][currentCell.y] = -1;
            agents[tarCell.x][tarCell.y] = agentId;
        }
//        System.err.println("moveAgentTo - agentlist after move: "+agents[tarCell.x][tarCell.y]);
//        System.err.println("moveAgentTo - agentID: " + agentId);
    }

    public void moveBoxTo_withID(char boxLetter, int box_id, Point boxCell, Point boxTarCell){
        for(Box box : this.getBoxes()){
            if(box.getBoxLetter()== boxLetter && box.getId() == box_id){
                box.setRow(boxTarCell.x);
                box.setColumn(boxTarCell.y);
                break;
            }
        }
        if(boxes[boxCell.x][boxCell.y]==boxLetter) {
            boxes[boxCell.x][boxCell.y] = ' ';
            boxes[boxTarCell.x][boxTarCell.y] = boxLetter;
        }
    }

    @Override
    public void executeMoveAction(int agentId, Point currentCell, Point tarCell) {
        System.err.println("executeMoveAction - isNeighor: "+this.isNeighbor(currentCell.x,currentCell.y,tarCell.x,tarCell.y));
        System.err.println("executeMoveAction - isFree: "+this.isFree(new Point(tarCell.x, tarCell.y)));
        if(this.isNeighbor(currentCell.x,currentCell.y,tarCell.x,tarCell.y) && this.isFree(new Point(tarCell.x, tarCell.y))){
            this.moveAgentTo(agentId, currentCell, tarCell);
            getCell(currentCell).toggleOccupied();
            getCell(currentCell).setCell_type(CellType.EMPTY);
            getCell(tarCell).toggleOccupied();
            getCell(tarCell).setCell_type(CellType.AGENT);
        }
    }

    @Override
    public void executePushAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point boxTarCell) {

        if(this.isNeighbor(currentCell.x,currentCell.y,boxCell.x,boxCell.y) && this.isNeighbor(boxCell.x,boxCell.y,boxTarCell.x,boxTarCell.y)
                && this.isFree(new Point(boxTarCell.x,boxTarCell.y))) {
            // move agent on level
            this.moveAgentTo(agentId, currentCell, boxCell);
            //move box on level
            Box box_which_is_moved = getSpecificBox(getCell(boxCell));
            System.err.println("Box before it is moved: " + box_which_is_moved.toString());
            this.moveBoxTo_withID(box_which_is_moved.getBoxLetter(), box_which_is_moved.getId(), boxCell, boxTarCell);
            System.err.println("Box after it is moved: " + getBoxByID(box_which_is_moved.getId()).toString());

            getCell(currentCell).toggleOccupied();
            getCell(currentCell).setCell_type(CellType.EMPTY);
            getCell(boxTarCell).toggleOccupied();
            getCell(boxCell).setCell_type(CellType.AGENT);
            getCell(boxTarCell).setCell_type(CellType.BOX);
        }
        else{
            System.err.println("executePushAction failed");
        }
    }

    @Override
    public void executePullAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point tarCell) {
        if(this.isNeighbor(currentCell.x,currentCell.y,tarCell.x,tarCell.y) && this.isNeighbor(boxCell.x,boxCell.y,currentCell.x,currentCell.y)
                && this.isFree(new Point(tarCell.x,tarCell.y))){
            // move agent on level
            this.moveAgentTo(agentId,currentCell,tarCell);
            //move box on level
            Box box_which_is_moved = getSpecificBox(getCell(boxCell));
            System.err.println("Box before it is moved: " + box_which_is_moved.toString());
            this.moveBoxTo_withID(box_which_is_moved.getBoxLetter(), box_which_is_moved.getId(), boxCell, currentCell);
            System.err.println("Box after it is moved: " + getBoxByID(box_which_is_moved.getId()).toString());

            getCell(boxCell).toggleOccupied();
            getCell(currentCell).setCell_type(CellType.BOX);
            getCell(boxCell).setCell_type(CellType.EMPTY);
            getCell(tarCell).setCell_type(CellType.AGENT);
        }
    }

    // Return a cell from the ArrayLevel (Point cell)
    public static Cell getCell(Point cell) {
//        System.err.println("Look what cell i'm fetching to be a childCell: " + cell.toString());
//        System.err.println("trying to get from cells hashmap: " + cells.get(cell).toString());
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
        if ((x <= height) && (y <= width))
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

    public Box getBoxByID(int box_id)
    {
        Box box_to_return = new Box();
        for(Box box : boxesArrayList)
        {
            if (box.getId() == box_id)
            {
                return box;
            }
        }
        return box_to_return;
    }

    public Agent getAgentByID(int agent_id) {
        Agent agent_to_return = new Agent();
        for(Agent agent : agentsArrayList)
        {
            if (agent.id == agent_id)
            {
                return agent;
            }
        }
        return agent_to_return;
    }
}