package KillerSudoku.GUI;

import KillerSudoku.Cage;
import KillerSudoku.GameState.GameState;
import KillerSudoku.GameState.PlayerCell;
import KillerSudoku.Puzzle.Puzzle;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class GameCanvas extends Canvas {
    private static final int BOARD_X = 0, BOARD_Y = 0;

    private static final int BOARD_BORDER_THICKNESS = 10, NONET_BORDER_THICKNESS = 5, CELL_BORDER_THICKNESS = 1; // Velicine razlicitih ivica

    private static final int CELL_SIZE = 80; // Duzina stranica za celije

    // Boje kojim se popunjavaju pozadina, ivice i drugi elementi
    private static final Color BACKGROUND_COLOR = Color.WHITE, TEXT_COLOR = Color.BLACK, BORDER_COLOR = Color.BLACK, SELECTION_COLOR = Color.RED, CROSSHAIR_COLOR = transp(Color.MAROON, 1), MATCHES_COLOR = transp(Color.CORAL, 1), NONET_COLOR = CROSSHAIR_COLOR;

    // 4 boje koje se koriste za bojenje kaveza
    private static final Color[] CAGE_COLORS = new Color[]{Color.rgb(255, 253, 152), Color.rgb(207, 231, 153), Color.rgb(203, 232, 250), Color.rgb(248, 207, 223), Color.rgb(248, 207, 223)};

    // Velicine fontova
    private static final double MAIN_NUM_FONT_SIZE = 50;
    private static final double CANDIDATE_FONT_SIZE = 25;
    private static final double CAGE_SUM_FONT_SIZE = 15;

    // Fontovi
    private static Font MAIN_NUM_FONT;
    private static Font CANDIDATE_FONT;
    private static Font CAGE_SUM_FONT;

    private GameState gameState; // Referenca stanju igre koje ovaj objekt reprezentuje
    private GraphicsContext g;

    public ColoredLabel correctLabel, incorrectLabel, remainingLabel, mistakesLabel, timerLabel;

    private int secondsPast;
    private Timer timer;
    private boolean timerRestarted;

    private boolean SHOW_CROSSHAIRS = true;
    private boolean SHOW_MATCHES = true;

    // Koriste se da bi se vidjela cijela sudoku tabla kad se mijenjaju dimenzije prozora
    private double ratio;
    private double offset;

    public GameCanvas(GameState gameState){
        super();

        this.gameState = gameState;

        ratio = 1;

        g = getGraphicsContext2D();
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);

        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());
        setOnMousePressed(e -> checkMousePressed(e.getX(), e.getY()));

        setFocusTraversable(true);
        setOnKeyPressed(e -> checkKeyPressed(e));

        timer = new Timer();
        startTimer();
    }

    public void startTimer(){ // Pocinje tajmer kad pocne igra
        secondsPast = 0;
        timerRestarted = true;
        if(timerLabel != null)
            timerLabel.setText("TIMER: " + formattedTime());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() { // Posle svake sekunde povecava se broj za 1
                Platform.runLater(() -> {
                    timerRestarted = false;
                    secondsPast++;
                    if(timerLabel != null)
                        timerLabel.setText("TIMER: " + formattedTime());
                    scheduleTimerTask(timer);
                });
            }
        }, 1000);
    }

    private void scheduleTimerTask(Timer timer){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(timerRestarted)
                        return;
                    secondsPast++;
                    if(timerLabel != null)
                        timerLabel.setText("TIMER: " + formattedTime());
                    scheduleTimerTask(timer);
                });
            }
        }, 1000);
    }

    private String formattedTime(){ // Format: 00:00
        String min = secondsPast / 60 + "";
        String sec = secondsPast % 60 + "";
        if(min.length() == 1)
            min = "0" + min;
        if(sec.length() == 1)
            sec = "0" + sec;
        return min + ":" + sec;
    }

    public void setCrosshairVisibility(boolean value){
        SHOW_CROSSHAIRS = value;
        draw();
    }

    public void setMatchesVisibility(boolean value){
        SHOW_MATCHES = value;
        draw();
    }

    private void checkKeyPressed(KeyEvent e){ // Provjerava za sve komande preko tastature
        if(gameState.hasWon()) // Ignorise se ako je zavrsena igra
            return;
        if(e.getCode() == KeyCode.ESCAPE){ // Escape ponistava selekciju
            gameState.selection = null;
            draw();
        }else if(gameState.selection != null && e.getCode().isDigitKey()){ // Pritisnut je broj
            byte num = (byte)(e.getCode().getName().charAt(e.getCode().getName().length() - 1) - '0');
            if(num == 0) // Ignorise se nula
                return;
            if(e.isShiftDown()) { // Kandidat
                gameState.getSelected().setState(PlayerCell.State.CANDIDATES);
                gameState.getSelected().toggleCandidate(num);
            }else { // Nije kandidat
                gameState.getSelected().setState(PlayerCell.State.FINALIZED);
                if(gameState.getSelected().getNum() == num) {
                    gameState.getSelected().clearNum();
                } else {
                    gameState.getSelected().setNum(num);
                    if(gameState.puzzle.getNum(gameState.selection.x, gameState.selection.y) != gameState.getSelected().getNum()) // Povecava broj gresaka ako je napravljena greska
                        gameState.mistakesMade++;
                }
            }
            draw();
        }else if(e.getCode().isArrowKey()){ // Strelice pomjeraju selekciju bez potrebe koriscenja misa
            switch (e.getCode()){
                case UP:
                    shiftSelection(0, -1);
                    break;
                case DOWN:
                    shiftSelection(0, 1);
                    break;
                case LEFT:
                    shiftSelection(-1, 0);
                    break;
                case RIGHT:
                    shiftSelection(1, 0);
                    break;
                default:
                    break;
            }
        }else if(e.getCode().isLetterKey()){ // Slicno kao strelice
            switch (e.getCode()){
                case W:
                    shiftSelection(0, -1);
                    break;
                case S:
                    shiftSelection(0, 1);
                    break;
                case A:
                    shiftSelection(-1, 0);
                    break;
                case D:
                    shiftSelection(1, 0);
                    break;
                default:
                    break;
            }
        }
    }

    private void shiftSelection(int dx, int dy){ // Pomjera se selekcija za 1 u nekom pravcu
        if(gameState.selection == null)
            gameState.selection = new Point(0, 0);
        else {
            gameState.selection.x += dx;
            gameState.selection.y += dy;
            if (gameState.selection.x < 0)
                gameState.selection.x = gameState.getWidth() - 1;
            else if (gameState.selection.x >= gameState.getWidth())
                gameState.selection.x = 0;
            if (gameState.selection.y < 0)
                gameState.selection.y = gameState.getHeight() - 1;
            else if (gameState.selection.y >= gameState.getHeight())
                gameState.selection.y = 0;
        }
        draw();
    }

    private void checkMousePressed(double mouseX, double mouseY){ // Provjerava za sve komande preko misa
        if(gameState.hasWon()) // Ignorise se ako je zavrsena igra
            return;
        if(getWidth() > getHeight()) // Kalkulise se pozicija misa imajuci u vid offset
            mouseX -= offset;
        else
            mouseY -= offset;
        mouseX /= ratio; // Kalkulise se pozicija misa imajuci u vid ratio
        mouseY /= ratio;
        for(int y = 0; y < gameState.getHeight(); y++) { // Za svaku celiju provjerava da li sadrzi kursor
            for (int x = 0; x < gameState.getWidth(); x++) {
                if(new Rectangle(cellInnerPos(x, y).x, cellInnerPos(x, y).y, CELL_SIZE, CELL_SIZE).contains(mouseX, mouseY)){
                    if(gameState.selection != null && gameState.selection.x == x && gameState.selection.y == y)
                        gameState.selection = null;
                    else
                        gameState.selection = new Point(x, y);
                    draw();
                    return;
                }
            }
        }
    }

    private Point cellInnerPos(int indX, int indY){ // top-left kraj unutrasnjosti celije (jer celije dijele ivice)
        int nonetBorderOnXNum = indX / 3;
        int nonetBorderOnYNum = indY / 3;
        return new Point(CELL_SIZE * indX + CELL_BORDER_THICKNESS * (indX - nonetBorderOnXNum) + BOARD_BORDER_THICKNESS + NONET_BORDER_THICKNESS * nonetBorderOnXNum,
                CELL_SIZE * indY + CELL_BORDER_THICKNESS * (indY - nonetBorderOnYNum) + BOARD_BORDER_THICKNESS + NONET_BORDER_THICKNESS * nonetBorderOnYNum);
    }

    private void adjustForCanvasSize(){ // Kalkulisu se ratio i offset koristeci dimenzije canvas-a i mijenjaju se velicine fontova da budu u skladu sa ratio
        ratio = Math.min(getWidth(), getHeight()) / 755.0;
        offset = (Math.max(getWidth(), getHeight()) - Math.min(getWidth(), getHeight())) / 2;

        MAIN_NUM_FONT =  Font.font("Ariel", FontWeight.BOLD, MAIN_NUM_FONT_SIZE * ratio);
        CANDIDATE_FONT =  Font.font("Ariel", FontWeight.SEMI_BOLD, CANDIDATE_FONT_SIZE * ratio);
        CAGE_SUM_FONT =  Font.font("Ariel", FontWeight.BOLD, CAGE_SUM_FONT_SIZE * ratio);
    }

    public void draw(){ // Glavna metoda gdje se crta na canvas-u. Pozvana je kad god se desi neka promjena koja zahtjeva ponovno crtanje
        adjustForCanvasSize();

        g.setFill(BACKGROUND_COLOR); // Oboji pozadina
        g.fillRect(0, 0, getWidth(), getHeight());
        drawCells();
        drawBorders();
        if(SHOW_CROSSHAIRS)
            drawCrosshair();
        if(SHOW_MATCHES)
            drawMatches();
        drawCageSums();

        if(correctLabel != null && incorrectLabel != null) { // Azuriraju labele (nisu dio canvas-a)
            correctLabel.change("CORRECT: " + gameState.getCorrect(), Color.GREEN);
            if (gameState.getIncorrect() == 0)
                incorrectLabel.change("INCORRECT: 0", Color.GRAY);
            else
                incorrectLabel.change("INCORRECT: " + gameState.getIncorrect(), Color.RED);
            remainingLabel.change("REMAINING: " + gameState.getRemaining(), Color.BLACK);
            if (gameState.mistakesMade == 0)
                mistakesLabel.change("MISTAKES: 0", Color.GRAY);
            else
                mistakesLabel.change("MISTAKES: " + gameState.mistakesMade, Color.RED);
        }
        if(!gameState.usedSolutionMenuItem && gameState.hasWon()){ // Ako je korisnik pobjedio pokazuje se poruka (ne pokaze se ako se iskoristio Show Solution menu item)
            timerRestarted = true;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Puzzle Solved");
            alert.setHeaderText("Congratulations! You've solved the puzzle.");
            alert.showAndWait();
            gameState.selection = null;
        }
    }

    private void drawCageSums(){ // Crtaju se sume kaveza
        if(gameState.puzzle.cages.length == 1)
            return;
        for(Cage c : gameState.puzzle.cages){
            Point p = cellInnerPos(c.getX(), c.getY());
            drawNum(c.getSum(), p.x, p.y, CAGE_SUM_FONT, CAGE_COLORS[c.colorInd].invert(), false);
        }
    }

    private void drawMatches(){ // Oznacavaju se sve celije sa istim brojem kao selektovana celija
        if(gameState.selection == null)
            return;
        if(gameState.getSelected().getNum() == 0)
            return;
        for (int i = 0; i < gameState.getWidth(); i++)
            for(int j = 0; j < gameState.getHeight(); j++)
                if((i != gameState.selection.x || j != gameState.selection.y) && gameState.getCell(i, j).getNum() == gameState.getSelected().getNum())
                    drawCellHighlight(i, j, MATCHES_COLOR);
    }

    private void drawCrosshair() { // Isticu se vertikala, horizontala i nonet kojim pripada selektovana celija
        if (gameState.selection == null)
            return;
        for (int i = 0; i < gameState.getWidth(); i++)
            if (i != gameState.selection.x)
                drawCellHighlight(i, gameState.selection.y, CROSSHAIR_COLOR);
        for (int i = 0; i < gameState.getHeight(); i++)
            if (i != gameState.selection.y)
                drawCellHighlight(gameState.selection.x, i, CROSSHAIR_COLOR);
        Point nonet = Puzzle.getNonet(gameState.selection.x, gameState.selection.y);
        for (int x = 0; x < 3; x += 1)
            for (int y = 0; y < 3; y += 1)
                if (nonet.x + x != gameState.selection.x && nonet.y + y != gameState.selection.y)
                    drawCellHighlight(nonet.x + x, nonet.y + y, NONET_COLOR);
    }

    private void drawCells(){ // Crtaju se sve celije
        for(int y = 0; y < gameState.getHeight(); y++){
            for(int x = 0; x < gameState.getWidth(); x++){
                Point pos = cellInnerPos(x, y);
                PlayerCell c = gameState.getCell(x, y);
                Point p = cellInnerPos(x, y);
                if(gameState.puzzle.cages.length == 1)
                    g.setFill(Color.WHITE);
                else
                    g.setFill(CAGE_COLORS[gameState.puzzle.cageOf(x, y).colorInd]);
                fillRect(p.x, p.y, CELL_SIZE, CELL_SIZE);
                switch (c.getState()){
                    case CANDIDATES:
                        for(int cx = 0; cx < 3; cx++){
                            for(int cy = 0; cy < 3; cy++){
                                byte i = (byte)(cy * 3 + cx + 1);
                                if(c.isCandidate(i))
                                    drawNum(i, pos.x + CELL_SIZE / 4 * (cx + 1), pos.y + CELL_SIZE / 4 * (cy + 1), CANDIDATE_FONT);
                            }
                        }
                        break;
                    case FINALIZED:
                        drawNum(c.getNum(), pos.x + CELL_SIZE / 2, pos.y + CELL_SIZE / 2, MAIN_NUM_FONT);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void drawBorders(){ // Crtaju se sve ivice
        int boardBorderWidth = cellInnerPos(gameState.getWidth() - 1, 0).x + CELL_SIZE + BOARD_BORDER_THICKNESS - BOARD_X;
        int boardBorderHeight = cellInnerPos(0, gameState.getHeight() - 1).y + CELL_SIZE + BOARD_BORDER_THICKNESS - BOARD_Y;

        drawBorder(BOARD_X, BOARD_Y, boardBorderWidth, boardBorderHeight, BOARD_BORDER_THICKNESS, BORDER_COLOR);
        for(int y = 0; y < gameState.getHeight() / 3; y++){
            for(int x = 0; x < gameState.getWidth() / 3; x++){
                Point topLeft = cellInnerPos(x * 3, y * 3);
                Point botRight = cellInnerPos(x * 3 + 2, y * 3 + 2);

                int bx = topLeft.x - NONET_BORDER_THICKNESS;
                int by = topLeft.y - NONET_BORDER_THICKNESS;
                int bw = botRight.x + CELL_SIZE + NONET_BORDER_THICKNESS - bx;
                int bh = botRight.y + CELL_SIZE + NONET_BORDER_THICKNESS - by;

                drawBorder(bx, by, bw, bh, NONET_BORDER_THICKNESS, BORDER_COLOR);
            }
        }

        for(int y = 0; y < gameState.getHeight(); y++){
            for(int x = 0; x < gameState.getWidth(); x++){
                Point pos = cellInnerPos(x, y);
                drawBorder(pos.x, pos.y, CELL_SIZE + CELL_BORDER_THICKNESS, CELL_SIZE + CELL_BORDER_THICKNESS, CELL_BORDER_THICKNESS, BORDER_COLOR);
            }
        }
        if(gameState.selection != null) {
            drawCellHighlight(gameState.selection.x, gameState.selection.y, SELECTION_COLOR);
        }
    }

    private void drawCellHighlight(int x, int y, Color color){ // Crta posebnu ivicu na datu celiju. Koristi se da se istakne ta celija
        if(gameState.hasWon())
            return;
        Point pos = cellInnerPos(x, y);
        drawBorder(pos.x, pos.y, CELL_SIZE + CELL_BORDER_THICKNESS, CELL_SIZE + CELL_BORDER_THICKNESS, NONET_BORDER_THICKNESS, color);
    }

    private void drawNum(int num, int x, int y, Font font){ // Za crtanje kandidata i brojeva na celijama
        drawNum(num, x, y, font, TEXT_COLOR, true);
    }

    private void drawNum(int num, int x, int y, Font font, Color color, boolean isCentered){ // Uopstenija metoda za crtanje brojeva. Koristi se jos za sume kaveza
        if(isCentered){
            g.setTextAlign(TextAlignment.CENTER);
            g.setTextBaseline(VPos.CENTER);
        }else{
            g.setTextAlign(TextAlignment.LEFT);
            g.setTextBaseline(VPos.TOP);
            x += 2;
            y -= 2;
        }
        g.setFont(font);
        g.setFill(color);
        fillText(num + "", x, y);
    }

    private void drawBorder(int x, int y, int width, int height, int thickness, Color color){ // Za crtanje ivice
        g.setFill(color);

        fillRect(x, y, width, thickness);
        fillRect(x, y, thickness, height);
        fillRect(x,y + height - thickness, width, thickness);
        fillRect(x + width - thickness, y, thickness, height);
    }

    private void fillRect(double x, double y, double w, double h){ // Za bojenje unutrasnjosti celija i za crtanje ivica
        if(getWidth() > getHeight())
            g.fillRect(offset + x * ratio, y * ratio, w * ratio, h * ratio);
        else
            g.fillRect(x * ratio, offset + y * ratio, w * ratio, h * ratio);
    }

    private void fillText(String text, double x, double y){ // Za crtanje teksta (trenutno samo je koristi metoda za crtanje brojeva)
        if(getWidth() > getHeight())
            g.fillText(text, offset + x * ratio, y * ratio);
        else
            g.fillText(text, x * ratio, offset + y * ratio);
    }

    private static Color transp(Color c, double a){ // Dodaje transparentnost datoj boji
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
}
