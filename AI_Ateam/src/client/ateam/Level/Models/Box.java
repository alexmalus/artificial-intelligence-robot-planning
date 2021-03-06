package client.ateam.Level.Models;

import client.ateam.projectEnum.Color;

public class Box {
    private char boxLetter;
    private Color color;
    private int row;
    private int column;
    private boolean isTaken = false;
    private int id;

    public Box(){
        color = null;
        row = -200;
        column = -200;
        id=-1;
    }

    public Box(int id, char boxLetter,Color color, int row, int column){
        this.id = id;
        this.boxLetter = boxLetter;
        this.color = color;
        this.row = row;
        this.column = column;
    }

    public char getBoxLetter(){
        return boxLetter;
    }
    public void setBoxLetter(char letter){this.boxLetter = letter;}
    public int getRow(){
        return row;
    }
    public int getColumn(){return column;}
    public void setRow(int row) {this.row=row;}
    public void setColumn(int column){this.column=column;}
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {this.color = color;}
    public boolean isTaken(){ return isTaken;}
    public void toggleisTaken() { isTaken = !isTaken;}
    public int getId(){return id;}
    @Override
    public String toString(){
        return "row: " + row + " column: " + column + ", color: " + color;
    }
}
