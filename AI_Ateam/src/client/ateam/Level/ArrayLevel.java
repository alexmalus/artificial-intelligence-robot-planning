package client.ateam.Level;

import client.ateam.projectEnum.Direction;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lasse on 24-04-2015.
 */
public class ArrayLevel { //implements ILevel {

    private static ArrayLevel arraylevel;
    private static int height;

    /**
     *
     *
     */
    public static int[] realMap; //The current parsed map

    private static int width;

    /**
     * Array with character a=0,b=1 etc. as index and color as value
     */
    private static ArrayList<String> boxColors = new ArrayList<String>();

    /**
     * Array with agent id as index and color as value
     */
    private static ArrayList<String> agentColors = new ArrayList<String>();

    /**
     * Array with agent positions in initial map, index is unordered
     */
    private static ArrayList<Integer> agentsArrayList = new ArrayList<Integer>();

    /**
     * Array with box positions in initial map
     */
    private static ArrayList<Integer> boxesArrayList= new ArrayList<Integer>();

    /**
     * Array with goal positions (static)
     */
    private static ArrayList<Integer> goalsArrayList= new ArrayList<Integer>();

    /**
     * Array with the empty positions
     */
    private static ArrayList<Integer> cachePositionArrayList= new ArrayList<Integer>();

    /**
     * Level class constructor, private because we want a static class
     */
    private ArrayLevel(){

    }

    /**
     *
     * @return width of level, will not be set before loadLevel
     */
    public static int getWidth(){
        return arraylevel.width;
    }

    public int[] loadFromString(String s){

        if (ArrayLevel.realMap != null) return ArrayLevel.realMap;

        int row = 0;
        Scanner input = new Scanner(s);
        //Initialize height and width
        setFileLength(new Scanner(s));
        //Initialize Colors
        extractColors(new Scanner(s));

        int[] agents = new int[10];
        for (int i = 0; i < 10; i++) {
            agents[i] = -1;
        }

        int[] internalMappoing = new int[ArrayLevel.width * ArrayLevel.height];// = new ArrayList<Integer>();
        // Start scanning
        while (input.hasNextLine()){
            int column=0;
            //Scanning untill the map begins
            String t = input.nextLine();
            if(t.equals("")){
                continue;
            }
            if(t.charAt(0)!='+' && t.charAt(0)!=' '){
                continue;
            }

            Scanner tokenizer = new Scanner(t);
            tokenizer.useDelimiter("");

            while(tokenizer.hasNext()){
                String symbol=tokenizer.next();

                //First bit
                if (symbol.equals("+")){
                    internalMappoing[row * ArrayLevel.width + column] = 0x1;
                } else if (symbol.equals(" ")){
                    internalMappoing[row * ArrayLevel.width + column] = 0x0;
                    // add all empty position into this cache position list
                    cachePositionArrayList.add(row * ArrayLevel.width + column);
                } else{
                    char a = symbol.charAt(0);

                    if (a==' '){
                        internalMappoing[row * ArrayLevel.width + column] = 0x0;
                    }
                    //if it is a box
                    else if (a>='A' && a<='Z'){
                        //TODO add color
                        int boxLetter = 0x2 + (((int)a - 0x41) << 4) + (boxColors[(int)a - 0x41] << 9);

                        internalMappoing[row * ArrayLevel.width + column] = boxLetter;
                        boxesArrayList.add(row * ArrayLevel.width + column);
                    }
                    //it is a player
                    else if (a>='0' && a<='9'){
                        int playerNumber = (int)a - 0x30;
                        int playerLetter = 0x4 + (playerNumber << 13) + (agentColors[playerNumber] << 17);
                        internalMappoing[row * ArrayLevel.width + column] = playerLetter;
                        agents[playerNumber] = row * ArrayLevel.width + column;
                    }
                    //if it is a goal
                    else if (a>='a' && a<='z'){
                        //TODO add color
                        int goalLetter = 0x8 + (((int)a - 0x61) << 21);
                        internalMappoing[row * ArrayLevel.width + column] = goalLetter;
                        goalsArrayList.add(row * ArrayLevel.width + column);
                    }
                }
                column++;
            }
            row++;
        }

        for (int i = 0; i < 10; i++) {
            int pos = agents[i];
            if (pos > -1)
                agentsArrayList.add(pos);
        }

        ArrayLevel.realMap = internalMappoing;
        return internalMappoing;
    }

    private void setFileLength(Scanner input) {
        String t = null;
        ArrayLevel.width = -1;
        while (input.hasNextLine()){
            t = input.nextLine();
            if(t.equals(""))
                continue;
            if(t.charAt(0)!='+' && t.charAt(0)!=' ')
                continue;
            ArrayLevel.height++;
            //Search width in current line
            int tempWidth = 0;
            Scanner tokenizer = new Scanner(t);
            tokenizer.useDelimiter("");
            while(tokenizer.hasNext()){
                tokenizer.next();
                tempWidth++;
            }
            if (tempWidth > ArrayLevel.width) ArrayLevel.width = tempWidth;
        }

    }

    private void extractColors(Scanner input1) {
        //boxColors = new int[26];
        //agentColors = new int[10];

        while (input1.hasNextLine()){
            String t = input1.nextLine();
            if(t.charAt(0)=='+' || t.charAt(0)==' '){
                return;
            }

            Scanner tokenizer = new Scanner(t);

            String color;
            if(tokenizer.hasNext()){

                String c = tokenizer.next();
                if(c.equals("red:")){
                    color="RED";
                } else if(c.equals("green:")){
                    color="GREEN";
                } else if(c.equals("magenta:")){
                    color="MAGENTA";
                } else if(c.equals("cyan:")){
                    color="CYAN";
                } else if(c.equals("orange:")){
                    color="ORANGE";
                } else if(c.equals("pink:")){
                    color="PINK";
                } else if(c.equals("yellow:")){
                    color="YELLOW";
                } else {
                    color="BLUE";
                }
                tokenizer.useDelimiter("");
                while(tokenizer.hasNext()){

                    String s = tokenizer.next();
                    if (s.equals(",") || s.equals(" ")){
                        continue;
                    }
                    char item = s.charAt(0);
                    //if we have a character, we have a box
                    if(item>='A' && item<='Z'){
                    //    int indx = ((int) item) - 0x41;
                    //    boxColors[indx] = color;//	boxes.add(new Box( -1,-1 ,item,color));
                        boxColors.add(color);
                    }
                    //otherwise we have a robot
                    else {
                        //int indx = ((int)item) - 48;
                        //agentColors[indx] = color;
                        agentColors.add(color);
                    }
                }
            }
        }
    }

    /**
     * Get the class instance
     * @return instance of Level
     */
    public static ArrayLevel getSingletonObject(){
        if(arraylevel == null){
            arraylevel = new ArrayLevel();
        }
        return arraylevel;
    }

    public static int getPosFromPosInDirection(int pos, Direction dir){
        int position = 0;
        switch (dir) {
            case NORTH:
                position = pos - width;
                break;
            case SOUTH:
                position = pos + width;
                break;
            case EAST:
                position = pos + 1;
                break;
            case WEST:
                position = pos - 1;
                break;

            default:
                break;
        }
        return 0 <= position && position < realMap.length ? position : -1;
    }

    @Override
    public int getAgentID() {
        return 0;
    }

    @Override
    public boolean isNeighbor() {
        return false;
    }

    @Override
    public boolean isBoxAt() {
        return false;
    }

    @Override
    public boolean isAgentAt() {
        return false;
    }

    @Override
    public boolean isFree() {
        return false;
    }

    @Override
    public boolean isGoalCompleted() {
        return false;
    }

    @Override
    public boolean getBoxLetter() {
        return false;
    }

    @Override
    public boolean getBoxColor() {
        return false;
    }

    @Override
    public boolean getGoalLetter() {
        return false;
    }
}
