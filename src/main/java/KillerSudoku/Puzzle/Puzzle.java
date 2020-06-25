package KillerSudoku.Puzzle;

import KillerSudoku.Cage;
import KillerSudoku.Solver.Solver;
import KillerSudoku.Utility;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Puzzle implements Serializable { // Predstavlja Killer Sudoku konfiguraciju u pocetnom stanju i sadrzi metode za njegovo generisanje
    private byte[][] cells;
    private int[][] cellCageIndexes; // Indeks kaveza kojem pripadaju sve celije
    private int width, height;
    public Cage[] cages;
    public boolean success;

    public Puzzle(int width, int height, int cageNum, float maxSec){
        this.width = width;
        this.height = height;

        success = false;
        long startTime = System.nanoTime();
        cells = new byte[height][width];
        fillIn(); // Postave se brojevi svih celija
        while (!success){ // Isprobavaju se razliciti proizvoljni kavezi dok se ne nadje particija sa tacno jednim rjesenjem ili dok ne prodje vrijeme
            if(System.nanoTime() - startTime > maxSec * 1000000000) // Ako je previse vremena proslo zaustavi se procedura i success ostaje false
                return;
            generateCages(cageNum);
            success =  Solver.numOfSolutions(cellCageIndexes, cages) == 1;
        }
    }

    private void fillIn(){ // Backtracking algoritam za postavljanje brojeva svih celija. Kada se naidje na konflikt (celija nema vise mogucnosti) vraca se korak unazad preko history steka
        int width = cells[0].length;
        int height = cells.length;
        Stack<FillCell> history = new Stack<>(); // Koristi da bi se moglo vratiti korak unazad
        List<Point> order = new ArrayList<>(); // Odredjuje po kom redosljedu se popunjavaju celije. Trenutno je uvjek isti redosljed
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                order.add(new Point(x, y));
        // Collections.shuffle(order);
        while (history.size() <= order.size()) { // Radi dok se ne popuni svaka celija
            if (history.size() == 0)
                history.push(new FillCell(order.get(0), 0, cells));
            FillCell currCell = history.peek();
            if (currCell.hasNextPossibility()) {
                currCell.tryNextPossibility(cells);
                if (history.size() < order.size())
                    history.push(new FillCell(order.get(currCell.cellInd + 1), currCell.cellInd + 1, cells));
                else
                    return; // Popunjeno
            } else { // Vraca se korak unazad
                history.pop();
                cells[currCell.y][currCell.x] = 0;
            }
        }
    }

    private void generateCages(int num){
        cages = new Cage[num];
        for (int i = 0; i < cages.length; i++)
            cages[i] = new Cage();

        cellCageIndexes = new int[height][width];
        for (int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                cellCageIndexes[y][x] = -1;

        ArrayList<CageGrower> cageGrowers = new ArrayList<>(); // Lista celija za prosiravanje kaveza.

        for(int i = 0; i < cages.length; i++){ // Proizvoljno se odabiru prvobitne celije svih kaveza
            int x = 0, y = 0;
            while(cellCageIndexes[y][x] != -1){
                x = Utility.rand(width - 1);
                y = Utility.rand(height - 1);
            }
            cellCageIndexes[y][x] = i;
            cageGrowers.add(new CageGrower(x, y, i));
        }

        for(int i = 0; i < cageGrowers.size(); i++)
            cellCageIndexes[cageGrowers.get(i).y][cageGrowers.get(i).x] = -1;

        while (cageGrowers.size() > 0){
            int minInd = indOfMin(cageGrowers);
            CageGrower cg = cageGrowers.get(minInd);
            cageGrowers.remove(minInd);

            if(cellCageIndexes[cg.y][cg.x] == -1 && !cages[cg.cageInd].hasNum(cells[cg.y][cg.x], cells)) { // Ako se nije vec prosla ova celija
                cellCageIndexes[cg.y][cg.x] = cg.cageInd; // Dodaje se celija kavezu
                cages[cg.cageInd].addCell(cg.x, cg.y, cells[cg.y][cg.x]);
                for (int x = -1; x <= 1; x++) // Dodaju se svi susjedi celije u cageGrowers da bi nastavljali prosirivanje njenog kaveza
                    for (int y = -1; y <= 1; y++)
                        if ((x == 0 || y == 0) && (x != 0 || y != 0) && cg.x + x >= 0 && cg.y + y >= 0 && cg.x + x < width && cg.y + y < height)
                            cageGrowers.add(new CageGrower(cg.x + x, cg.y + y, cg.cageInd));
            }
        }

        for (int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                if(cellCageIndexes[y][x] == -1){
                    generateCages(num);
                    return;
                }

        List<Integer> order = new ArrayList<>();
        for(int i = 0; i < cages.length; i++)
            order.add(i);
        colorCages(order); // Oboji kaveze da bi korisnik znao koje im celije pripadaju
    }

    private int indOfMin(ArrayList<CageGrower> cageGrowers){ // Vraca indeks celije koja pripada kavezu sa najmanjim trenutnim brojem celija
        int minInd = 0;
        int min = cages[cageGrowers.get(0).cageInd].cells.size();
        for(int i = 1; i < cageGrowers.size(); i++) {
            int n = cages[cageGrowers.get(i).cageInd].cells.size();
            if (n < min || (n == min && Utility.rand(1) == 1)){
                min = n;
                minInd = i;
            }
        }
        return minInd;
    }


    private void colorCages(List<Integer> order){ // Isprobavaju se proizvoljni redovi bojanja kaveza dok jedan ne uspije da ih oboji tako da susjedni kavezi su razlicite boje
        for(int i = 0; i < cages.length; i++)
            cages[i].colorInd = 100;
        Collections.shuffle(order);
        for(int i = 0; i < order.size(); i++) {
            if (!tryColorCage(order.get(i))) {
                colorCages(order);
                return;
            }
        }
    }

    private boolean tryColorCage(int cageInd){ // Oboji kavez prvom bojom kojom nisu obojeni njegovi susjedi
        for(int i = 0; i < 4; i++) {
            if(isColorFree((byte)i, cageInd)) {
                cages[cageInd].colorInd = (byte)i;
                return true;
            }
        }
        return false;
    }

    private boolean isColorFree(byte c, int cageInd){ // Vraca true ako susjedi datog kaveza ne koriste datu boju
        for(int i = 0; i < cages[cageInd].cells.size(); i++)
            if (!isColorFree(c, cages[cageInd].cells.get(i).x, cages[cageInd].cells.get(i).y, cageInd))
                return false;
        return true;
    }

    private boolean isColorFree(byte c, int x, int y, int cageInd){ // Vraca true ako susjedi date celije ne koriste datu boju osim ako pripadaju istom kavezu te celije
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if((i == 0 || j == 0) && (i != 0 || j != 0) && x + i >= 0 && y + j >= 0 && x + i < width && y + j < height){
                    if(cellCageIndexes[y + j][x + i] != cageInd)
                        if(cages[cellCageIndexes[y + j][x + i]].colorInd == c)
                            return false;
                }
            }
        }
        return true;
    }

    public Cage cageOf(int cellX, int cellY){
        return cages[cellCageIndexes[cellY][cellX]];
    }

    public byte getNum(int x, int y){
        return cells[y][x];
    }

    public static Point getNonet(int cellX, int cellY){ // Pomocna metoda za vracaje poziciju top-left celije noneta kojem pripada data celija
        return new Point((cellX / 3) * 3, (cellY / 3) * 3);
    }
}
