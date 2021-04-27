package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import javafx.scene.input.KeyCode;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;

public class MyModel extends Observable implements IModel {
    /* Model data members */
    private Server generatorServer;
    private Server solverServer;
    private Maze maze ;
    private Solution solution;
    private int playerPosRowIdx = 1;
    private int playerPosColIdx = 1;
    private int mazeGoalPosRowIdx = 1;
    private int mazeGoalPosColIdx = 1;
    private int[][] grid;
    private boolean isMazeExist=false;
    private boolean serversOn = false;
    /* End Model data members */

    /** Ctor, creates 2 servers: a generator and a solver */
    public MyModel() {
        generatorServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solverServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }
    /**
     * calls Server.start() for both servers */
    public void startServers() {
        serversOn = true;
        generatorServer.start();
        solverServer.start();
    }
    /**
     * calls Server.stop() for both servers */
    public void stopServers() {
        if(serversOn){
        generatorServer.stop();
        solverServer.stop();
        }
    }
    /**
     * generates a Maze instance with given dimensions using communication with Server (ServerStrategyGenerateMaze)
     * Updates this.maze with the freshly created Maze (doesn't notify ViewModel)
     * @param numOfRows - int indicating rows dimensions
     * @param numOfCols - int indicating cols dimensions*/
    private void generateMazeThroughGeneratorServer(int numOfRows, int numOfCols) {
        try {
            /* Code from part-B test: "RunCommunicateWithServers" written by Aviad */
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{numOfRows, numOfCols};
                        /* write the desired Maze dimensions to the OutStream */
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        /* get compressed Maze from the InStream */
                        byte[] compressedMaze = (byte[])fromServer.readObject();
                        /*### Decompress the compressed-maze read from server ###*/
                        InputStream decompressorIS = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[1000000];//todo consider these dims: byte[numOfRows * numOfCols + 12] - logically no reason it won't be the size
                        /*Fill decompressedMaze with bytes*/
                        decompressorIS.read(decompressedMaze);
                        /*create new Maze */
                        Maze newMaze = new Maze(decompressedMaze);
                        /* update maze data member */
                        setMaze(newMaze);//this.maze = newMaze
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });//end of concrete Client ctor
            /* invoking the anonymous "clientStrategy" implemented above */
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    /**
     *public method for generating a Maze
     * @param numOfRows numOfRows for Maze
     * @param numOfCols numOfCols for Maze */
    @Override
    public void generateMaze(int numOfRows, int numOfCols) {
        /* generate the maze through client-server communication */
        generateMazeThroughGeneratorServer(numOfRows, numOfCols);
        /* UPDATE all relevant fields */
        grid = maze.getMazeGrid();
        setPlayerPosRowIdx(maze.getStartPosition().getRowIndex()); //playerStartPosRow = startPosRowIdx
        setPlayerPosColIdx(maze.getStartPosition().getColumnIndex()); //playerStartPosRow = startPosColIdx
        setMazeGoalPosRowIdx(maze.getGoalPosition().getRowIndex());
        setMazeGoalPosColIdx(maze.getGoalPosition().getColumnIndex());
        isMazeExist = true;
        setChanged();//-->hasChanged() will return True
        /* Notify ViewModel */
        notifyObservers("generated");//todo if not running properly: change notifyObservers() to notifyObservers(Object) and pass the Maze/Solution itself

        /*todo check that removing "updateFields" didn't screw everything up*/
        /*updateFields(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());*/
    }


    private void solveMazeThroughSolverServer() {
        try {
            /* Code from part-B test: "RunCommunicateWithServers" written by Aviad */
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution)fromServer.readObject();//do something with the solution
                        /*update solution so that mazeDisplayer can use getter to take it*/
                        solution = mazeSolution;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            /* invoking the anonymous "clientStrategy" implemented above */
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    /**
     * solve a maze */
    public void solveMaze(){
        if(isMazeExist){
        solveMazeThroughSolverServer();
        setChanged();
        notifyObservers("solved");}
    }

    @Override
    /** moves the player onKey pressed
     * valid keys are: all NUMPAD except 5, arrows   */
    public void movePlayerModelLogic(KeyCode direction) {
        int [][] mazeGrid = grid;
        switch (direction) {
            /*----------------------90 degrees moves---------------------*/
            case UP:
            case NUMPAD8:
                if(playerPosRowIdx >= 1 && mazeGrid[playerPosRowIdx -1][playerPosColIdx]!=1)
                    playerPosRowIdx--;
                break;
            case DOWN:
            case NUMPAD2:
                if(playerPosRowIdx +1 < mazeGrid.length && mazeGrid[playerPosRowIdx +1][playerPosColIdx]!=1)
                    playerPosRowIdx++;
                break;
            case RIGHT:
            case NUMPAD6:
                if(playerPosColIdx + 1 < mazeGrid[0].length && mazeGrid[playerPosRowIdx][playerPosColIdx +1] != 1)
                    playerPosColIdx++;
                break;
            case LEFT:
            case NUMPAD4:
                if(playerPosColIdx >= 1 && mazeGrid[playerPosRowIdx][playerPosColIdx -1]!=1)
                    playerPosColIdx--;
                break;
            /*----------------------diagonal moves---------------------*/
            case NUMPAD1:
                if(playerPosColIdx >= 1 && playerPosRowIdx + 1 < mazeGrid.length && mazeGrid[playerPosRowIdx + 1][playerPosColIdx -1] != 1) {
                    if ((mazeGrid[playerPosRowIdx][playerPosColIdx - 1] != 1) || ( mazeGrid[playerPosRowIdx +1][playerPosColIdx]!=1)) {
                        playerPosColIdx--;
                        playerPosRowIdx++;
                    }
                }
                break;
            case NUMPAD3:
                if(playerPosColIdx + 1 < mazeGrid[0].length && playerPosRowIdx +1 < mazeGrid.length && mazeGrid[playerPosRowIdx + 1][playerPosColIdx +1]!=1) {
                    if ((playerPosColIdx +1 < mazeGrid[0].length && mazeGrid[playerPosRowIdx][playerPosColIdx +1]!=1) || (playerPosRowIdx +1 < mazeGrid.length && mazeGrid[playerPosRowIdx +1][playerPosColIdx]!=1)){
                        playerPosColIdx++;
                        playerPosRowIdx++;
                    }
                }
                break;
            case NUMPAD9:
                if(playerPosColIdx +1 < mazeGrid[0].length && playerPosRowIdx >= 1 && mazeGrid[playerPosRowIdx - 1][playerPosColIdx +1]!=1) {
                    if ((playerPosColIdx +1 < mazeGrid[0].length && mazeGrid[playerPosRowIdx][playerPosColIdx +1]!=1) || (playerPosRowIdx >= 1 && mazeGrid[playerPosRowIdx -1][playerPosColIdx]!=1)) {
                        playerPosColIdx++;
                        playerPosRowIdx--;
                    }
                }
                break;
            case NUMPAD7:
                if(playerPosColIdx >=1 && playerPosRowIdx >= 1 && mazeGrid[playerPosRowIdx - 1][playerPosColIdx -1]!=1) {
                    if ((playerPosColIdx >= 1 && mazeGrid[playerPosRowIdx][playerPosColIdx -1]!=1) || (playerPosRowIdx >= 1 && mazeGrid[playerPosRowIdx -1][playerPosColIdx]!=1)){
                        playerPosColIdx--;
                        playerPosRowIdx--;
                    }
                }
                break;
        }
        setChanged();
        notifyObservers("playerMove");
    }
    /**
     * returns True if the player reached the end point of the maze game */
    //todo check Noa's equivalent method in ViewModel
    public boolean isPlayerAtGoalPosition(){
        if(playerPosRowIdx == mazeGoalPosRowIdx && playerPosColIdx == mazeGoalPosColIdx){
            return true;
        }
        return false;
    }

    /*------------- Getters ---------------------*/
    @Override
    public int getPlayerPosRowIdx() {
        return playerPosRowIdx;
    }
    @Override
    public int getPlayerPosColIdx() {
        return playerPosColIdx;
    }
    @Override
    public int getMazeGoalPosRowIdx() {
        return mazeGoalPosRowIdx;
    }
    @Override
    public int getMazeGoalPosColIdx() {
        return mazeGoalPosColIdx;
    }
    @Override
    public int getStartPositionRow() {
        return maze.getStartPosition().getRowIndex();
    }
    @Override
    public int getStartPositionColumn() {
        return maze.getStartPosition().getColumnIndex();
    }
    @Override
    public int[][] getMazeGrid() {
        return this.grid;
    }
    public Maze getGameObject(){
        return maze;
    }

    @Override
    public Solution getSolution() {//for MazeDisplayer Usage
        return solution;
    }
    /*------------- End Getters ---------------------*/


    /*------------- Setters ---------------------*/
    private void setMaze(Maze mazeObj) {
        this.maze = mazeObj;
    }
    public void setPlayerPosRowIdx(int PlayerPosRowIdx) {
        this.playerPosRowIdx = PlayerPosRowIdx;
    }
    public void setPlayerPosColIdx(int PlayerPosColIdx) {
        this.playerPosColIdx = PlayerPosColIdx;
    }
    public void setMazeGoalPosRowIdx(int mazeGoalPosRowIdx) {
        this.mazeGoalPosRowIdx = mazeGoalPosRowIdx;
    }
    public void setMazeGoalPosColIdx(int mazeGoalPosColIdx) {
        this.mazeGoalPosColIdx = mazeGoalPosColIdx;
    }
    /*------------- End Setters ---------------------*/



    /**
     * save the maze to a GIVEN FILE (passed from ViewController) */
    public void saveMaze(File saveFile){
        File endFile = new File(saveFile.getPath());
        try {
            /*game state params --> save to file*/
            endFile.createNewFile();
            StringBuilder  builder = new StringBuilder();
            builder.append(playerPosRowIdx+"\n");
            builder.append(playerPosColIdx+"\n");
            builder.append(mazeGoalPosRowIdx+"\n");
            builder.append(mazeGoalPosColIdx+"\n");
            builder.append(grid.length+"\n");
            builder.append(grid[0].length+"\n");
            /*write maze grid to file */
            for(int i = 0; i < grid.length; i++)
            {
                for(int j = 0; j < grid[0].length; j++)
                {
                    builder.append(grid[i][j]+"");
                    if(j < grid[0].length - 1)
                        builder.append(",");
                }
                builder.append("\n");
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.getPath()));
            writer.write(builder.toString());
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /** load the maze from saved file after user pressed the "load maze" menu item*/
    public void loadMaze(File file){
        int goalRowIdx = 0, goalColIdx = 0 , playerRowIdx = 0, playerColIdx= 0, mazeNumOfRows = 0, mazeNumOfCols = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            /*read 6 lines from file -- the saved parameters of a maze game */
            for( int i = 0 ; i < 6 ; i++){
                String line = br.readLine();
                if (line != null) {
                        if(i == 0)
                            playerRowIdx = Integer.parseInt(line);
                        if(i == 1)
                            playerColIdx = Integer.parseInt(line);
                        if(i == 2)
                            goalRowIdx = Integer.parseInt(line);
                        if(i == 3)
                            goalColIdx = Integer.parseInt(line);
                        if(i == 4)
                            mazeNumOfRows = Integer.parseInt(line);
                        if(i == 5)
                            mazeNumOfCols = Integer.parseInt(line);
                 }
            }
            int[][] grid = new int[mazeNumOfRows][mazeNumOfCols];
            String line = "";
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                int col = 0;
                for (String c : cols) {
                    grid[row][col] = Integer.parseInt(c);
                    col++;
                }
                row++;
            }
            br.close();
            Position start = new Position(playerRowIdx, playerColIdx);
            Position end  = new Position(goalRowIdx, goalColIdx);
            this.grid = grid;
            this.maze = new Maze(grid, start, end);
            this.playerPosColIdx = playerColIdx;
            this.playerPosRowIdx = playerRowIdx;
            this.mazeGoalPosColIdx = playerPosColIdx;
            this.mazeGoalPosRowIdx = playerPosRowIdx;
            isMazeExist=true;
            setChanged();
            notifyObservers("loaded");
        } catch (IOException e){
            e.printStackTrace();
        }


    }
}

