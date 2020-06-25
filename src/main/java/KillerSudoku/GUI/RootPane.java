package KillerSudoku.GUI;

import KillerSudoku.GUI.Menus.GameMenu;
import KillerSudoku.GUI.Menus.HelpMenu;
import KillerSudoku.GUI.Menus.ViewMenu;
import KillerSudoku.GameState.GameState;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RootPane extends BorderPane { // Glavni GUI element koji sadrzi sve ostale
    public RootPane(Stage primaryStage){
        super();
        GameState gameState = new GameState();
        gameState.init(9, 9, 40, 0, 5); // Prvobitna igra koja automatski zapocinje

        GameCanvas gameCanvas = new GameCanvas(gameState); // Canvas
        ColoredLabel correctLabel = new ColoredLabel("", Color.BLACK); // Donje labele
        ColoredLabel incorrectLabel = new ColoredLabel("", Color.BLACK);
        ColoredLabel mistakesLabel = new ColoredLabel("", Color.BLACK);
        ColoredLabel remainingLabel = new ColoredLabel("", Color.BLACK);
        ColoredLabel timerLabel = new ColoredLabel("TIMER: 00:00", Color.BLACK);

        Pane wrapperPane = getGamePane(gameCanvas);

        // Dodaju se svi ostali elementi
        setCenter(wrapperPane);
        setTop(getMenuBar(primaryStage, gameCanvas, gameState, correctLabel, incorrectLabel, mistakesLabel, remainingLabel, timerLabel, wrapperPane));
        setBottom(getBotBox(gameCanvas, correctLabel, incorrectLabel, mistakesLabel, remainingLabel, timerLabel));
    }

    // HBox koji sadrzi neke labele sa informacijama o igri
    private HBox getBotBox(GameCanvas gameCanvas, ColoredLabel correctLabel, ColoredLabel incorrectLabel, ColoredLabel mistakesLabel, ColoredLabel remainingLabel, ColoredLabel timerLabel){
        HBox botBox = new HBox();

        gameCanvas.correctLabel = correctLabel;
        gameCanvas.incorrectLabel = incorrectLabel;
        gameCanvas.mistakesLabel = mistakesLabel;
        gameCanvas.remainingLabel = remainingLabel;
        gameCanvas.timerLabel = timerLabel;

        botBox.getChildren().addAll(correctLabel, incorrectLabel, mistakesLabel, remainingLabel, timerLabel);
        botBox.setSpacing(10);

        return botBox;
    }

    // Sadrzi sve menije
    private MenuBar getMenuBar(Stage primaryStage, GameCanvas gameCanvas, GameState gameState, ColoredLabel correctLabel, ColoredLabel incorrectLabel, ColoredLabel mistakesLabel, ColoredLabel remainingLabel, ColoredLabel timerLabel, Pane wrapperPane){
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(new GameMenu(primaryStage, gameCanvas, gameState), new ViewMenu(gameCanvas, correctLabel, incorrectLabel, mistakesLabel, remainingLabel, timerLabel), new HelpMenu());
        return menuBar;
    }

    private Pane getGamePane(GameCanvas gameCanvas){ // Koristi se da bi canvas uvjek automatski zauzimao najveci dio prozora
        Pane wrapperPane = new Pane(gameCanvas);
        gameCanvas.widthProperty().bind(wrapperPane.widthProperty());
        gameCanvas.heightProperty().bind(wrapperPane.heightProperty());

        return wrapperPane;
    }
}
