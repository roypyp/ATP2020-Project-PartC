package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas {

    private int playerPosRow;
    private int playerPosCol;
    private static int winnerCounter = 0;//todo find out if necessary later(better to chuck redundancies)
    private Solution solutionObj;
    private Maze maze;
    private int[][] grid;

    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameSolution = new SimpleStringProperty();
    private StringProperty ImageFileNameFlag = new SimpleStringProperty();
    private StringProperty rickIMG = new SimpleStringProperty();
    private StringProperty mortyIMG = new SimpleStringProperty();
    private StringProperty victoryImage = new SimpleStringProperty();
    private StringProperty jerryIMG = new SimpleStringProperty();
    private StringProperty bethIMG = new SimpleStringProperty();
    private StringProperty poopybuttIMG = new SimpleStringProperty();
    private StringProperty summerIMG = new SimpleStringProperty();
    private Image playerImage;
    private Image wallImage;
    private Image endPointImage;
    private Image solutionPathImage;

    public static MediaPlayer mediaPlayer;
    //TODO ADD REDUNDANT FIELDS - WE CANNOT KEEP SAME AMOUNT OF MEMEBERS IN ANY CLASS

    public MazeDisplayer() {
        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }

    public static void audioChooser(int selectedAudioIndex) {
        /*stop the audio playing currently before switching */
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.pause();
        }
        //todo change to switch-case
        String path = "";
        if (selectedAudioIndex == 0) {//todo change music opening scene
            path = "resources/mp3/play1.mp3";
        }
        else if (selectedAudioIndex == 1) {//todo change music choose players scene
            path = "resources/mp3/play4.mp3";
            winnerCounter = 0;
        }
        else if (selectedAudioIndex == 2) {//todo change music game victory scene
            path = "resources/mp3/play3.mp3";
        }
        //Media player = new Media(Paths.get(path).toUri().toString());
        Media player = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(player);
        mediaPlayer.play();
        mediaPlayer.setMute(MyViewController.mute);
    }

    public static void stopMusic(){
        mediaPlayer.setMute(MyViewController.mute);
    }

    //todo change players names in fxml and here
    public void getUserChoiceOfPlayer(String s) throws Exception {
        if (s.equals("rick"))
            playerImage = new Image(new FileInputStream(rickIMG.get()));
        if (s.equals("morty"))
            playerImage = new Image(new FileInputStream(mortyIMG.get()));
        if (s.equals("summer"))
            playerImage = new Image(new FileInputStream(summerIMG.get()));
        if (s.equals("poopybutt"))
            playerImage = new Image(new FileInputStream(poopybuttIMG.get()));
        if (s.equals("jerry"))
            playerImage = new Image(new FileInputStream(jerryIMG.get()));
        if (s.equals("beth"))
            playerImage = new Image(new FileInputStream(bethIMG.get()));
    }

    private void showStageForUserWinningTheGame(String alertMessage) {
        try {
            Pane pane = new Pane();
            Stage newStage = new Stage();
            String path = "resources/Images/winnerUserImage.jpg";//todo change this hard-coded path to stringProperty path (use get+set)
            Image imageUserWonScene = new Image(Paths.get(path).toUri().toString());
            ImageView imageviewUserWonScene = new ImageView(imageUserWonScene);
            /* add ImageView to Pane's children */
            pane.getChildren().add(imageviewUserWonScene);
            Scene scene = new Scene(pane);
            newStage.setScene(scene);
            /* show the UserWon scene */
            newStage.show();
            /* play the UserWon Audio */
            audioChooser(2);
            /* if user presses the exit window button then stop the music from playing*/
            newStage.setOnCloseRequest( event ->  mediaPlayer.stop() );//Sets the value of the property onCloseRequest
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** draws the maze scene including the player(this method should be invoked after each movement of the player) */
    public void redraw() {//todo change the order of drawings
        if (grid != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / grid.length;
            double cellWidth = canvasWidth / grid[0].length;
            try {
                /*get gc for this maze Canvas */
                GraphicsContext graphicsContext2D = getGraphicsContext2D();
                /*clear the entire canvas */
                graphicsContext2D.clearRect(0, 0, getWidth(), getHeight());
                /* get paths for images and create an Image instance for each of them */
                wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                /* draw the maze itself (walls) */
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] == 1) {
                            graphicsContext2D.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
                endPointImage = new Image(new FileInputStream(ImageFileNameFlag.get()));
                /*draw the goal Image in the Maze's Goal Position */
                Position goalPosition = maze.getGoalPosition();
                int goalPosRow = goalPosition.getRowIndex();
                int goalPosCol = goalPosition.getColumnIndex();
                graphicsContext2D.drawImage(endPointImage, goalPosCol * cellWidth, goalPosRow * cellHeight, cellWidth, cellHeight);
                /*draw the player's Image in player's current position */ //todo find out who is responsible to call setStartPos() VS setPos() -> View or MazeDisp?
                graphicsContext2D.drawImage(playerImage, playerPosCol * cellWidth, playerPosRow * cellHeight, cellWidth, cellHeight);
                if (playerPosRow == goalPosRow && playerPosCol == goalPosCol && winnerCounter == 0) {
                    winnerCounter++;
                    showStageForUserWinningTheGame("You Are The Winner");//todo change to something rick&Morty-shy
                }
            } catch (FileNotFoundException e) {
            }
        }
    }

    /** draws the solution to the maze graphically on screen from whichever position the player's at currently,
     * until the player starts moving (then the solution will disappear) until next time the user presses "Solve Maze"
     **/
    public void drawSolution() {
        try {
            /*get Maze Canvas dimensions */
            double width = getWidth();
            double height = getHeight();
            /*get single cell dimesions */
            double cellWidth = width / grid[0].length;
            double cellHeight = height / grid.length;
            /* create Image instance of the Solution-step Image */
            solutionPathImage = new Image(new FileInputStream(ImageFileNameSolution.get()));
            /* create Image instance of the Wall-Brick Image */
            wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
            int[][] grid = maze.getMazeGrid();
            GraphicsContext graphicsContext = getGraphicsContext2D();
            /* reset the Maze canvas */
            graphicsContext.clearRect(0, 0, getWidth(), getHeight());
            /*Draw walls and goal point*/
            ArrayList<AState> path = solutionObj.getSolutionPath();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (grid[i][j] == 1) {
                        graphicsContext.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    }
                    /*if this cell is part of the path draw the solution path image */
                    AState p = new MazeState(new Position(i, j), null, 0);//using generic AState makes sense design-wise
                    if (path.contains(p)) {
                        graphicsContext.drawImage(solutionPathImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
            graphicsContext.drawImage(playerImage, playerPosCol * cellWidth, playerPosRow * cellHeight, cellWidth, cellHeight);
            endPointImage = new Image(new FileInputStream(ImageFileNameFlag.get()));
            /*draw the goal Image in the Maze's Goal Position */
            Position goalPosition = maze.getGoalPosition();
            int goalPosRow = goalPosition.getRowIndex();
            int goalPosCol = goalPosition.getColumnIndex();
            graphicsContext.drawImage(endPointImage, goalPosCol * cellWidth, goalPosRow * cellHeight, cellWidth, cellHeight);

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*reset the mazeSolution */
        solutionObj = null;
    }


/*----------- Setters --------------------------------------------------------------------------------------*/
    /*-- set Maze related members */
    /**@param  startPosRow starting point row index
     * @param startPosCol starting point col index
     * this method DOES NOT call redraw(), but simply sets the values */
    public void setPlayerStartPosition(int startPosRow, int startPosCol) {
        this.playerPosRow =  startPosRow;
        this.playerPosCol = startPosCol;
    }
    /**@param  playerPosRow starting point row index
     * @param  playerPosCol point col index
     * this method calls redraw() to redraw the scene */
    public void setPlayerPositionAndRedraw(int playerPosRow, int playerPosCol) {
        this.playerPosRow = playerPosRow;
        this.playerPosCol = playerPosCol;
        redraw();
    }
    public void setMazeGridAndRedraw(int[][] grid) {
        this.grid = grid;
        redraw();
    }
    public void setSolutionObj(Solution sol) {
        this.solutionObj = sol;
    }
    public void setMaze(Maze maze) {
        this.maze = maze;
    }
    /*-- set sound ON/OFF*/

    /*--set ImageFileName scene objects */
    public void setImageFileNameFlag(String imageFileNameFlag) {
        this.ImageFileNameFlag.set(imageFileNameFlag);
    }
    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.ImageFileNameSolution.set(imageFileNameSolution);
    }
    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }
    public void setVictoryImage(String victoryImage) {
        this.victoryImage.set(victoryImage);
    }
    /*--set ImageFileName Players */
    public void setRickIMG(String rickIMG) {
        this.rickIMG.set(rickIMG);
    }
    public void setMortyIMG(String mortyIMG) {
        this.mortyIMG.set(mortyIMG);
    }
    public void setJerryIMG(String jerryIMG) {
        this.jerryIMG.set(jerryIMG);
    }
    public void setBethIMG(String bethIMG) {
        this.bethIMG.set(bethIMG);
    }
    public void setPoopybuttIMG(String poopybuttIMG) {
        this.poopybuttIMG.set(poopybuttIMG);
    }
    public void setSummerIMG(String summerIMG) {
        this.summerIMG.set(summerIMG);
    }
/*----------- End Setters --------------------------------------------------------------------------------------*/


/*----------- Getters --------------------------------------------------------------------------------------*/
    public int getPlayerPosRow() {
        return playerPosRow;
    }
    public int getPlayerPosCol() {
        return playerPosCol;
    }
    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }
    public String getImageFileNameFlag() {
        return ImageFileNameFlag.get();
    }
    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }
    public String getImageFileNameSolution() {
        return ImageFileNameSolution.get();
    }
    public String getVictoryImage() {
        return victoryImage.get();
    }
    /*-- get a player's Image path from fxml */
    public String getPoopybuttIMG() {
        return poopybuttIMG.get();
    }
    public String getSummerIMG() {
        return summerIMG.get();
    }
    public String getRickIMG() {
        return rickIMG.get();
    }
    public String getMortyIMG() {
        return mortyIMG.get();
    }
    public String getJerryIMG() {
        return jerryIMG.get();
    }
    public String getBethIMG() {
        return bethIMG.get();
    }
/*----------- End Getters --------------------------------------------------------------------------------------*/





}
