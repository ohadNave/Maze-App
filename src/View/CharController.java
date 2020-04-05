package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.event.KeyEvent;

public class CharController {


    public MyViewController viewController;
    public Stage primaryStage;
    public Scene scene;
    public Button captainButton;
    public Button deadpoolButton;


    public void init() throws Exception{

        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        //------------------
        FXMLLoader ViewFXML = new FXMLLoader(getClass().getResource("/View/MyView.fxml"));
        Parent playWindow = ViewFXML.load();
        viewController = ViewFXML.getController();
        viewController.init(primaryStage);
        scene = new Scene(playWindow,1200,800);
        primaryStage.setScene(scene);
        //----------------------
        viewController.setResizeEvent(scene);
        viewController.setViewModel(viewModel);
        viewModel.addObserver(viewController);
        primaryStage.show();
    }


    public void captain() throws Exception{
        this.init();
            viewController.setMazeCharacter("cap");
    }

    public void deadpool() throws Exception {
        this.init();
        viewController.setMazeCharacter("dead");
    }

    public void thor() throws Exception {
        this.init();
        viewController.setMazeCharacter("thor");
    }

    public void groot() throws Exception {
        this.init();
        viewController.setMazeCharacter("groot");
    }

    public void hulk() throws Exception {
        this.init();
        viewController.setMazeCharacter("hulk");
    }

    public void iron() throws Exception {
        this.init();
        viewController.setMazeCharacter("iron");
    }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }

    public void setScene(Scene scene) { this.scene = scene; }
}
