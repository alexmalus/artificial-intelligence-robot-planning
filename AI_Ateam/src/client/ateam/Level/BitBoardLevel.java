package client.ateam.Level;

import client.ateam.projectEnum.Direction;

import java.util.*;

/**
 * Created by Lasse & Joakim on 24-04-2015.
 */
public class BitBoardLevel implements ILevel {

    private static BitBoardLevel level;
    private static int height;

    /**
     *
     *
     */
    public static int[] realMap; //The current parsed map

    private static int width;
    public final static int BLUE 	= 0x0;
    public final static int RED 	= 0x2;
    public final static int GREEN 	= 0x4;
    public final static int CYAN 	= 0x6;
    public final static int MAGENTA = 0x8;
    public final static int ORANGE 	= 0xA;
    public final static int PINK 	= 0xC;
    public final static int YELLOW 	= 0xE;

    //BLUE, RED, GREEN, CYAN, MAGENTA, ORANGE, PINK, YELLOW

    /**
     * Array with character a=0,b=1 etc. as index and color as value
     */
    private static int[] boxColors;

    /**
     * Array with agent id as index and color as value
     */
    private static int[] agentColors;

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
    private BitBoardLevel(){

    }

    /**
     *
     * @return height of level, will not be set before loadLevel
     */
    public static int getHeight(){
        return BitBoardLevel.height;
    }
    /**
     *
     * @return width of level, will not be set before loadLevel
     */
    public static int getWidth(){
        return BitBoardLevel.width;
    }

    /**
     * Return the number of boxes in the map
     */
    public static int getNumberOfBoxes() {
        return BitBoardLevel.boxColors.length;
    }

    /**
     * Return the number of completed goals in the level
     * @param map the reference to the map
     */
    public static int getNumberOfBoxesAtGoal(int map[]) {
        return 0;
    }

    /**
     * Get the the color of an agent in the level.
     * @param agentId The Id (number) if the agent
     * @return A integer representing the color of the agent.
     */
    public static int getAgentColor(int agentId) {
        return BitBoardLevel.agentColors[agentId];
    }

    /**
     * Get the position of a box on the REAL MAP
     * based on its Id.
     * @param boxId
     * @return the box position or -1 if it could not be found
     */
    public static int getBoxPositionFromId(int boxId) {
        if (boxId >=0 && boxId < boxesArrayList.size()) {
            int pos = boxesArrayList.get(boxId);
            return pos;
        }
        System.err.println("getBoxPositionFromId: Box with id: "+boxId+" could not be found!");
        return -1;
    }

    /**
     * Return a goal letter from a goal position
     * NOTE: The letter returned is lowercase!
     * @param field the field to test
     * @return The ASCII / UTF-8 value of the letter
     */
    public static char getGoalLetter(int field) {
        if (BitBoardLevel.isGoal(field)) {
            return (char) ((field >> 21)+0x61);
        }
        else {
            System.err.println("getGoalLetter: provided field do not contain a goal!");
            return '0';
        }
    }

    /**
     * Returns the letter of a box in a ASCII or UTF-8 char
     * NOTE: The letter returned is uppercase!
     * @param field
     * @return
     */
    public static char getBoxLetter(int field) {
        if (BitBoardLevel.isBox(field)) {
            return (char) (((field >> 4) & 0x1F) + 0x41);
        }
        else {
            System.err.println("getBoxLetter: provided field do not contain a box!");
            return '0';
        }
    }

    /**
     * Return the number representing the color of a box
     * @param field
     * @return
     */
    public static int getBoxColor(int field) {
        char boxLetter = BitBoardLevel.getBoxLetter(field);
        if (boxLetter == '0') return -1;
        return boxColors[boxLetter - 0x41];
    }


    /**
     * Return the Box ID from a position that contains a box,
     * postion in real map
     * @param position The position of the box, NOT the field!
     * @return The Box ID, else -1
     */
    public static int getBoxIdFromPosition(int position) {
        if (boxesArrayList.contains(position))
            return boxesArrayList.indexOf(position);
        else {
            System.err.println("getBoxIdFromPosition: provided field do not contain a box!");
            return -1;
        }
    }

    /**
     * Get the agent id from the the agent on the provided field
     * @param field
     * @return An integer with the agent id (not ascii or utf-8)
     */
    public static int getAgentId(int field) {
        if (BitBoardLevel.isAgent(field)) {
            return (int) (field >> 13) & 0xF;
        }
        else {
            System.err.println("getAgentId: provided field do not contain an agent!");
            return -1;
        }
    }

    /**
     * If you have a single number index to the level map array, this will translate the global index of a row index.
     * Use this to translate a global index to a row and column position.
     * @param index The global index
     * @return The row number of this index
     */
    public static int getRowFromIndex(int index) {
        return index / BitBoardLevel.width; //Integer division
    }

    /**
     * Translate a global index into a column number. Use this in conjunction with getRowFromIndex().
     * @param index Global index of a position
     * @return The column number of the global index
     */
    public static int getColumnFromIndex(int index) {
        return index % BitBoardLevel.width;
    }

    /**
     * Get the class instance
     * @return instance of Level
     */
    public static BitBoardLevel getSingletonObject(){
        if(level == null){
            level = new BitBoardLevel();
        }
        return level;
    }

    /**
     * Test if an integer field is empty.
     * @param field
     * @return boolean true is empty
     */
    public static boolean isEmpty(int field) {
        return ((field & 0x7) == 0) ? true : false;
    }

    /**
     * return true if field has a box
     * @param field
     * @return
     */
    public static boolean isBox(int field) {
        return ((field & 0x2) != 0) ? true : false;
    }

    /**
     * return true is field has a goal
     * @param field
     * @return
     */
    public static boolean isGoal(int field) {
        return ((field & 0x8) != 0) ? true : false;
    }

    /**
     * Return true is field has an agent
     * @param field
     * @return
     */
    public static boolean isAgent(int field) {
        return ((field & 0x4) != 0) ? true : false;
    }

    /**
     * Return true if field is a WALL
     * @param field
     * @return
     */
    public static boolean isWall(int field) {
        return ((field & 0x1) != 0) ? true : false;
    }

    /**
     * * nnnn nngg gggc cccp pppc cccb bbbb GPBW
     * N=box id
     * G=goal letter
     * C=player color
     * P=player number
     * C=box color
     * B= box letter
     * First four is one hot encoding for wall, box, player or goal.
     *
     * b = box char (ASCII hex - 0x40)
     * c = box color, defined in static int in class
     * p = player int
     * c = player color, defined in static int in class
     * g = goalLetter, up in as (ASCII hex - 0x60)
     * x = null
     *
     * @param s Input string to parse
     */
    public int[] loadFromString(String s){

        if (BitBoardLevel.realMap != null) return BitBoardLevel.realMap;

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

        int[] internalMappoing = new int[BitBoardLevel.width * BitBoardLevel.height];// = new ArrayList<Integer>();
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
                    internalMappoing[row * BitBoardLevel.width + column] = 0x1;
                } else if (symbol.equals(" ")){
                    internalMappoing[row * BitBoardLevel.width + column] = 0x0;
                    // add all empty position into this cache position list
                    cachePositionArrayList.add(row * BitBoardLevel.width + column);
                } else{
                    char a = symbol.charAt(0);

                    if (a==' '){
                        internalMappoing[row * BitBoardLevel.width + column] = 0x0;
                    }
                    //if it is a box
                    else if (a>='A' && a<='Z'){
                        //TODO add color
                        int boxLetter = 0x2 + (((int)a - 0x41) << 4) + (boxColors[(int)a - 0x41] << 9);

                        internalMappoing[row * BitBoardLevel.width + column] = boxLetter;
                        boxesArrayList.add(row * BitBoardLevel.width + column);
                    }
                    //it is a player
                    else if (a>='0' && a<='9'){
                        int playerNumber = (int)a - 0x30;
                        int playerLetter = 0x4 + (playerNumber << 13) + (agentColors[playerNumber] << 17);
                        internalMappoing[row * BitBoardLevel.width + column] = playerLetter;
                        agents[playerNumber] = row * BitBoardLevel.width + column;
                    }
                    //if it is a goal
                    else if (a>='a' && a<='z'){
                        //TODO add color
                        int goalLetter = 0x8 + (((int)a - 0x61) << 21);
                        internalMappoing[row * BitBoardLevel.width + column] = goalLetter;
                        goalsArrayList.add(row * BitBoardLevel.width + column);
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

        BitBoardLevel.realMap = internalMappoing;
        return internalMappoing;
    }
    private void setFileLength(Scanner input) {
        String t = null;
        BitBoardLevel.width = -1;
        while (input.hasNextLine()){
            t = input.nextLine();
            if(t.equals(""))
                continue;
            if(t.charAt(0)!='+' && t.charAt(0)!=' ')
                continue;
            BitBoardLevel.height++;
            //Search width in current line
            int tempWidth = 0;
            Scanner tokenizer = new Scanner(t);
            tokenizer.useDelimiter("");
            while(tokenizer.hasNext()){
                tokenizer.next();
                tempWidth++;
            }
            if (tempWidth > BitBoardLevel.width) BitBoardLevel.width = tempWidth;
        }



    }

    private void extractColors(Scanner input1) {
        boxColors = new int[26];
        agentColors = new int[10];

        while (input1.hasNextLine()){
            String t = input1.nextLine();
            if(t.charAt(0)=='+' || t.charAt(0)==' '){
                return;
            }

            Scanner tokenizer = new Scanner(t);

            int color;
            if(tokenizer.hasNext()){

                String c = tokenizer.next();
                if(c.equals("red:")){
                    color=RED;
                } else if(c.equals("green:")){
                    color=GREEN;
                } else if(c.equals("magenta:")){
                    color=MAGENTA;
                } else if(c.equals("cyan:")){
                    color=CYAN;
                } else if(c.equals("orange:")){
                    color=ORANGE;
                } else if(c.equals("pink:")){
                    color=PINK;
                } else if(c.equals("yellow:")){
                    color=YELLOW;
                } else {
                    color=BLUE;
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
                        int indx = ((int) item) - 0x41;
                        boxColors[indx] = color;//	boxes.add(new Box( -1,-1 ,item,color));
                    }
                    //otherwise we have a robot
                    else {
                        int indx = ((int)item) - 48;
                        agentColors[indx] = color;
                    }
                }
            }
        }
    }

    /**
     *
     * @return array of agent positions , null before loadLevel
     *
     */
    public ArrayList<Integer> getAgents(){
        return agentsArrayList;
    }

    /**
     * Determines if there is only one agent on the map currently.
     * @return true if there is excactly 1 agent on the map.
     */
    public static boolean singleAgentMode() {
        return agentsArrayList.size() == 1;
    }

    /**
     *
     * @return , null before loadLevel
     */
    public ArrayList<Integer> getBoxes(){
        return boxesArrayList;
    }

    /**
     *
     * @return a array of goalPositions , null before loadLevel
     */
    public ArrayList<Integer> getGoals(){
        return goalsArrayList;
    }


    public static ArrayList<Integer> getCachePosition(){
        return cachePositionArrayList;
    }

    public static String color2string(int color) {
        switch (color) {
            case 0x0:
                return "BLUE";
            case 0x2:
                return "RED";
            case 0x4:
                return "GREEN";
            case 0x6:
                return "CYAN";
            case 0x8:
                return "MAGENTA";
            case 0xA:
                return "ORANGE";
            case 0xC:
                return "PINK";
            case 0xE:
                return "YELLOW";
            default:
                break;
        }
        return "UNKNOWN-COLOR";
    }

    /**
     * Return a free (empty) position next to a given position.
     * This could be the position to the left, right, above or below the given position.
     * @param position The position to find a neighbor to
     * @param map The map to use
     * @return The new POSITION on the provided map, return -1 if no free position could be found.
     */
    public static int getFreePosNextTo(int position, int map[]) {
        if (position-1 >= 0 && BitBoardLevel.isEmpty(map[position-1]))
            return position-1;
        else if (position+1 < map.length && BitBoardLevel.isEmpty(map[position+1]))
            return position+1;
        else if (position-BitBoardLevel.width > 0 && BitBoardLevel.isEmpty(map[position-BitBoardLevel.width]))
            return position-BitBoardLevel.width;
        else if (position+BitBoardLevel.width < map.length && BitBoardLevel.isEmpty(map[position+BitBoardLevel.width]))
            return position+BitBoardLevel.width;
        else
            return -1;

    }

    /**
     * Check if two positions are neighbors
     * @param curPos
     * @param neighborPos
     * @return
     */
    public static boolean isNeighbor(int curPos, int neighborPos) {
        if (curPos >= BitBoardLevel.realMap.length || curPos < 0 || neighborPos < 0 || neighborPos >= BitBoardLevel.realMap.length)
            return false; //outside bounds of map
        if (curPos-1 == neighborPos || curPos+1 == neighborPos) // right left neighbor
            return true;
        if (curPos-BitBoardLevel.getWidth() == neighborPos || curPos+BitBoardLevel.getWidth() == neighborPos) // up down neighbor
            return true;

        return false;
    }

    /**
     * Given a position in a map, this function will return the measure of freedom surrounding it.
     * Free field will increment by 2, free goal field increment by one
     * @param position
     * @param map
     * @return
     */
    public static int freedomDegree(int position, Map map) {
        int free = 0;
        if (position-1 >= 0 && BitBoardLevel.isEmpty(map.get(position-1)))
            free += (BitBoardLevel.isGoal(map.get(position-1))) ? 1 : 2;
        if ((position+1 < map.length && BitBoardLevel.isEmpty(map.get(position+1))))
            free += (BitBoardLevel.isGoal(map.get(position+1))) ? 1 : 2;
        if (position-BitBoardLevel.getWidth() >= 0 && BitBoardLevel.isEmpty(map.get(position-BitBoardLevel.getWidth())))
            free += (BitBoardLevel.isGoal(map.get(position-BitBoardLevel.getWidth()))) ? 1 : 2;
        if (position+BitBoardLevel.getWidth() < map.length && BitBoardLevel.isEmpty(map.get(position+BitBoardLevel.getWidth())))
            free += (BitBoardLevel.isGoal(map.get(position+BitBoardLevel.getWidth()))) ? 1 : 2;

        return free;
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

    /**
     * Test whether or not the given field contains a box which is on its goal
     * @param field The field to test
     * @return true if the field has a box on its goal
     */
    public static boolean boxIsOnItsGoal(int field) {
        if (BitBoardLevel.isGoal(field)
                && Character.toLowerCase(BitBoardLevel.getBoxLetter(field))
                == Character.toLowerCase(BitBoardLevel.getGoalLetter(field))
                ) {
            return true;
        }
        else
            return false;
    }

    /**
     * Add a wall to a given position on the map
     *
     */
    public static void addWall(int map[], int position) {
        if (!BitBoardLevel.isEmpty(map[position]))
            System.err.println("Level: Wall added to non empty position: "+BitBoardLevel.coordsToString(position));
        map[position] |= 0x1;
    }

    /**
     * Pretty print a position on the map
     * @param index
     * @return coordinate string
     */
    public static String coordsToString(int index) {
        return "("+(BitBoardLevel.getColumnFromIndex(index)+1)+","+(BitBoardLevel.getRowFromIndex(index)+1)+")";
    }

    public static boolean isActionApplicable(int[] map, Action action, int agentPos, boolean silent) {
        return BitBoardLevel.isActionApplicable(new Map(map,null), action, agentPos, silent);
    }

    public static boolean isActionApplicable(Map map, Action action, int agentPos, boolean silent){
        int field;
        int fieldPos;
        int box;
        int agent;
        switch (action.type()) {

            case MOVE:
                fieldPos = getPosFromPosInDirection(agentPos, action.direction());// action.newY(y) * width + action.newX(x);
                field = map.get(fieldPos);
                if(isEmpty(field)){
                    agent = map.get(agentPos);
                    if((agent & 0x4) != 0x4)
                        break;

                    return true;
                }
                break;

            case PULL:
                fieldPos = getPosFromPosInDirection(agentPos, action.direction());
                field = map.get(fieldPos);
                //Box Dir, the direction of the box
                //agentdirection the direction the agent moves
                if(isEmpty(field)){
                    int boxPos = getPosFromPosInDirection(agentPos, action.boxDirection());

                    box = map.get(boxPos);
                    agent = map.get(agentPos);

                    if (!isBox(box) || !isAgent(agent)){
                        if(!silent)
                            System.err.println("PULL: box or agent not on right position - boxpos: "
                                    + BitBoardLevel.coordsToString(boxPos)
                                    + " agentpos: " + BitBoardLevel.coordsToString(agentPos));
                        break;
                    }

                    if( getBoxColor(box) != getAgentColor(getAgentId(agent))){
                        if(!silent)
                            System.err.println("agent and box not same color");
                        break;
                    }

                    return true;

                }
                break;
            case PUSH:
                //Box Dir, the direction the box moves
                //agten dir, the direction of the box

                //get the boc, should be in the agnet direction
                int boxPos = getPosFromPosInDirection(agentPos, action.direction());

                //Init box and agent
                box = map.get(boxPos);
                agent = map.get(agentPos);

                //check if box and agent is what they say they are
                if (!isBox(box) || !isAgent(agent)){
                    if(!silent)
                        System.err.println("PUSH: box or agent not on right position - boxpos: "
                                + Level.coordsToString(boxPos)
                                + " agentpos: " + Level.coordsToString(agentPos));
                    break;
                }
                //Check if agent and box is same color
                if( getBoxColor(box) != getAgentColor(getAgentId(agent))){
                    if(!silent)
                        System.err.println("agent and box not same color");
                    break;
                }

                fieldPos = getPosFromPosInDirection(boxPos, action.boxDirection());
                field = map.get(fieldPos);

                //Check if  the field the box is moving to is empty
                if(!isEmpty(field)){
                    if(!silent)
                        System.err.println("try to push box to non empty field");
                    break;
                }

                return true;

            case NOOP:
                return true;

            default:
                break;
        }

        return false;
    }

    /**
     * Out-of-place version of applyAction
     * @param map
     * @param action
     * @param agentPos
     * @param silent
     * @return
     */
    public static boolean applyAction(int[] map, Action action, int agentPos, boolean silent) {
        return BitBoardLevel.applyAction(new Map(map, null), action, agentPos, silent);
    }

    /**
     * * nnnn nngg gggc cccp pppc cccb bbbb GPBW
     * N=box id
     * G=goal letter
     * C=player color
     * P=player number
     * C=box color
     * B= box letter
     * First four is one hot encoding for wall, box, player or goal
     *
     * @param map applied action are changed inline in this map
     * @param action
     * @param agentPos - agent index
     * @param silent Set to true if you dont want console errors
     * @param inline Set to true if you want in-place editing of map, false is out-of-place.
     * @return
     */
    public static boolean applyAction(Map map, Action action, int agentPos, boolean silent) {
        if (!BitBoardLevel.isActionApplicable(map,action,agentPos,silent))
            return false;

        if (action.type() == ActionType.NOOP)
            return true;

        //If the action is applicable a new map is created and returned
        int boxPos;
        int fieldPos = getPosFromPosInDirection(agentPos, action.direction());

        int box;
        int agent = map.get(agentPos);

        switch (action.type()) {
            case MOVE:

                //add agent to new field
                map.set(fieldPos, map.get(fieldPos) | (agent & 0x1FE004));
                map.set(agentPos, map.get(agentPos) & 0xFFE01FFB);
                break;

            case PULL:
                boxPos = getPosFromPosInDirection(agentPos, action.boxDirection());
                box = map.get(boxPos);

                //addAgent
                map.set(fieldPos, map.get(fieldPos) | (agent & 0x1FE004));
                //Remove agent and add box
                map.set(agentPos, map.get(agentPos) & 0xFFE01FFB);
                map.set(agentPos, map.get(agentPos) | (box & 0x1FF2));
                //remove box
                map.set(boxPos, map.get(boxPos) & 0xFFFFE00D);

                //If we are changing RealMap, update the boxesArrayList
                if (Level.realMap == map.map && map.isRoot()) {
                    int bId = Level.getBoxIdFromPosition(boxPos);
                    Level.boxesArrayList.set(bId, agentPos);
                }
                break;

            case PUSH:
                boxPos = getPosFromPosInDirection(agentPos, action.direction());
                box = map.get(boxPos);

                fieldPos = getPosFromPosInDirection(boxPos, action.boxDirection());
                //addBox
                map.set(fieldPos, map.get(fieldPos) | (box & 0x1FF2));
                //Remove box and add Agent
                map.set(boxPos, map.get(boxPos) & 0xFFFFE00D);
                map.set(boxPos, map.get(boxPos) | (agent & 0x1FE004));
                //remove Agent
                map.set(agentPos, map.get(agentPos) & 0xFFE01FFB);

                //If we are changing RealMap, update the boxesArrayList
                if (BitBoardLevel.realMap == map.map && map.isRoot()) {
                    int bId = Level.getBoxIdFromPosition(boxPos);
                    BitBoardLevel.boxesArrayList.set(bId, fieldPos);
                }

                break;

            default:

                break;
        }
        return true;
    }

    public static int getIndexFromColoumnAndRow(int playerx, int playery) {

        return playery * width + playerx;
    }

    private static class DistanceBFS {
        private int[] distancesToGoal;
        private LinkedList<Integer> frontSet = new LinkedList<Integer>();
        private int goal;

        public DistanceBFS(int goal) {
            this.goal = goal;
            distancesToGoal = new int[realMap.length];
            frontSet.add(goal);
        }

        public int shortestDistanceOnMap(int from) {
            if (from == goal)
                return 0;

            int dist = distancesToGoal[from];
            while (dist == 0 && frontSet.size() > 0) {
                dist = distancesToGoal[from];
				/*if (dist != 0)
					break;*/

                int next = frontSet.poll();
                int thisDist = distancesToGoal[next];

                List<Integer> neighbors = Arrays.asList(
                        BitBoardLevel.getPosFromPosInDirection(next, ActionDirection.EAST),
                        BitBoardLevel.getPosFromPosInDirection(next, ActionDirection.WEST),
                        BitBoardLevel.getPosFromPosInDirection(next, ActionDirection.NORTH),
                        BitBoardLevel.getPosFromPosInDirection(next, ActionDirection.SOUTH));

                for (int i : neighbors) {
                    if (i > 0 && !BitBoardLevel.isWall(realMap[i]) && distancesToGoal[i] == 0 && i != goal) {
                        frontSet.add(i);
                        distancesToGoal[i] = thisDist+1;
                    }
                }
            }

            if (dist == 0)
                System.err.print("");

            return dist != 0 ? dist : Integer.MAX_VALUE;
        }
    }

    private static TreeMap<Integer,DistanceBFS> distances = new TreeMap<Integer,DistanceBFS>();

    public static int shortestDistanceOnMap(int from, int to) {
        if (from == to)
            return 0;

        DistanceBFS dist = distances.get(to);
        if (dist == null) {
            dist = new DistanceBFS(to);
            distances.put(to, dist);
        }

        return dist.shortestDistanceOnMap(from);
    }

    public static void resetShortestDistances() {
        distances = new TreeMap<Integer,DistanceBFS>();
    }

    // shortest distance list contains the positions that agents and boxes pass to the goal
    // these positions had better not be used as contingency positions
    public static ArrayList<Integer> shortestPathList(int from, int to) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(from);

        if (from == to)
            return path;

        int dist = BitBoardLevel.shortestDistanceOnMap(from, to);

        List<Integer> neighbors = Arrays.asList(
                BitBoardLevel.getPosFromPosInDirection(from, ActionDirection.EAST),
                BitBoardLevel.getPosFromPosInDirection(from, ActionDirection.WEST),
                BitBoardLevel.getPosFromPosInDirection(from, ActionDirection.NORTH),
                BitBoardLevel.getPosFromPosInDirection(from, ActionDirection.SOUTH));

        for (Integer i : neighbors) {
            if (BitBoardLevel.shortestDistanceOnMap(i, to) == dist-1) {
                path.addAll(BitBoardLevel.shortestPathList(i, to));
                break;
            }
        }

        return path;
    }

    private static boolean isObstructing(int field, int color) {
        if (BitBoardLevel.isEmpty(field) /*&& !Level.isGoal(field)*/)
            return false;
        if (BitBoardLevel.isBox(field) && BitBoardLevel.getBoxColor(field) == color)
            return false;
        if (BitBoardLevel.isAgent(field))
            return false;
        return true;
    }

    public static boolean isObstructing(int pos, Map map, int color) {
        int up = BitBoardLevel.getPosFromPosInDirection(pos, ActionDirection.NORTH);
        int down = BitBoardLevel.getPosFromPosInDirection(pos, ActionDirection.SOUTH);
        int left = BitBoardLevel.getPosFromPosInDirection(pos, ActionDirection.WEST);
        int right = BitBoardLevel.getPosFromPosInDirection(pos, ActionDirection.EAST);
        int upleft = BitBoardLevel.getPosFromPosInDirection(up, ActionDirection.WEST);
        int upright = BitBoardLevel.getPosFromPosInDirection(up, ActionDirection.EAST);
        int downleft = BitBoardLevel.getPosFromPosInDirection(down, ActionDirection.WEST);
        int downright = BitBoardLevel.getPosFromPosInDirection(down, ActionDirection.EAST);

        up = map.get(up);
        down = map.get(down);
        left = map.get(left);
        right = map.get(right);
        upleft = map.get(upleft);
        upright = map.get(upright);
        downleft = map.get(downleft);
        downright = map.get(downright);

        if (BitBoardLevel.isGoal(map.get(pos)))
            return true;

        if (!BitBoardLevel.isObstructing(up, color) && !BitBoardLevel.isObstructing(left, color) && BitBoardLevel.isObstructing(upleft, color))
            return true;
        if (!BitBoardLevel.isObstructing(down, color) && !BitBoardLevel.isObstructing(left, color) && BitBoardLevel.isObstructing(downleft, color))
            return true;
        if (!BitBoardLevel.isObstructing(up, color) && !BitBoardLevel.isObstructing(right, color) && BitBoardLevel.isObstructing(upright, color))
            return true;
        if (!BitBoardLevel.isObstructing(down, color) && !BitBoardLevel.isObstructing(right, color) && BitBoardLevel.isObstructing(downright, color))
            return true;

        if (!BitBoardLevel.isObstructing(up, color) && !BitBoardLevel.isObstructing(down, color) &&
                BitBoardLevel.isObstructing(left, color) && BitBoardLevel.isObstructing(right, color))
            return true;
        if (BitBoardLevel.isObstructing(up, color) && BitBoardLevel.isObstructing(down, color) &&
                !BitBoardLevel.isObstructing(left, color) && !BitBoardLevel.isObstructing(right, color))
            return true;

        if (BitBoardLevel.isObstructing(up, color) && BitBoardLevel.isObstructing(down, color)){
            if (BitBoardLevel.isBox(right) && BitBoardLevel.getBoxColor(right) != color)
                return true;
            if (BitBoardLevel.isBox(left) && BitBoardLevel.getBoxColor(left) != color)
                return true;
        }
        if (BitBoardLevel.isObstructing(right, color) && BitBoardLevel.isObstructing(left, color)){
            if (BitBoardLevel.isBox(up) && BitBoardLevel.getBoxColor(up) != color)
                return true;
            if (BitBoardLevel.isBox(down) && BitBoardLevel.getBoxColor(down) != color)
                return true;
        }

		/*int numObs = 0;
		if (Level.isObstructing(up, color)) ++numObs;
		if (Level.isObstructing(down, color)) ++numObs;
		if (Level.isObstructing(left, color)) ++numObs;
		if (Level.isObstructing(right, color)) ++numObs;

		return numObs != 3;*/
        return false;
    }

    public static boolean isBlockedIn(int pos, Map map) {
        int up = map.get(BitBoardLevel.getPosFromPosInDirection(pos, ActionDirection.NORTH));
        int down = map.get(BitBoardLevel.getPosFromPosInDirection(pos, ActionDirection.SOUTH));
        int left = map.get(BitBoardLevel.getPosFromPosInDirection(pos, ActionDirection.WEST));
        int right = map.get(BitBoardLevel.getPosFromPosInDirection(pos, ActionDirection.EAST));

        return !BitBoardLevel.isEmpty(up) && !BitBoardLevel.isEmpty(down) && !BitBoardLevel.isEmpty(left) && !BitBoardLevel.isEmpty(right);
    }

    public BitBoardLevel(ArrayList<String> strLevel){

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
