package client.ateam.Level.Models;

import client.ateam.projectEnum.Color;

public class Box {
    private char boxLetter;
    private Color color;
    private int row;
    private int column;
    private boolean isTaken = false;

    public Box(){
        color = null;
        row = -200;
        column = -200;
    }

    public Box(char boxLetter,Color color, int row, int column){
        this.boxLetter = boxLetter;
        this.setColor(color);
        this.row = row;
        this.column = column;
    }

    public char getBoxLetter(){
        return boxLetter;
    }
    public int getRow(){
        return row;
    }
    public int getColumn(){
        return column;
    }
    public void setRow(int row) {this.row=row;}
    public void setColumn(int column){this.column=column;}
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public boolean isTaken(){ return isTaken;}
    @Override
    public String toString(){
        return "row: " + row + " column: " + column + ", color: " + color;
    }
}
