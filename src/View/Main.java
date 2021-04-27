package View;

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

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Avengers Maze Game");
        FXMLLoader menuFXML = new FXMLLoader(getClass().getResource("View/firstScene.fxml"));
        FXMLLoader selectCharFXML = new FXMLLoader(getClass().getResource("View/selectChar.fxml"));
        FXMLLoader viewFXML = new FXMLLoader(getClass().getResource("View/MyView.fxml"));
        //----------------------//
        /* set the Scene's layout */
        Parent root = menuFXML.load();

        Parent newGame = selectCharFXML.load();
        Parent play = viewFXML.load();
        //----------------------//
        MyViewController viewController = viewFXML.getController();
        firstSceneController menuController = menuFXML.getController();
        PlayerController charController = selectCharFXML.getController();
        //----------------------//
        startScene = new Scene(root,1200,800);
        newGameScene = new Scene(newGame,900,600);
        playScene = new Scene(play,1200,800);
        //----------------------//
        menuController.setPrimaryStage(primaryStage);
        menuController.setScene(newGameScene);
        charController.setPrimaryStage(menuController.getPrimaryStage());
        charController.setScene(playScene);
        //----------------------//
        SetStageCloseEvent(primaryStage, viewController);
        primaryStage.setScene(startScene);
        primaryStage.show();
    }


    private void SetStageCloseEvent(Stage primaryStage, MyViewController viewController) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    viewController.stopServers();
                    System.exit(0);
                } else { windowEvent.consume(); }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
