package util;



public class HighScore {
    private int _ID;
    private String user;
    private String date;
    private String time;
    private String pic;
    private String puzzres;


    // Constructor para la instanciación de objetos HACIA la base de datos.
    public HighScore(String user, String date, String time, String pic, String puzzres) {
        this._ID = 0;
        this.user = user;
        this.date = date;
        this.time = time;
        this.pic = pic;
        this.puzzres = puzzres;
    }
    // Constructor para instaciación de objetos DESDE la base de datos
    public HighScore(int _ID, String user, String date, String time, String pic, String puzzres) {
        this._ID = _ID;
        this.user = user;
        this.date = date;
        this.time = time;
        this.pic = pic;
        this.puzzres = puzzres;
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
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


    @Override
    public String toString() {
        return "HighScore{}";
    }

}


