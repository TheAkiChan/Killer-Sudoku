package KillerSudoku.GameState;

import java.io.Serializable;
import java.util.Arrays;

public class PlayerCell implements Serializable{ // Predstavlja jednu celiju, njeno stanje, broj i kandidate

    public enum State{
        CANDIDATES, FINALIZED
    }

    private boolean[] candidates;
    private State state;
    private byte num;

    public PlayerCell(){
        super();

        state = State.CANDIDATES;
        candidates = new boolean[9];
        for(int i = 0; i < 9; i++)
            candidates[i] = false;
        num = 0;
    }

    public State getState(){
        return state;
    }

    public void setState(State state){
        if(this.state == state)
            return;
        this.state = state;
        if(state == State.CANDIDATES) { // Resetuju se svi kandidati i broj na celiji
            num = (byte) 0;
            clearCandidates();
        }
    }

    public byte getNum(){
        return num;
    }

    public boolean isCandidate(byte i){ // Da li je prikazan kandidat i
        return candidates[i-1];
    }

    public void toggleCandidate(int i){ // Invertuje vidljivost kandidata i
        if(state == State.CANDIDATES)
            candidates[i-1] = !candidates[i-1];
    }

    public void clearCandidates(){
        if(state == State.CANDIDATES)
            Arrays.fill(candidates, false);
    }

    public void setNum(byte n){ // Postavi se broj na celiji
        num = n;
        clearCandidates();
        state = State.FINALIZED;
    }

    public void clearNum(){ // Izbrise se broj sa celije
        num = (byte)0;
        state = State.CANDIDATES;
        clearCandidates();
    }
}
