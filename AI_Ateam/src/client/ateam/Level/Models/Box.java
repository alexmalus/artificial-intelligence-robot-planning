package client.ateam.Level.Models;

import client.ateam.projectEnum.Color;

/**
 * Created by Lasse on 24-04-2015.
 */

public class Box{
    public char boxLetter;
    public Color color;
    public int row;
    public int column;

    public Box(char boxLetter,Color color, int row, int column){
        this.boxLetter = boxLetter;
        this.color = color;
        this.row = row;
        this.column = column;

    }
    


}


//public class Box {
//
//    private char boxLetter;
//    /**
//     * Return the number of boxes in the map
//     */
//    public static int getNumberOfBoxes() {
//        return ArrayLevel.boxColors.length;
//    }
//
//
//    /**
//     * Returns the letter of a box in a ASCII or UTF-8 char
//     * NOTE: The letter returned is uppercase!
//     * @param field
//     * @return
//     */
//    public static char getBoxLetter(int field) {
//        if (ArrayLevel.isBox(field)) {
//            return (char) (((field >> 4) & 0x1F) + 0x41);
//        } else {
//            System.err.println("getBoxLetter: provided field do not contain a box!");
//            return '0';
//        }
//    }
//
//    /**
//     * Return the number representing the color of a box
//     * @param field
//     * @return
//     */
//    public static int getBoxColor(int field) {
//        char boxLetter = ArrayLevel.getBoxLetter(field);
//        if (boxLetter == '0') return -1;
//        return boxColors[boxLetter - 0x41];
//    }
//
//
//    /**
//     * Return the Box ID from a position that contains a box,
//     * postion in real map
//     * @param position The position of the box, NOT the field!
//     * @return The Box ID, else -1
//     */
//
//    public static int getBoxIdFromPosition(int position) {
//        if (boxesArrayList.contains(position))
//            return boxesArrayList.indexOf(position);
//        else {
//            System.err.println("getBoxIdFromPosition: provided field do not contain a box!");
//            return -1;
//        }
//    }
//
//    /**
//     * return true if field has a box
//     * @param field
//     * @return
//     */
//    public static boolean isBox(int field) {
//        return ((field & 0x2) != 0) ? true : false;
//    }
//
//
//}
