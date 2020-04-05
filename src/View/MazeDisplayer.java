package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import View.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

public class MazeDisplayer extends Canvas {

    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameSolution = new SimpleStringProperty();
    private StringProperty ImageFileNameFlag = new SimpleStringProperty();
    private StringProperty captainAmericaImage = new SimpleStringProperty();
    private StringProperty deadPoolImage = new SimpleStringProperty();
    private StringProperty victoryImage = new SimpleStringProperty();
    private StringProperty grootImage = new SimpleStringProperty();
    private StringProperty hulkImage = new SimpleStringProperty();
    private StringProperty ironmanImage = new SimpleStringProperty();
    private StringProperty thor = new SimpleStringProperty();
    private Image charImage;
    private Image wallImage;
    private Image flagImage;
    private Image goalImage;
    private Maze maze;
    private Solution mazeSolution;
    private int[][] grid;
    private int characterPositionRow;
    private int characterPositionColumn;
    public static MediaPlayer mediaPlayer;
    public static boolean soundIsOn = false;
    private static int winnerCounter = 0;


    public MazeDisplayer() {
        widthProperty().addListener(e -> redraw());
        heightProperty().addListener(e -> redraw());
    }


    public static void playMusic(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.pause();
            soundIsOn = false;
        }
        String path = "";
        if (index == 0) {
            path = "resources/Images/The-Avengers-Theme-Song.mpeg";
            soundIsOn = true;
        }
        if (index == 1) {
            path = "resources/Images/Captain_America-Avengers_Assemble.mpeg";
            soundIsOn = true;
            winnerCounter = 0;
        }
        if (index == 2) {
            path = "resources/Images/victory.mp3";
            soundIsOn = true;
        }

        Media player = new Media(Paths.get(path).toUri().toString());
        mediaPlayer = new MediaPlayer(player);
        mediaPlayer.play();
    }

    public void redraw() {
        if (grid != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / grid.length;
            double cellWidth = canvasWidth / grid[0].length;
            try {
                GraphicsContext graphicsContext2D = getGraphicsContext2D();
                graphicsContext2D.clearRect(0, 0, getWidth(), getHeight()); //Clears the canvas
                wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                flagImage = new Image(new FileInputStream(ImageFileNameFlag.get()));

                //goal drawing
                Position goalPosition = maze.getGoalPosition();
                int goalPosRow = goalPosition.getRowIndex();
                int goalPosCol = goalPosition.getColIndex();
                graphicsContext2D.drawImage(flagImage, goalPosCol * cellWidth, goalPosRow * cellHeight, cellWidth, cellHeight);

                //Draw Maze
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] == 1) { // if cell is a wall
                            graphicsContext2D.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
                graphicsContext2D.drawImage(charImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
                if (characterPositionRow == goalPosRow && characterPositionColumn == goalPosCol && winnerCounter == 0) {
                    winnerCounter++;
                    showAlert("You Are The Winner");
                }
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(String.format("Image doesn't exist: %s", e.getMessage()));
                alert.show();
            }
        }
    }

    private void showAlert(String alertMessage) {
        try {
            Pane pane = new Pane();
            Stage newStage = new Stage();
            String path = "resources/Images/victory.jpg";
            Image victory = new Image(Paths.get(path).toUri().toString());
            pane.getChildren().add(new ImageView(victory));
            Scene scene = new Scene(pane);
            newStage.setScene(scene);
            newStage.show();
            playMusic(2);
            newStage.setOnCloseRequest( event ->  mediaPlayer.stop() );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getCharSelection(String s) throws Exception {
        if (s.equals("cap"))
            charImage = new Image(new FileInputStream(captainAmericaImage.get()));
        if (s.equals("dead"))
            charImage = new Image(new FileInputStream(deadPoolImage.get()));
        if (s.equals("thor"))
            charImage = new Image(new FileInputStream(thor.get()));
        if (s.equals("iron"))
            charImage = new Image(new FileInputStream(ironmanImage.get()));
        if (s.equals("groot"))
            charImage = new Image(new FileInputStream(grootImage.get()));
        if (s.equals("hulk"))
            charImage = new Image(new FileInputStream(hulkImage.get()));
    }

    public void drawSolution() {
        try {
            double width = getWidth();
            double height = getHeight();
            double wid = width / grid[0].length;
            double hig = height / grid.length;
            goalImage = new Image(new FileInputStream(ImageFileNameSolution.get()));
            wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));

            int[][] grid = maze.getGrid();
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, getWidth(), getHeight());

            //---Draw walls and goal point---//
            ArrayList<AState> path = mazeSolution.getSolutionPath();
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (grid[i][j] == 1) {
                        graphicsContext.drawImage(wallImage, j * wid, i * hig, wid, hig);
                    }
                    AState p = new MazeState(new Position(i, j), 0, null);
                    if (path.contains(p)) {
                        graphicsContext.drawImage(goalImage, j * wid, i * hig, wid, hig);
                    }
                }
            }

            graphicsContext.drawImage(charImage, characterPositionColumn * wid, characterPositionRow * hig, wid, hig);

            //---get solution path---//
            for (AState s : path) {
                int stateRow = getStateRow(s.toString());
                int stateCol = getStateCol(s.toString());
                graphicsContext.drawImage(goalImage, stateCol * wid, stateRow * hig, wid, hig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mazeSolution = null;
    }


    private int getStateRow(String state) {
        int row = 0;
        String s = new String();
        for (Character c : state.toCharArray()) {
            if (c.equals(',')) {
                break;
            }
            if (Character.isDigit(c))
                s = s + "" + c;
        }
        row = Integer.parseInt(s);
        return row;
    }

    private int getStateCol(String state) {

        int col = 0;
        String s = new String();
        for (Character c : state.toCharArray()) {
            if (c.equals('}')) {
                break;
            }
            if (Character.isDigit(c))
                s = s + "" + c;
            if (c.equals(',')) {
                s = "";
            }
        }
        col = Integer.parseInt(s);
        return col;
    }


    public void setCharStartPosition(int x, int y) {
        this.characterPositionRow = x;
        this.characterPositionColumn = y;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
        redraw();
    }


    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }

    public void setMazeSolution(Solution sol) {
        this.mazeSolution = sol;
    }

    public String getImageFileNameFlag() {
        return ImageFileNameFlag.get();
    }

    public StringProperty imageFileNameFlagProperty() {
        return ImageFileNameFlag;
    }

    public void setImageFileNameFlag(String imageFileNameFlag) {
        this.ImageFileNameFlag.set(imageFileNameFlag);
    }

    public String getCaptainAmericaImage() {
        return captainAmericaImage.get();
    }

    public StringProperty captainAmericaImageProperty() {
        return captainAmericaImage;
    }

    public void setCaptainAmericaImage(String captainAmericaImage) {
        this.captainAmericaImage.set(captainAmericaImage);
    }

    public String getDeadPoolImage() {
        return deadPoolImage.get();
    }

    public StringProperty deadPoolImageProperty() {
        return deadPoolImage;
    }

    public void setDeadPoolImage(String deadPoolImage) {
        this.deadPoolImage.set(deadPoolImage);
    }

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public String getImageFileNameSolution() {
        return ImageFileNameSolution.get();
    }

    public StringProperty imageFileNameSolutionProperty() {
        return ImageFileNameSolution;
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.ImageFileNameSolution.set(imageFileNameSolution);
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
    }

    public String getGrootImage() {
        return grootImage.get();
    }

    public StringProperty grootImageProperty() {
        return grootImage;
    }

    public void setGrootImage(String grootImage) {
        this.grootImage.set(grootImage);
    }

    public String getHulkImage() {
        return hulkImage.get();
    }

    public StringProperty hulkImageProperty() {
        return hulkImage;
    }

    public void setHulkImage(String hulkImage) {
        this.hulkImage.set(hulkImage);
    }

    public String getIronmanImage() {
        return ironmanImage.get();
    }

    public StringProperty ironmanImageProperty() {
        return ironmanImage;
    }

    public void setIronmanImage(String ironmanImage) {
        this.ironmanImage.set(ironmanImage);
    }

    public String getThor() {
        return thor.get();
    }

    public StringProperty thorProperty() {
        return thor;
    }

    public void setThor(String thor) {
        this.thor.set(thor);
    }

    public String getVictoryImage() {
        return victoryImage.get();
    }

    public StringProperty victoryImageProperty() {
        return victoryImage;
    }

    public void setVictoryImage(String victoryImage) {
        this.victoryImage.set(victoryImage);
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public void setSoundOn() {
        soundIsOn = true;
    }

    public void setSoundOff() {
        soundIsOn = false;
    }
}
