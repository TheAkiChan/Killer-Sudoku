package KillerSudoku.Puzzle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class FillCell { // Koristi za popunjavanje celija sa brojevima tako da prate pravila obicnog sudoku-a. Ne bavi se kavezima
    public int x, y, cellInd;
    private ArrayList<Byte> possibilities;

    public FillCell(Point cell, int cellInd, byte[][] cells){
        this.x = cell.x;
        this.y = cell.y;
        this.cellInd = cellInd;
        possibilities = new ArrayList<>();
        setPossibilities(cells);
        Collections.shuffle(possibilities); // Da bi se svaki put dobila nova konfiguracija brojeva
    }

    private void setPossibilities(byte[][] cells){ // Postavlja pocetne mogucnosti prateci pravila obicnog sudoku-a
        for(int i = 1; i <= 9; i++)
            possibilities.add((byte)i);
        int width = cells[0].length;
        int height = cells.length;
        for(int i = 0; i < width; i++)
            if(i != x)
                removePossibility(cells[y][i]);
        for(int i = 0; i < height; i++)
            if(i != y)
                removePossibility(cells[i][x]);
        Point nonet = Puzzle.getNonet(x, y);
        for(int i = nonet.x; i < nonet.x + 3; i++){
            for(int j = nonet.y; j < nonet.y + 3; j++){
                if(i != x || j != y)
                    removePossibility(cells[j][i]);
            }
        }
    }

    private void removePossibility(byte p){ // Eliminise mogucnost
        if(p == 0)
            return;
        for(int i = 0; i < possibilities.size(); i++)
            if(possibilities.get(i) == p) {
                possibilities.remove(i);
                return;
            }
    }

    public void tryNextPossibility(byte[][] cells){
        cells[y][x] = possibilities.get(0);
        possibilities.remove(0);
    }

    public boolean hasNextPossibility(){
        return possibilities.size() != 0;
    }
}

