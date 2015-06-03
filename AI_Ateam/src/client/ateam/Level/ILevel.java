package client.ateam.Level;

import client.ateam.Level.Models.Agent;
import client.ateam.Level.Models.Box;
import client.ateam.Level.Models.Goal;
import client.ateam.Task;
import client.ateam.projectEnum.Color;

import java.awt.*;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lasse on 24-04-2015.
 */
public interface ILevel {

    int getAgentID();
    boolean isNeighbor(int curRow, int curCol,int neighborRow, int neighborCol);
    boolean isBoxAt(char boxLetter, int row, int col);
    boolean isAgentAt(int agentId, int row, int col);
//    boolean isFree(int row, int col);
    boolean isFree(Point cell);
    boolean isGoalCompleted();
    public char getBoxLetter(int row, int col);
    public Color getBoxColor(int row, int col);
    public char getGoalLetter(int row, int col);
    public void ReadMap() throws Exception;
    public ArrayList<Agent> getAgents();
    public Agent getSpecificAgent(Cell cell);
    public ArrayList<Box> getBoxes();
    public Box getSpecificBox(Cell cell);
    public ArrayList<Goal> getGoals();
    public ArrayList<Task> getTasks();

    public void executeMoveAction(int agentId, Point currentCell, Point tarCell);
    public void executePushAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point boxTarCell);
    public void executePullAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point tarCell);
}
