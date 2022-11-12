package dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import util.HighScore;

public class SQLiteHelper extends SQLiteOpenHelper {
    private final String TAG = "SQLiteHelper";
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
    private void updateDB(SQLiteDatabase sqLiteDatabase){
        Log.d(TAG, "updateDB");
        String dropQuery = "DROP TABLE HighScores";
        String query = "CREATE TABLE HighScores (" +
                "_ID integer primary key autoincrement, " +
                "User text, " +
                "Date text, " +
                "Time text, " +
                "Pic text, " +
                "PuzzRes integer," +
                "Moves integer);";
        try {
            sqLiteDatabase.execSQL(dropQuery);
            sqLiteDatabase.execSQL(query);
        }catch (Exception e){
            Log.d(TAG, e.getMessage() + "\r\n" + e.getCause());
        }
    }
    public void onDowngrade(SQLiteDatabase db, int a, int b){
        Log.d(TAG, "onDowngrade");
    }
    public void onUpgrade(SQLiteDatabase db, int a, int b){
        Log.d(TAG, "onUpgrade");
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
        try{
            //this.getWritableDatabase().insert("HighScores", null, content);
            this.getWritableDatabase().insertOrThrow("HighScores", null, content);
        }catch (Exception e){
            Log.d(TAG, e.getMessage() + "\r\n" + e.getCause());
            updateDB(this.getWritableDatabase());
            insert_HS_Row(highScore);
        }
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



