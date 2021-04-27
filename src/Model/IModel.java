package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;

public interface IModel {
    public void generateMaze(int width,int height);
    public void solveMaze();
    public int[][] getMazeGrid();
    public Maze getGameObject();
    public void movePlayerModelLogic(KeyCode movement);
    public int getPlayerPosRowIdx();
    public int getPlayerPosColIdx();
    public void stopServers();
    public int getMazeGoalPosRowIdx();
    public int getMazeGoalPosColIdx();
    public int getStartPositionRow();
    public int getStartPositionColumn();
    public void saveMaze(File saveFile);
    public void loadMaze(File file);
    public boolean isPlayerAtGoalPosition();

    public Solution getSolution();//todo get rid of this getSolution() later

}
