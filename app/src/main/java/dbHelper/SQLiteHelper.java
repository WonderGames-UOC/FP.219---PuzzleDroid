package dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
        Log.d(TAG, "onCreate");
        String query = "CREATE TABLE HighScores (" +
                "_ID integer primary key autoincrement, " +
                "User text, " +
                "Date text, " +
                "Time text, " +
                "Pic text, " +
                "PuzzRes integer," +
                "Moves integer);";
        sqLiteDatabase.execSQL(query);

        //Table to store images files path for the image random selector.
        query = "CREATE TABLE ImageFiles (" +
                "_ID integer primary key autoincrement," +
                "Path text," +
                "Seen integer);";
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
        Log.d(TAG, "insert_HS_Row");
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
        Log.d(TAG, "return_HS_List");
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

    //Verifies if the image file table exist and if it does not it calls the method that creates the table.
    public int checkTableFilesExist(){
        int res = -2;
        try{
            Cursor cursor =  getReadableDatabase().rawQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'ImageFiles';", null);
            res = cursor.getCount();
            if(res < 1){
                res = (int) createFilesTable(getWritableDatabase());
            }
        }catch (SQLiteException e){
            Log.e(TAG, "checkTableFilesExists err: " + e.getMessage());
        }
        return res;
    }
    //Creates the ImageFile table.
    public long createFilesTable(SQLiteDatabase db){
        String query = "CREATE TABLE ImageFiles (" +
                "_ID integer primary key autoincrement," +
                "Path text," +
                "Seen integer);";
        try {
            db.execSQL(query);
            return 1;
        }catch (SQLException e){
            Log.e(TAG, "createFilesTable err: " + e.getMessage());
        }
        return 0;
    }
    //Inserts a file path into the ImageFile table.
    public long insertFile(String path){
        Log.d(TAG, "insertFile");
        ContentValues contentValues = new ContentValues();
        contentValues.put("Path", path);
        contentValues.put("Seen", -1);
        long res = -2;
        try {
            res = getWritableDatabase().insert("ImageFiles", null,contentValues);
        }catch (SQLiteException e){
            Log.d(TAG, "insertFiles error: " + e.getMessage());
        }
        return res;
    }
    //Returns a string list of image files paths. Seen attribute value equal to -1.
    public List<String> getNotUsedFilesPath(){
        Log.d(TAG, "getNotUsedFiles");
        SQLiteDatabase db = getReadableDatabase();
        String[] arg = {"-1"};
        List<String> paths = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM ImageFiles WHERE Seen = ?", arg);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            paths.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return paths;
    }
    //Returns all the image files path stored in database.
    public List<String> getAllFilesPath(){
        Log.d(TAG, "getAllFiles");
        SQLiteDatabase db = getReadableDatabase();
        List<String> paths = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT Path FROM ImageFiles", null, null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            paths.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return paths;
    }
    //Returns the used image files path. Seen status equal to 0.
    public List<String> getUsedFilesPath(){
        Log.d(TAG, "getUsedFiles");
        String[] arg = {"0"};
        List<String> paths = new ArrayList<String>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT Path FROM ImageFiles WHERE Seen = ?", null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            paths.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return paths;
    }
    //Returns a cursor with the rows of the non used image files paths.
    public Cursor getNotUsedFiles(){
        Log.d(TAG, "getNotUsedFiles");
        SQLiteDatabase db = getReadableDatabase();
        String[] arg = {"-1"};
        Cursor cursor = db.rawQuery("SELECT * FROM ImageFiles WHERE Seen = ?", arg);
        return cursor;
    }
    //Update the status of an image file path by its path. Sets Seen attribute to 0.
    public long updateFile(String path){
        Log.d(TAG, "updateFile");
        String[] args = {path};
        ContentValues contentValues = new ContentValues();
        contentValues.put("Seen", 0);
        long res = -2;
        try {
            res = getWritableDatabase().update("ImageFiles",contentValues,"Path=?",args);
        }catch (Exception e){
            Log.d(TAG, "updateFiles error: " + e.getMessage());
        }
        return  res;
    }
    //Deletes all the rows of the table. Allows a fresh restart.
    public long deleteAllFiles(){
        Log.d(TAG, "deleteAllFiles");
        long res = -2;
        try {
            res = getWritableDatabase().delete("ImageFiles", null, null);
        }catch (Exception e){
            Log.d(TAG,"resetFiles err: " + e.getMessage());
        }
        return  res;
    }
    //Restores the status of all the images files path stored to not seen.
    public long resetFiles(){
        Log.d(TAG, "resetFiles");
        long res = -2;
        ContentValues contentValues  = new ContentValues();
        contentValues.put("Seen", -1);
        try {
            res = getWritableDatabase().update("ImageFiles", contentValues, null,null);
        }catch (Exception e){
            Log.e(TAG, "resetFiles err: " + e.getMessage());
        }
        return res;
    }
    //Returns the number of rows-
    public long countFilesInDb(){
        SQLiteDatabase db = getWritableDatabase();
        long size = 0;
        checkTableFilesExist();
        size = DatabaseUtils.queryNumEntries(db, "ImageFiles");
        return  size;
    }
    //Returns the number of rows that match a certain status.
    public long countFilesInDb(int status){ //Status can be -1 not seen or 0 seen.
        String[] arg = {String.valueOf(status)};
        SQLiteDatabase db = getWritableDatabase();
        long size = 0;
        checkTableFilesExist();
        size = DatabaseUtils.queryNumEntries(db, "ImageFiles","Seen=?", arg);
        return  size;
    }
    //Returns the path attribute by row ID.
    public String getFilePath(int index){
        SQLiteDatabase db = getReadableDatabase();
        String[] arg = {String.valueOf(index)};
        Cursor cursor = db.rawQuery("SELECT Path FROM ImageFiles WHERE _ID = ?", arg);
        cursor.moveToFirst();
        return cursor.getString(0);
    }
    //Update an image file path to seen by it ID number.
    public void setFileAsUsed(int index){
        SQLiteDatabase db = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Seen", 0);
        String[] arg = {String.valueOf(index)};
        try{
            db.update("ImageFiles", contentValues, "_ID=?", arg);
        }catch (Exception e){
            Log.e(TAG, "SetFileAsUsed; " + e.getMessage());
        }

    }
}



