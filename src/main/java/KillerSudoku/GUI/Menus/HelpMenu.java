package KillerSudoku.GUI.Menus;

import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class HelpMenu extends Menu {
    public HelpMenu(){
        setText("Help");

        MenuItem gameInfoItem = new MenuItem("Game Info");
        gameInfoItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("What is Killer sudoku?");
            alert.setHeaderText("The 9x9 grid is divided into 9 3x3 nonets.\nIt is also divided into cages which are connected cells of the same color.\nEach cage has a small number in its top-left corner.\n\nThe objective is to fill out the 9x9 grid with numbers 1 to 9.\nThe following conditions must be met: \n\t1. Each row, column, and nonet must contain each number exactly once\n\t2. The sum of all numbers in a cage must match the small number printed in its corner\n\t3. All numbers in the same cage must be different");
            alert.showAndWait();
        });

        MenuItem controlsItem = new MenuItem("Controls");
        controlsItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Controls");
            alert.setHeaderText("Use the mouse, arrow keys or WASD keys to select cells on the board. \nClick the selected cell or press ESCAPE to deselect it. \nPress numbers 1 to 9 to place them on the selected cell. \nHold shift while doing so to enter them as candidates. \nIf the number is already entered it will be erased instead.");
            alert.showAndWait();
        });

        getItems().addAll(gameInfoItem, controlsItem);
    }
}
