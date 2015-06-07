package client.ateam.Level;

import client.ateam.projectEnum.CellType;

import java.awt.Point;

// Grid cell class
public class Cell
{
    private Point cell;						// The location of this cell; Point (r, c)
    private Point location;					// The location of this cell; Point (x, y)
    private CellType cell_type;
    private boolean isOccupied 	= false;	// Whether or not agents can enter this cell

    public Cell(){
        this.cell = new Point(-1,-1);
        this.location = new Point(-1,-1);
    }
    // Constructor, creates a new cell for the grid
    public Cell(int r, int c)
    {
        // Row and column numbers for this cell
        this.cell = new Point(r, c);
    }

    public Cell(int r, int c, CellType cell_type)
    {
        // Row and column numbers for this cell
        this.cell = new Point(r, c);
        this.cell_type = cell_type;
    }

    // Returns Point(x, y)
    public Point getLocation() {
        return location;
    }

    public void setLocation(){
//        System.err.println("trying to set location of this cell: " + cell.toString());
        this.location = ArrayLevel.locationFromCell(cell);
    }

    // Returns Point(r, c)
    public Point getArrayLevelLocation() {
        return cell;
    }

    // Return x-coordinate from Point(x, y)
    public int getX() {
        return location.x;
    }

    // Return y-coordinate from Point(x, y)
    public int getY() {
        return location.y;
    }

    // Return row from Point(r, c)
    public int getR() {
        return cell.x;
    }

    public CellType getCell_type(){
        return cell_type;
    }

    // Return column from Point(r, c)
    public int getC() {
        return cell.y;
    }

    // Returns whether or not the cell is playable
    public boolean isOccupied() {
        return isOccupied;
    }

    public boolean isOccupied(boolean preliminary_path_build) {
//        System.err.println("cell type(preliminary path build): " + cell_type);
        if (preliminary_path_build)
        {
            if (cell_type == CellType.WALL)
            {
                return isOccupied;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return(cell_type == CellType.AGENT || cell_type == CellType.BOX || cell_type == CellType.WALL);
        }
    }

    public void toggleOccupied(){
        isOccupied = !isOccupied;
    }

    public void setCell_type(CellType cell_type) { this.cell_type = cell_type;}

    public void setRowColumn(int x, int y)
    {
        cell.x = x;
        cell.y = y;
    }

    public String getRowColumn()
    {
        return "row: " + getR() + ", column: " + getC();
    }

    // Override toString method
    public String toString()
    {
        return "Loc:" + location;
    }

}
