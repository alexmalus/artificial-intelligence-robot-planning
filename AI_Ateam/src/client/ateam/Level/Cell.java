package client.ateam.Level;

import java.awt.Point;

// Grid cell class
public class Cell
{
    private Point cell;						// The location of this cell; Point (r, c)
    private Point location;					// The location of this cell; Point (x, y)

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

    // Returns Point(x, y)
    public Point getLocation() {
        return location;
    }

    public void setLocation(){
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

    // Return column from Point(r, c)
    public int getC() {
        return cell.y;
    }

    // Returns whether or not the cell is playable
    public boolean isOccupied() {
        //TODO: need a way to check if the current location is occupied or not
        //each agent,wall,box need a cell and we need to mark them as being occupied and then free them up afterwards
//        return (walls[location.getX()][location.getY()] == true);
        return isOccupied;
    }

    public void toggleOccupied(){
        isOccupied = !isOccupied;
    }

    // Override toString method
    public String toString()
    {
        return "Loc:" + location;
    }

}
