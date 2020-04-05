package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuController {

    public Scene scene;

    public Stage getPrimaryStage() { return primaryStage; }

    public Stage primaryStage;

    public void setScene(Scene scene) { this.scene = scene; }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        MazeDisplayer.playMusic(0);
    }


    public void newGameClicked() throws Exception{ primaryStage.setScene(scene); }


    public void exitClicked(){
        System.exit(0);
    }
}
