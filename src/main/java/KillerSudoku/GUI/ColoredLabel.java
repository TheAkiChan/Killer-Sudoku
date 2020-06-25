package KillerSudoku.GUI;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ColoredLabel extends Label { // Labela obojena posebnom bojom
    public ColoredLabel(String str, Color color){
        super();
        change(str, color);
        setFont(Font.font(getFont().getFamily(), FontWeight.BOLD, getFont().getSize()));
    }

    public void change(String str, Color color){
        setText(str);
        setTextFill(color);
    }
}
