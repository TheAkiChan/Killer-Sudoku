package KillerSudoku;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Cage implements Serializable {
    private int sum;
    private int x, y; // Celija na kojoj se prikazuje suma kaveza
    public byte colorInd; // Identifikator boje kaveza.
    public ArrayList<Point> cells;

    public Cage(){
        sum = 0;
        cells = new ArrayList<>();
    }

    public void addCell(int x, int y, byte cellNum){ // Dodaje novu celiju
        cells.add(new Point(x, y));
        sum += cellNum;
        calcPosition();
    }

    private void calcPosition(){ // Kalkulise na kojoj celiji da stoji suma
        int bestInd = 0;
        for(int i = 1; i < cells.size(); i++){
            if(cells.get(i).y < cells.get(bestInd).y || (cells.get(i).y == cells.get(bestInd).y && cells.get(i).x < cells.get(bestInd).x))
                bestInd = i;
        }
        x = cells.get(bestInd).x;
        y = cells.get(bestInd).y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getSum(){
        return sum;
    }

    public boolean hasNum(byte num, byte[][] cells){
        for(Point p : this.cells)
            if(cells[p.y][p.x] == num)
                return true;
        return false;
    }
}
