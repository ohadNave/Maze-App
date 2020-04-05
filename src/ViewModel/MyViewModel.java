package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;
    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();


    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            if(arg.equals("movement")){
                updateRowsAndCols();
                setChanged();
                notifyObservers("movement");
            }
            if (arg.equals("generated")){
                updateRowsAndCols();
                setChanged();
                notifyObservers("generated");
            }
            if ((arg.equals("solved"))){
                setChanged();
                notifyObservers("solved");
            }
            if((arg.equals("loded"))){
                updateRowsAndCols();
                setChanged();
                notifyObservers("loded");
            }
        }
    }

    private void updateRowsAndCols(){
        characterPositionRowIndex = model.getCharacterPositionRow();
        characterPositionRow.set(characterPositionRowIndex + "");
        characterPositionColumnIndex = model.getCharacterPositionColumn();
        characterPositionColumn.set(characterPositionColumnIndex + "");
    }

    public Maze getTheMaze(){ return model.getTheMaze(); }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }
    public void stopServers(){
        model.stopServers();
    }

    public void newGame(int row, int col){
        model.newGame(row, col);
    }
    public void solveMaze(){
        model.solveMaze();
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public int[][] getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public void setCharacterPositionRowIndex(Integer y){
        characterPositionRow.setValue(y.toString());
    }

    public void setCharacterPositionColumnIndexIndex(Integer x){
        characterPositionRow.setValue(x.toString());
    }

    public Solution getSolution(){ return model.getSolution() ; }

    public void SaveGame(File saveFile) throws IOException {
        model.SaveGame(saveFile);
    }
    public void loadGame(File file) throws IOException, ClassNotFoundException {
        model.loadGame(file);
    }

}
