package KillerSudoku.Solver;

import KillerSudoku.Cage;
import KillerSudoku.Puzzle.Puzzle;

import java.awt.*;
import java.util.ArrayList;

public class Solver {
    public static int numOfSolutions(int[][] cellCageIndexes, Cage[] cages){ // Nalazi da li dati killer sudoku ima 0, 1 ili vise od 1 rjesenja
        SolverCell[][] cells = new SolverCell[cellCageIndexes.length][cellCageIndexes[0].length];
        for(int i = 0; i < cells.length; i++)
            for(int j = 0; j < cells[i].length; j++)
                cells[i][j] = new SolverCell(j, i);
        int oldPossNum = -1;
        int newPossNum = updateCellPossibilities(cells, cellCageIndexes, cages);
        while(newPossNum != oldPossNum){ // Prolazi kroz sve celije i rekalkulise njihove mogucnosti dok se ne mogu vise smanjivati
            oldPossNum = newPossNum;
            newPossNum = updateCellPossibilities(cells, cellCageIndexes, cages);
        }
        if(newPossNum == cells.length * cells[0].length)
            return 1;
        ArrayList<SolverCell> undecided = new ArrayList<>();
        for(int i = 0; i < cells.length; i++)
            for(int j = 0; j < cells[i].length; j++)
                if(cells[i][j].possibilities.size() > 1)
                    undecided.add(cells[i][j]);
        if(undecided.size() > 35) // Preveliki broj da se prodje svaka kombinacija
            return 2; // Nije bitan tacan broj, samo da je veci od 1
        byte[][] cellsCurr = new byte[cells.length][cells[0].length];
        for(int i = 0; i < cells.length; i++)
            for(int j = 0; j < cells[i].length; j++)
                if(cells[i][j].possibilities.size() == 1)
                    cellsCurr[i][j] = cells[i][j].possibilities.get(0);
        return numOfValidCombinations(cellsCurr, cellCageIndexes, cages, undecided, 0);
    }

    private static int numOfValidCombinations(byte[][] cells, int[][] cellCageIndexes, Cage[] cages, ArrayList<SolverCell> undecided, int curr){ // Provjerava svaku kombinaciju brojeva za sve celije koje imaju vise od jednu mogucnost
        if(curr >= undecided.size()){
            for(int i = 0; i < undecided.size(); i++) {
                int x = undecided.get(i).x;
                int y = undecided.get(i).y;
                if (!isValid(cells, x, y, cages[cellCageIndexes[y][x]], true))
                    return 0;
            }
            return 1;
        }
        SolverCell c = undecided.get(curr);
        int num = 0;
        for(int i = 0; i < c.possibilities.size(); i++){
            cells[c.y][c.x] = c.possibilities.get(i);
            if(isValid(cells, c.x, c.y, cages[cellCageIndexes[c.y][c.x]], false)) // Ostavlja provjeru suma za sami kraj jer neke celije su i dalje 0
                num += numOfValidCombinations(cells, cellCageIndexes, cages, undecided, curr + 1);
            if(num >= 2)
                return 2;
        }
        cells[c.y][c.x] = 0; // Vrati na prazno na kraju
        return num;
    }

    private static boolean isValid(byte[][] cells, int x, int y, Cage cage, boolean checkCageSum){ // Provjerava da li data celija zadovoljava sve uslove igre
        int width = cells[0].length;
        int height = cells.length;
        byte num = cells[y][x];
        for(int i = 0; i < width; i++)
            if(i != x && cells[y][i] == num)
                return false;
        for(int i = 0; i < height; i++)
            if(i != y && cells[i][x] == num)
                return false;
        Point nonet = Puzzle.getNonet(x, y);
        for(int i = nonet.x; i < nonet.x + 3; i++){
            for(int j = nonet.y; j < nonet.y + 3; j++){
                if(i != x || j != y)
                    if(cells[j][i] == num)
                        return false;
            }
        }
        if(cage == null)
            return true;
        for(int i = 0; i < cage.cells.size(); i++)
            if(cage.cells.get(i).x != x || cage.cells.get(i).y != y)
                if(cells[cage.cells.get(i).y][cage.cells.get(i).x] == num)
                    return false;
         if(checkCageSum){
             int sum = 0;
             for(Point p : cage.cells)
                 sum += cells[p.y][p.x];
             return sum == cage.getSum();
         }
        return true;
    }

    private static int updateCellPossibilities(SolverCell[][] cells, int[][] cellCageIndexes, Cage[] cages){ // Za sve celije kalkulise mogucnosti i vraca sumu mogucnosti svih celija
        int totalPossNum = 0;
        for(int i = 0; i < cells.length; i++)
            for(int j = 0; j < cells[i].length; j++) {
                int n = cells[i][j].updatePossibilities(cells, cages[cellCageIndexes[i][j]]);
                if(n == 0)
                    return 0;
                totalPossNum += n;
            }
        return totalPossNum;
    }
}
