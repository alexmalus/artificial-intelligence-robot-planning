package client.ateam.Level.Models;

public class Goal {
    private char letter;
    private int y;
    private int x;

    public Goal(){
        letter = ' ';
        y = 0;
        x = 0;
    }
    public Goal(char letter, int y, int x){
        this.letter = letter;
        // this.position = Level.getIndexFromColoumnAndy(x,y);
        this.y = y;
        this.x = x;

    }

    public char getGoalLetter(){
        return letter;
    }
    public int gety(){
        return y;
    }
    public int getx(){
        return x;
    }
}

