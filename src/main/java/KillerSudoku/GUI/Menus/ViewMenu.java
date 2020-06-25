package KillerSudoku.GUI.Menus;

import KillerSudoku.GUI.ColoredLabel;
import KillerSudoku.GUI.GameCanvas;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;

public class ViewMenu extends Menu{
    public ViewMenu(GameCanvas gameCanvas, ColoredLabel correctLabel, ColoredLabel incorrectLabel, ColoredLabel mistakesLabel, ColoredLabel remainingLabel, ColoredLabel timerLabel){
        setText("View");

        CheckMenuItem crosshairItem = new CheckMenuItem("Crosshair");
        crosshairItem.setSelected(true);
        crosshairItem.setOnAction(e -> {
            gameCanvas.setCrosshairVisibility(crosshairItem.isSelected());
        });

        CheckMenuItem matchesItem = new CheckMenuItem("Matches");
        matchesItem.setSelected(true);
        matchesItem.setOnAction(e -> {
            gameCanvas.setMatchesVisibility(matchesItem.isSelected());
        });

        CheckMenuItem statsItem = new CheckMenuItem("Statistics");
        statsItem.setSelected(true);
        statsItem.setOnAction(e -> {
            correctLabel.setVisible(statsItem.isSelected());
            incorrectLabel.setVisible(statsItem.isSelected());
            mistakesLabel.setVisible(statsItem.isSelected());
            remainingLabel.setVisible(statsItem.isSelected());
        });

        CheckMenuItem timerItem = new CheckMenuItem("Timer");
        timerItem.setSelected(true);
        timerItem.setOnAction(e -> {
            timerLabel.setVisible(timerItem.isSelected());
        });

        getItems().addAll(crosshairItem, matchesItem, statsItem, timerItem);
    }
}
