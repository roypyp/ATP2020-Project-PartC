package View;

import Server.Configurations;
import algorithms.mazeGenerators.EmptyMazeGenerator;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.SimpleMazeGenerator;
import algorithms.search.BestFirstSearch;
import algorithms.search.BreadthFirstSearch;
import algorithms.search.DepthFirstSearch;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesController implements Initializable{

    @FXML
    public Stage stage;
    //public TextField searchingAlgorithm;
    //public TextField generator;
    public ChoiceBox<String> searchingAlgorithm;
    public ChoiceBox<String> generator;



    @Override
    public void initialize(URL location, ResourceBundle resources){
        generator.getItems().addAll("EmptyMazeGenerator","SimpleMazeGenerator","MyMazeGenerator");
        searchingAlgorithm.getItems().addAll("BreadthFirstSearch","DepthFirstSearch", "BestFirstSearch");
        try{
            Properties properties = new Properties();
            properties.load(new FileInputStream("resources/config.properties"));

            String a1= properties.getProperty("searchingAlgorithm");
            String a2= properties.getProperty("generator");
            if(a1.equals("BestFirstSearch")){
                searchingAlgorithm.setValue("BestFirstSearch");}
            else if(a1.equals("DepthFirstSearch")){
                searchingAlgorithm.setValue("DepthFirstSearch");}
            else if(a1.equals("BreadthFirstSearch")){
                searchingAlgorithm.setValue("BreadthFirstSearch");}
            if(a2.equals("MyMazeGenerator")){
                generator.setValue("MyMazeGenerator");}
            else if(a2.equals("SimpleMazeGenerator")){
                generator.setValue("SimpleMazeGenerator");}
            else if(a2.equals("EmptyMazeGenerator")){
                generator.setValue("EmptyMazeGenerator");}


        }
        catch (Exception e){}
    }



    public void UpdateClicked(){
        Configurations.setMazeAlgorithm(generator.getValue());
        Configurations.setSearchingAlgorithm(searchingAlgorithm.getValue());
    }
    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

}