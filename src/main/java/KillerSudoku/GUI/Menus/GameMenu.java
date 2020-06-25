package KillerSudoku.GUI.Menus;

import KillerSudoku.GUI.GameCanvas;
import KillerSudoku.GameState.GameState;
import KillerSudoku.Utility;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class GameMenu extends Menu {
    public GameMenu(Stage primaryStage, GameCanvas gameCanvas, GameState gameState){
        setText("Game");

        MenuItem showSolutionItem = new MenuItem("Show Solution");
        showSolutionItem.setOnAction(e -> {
            gameState.usedSolutionMenuItem = true;
            for(int i = 0; i < gameState.getWidth(); i++)
                for(int j = 0; j < gameState.getHeight(); j++)
                    gameState.getCell(i, j).setNum(gameState.puzzle.getNum(i, j));
            gameCanvas.draw();
        });

        Menu newGameSubNenu = new Menu("New Game");

        MenuItem easyItem = new MenuItem("Easy");
        easyItem.setOnAction(e -> {
            gameState.init(gameState.getWidth(), gameState.getHeight(), 40, Utility.rand(10, 15), 5);
            gameCanvas.startTimer();
            gameCanvas.draw();
        });

        MenuItem mediumItem = new MenuItem("Medium");
        mediumItem.setOnAction(e -> {
            gameState.init(gameState.getWidth(), gameState.getHeight(), 40, Utility.rand(4, 8), 5);
            gameCanvas.startTimer();
            gameCanvas.draw();
        });

        MenuItem hardItem = new MenuItem("Hard");
        hardItem.setOnAction(e -> {
            gameState.init(gameState.getWidth(), gameState.getHeight(), 40, 0, 5);
            gameCanvas.startTimer();
            gameCanvas.draw();
        });

        newGameSubNenu.getItems().addAll(easyItem, mediumItem, hardItem);

        MenuItem saveGameItem = new MenuItem("Save Game");
        saveGameItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Game");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("KillerSudoku", "*.ks"));
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                gameState.save(file);
            }
        });

        MenuItem loadGameItem = new MenuItem("Load Game");
        loadGameItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Game");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("KillerSudoku", "*.ks"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                gameState.load(file);
                gameCanvas.startTimer();
                gameCanvas.draw();
            }
        });
        getItems().addAll(newGameSubNenu, saveGameItem, loadGameItem, showSolutionItem);
    }
}
