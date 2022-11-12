package util;



public class HighScore {
    private String _ID;
    private String user;
    private String date;
    private String time;
    private String pic;
    private String puzzres;
    private String moves;


    // Constructor para la instanciación de objetos HACIA la base de datos.
    public HighScore(String user, String date, String time, String pic, String puzzres, String moves) {
        this._ID = null;
        this.user = user;
        this.date = date;
        this.time = time;
        this.pic = pic;
        this.puzzres = puzzres;
        this.moves = moves;
    }
    // Constructor para instaciación de objetos DESDE la base de datos
    public HighScore(String _ID, String user, String date, String time, String pic, String puzzres, String moves) {
        this._ID = null;
        this.user = user;
        this.date = date;
        this.time = time;
        this.pic = pic;
        this.puzzres = puzzres;
        this.moves = moves;
    }

    public String get_ID() {
        return _ID;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPuzzres() {
        return puzzres;
    }

    public void setPuzzres(String puzzres) {
        this.puzzres = puzzres;
    }

    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }

    @Override
    public String toString() {
        return "HighScore{}";
    }

}


