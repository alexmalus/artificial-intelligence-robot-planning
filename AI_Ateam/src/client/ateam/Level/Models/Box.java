package client.ateam.Level.Models;

import client.ateam.projectEnum.Color;

public class Box {
    private char boxLetter;
    private Color color;
    //row = y, x = column
    private int x;
    private int y;
    private boolean isTaken = false;

    public Box(char boxLetter,Color color, int y, int x){
        this.boxLetter = boxLetter;
        this.setColor(color);
        this.x = x;
        this.y = y;
    }

    public char getBoxLetter(){
        return boxLetter;
    }
    public int gety(){
        return y;
    }
    public int getx(){
        return x;
    }
    public void sety(int y) {this.y=y;}
    public void setx(int x){this.x=x;}
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    @Override
    public String toString(){
        return "row: " + y + " column: " + x;
    }
}
