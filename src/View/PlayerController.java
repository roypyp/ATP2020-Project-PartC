package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlayerController {
    public MyViewController viewController;
    public Stage primaryStage;
    public Scene scene;
    //public Button captainButton;
    //public Button deadpoolButton;

    public void init() throws Exception {
        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        //------------------
        FXMLLoader ViewFXML = new FXMLLoader(getClass().getResource("/View/MyView.fxml"));
        Parent playWindow = ViewFXML.load();
        viewController = ViewFXML.getController();
        viewController.setStageInView(primaryStage);
        scene = new Scene(playWindow,1200,800);
        primaryStage.setScene(scene);
        //----------------------
        viewController.setResizeEvent(scene);
        viewController.setViewModel(viewModel);
        viewModel.addObserver(viewController);
        primaryStage.show();
    }

    public void rick() throws Exception {
        this.init();
            viewController.setPlayerAcordingToUserChoise("rick");
    }
    public void morty() throws Exception {
        this.init();
        viewController.setPlayerAcordingToUserChoise("morty");
    }
    public void summer() throws Exception {
        this.init();
        viewController.setPlayerAcordingToUserChoise("summer");
    }
    public void jerry() throws Exception {
        this.init();
        viewController.setPlayerAcordingToUserChoise("jerry");
    }
    public void beth() throws Exception {
        this.init();
        viewController.setPlayerAcordingToUserChoise("beth");
    }
    public void poopybutt() throws Exception {
        this.init();
        viewController.setPlayerAcordingToUserChoise("poopybutt");
    }
    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }
    public void setScene(Scene scene) { this.scene = scene; }
}
