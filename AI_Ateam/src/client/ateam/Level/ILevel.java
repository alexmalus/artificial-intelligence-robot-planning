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

public interface ILevel {

    boolean isNeighbor(int curRow, int curCol,int neighborRow, int neighborCol);
    boolean isFree(Point cell);
    public void ReadMap() throws Exception;
    public ArrayList<Agent> getAgents();
    public ArrayList<Box> getBoxes();
    public ArrayList<Goal> getGoals();
    public ArrayList<Task> getTasks();
    public Agent getSpecificAgent(Cell cell);
    public Box getSpecificBox(Cell cell);

    public void executeMoveAction(int agentId, Point currentCell, Point tarCell);
    public void executePushAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point boxTarCell);
    public void executePullAction(int agentId, char boxLetter, Point currentCell, Point boxCell, Point tarCell);
}
