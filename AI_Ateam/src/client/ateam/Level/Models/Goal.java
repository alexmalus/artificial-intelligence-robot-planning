package client.ateam.Level.Models;

public class Goal {
    private char letter;
    private int row;
    private int column;

    public Goal(){
        letter = ' ';
        row = -200; //such low values in order to make sure their abs values are still less than those of real goals
        column = -200;
    }
    public Goal(char letter, int row, int column){
        this.letter = letter;
        // this.position = Level.getIndexFromColoumnAndRow(column,row);
        this.row = row;
        this.column = column;

    }

    public char getGoalLetter(){
        return letter;
    }
    public int getRow(){
        return row;
    }
    public int getColumn(){
        return column;
    }
    public void setRow(int row) {this.row = row;}
    public void setColumn(int column) { this.column = column;}
    @Override
    public String toString(){
        return "row: " + row + " column: " + column;
    }
}

