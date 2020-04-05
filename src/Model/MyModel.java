package Model;

import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import Client.*;
import algorithms.mazeGenerators.Maze;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends Observable implements IModel {

    private int characterPositionRow;
    private int characterPositionColumn;
    private Maze maze;
    private Solution sol;
    private Server generateServer;
    private Server solvingServer;
    private static boolean isGenerated = false;
    private boolean isLoaded = false;


    public MyModel() {
        generateServer = new Server(5400,1000, new ServerStrategyGenerateMaze());
        solvingServer = new Server(5401,1500, new ServerStrategySolveSearchProblem());
    }

    public void startServers() {
        generateServer.start();
        solvingServer.start();
    }

    public void stopServers() {
        generateServer.stop();
        solvingServer.stop();
    }

    /**
     * instead of anonymous class - we built a concrete class which its purpose is to send rows and cols parameters to server and get Maze object from it.
     */
    class clientServerGenerationStrategy implements IClientStrategy {
        private int rowsToBuild;
        private int colsToBuild;

        clientServerGenerationStrategy(int r, int c) {
            this.rowsToBuild = r;
            this.colsToBuild = c;
        }
        @Override
        public void clientStrategy(InputStream inputStream, OutputStream outputStream) throws Exception {
//            checkArgs();
            ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
            ObjectInputStream fromServer = new ObjectInputStream(inputStream);
            toServer.flush();
            int[] mazeStats = new int[]{rowsToBuild, colsToBuild};
            toServer.writeObject(mazeStats);
            toServer.flush();
            byte[] compressedMaze = (byte[]) fromServer.readObject();
            InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
            byte[] decompressedMaze = new byte[mazeStats[0] * mazeStats[1] + 12];
            is.read(decompressedMaze); //Fill decompressedMaze with bytes
            Maze mazeFromServer = new Maze(decompressedMaze);
            setMaze(mazeFromServer);//// change here
            setCharacterPositionRow(mazeFromServer.getStartPosition().getRowIndex()); //set start position row
            setCharacterPositionColumn(mazeFromServer.getStartPosition().getColIndex()); //set start position col
            isGenerated = true;
            setChanged();
            notifyObservers("generated");
        }

//        private void checkArgs(){
//            if(this.rowsToBuild == 1 || this.colsToBuild == 1){
//                this.rowsToBuild = 10;
//                this.colsToBuild = 10;
//            }
//        }
    }

    /**
     * instead of anonymous class - we built a concrete class which its purpose is to send maze object to server and get solution object from it.
     */
    class clientServerSolvingStrategy implements IClientStrategy{

        private Maze maze;

        public clientServerSolvingStrategy(Maze maze){ this.maze = maze; }

        @Override
        public void clientStrategy(InputStream inputStream, OutputStream outputStream) throws Exception {
            ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
            ObjectInputStream fromServer = new ObjectInputStream(inputStream);
            toServer.flush();
            setNewStart();
            toServer.writeObject(maze);
            toServer.flush();
            Solution solution = (Solution) fromServer.readObject();
            setSol(solution);
            setChanged();
            notifyObservers("solved");
        }
    }


    @Override
    public void generateMaze(int width, int height) {
        try
        {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new clientServerGenerationStrategy(height, width));
            client.communicateWithServer();
        }
        catch (Exception e) { e.printStackTrace(); }
    }


    public void solveMaze() {
        if(isGenerated || isLoaded){
            try
            {
                Client client = new Client(InetAddress.getLocalHost(), 5401, new clientServerSolvingStrategy(maze));
                client.communicateWithServer();

            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        int[][] grid = maze.getGrid();
        switch (movement) {
            /* UP */
            case UP:
            case DIGIT8:
            case NUMPAD8:
                if ((characterPositionRow - 1 >= 0) && grid[characterPositionRow - 1][characterPositionColumn] == 0) {
                    characterPositionRow--;
                } else {
                    noMoveAlert("You cant can't move this way");
                }
                break;
             /* UP-RIGHT */
            case DIGIT9:
            case NUMPAD9:
                if((characterPositionRow - 1 >= 0) &&(characterPositionColumn <= grid[0].length - 1) && grid[characterPositionRow - 1][characterPositionColumn + 1] == 0){
                    if(grid[characterPositionRow - 1][characterPositionColumn] == 0 || grid[characterPositionRow][characterPositionColumn + 1] == 0){
                        characterPositionRow--;
                        characterPositionColumn++;
                    }
                }
                else {
                    noMoveAlert("You cant can't move this way");
                }
                break;
             /* RIGHT */
            case RIGHT:
            case DIGIT6:
            case NUMPAD6:
                if ((characterPositionColumn + 1 <= grid[0].length - 1) && (grid[characterPositionRow][characterPositionColumn + 1] == 0)) {
                    characterPositionColumn++;
                } else {
                    noMoveAlert("You cant can't move this way");
                }
                break;
            /* RIGHT-DOWN */
            case DIGIT3:
            case NUMPAD3:
                if ((characterPositionRow + 1 <= grid.length - 1) &&(characterPositionColumn + 1 <= grid[0].length) && grid[characterPositionRow + 1][characterPositionColumn + 1] == 0){
                    if (grid[characterPositionRow + 1][characterPositionColumn] == 0 || grid[characterPositionRow][characterPositionColumn + 1] == 0){
                        characterPositionRow++;
                        characterPositionColumn++;
                    }
                }else {
                    noMoveAlert("You cant can't move this way");
                }
                break;
            /* DOWN */
            case DOWN:
            case DIGIT2:
            case NUMPAD2:
                if ((characterPositionRow + 1 <= grid.length - 1) && grid[characterPositionRow + 1][characterPositionColumn] == 0){
                    characterPositionRow++;
                }
                else {
                    noMoveAlert("You cant can't move this way");
                }
                break;
            /* DOWN-LEFT */
            case DIGIT1:
            case NUMPAD1:
                if ((characterPositionRow + 1 <= grid.length - 1) &&(characterPositionColumn - 1 >= 0) && grid[characterPositionRow + 1][characterPositionColumn - 1] == 0){
                    if(grid[characterPositionRow + 1][characterPositionColumn] == 0 || grid[characterPositionRow][characterPositionColumn - 1] == 0){
                        characterPositionRow++;
                        characterPositionColumn--;
                    }
                }
                else {
                    noMoveAlert("You cant can't move this way");
                }
                break;
            /* LEFT */
            case LEFT:
            case DIGIT4:
            case NUMPAD4:
                if ((characterPositionColumn - 1 >= 0) && grid[characterPositionRow][characterPositionColumn - 1] == 0) {
                    characterPositionColumn--;
                } else {
                    noMoveAlert("You cant can't move this way");
                }
                break;
            /* UP-LEFT */
            case DIGIT7:
            case NUMPAD7:
                if((characterPositionRow - 1 >= 0) &&(characterPositionColumn - 1 >= 0) && grid[characterPositionRow - 1][characterPositionColumn - 1] == 0){
                    if(grid[characterPositionRow - 1][characterPositionColumn] == 0 || grid[characterPositionRow][characterPositionColumn - 1] == 0){
                        characterPositionRow--;
                        characterPositionColumn--;
                    }
                }else {
                    noMoveAlert("You cant can't move this way");
                }
                break;
        }
        setChanged();
        notifyObservers("movement");
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public void setCharacterPositionRow(int characterPositionRow) { this.characterPositionRow = characterPositionRow; }

    public void setCharacterPositionColumn(int characterPositionColumn) { this.characterPositionColumn = characterPositionColumn; }

    public Solution getSolution(){
        return this.sol;
    }

    private void setNewStart(){
        maze.setS_p(new Position(characterPositionRow,characterPositionColumn));
    }

    public void setSol(Solution sol) { this.sol = sol; }

    private void setMaze(Maze mazeGrid) {
        this.maze = mazeGrid;
    }

    public Maze getTheMaze(){
        return maze;
    }

    @Override
    public int[][] getMaze() {
        return maze.getGrid();
    }

    public void newGame(int row, int col){ generateMaze(row,col); }

    private void noMoveAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void SaveGame(File file) throws IOException {
        File mazeFileToSave = new File(file.getPath());
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(mazeFileToSave));
        Position position = new Position(characterPositionRow, characterPositionColumn);
        byte[] bytes = maze.toByteArray();
        ByteArrayOutputStream byteMazeStream = new ByteArrayOutputStream();
        OutputStream compressTheMaze = new MyCompressorOutputStream(byteMazeStream);
        compressTheMaze.write(bytes);
        bytes = byteMazeStream.toByteArray();
        Object[] objects = new Object[4];
        objects[0] = bytes;
        objects[1] = position;
        int[][] grid = maze.getGrid();
        objects[2] = grid.length;
        int numOfCol = grid[0].length;
        objects[3] = numOfCol;
        out.writeObject(objects);
        compressTheMaze.flush();
        out.flush();
        out.close();

    }
    public void loadGame(File file) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(file.getPath());
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        Object[] objectsToRead = (Object[]) objectIn.readObject();
        int numOfRows = (int) objectsToRead[2];
        int numOfcols = (int) objectsToRead[3];
        byte[] compressedMaze = (byte[]) objectsToRead[0];
        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
        byte[] decompressedMaze = new byte[(numOfRows * numOfcols) + 12];
        is.read(decompressedMaze);
        maze = new Maze(decompressedMaze);
        Position currPos = (Position) objectsToRead[1];
        characterPositionRow = currPos.getRowIndex();
        characterPositionColumn = currPos.getColIndex();
        is.close();
        isLoaded = true;
        setChanged();
        notifyObservers("loded");
    }

}

