package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;

public interface IModel {
    void generateMaze(int width, int height);
    void moveCharacter(KeyCode movement);
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void solveMaze();
    Maze getTheMaze();
    Solution getSolution();
    void SaveGame(File file) throws IOException;
    void loadGame(File file) throws IOException, ClassNotFoundException;
    void newGame(int row, int col);
    void stopServers();
//    void setMazeFromGrid(int[][] maze);
}
