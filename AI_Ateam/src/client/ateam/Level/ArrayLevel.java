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

public class ArrayLevel implements ILevel {

    private BufferedReader serverMessages = new BufferedReader( new InputStreamReader( System.in ) );

    public static int MAX_ROW = 70;
    public static int MAX_COLUMN = 70;
    public boolean[][] walls = new boolean[MAX_ROW][MAX_COLUMN];

    private static int height;
    private static int width;

    private static HashMap<Point, Cell> cells = null; // Our list of each cell in this ArrayLevel

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

    @Override
    public boolean isNeighbor(int curRow, int curCol,int neighborRow, int neighborCol) {
        return (((curRow-1 == neighborRow) && (curCol == neighborCol)) || ((curRow+1 == neighborRow) && (curCol == neighborCol))||
                ((curRow==neighborRow)&&(curCol+1==neighborCol)) || ((curRow==neighborRow)&&(curCol-1==neighborCol)));
    }

    @Override
    public boolean isFree(Point cell){
        return !getCell(cell).isOccupied();
    }

    public void ReadMap() throws Exception {
        Map< Character, Color > colors = new HashMap< Character, Color >();
        String line;
        Color color;
        Cell tempCell;
        cells = new HashMap<Point, Cell>();         // Create our cellList
        int cell_id = 0; //unique id for every agent & box. will get incremented every time we find one of them
        boolean is_multi_agent_system = false;

        for(int x=0;x<walls.length;x++)
            for(int y=0;y<walls[x].length;y++)
                walls[x][y] = false;

        int levelLines = 0;

        // Read lines specifying colors
        while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
            line = line.replaceAll( "\\s", "" );
            String[] colonSplit = line.split( ":" );
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
                } else if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
                    if (is_multi_agent_system)
                    {
                        boxesArrayList.add(new Box(cell_id, chr, colors.get(chr), levelLines, i));
                    }
                    else{
                        boxesArrayList.add(new Box(cell_id, chr, Color.BLUE, levelLines, i));
                    }
                    cell_id++;
                    tempCell = new Cell(levelLines, i, CellType.BOX);
                    tempCell.toggleOccupied(); //the cell is Occupied
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
                } else if ( 'a' <= chr && chr <= 'z' ) { // Goal cells
                    goalsArrayList.add(new Goal(chr,levelLines,i));
                    tempCell = new Cell(levelLines, i, CellType.GOAL);
                    cells.put(tempCell.getArrayLevelLocation(), tempCell);
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
        for(Map.Entry<Point, Cell> temp_Cell : cells.entrySet()){
           temp_Cell.getValue().setLocation();
        }
    }

    @Override
    public ArrayList<Agent> getAgents() {
        return agentsArrayList;
    }

    @Override
    public ArrayList<Goal> getGoals() {
        return goalsArrayList;
    }

    @Override
    public ArrayList<Task> getTasks() {return taskList;}

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

    public void moveAgentTo(int agentId, Point tarCell){
        for(Agent agent : this.getAgents()){
            if(agent.id == agentId)
            {
                agent.row = tarCell.x;
                agent.column = tarCell.y;
                break;
            }
        }
    }

    public void moveBoxTo(char boxLetter, int box_id, Point boxTarCell){
        for(Box box : this.getBoxes()){
            if(box.getBoxLetter()== boxLetter && box.getId() == box_id){
                box.setRow(boxTarCell.x);
                box.setColumn(boxTarCell.y);
                break;
            }
        }
    }

    @Override
    public void executeMoveAction(int agentId, Point currentCell, Point tarCell) {
        if(this.isNeighbor(currentCell.x,currentCell.y,tarCell.x,tarCell.y) && this.isFree(new Point(tarCell.x, tarCell.y))){
            this.moveAgentTo(agentId, tarCell);
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
            this.moveAgentTo(agentId, boxCell);
            Box box_which_is_moved = getSpecificBox(getCell(boxCell));
            this.moveBoxTo(box_which_is_moved.getBoxLetter(), box_which_is_moved.getId(), boxTarCell);

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
        Box box_which_is_moved = getSpecificBox(getCell(boxCell));
        if(this.isNeighbor(currentCell.x,currentCell.y,tarCell.x,tarCell.y) && this.isNeighbor(boxCell.x,boxCell.y,currentCell.x,currentCell.y)
                && this.isFree(new Point(tarCell.x,tarCell.y))){
            this.moveAgentTo(agentId,tarCell);
            this.moveBoxTo(box_which_is_moved.getBoxLetter(), box_which_is_moved.getId(), currentCell);

            getCell(boxCell).toggleOccupied();
            getCell(currentCell).setCell_type(CellType.BOX);
            getCell(boxCell).setCell_type(CellType.EMPTY);
            getCell(tarCell).setCell_type(CellType.AGENT);
            getCell(tarCell).toggleOccupied();
        }
        else
        {
            System.err.println("executePullAction failed");
        }
    }

    public static Cell getCell(Point cell) {
        return cells.get(cell);
    }

    public static Cell getCell(int r, int c) {
        return getCell(new Point(r, c));
    }

    public static Cell getCellFromLocation(int x, int y) {
        return getCell(cellFromLocation(x, y));
    }

    public static Point locationFromCell(Point cell) {
        return locationFromCell(cell.x, cell.y);
    }

    public static Point locationFromCell(int r, int c)
    {
        if (r <= height && c <= width) // Make sure this point is within the maximum admissable lengths
        {
            return new Point(r, c);
        }
        return new Point();
    }

    public static Point cellFromLocation(int x, int y)
    {
        if ((x <= height) && (y <= width))
        {
            return new Point(x, y);
        }

        return new Point();
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