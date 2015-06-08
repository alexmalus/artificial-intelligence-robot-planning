package client.ateam.Level;

import client.ateam.projectEnum.CellType;

import java.awt.Point;

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
    public Cell(int r, int c)
    {
        this.cell = new Point(r, c);
    }

    public Cell(int r, int c, CellType cell_type)
    {
        this.cell = new Point(r, c);
        this.cell_type = cell_type;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(){
        this.location = ArrayLevel.locationFromCell(cell);
    }

    public Point getArrayLevelLocation() {
        return cell;
    }

    public int getX() {
        return location.x;
    }

    public int getY() {
        return location.y;
    }

    public int getR() {
        return cell.x;
    }

    public CellType getCell_type(){
        return cell_type;
    }

    public int getC() {
        return cell.y;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public boolean isOccupied(boolean preliminary_path_build) {
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

    public String toString()
    {
        return "Loc:" + location;
    }

}
