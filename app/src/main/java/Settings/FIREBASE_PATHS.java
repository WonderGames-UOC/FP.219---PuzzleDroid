package Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class FIREBASE_PATHS {
    public static final String URL = "https://puzzledroid-2605b-default-rtdb.firebaseio.com/";
    public static final String USERS = "Users";
    public static final String HIGHSCORES = "HighScores";
    public static final String EMAIL = "Email";
    public static final String IMAGESSEEN = "ImagesSeen";
    public static final String SCORES = "Scores";
    public static final String LASTUPDATE = "LastUpdate";
    public static final String CREATION = "CreatedAt";

    public static final String STORAGE_IMG = "PuzzleDroidImages";

    public static String getCurrentDateTime(){
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = formatter.format(date);
        return timestamp;
    }
}
