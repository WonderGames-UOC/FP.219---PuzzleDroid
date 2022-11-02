package dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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
                "PuzzRes integer);";

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

    // Método para inserción de datos
    public void insertRow(String user, String date, String time, String pic, int puzzres ){
        ContentValues content = new ContentValues();
        content.put("User", user);
        content.put("Date", date);
        content.put("Time", time);
        content.put("Pic", pic);
        content.put("PuzzRes", puzzres);
        this.getWritableDatabase().insert("HighScores", null, content);
    }
}
