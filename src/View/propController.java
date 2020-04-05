package View;

import View.MyViewController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.Observable;
import java.util.Properties;
import java.util.ResourceBundle;

public class propController implements Initializable{

    @FXML
    public Properties prop;
    public Stage stage;

    public TextField algo;
    public TextField generator;



    @Override
    public void initialize(URL location, ResourceBundle resources){
        try{
            prop = null;
            InputStream input = MyViewController.class.getClassLoader().getResourceAsStream("config.properties");
            prop = new Properties();
            prop.load(input);
            algo.setText(prop.getProperty("searchAlgorithm"));
            generator.setText(prop.getProperty("generatorType"));
        }
        catch (Exception e){}
    }




    public Stage getStage() { return stage; }

    public void setStage(Stage stage) { this.stage = stage; }

    public void  setGenerator(String s){ prop.setProperty("generatorType",s); }

}