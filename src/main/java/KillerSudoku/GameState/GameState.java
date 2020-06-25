package KillerSudoku.GameState;

import KillerSudoku.Puzzle.Puzzle;
import KillerSudoku.Utility;
import javafx.scene.control.Alert;

import java.awt.*;
import java.io.*;

public class GameState implements Serializable { // Trenutno stanje igre. Koristi se i da se sacuva igra na fajlu
    private PlayerCell[][] cells;
    public Puzzle puzzle;
    private int width, height;
    public int mistakesMade;
    public Point selection;
    public boolean success, usedSolutionMenuItem;

    public void init(int width, int height, int cageNum, int givenNum, float maxSec) { // Inicijalizacija igre
        this.width = width;
        this.height = height;

        PlayerCell[][] cells = new PlayerCell[height][width];
        Puzzle puzzle = new Puzzle(width, height, cageNum, maxSec);
        success = puzzle.success; // Prati da li se uspjesno generisalo sve
        if(!success)
            return;
        mistakesMade = 0;
        this.cells = cells;
        this.puzzle = puzzle;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                cells[y][x] = new PlayerCell();
        selection = null;
        usedSolutionMenuItem = false;

        for(int i = 0; i < givenNum; i++){
            int x = Utility.rand(width - 1);
            int y = Utility.rand(height - 1);
            if(cells[y][x].getNum() == 0)
                cells[y][x].setNum(puzzle.getNum(x, y));
            else
                i--;
        }
    }

    public PlayerCell getCell(int x, int y){
        return cells[y][x];
    }

    public PlayerCell getSelected(){
        return cells[selection.y][selection.x];
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getCorrect(){ // Broj celija kojih je korisnik uspjesno odredio
        int correct = 0;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                if(cells[y][x].getState() == PlayerCell.State.FINALIZED)
                    if(cells[y][x].getNum() == puzzle.getNum(x, y))
                        correct++;
        return correct;
    }

    public int getIncorrect(){ // Broj netacnih
        int incorrect = 0;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                if(cells[y][x].getState() == PlayerCell.State.FINALIZED)
                    if(cells[y][x].getNum() != puzzle.getNum(x, y))
                        incorrect++;
        return incorrect;
    }

    public int getRemaining(){ // Broj celija kojih korisnik nije popunio
        int remaining = 0;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                if(cells[y][x].getState() == PlayerCell.State.CANDIDATES)
                    remaining++;
        return remaining;
    }

    public boolean hasWon(){ // Da li je igra zavrsena
        return getCorrect() == width * height;
    }

    public void save(File file){ // Sacuva igru na fajlu
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private GameState fromFile(File file){ // Cita igru od fajla i vraca njeno trenutno stanje
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fis);
            GameState gs = (GameState) in.readObject();
            in.close();
            return gs;
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to Load");
            alert.setHeaderText("Failed to load game from the file \"" + file.getAbsolutePath() + "\".");
            alert.showAndWait();
        }
        return null;
    }

    public void load(File file){ // Kopira stanje sa fajla
        GameState gs = fromFile(file);
        if(gs == null)
            return;
        this.cells = gs.cells;
        this.puzzle = gs.puzzle;
        this.width = gs.width;
        this.height = gs.height;
        this.mistakesMade = gs.mistakesMade;
        this.selection = gs.selection;
        this.success = gs.success;
    }
}
