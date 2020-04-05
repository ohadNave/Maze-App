package View;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class HelpController extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception { this.primaryStage = primaryStage; }

    public Pane pane;

    public void showHelp() throws Exception{
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Image help =  new Image(new FileInputStream("resources/Images/help.jpg"));
        ImageView imageView = new ImageView(help);
        alert.setGraphic(imageView);
        alert.show();
    }
}
