package entities;

import java.util.ArrayList;

public class Scores {
    public ArrayList<String> getId() {
        return scores;
    }

    public void setId(String id) {
        this.scores = scores;
    }

    ArrayList<String> scores;

    public Scores(ArrayList<String>scores){
        this.scores = scores;
    }
}
