package client.ateam.Level.Models;

public class Goal {
    private char letter;
    private int row;
    private int column;

    public Goal(){
        letter = ' ';
        row = -200;
        column = -200;
    }
    public Goal(char letter, int row, int column){
        this.letter = letter;
        this.row = row;
        this.column = column;

    }

    public char getGoalLetter(){return letter;}
    public int getRow(){return row;}
    public int getColumn(){return column;}
    @Override
    public String toString(){return "row: " + row + " column: " + column;}
}

