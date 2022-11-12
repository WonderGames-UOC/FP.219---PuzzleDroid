package dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import util.HighScore;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE HighScores (" +
                "_ID integer primary key autoincrement, " +
                "User text, " +
                "Date text, " +
                "Time text, " +
                "Pic text, " +
                "PuzzRes integer," +
                "Moves integer);";

        sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Método para la apertura de la BD
    public void openDB (){
        this.getWritableDatabase();
    }

    // Método para el cierre de la BD
    public void closeDB (){
        this.close();
    }

    // Método para inserción de High Scores
    public void insert_HS_Row(HighScore highScore) {
        ContentValues content = new ContentValues();
        content.put("User", highScore.getUser());
        content.put("Date", highScore.getDate());
        content.put("Time", highScore.getTime());
        content.put("Pic", highScore.getPic());
        content.put("PuzzRes", highScore.getPuzzres());
        content.put("Moves", highScore.getMoves());
        this.getWritableDatabase().insert("HighScores", null, content);
        this.close();
    }

    // Método para listar los High Scores
    public List<HighScore> return_HS_List(){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM HighScores ORDER BY PuzzRes DESC,Time LIMIT 5", null);
        List <HighScore> hs = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                hs.add(new HighScore(cursor.getString(0), // _ID
                        cursor.getString(1), // user
                        cursor.getString(2), // date
                        cursor.getString(3), // time
                        cursor.getString(4), // pic
                        cursor.getString(5),  // puzzres
                        cursor.getString(6))); // moves
            }while (cursor.moveToNext());
        }
        cursor.close();
        this.close();
        return hs;
    }

}



