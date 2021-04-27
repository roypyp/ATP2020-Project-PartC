package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private int playerPosRowIdx;
    private int playerPosColIdx;
    public StringProperty spPlayerPosRow = new SimpleStringProperty();
    public StringProperty spPlayerPosCol = new SimpleStringProperty();

    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            if(arg.equals("playerMove")){
                updateRowsAndCols();
                setChanged();
                notifyObservers("playerMove");//call view.update()
            }
            if (arg.equals("generated")){
                updateRowsAndCols();
                setChanged();
                notifyObservers("generated");//call view.update()
            }
            if ((arg.equals("solved"))){
               //todo convert solution to int[][] for graphic display
                setChanged();
                notifyObservers("solved");//call view.update()
            }
            if((arg.equals("loaded"))){
                updateRowsAndCols();
                setChanged();
                notifyObservers("loaded");//call view.update()//todo pass the object instead
            }
        }
    }


    public boolean validateMazeGenerationParams(int row, int col) {
        if (row <= 1 || col <= 1) {
            return false;
        }
        return true;
    }

    private void updateRowsAndCols(){
        playerPosRowIdx = model.getPlayerPosRowIdx();
        playerPosColIdx = model.getPlayerPosColIdx();
        spPlayerPosRow.set(playerPosRowIdx + "");
        spPlayerPosCol.set(playerPosColIdx + "");
    }

    public Maze getGameObject(){
        return model.getGameObject();
    }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }
    public void stopServers(){
        model.stopServers();
    }

    public void newGame(int row, int col){
        model.generateMaze(row, col);
    }
    public void solveMaze(){
        model.solveMaze();
    }

    public void movePlayer(KeyCode direction){
        model.movePlayerModelLogic(direction);
    }

    public int[][] getMaze() {
        return model.getMazeGrid();
    }

    public int getPlayerPosRow() {
        return playerPosRowIdx;
    }

    public int getPlayerPosCol() {
        return playerPosColIdx;
    }

    public void setPlayerPosRowIdx(Integer y){
        spPlayerPosRow.setValue(y.toString());
    }

    public void setCharacterPositionColumnIndexIndex(Integer x){
        spPlayerPosRow.setValue(x.toString());
    }

    public Solution getSolution(){ return model.getSolution() ; }
    public void SaveGame(File saveFile) throws IOException {
        model.saveMaze(saveFile);
    }
    public void loadGame(File file) throws IOException, ClassNotFoundException {
        model.loadMaze(file);
    }

}
