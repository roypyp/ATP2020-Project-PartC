package View;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class firstSceneController {

    public Scene scene;
    public Button btn_newGame;
    public Button btn_exit;
    private MazeDisplayer mazeDisplayer;
    public Stage primaryStage;


    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        MazeDisplayer.audioChooser(0);
    }
    public void setMazeDisplayer(MazeDisplayer mazeDisplayer) {
        this.mazeDisplayer = mazeDisplayer;
    }

    /**handler for button "New Game" */
    public void newGameClicked() throws Exception {
        primaryStage.setScene(scene);
    }

}
