package entities;

public class User {
    String Email;
    Scores scores;
    public User(String Email, Scores scores){
        this.Email = Email;
        this.scores = scores;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }
}
