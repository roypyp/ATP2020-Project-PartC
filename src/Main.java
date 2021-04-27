import Model.IModel;
import Model.MyModel;
import View.MyViewController;
import View.PlayerController;
import View.firstSceneController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class Main extends Application {

    public Scene startScene;
    public Scene newGameScene;
    public Scene playScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        primaryStage.setTitle("Rick and Morty Maze Game");

        FXMLLoader firstSceneFXML = new FXMLLoader(getClass().getResource("View/firstScene.fxml"));
        Parent root = firstSceneFXML.load();
        startScene = new Scene(root,1200,800);
        firstSceneController firstSceneCont = firstSceneFXML.getController();
        /*#################################################################################################*/
        FXMLLoader selectCharFXML = new FXMLLoader(getClass().getResource("View/choosePlayer.fxml"));
        Parent newGame = selectCharFXML.load();
        newGameScene = new Scene(newGame,900,600);
        PlayerController charController = selectCharFXML.getController();
        /*#################################################################################################*/
        FXMLLoader viewFXML = new FXMLLoader(getClass().getResource("View/MyView.fxml"));
        Parent play = viewFXML.load();
        playScene = new Scene(play,1200,800);
        /*#################################################################################################*/
        firstSceneCont.setPrimaryStage(primaryStage);
        firstSceneCont.setScene(newGameScene);
        charController.setPrimaryStage(firstSceneCont.getPrimaryStage());
        charController.setScene(playScene);
        /*#################################################################################################*/
        setStageCloseEvent(primaryStage, model);
        primaryStage.setScene(startScene);
        primaryStage.show();
    }


    private void setStageCloseEvent(Stage primaryStage, MyModel model) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    // ... user chose OK
                    // Close the program properly
                    model.stopServers();
                    System.exit(0);
                } else { windowEvent.consume(); }
            }
        });
    }

}
