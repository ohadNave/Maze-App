package View;

import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class MyViewController implements IView, Observer {

    private Stage stage;
    private MyViewModel viewModel;
    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();
    public boolean isOneMaze = false;
    private Timeline timeline = new Timeline(60);

    @FXML
    public MazeDisplayer mazeDisplayer;
    public TextField txtfld_rowsNum;
    public TextField txtfld_columnsNum;
    public Label lbl_characterRow;
    public Label lbl_characterColumn;
    public Button btn_generateMaze;
    private int displayCounter = 1;
    public Pane pane;
    

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            if (arg.equals("movement")) {
                displayMaze(viewModel.getMaze());
            }
            if (arg.equals("generated")) {
                isOneMaze = true;
                btn_generateMaze.setDisable(false);
                mazeDisplayer.setMaze(viewModel.getTheMaze());
                Integer y = viewModel.getTheMaze().getStartPosition().getRowIndex();
                Integer x = viewModel.getTheMaze().getStartPosition().getColIndex();
                mazeDisplayer.setCharStartPosition(y, x);
                bindProperties(viewModel);
                displayMaze(viewModel.getMaze());
            }
            if ((arg.equals("solved"))) {
                mazeDisplayer.setMazeSolution(getSolution());
                mazeDisplayer.drawSolution();
            }
            if ((arg.equals("loded"))) {
                mazeDisplayer.setMaze(viewModel.getTheMaze());
                mazeDisplayer.setGrid(viewModel.getMaze());
                mazeDisplayer.setCharacterPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn());
                bindProperties(viewModel);
                displayMaze(viewModel.getMaze());

            }
        }
    }

    public void stopServers() {
        if (isOneMaze) {
            viewModel.stopServers();
        }
    }

    public void showAbout() throws Exception {
        Pane pane = new Pane();
        Stage newStage = new Stage();
        String path = "resources/Images/abou2.jpg";
        Image help = new Image(Paths.get(path).toUri().toString());
        pane.getChildren().add(new ImageView(help));
        Scene scene = new Scene(pane);
        newStage.setScene(scene);
        newStage.show();
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setGrid(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        if (displayCounter != 1) mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        displayCounter++;
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_characterRow.textProperty().bind(viewModel.characterPositionRow);
        lbl_characterColumn.textProperty().bind(viewModel.characterPositionColumn);
    }

    public void openConfigurations() throws Exception {
        Stage stage = new Stage();
        stage.setTitle("Properties");
        FXMLLoader propFXML = new FXMLLoader(getClass().getResource("/View/Properties.fxml"));
        Parent root = propFXML.load();
        propController propController = propFXML.getController();
        propController.setStage(stage);
        Scene scene = new Scene(root, 500, 250);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * get parameters from user input and generates a new maze based on those.
     */
    public void generateMaze() {
        try {
            int height = Integer.valueOf(txtfld_rowsNum.getText());
            int width = Integer.valueOf(txtfld_columnsNum.getText());
            if (isValidArgs(height, width)) {
                btn_generateMaze.setDisable(true);
                this.viewModel.generateMaze(width, height);
                mazeDisplayer.playMusic(1);
            } else {
                showAlert("Please enter valid number");
            }

        } catch (NumberFormatException e) {
            showAlert("Please enter valid number");
        }
    }

    private boolean isValidArgs(int row, int col) {
        if (row <= 1 || col <= 1) {
            return false;
        }
        return true;
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    /**
     * get maze from server ,solving it and returning solution.
     */
    public void solveMaze() {
        viewModel.solveMaze();
    }

    public void HelpClicked() throws Exception {
        Pane pane = new Pane();
        Stage newStage = new Stage();
        String path = "resources/Images/help.jpg";
        Image help = new Image(Paths.get(path).toUri().toString());
        pane.getChildren().add(new ImageView(help));
        Scene scene = new Scene(pane);
        newStage.setScene(scene);
        newStage.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }


    @Override
    public void Load() throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Files", "*.maze"));
        File loadFile = fileChooser.showOpenDialog(stage);
        if (loadFile != null) {
            viewModel.loadGame(loadFile);
            mazeDisplayer.playMusic(1);
        } else {
            System.out.println("Error Loading File");
        }
    }


    public void setOnScroll(ScrollEvent scroll) {
        double zoomScale;
        if (scroll.isControlDown()) {
            zoomScale = 1.5;
            double deltaY = scroll.getDeltaY();
            if(deltaY < 0){
                zoomScale = 1/ zoomScale;
            }
            zoom(mazeDisplayer, zoomScale, scroll.getSceneX(), scroll.getSceneY());
            mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * zoomScale);
            mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * zoomScale);
            scroll.consume();
        }
    }


    @Override
    public void New() {
        mazeDisplayer.mediaPlayer.stop();
        try {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            int columns = Integer.valueOf(txtfld_columnsNum.getText());
            if (isValidArgs(rows, columns)) {
                btn_generateMaze.setDisable(false);
                viewModel.newGame(rows, columns);
                mazeDisplayer.playMusic(1);
            } else {
                showAlert("Please enter valid number");
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter valid number");
        }

    }

    @Override
    public void Save() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Maze Files", "*.maze")
        );
        fileChooser.setInitialFileName("My Maze To Save");
        File saveFile = fileChooser.showSaveDialog(stage);
        if (saveFile != null) {
            viewModel.SaveGame(saveFile);
        }
    }

    public void init(Stage stage) {
        this.stage = stage;
    }


    public void setResizeEvent(Scene scene) {
        stage.setScene(scene);
        stage.setResizable(true);
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.widthProperty().bind(pane.widthProperty());
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.heightProperty().bind(pane.heightProperty());
            }
        });
        stage.setScene(scene);
        stage.hide();
        stage.show();
    }


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }

    public Solution getSolution() {
        return viewModel.getSolution();
    }

    public String getHelpPhoto() {
        return helpPhoto.get();
    }

    public StringProperty helpPhotoProperty() {
        return helpPhoto;
    }

    public void setHelpPhoto(String helpPhoto) {
        this.helpPhoto.set(helpPhoto);
    }

    public StringProperty helpPhoto = new SimpleStringProperty();

    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public void setMazeCharacter(String s) throws Exception {
        mazeDisplayer.getCharSelection(s);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        int maxSize = Math.max(viewModel.getMaze()[0].length, viewModel.getMaze().length);
        double cellHeight = mazeDisplayer.getHeight() / maxSize;
        double cellWidth = mazeDisplayer.getWidth() / maxSize;
        double canvasHeight = mazeDisplayer.getHeight();
        double canvasWidth = mazeDisplayer.getWidth();
        int rowMazeSize = viewModel.getMaze().length;
        int colMazeSize = viewModel.getMaze()[0].length;
        double startRow = (canvasHeight / 2 - (cellHeight * rowMazeSize / 2)) / cellHeight;
        double startCol = (canvasWidth / 2 - (cellWidth * colMazeSize / 2)) / cellWidth;
        double mouseX = (int) ((mouseEvent.getX()) / (mazeDisplayer.getWidth() / maxSize) - startCol);
        double mouseY = (int) ((mouseEvent.getY()) / (mazeDisplayer.getHeight() / maxSize) - startRow);
        if (mouseY < viewModel.getCharacterPositionRow() && mouseX == viewModel.getCharacterPositionColumn())
            viewModel.moveCharacter(KeyCode.UP);
        if (mouseY > viewModel.getCharacterPositionRow() && mouseX == viewModel.getCharacterPositionColumn())
            viewModel.moveCharacter(KeyCode.DOWN);
        if (mouseX < viewModel.getCharacterPositionColumn() && mouseY == viewModel.getCharacterPositionRow())
            viewModel.moveCharacter(KeyCode.LEFT);
        if (mouseX > viewModel.getCharacterPositionColumn() && mouseY == viewModel.getCharacterPositionRow())
            viewModel.moveCharacter(KeyCode.RIGHT);
    }

    void zoom(Node node, double factor, double x, double y) {
        double oldScale = node.getScaleX();
        double scale = oldScale * factor;
        double f = (scale / oldScale) - 1;

        Bounds bounds = node.localToScene(node.getLayoutBounds(), true);
        double dx = (x-(bounds.getWidth() / 2+bounds.getMinX()));
        double dy = (y-(bounds.getHeight() / 2+bounds.getMinY()));

        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), node.getTranslateX()-f * dx)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateYProperty(), node.getTranslateY()-f * dy)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.scaleXProperty(), scale)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.scaleYProperty(), scale))
        );
        timeline.play();
    }

}
