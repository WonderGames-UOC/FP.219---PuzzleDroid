package gameMechanics;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.HighScore;


public class CalendarData extends AppCompatActivity {
    String TAG = "CalendarData";
    ArrayList<String> events = new ArrayList<>();


    String TimeZone = Calendar.getInstance().getTimeZone().getID();

    //funcion a llamar para llamar al thread y guardar datos en el calendario
    public void saveScoreInCalendar (Context context, HighScore highScore) {
        SaveScoreInCalendarThread p = new SaveScoreInCalendarThread(context, highScore );
        p.start();
    }

    //thread para guardar la score como evento en el calendario
    class SaveScoreInCalendarThread extends Thread {
        String moves;
        String userName;
        Context context;
        String difficulty;
        String descriptionText;
        String date;
        String time;
        //asignacion de valores para el thread
        SaveScoreInCalendarThread(Context context, HighScore highScore) {
            this.moves = highScore.getMoves();
            this.userName = highScore.getUser();
            this.time = highScore.getTime();
            this.date = highScore.getDate();
            this.difficulty = String.valueOf(highScore.getPuzzres());
            this.descriptionText = time + " | " + date + " | " + userName + " | " + difficulty;
            this.context = context;
        }

        public void run() {

            //guardamos la puntuación como evento en el calendario
            long now = Calendar.getInstance().getTimeInMillis();

            ContentValues cv = new ContentValues();
            ContentResolver cr = context.getContentResolver();
            long calendarId = getCalendarId(context);
            cv.put(CalendarContract.Events.TITLE, "New score");
            cv.put(CalendarContract.Events.DESCRIPTION, descriptionText);
            cv.put(CalendarContract.Events.DTSTART, now);
            cv.put(CalendarContract.Events.DTEND, now + 60);
            cv.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone);
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);
            Log.d(TAG, "created");
            long eventID = Long.parseLong(uri.getLastPathSegment());
            String IDStr = eventID+"";
            Log.d(TAG, IDStr);
        }
    }

    public ArrayList<String> getRecentScores(Context context) {
        GetRecentScoresThread p = new GetRecentScoresThread(context);
        p.start();
        try {
            p.join();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        return events;

    }

    class GetRecentScoresThread extends Thread {
        Context context;

        //asignacion de valores para el thread
        GetRecentScoresThread(Context context) {
            this.context = context;
        }

        public void run() {
            Cursor cursor = null;
            ContentResolver cr = context.getContentResolver();
            long calendarId = getCalendarId(context);
            String[] EVENT_PROJECTION = new String [] {
                    CalendarContract.Events.DESCRIPTION,
            };

            //query de los eventos a devolver
            Uri uri = CalendarContract.Events.CONTENT_URI;
            String selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?) AND " +
                    "(" + CalendarContract.Events.TITLE +" = ?))";
            String[] selectionArgs = new String[] {calendarId+"", "New score"};
            cursor = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, "DTSTART DESC");
            try {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String description = cursor.getString(0);
                        //añadir evento a la lista a devolver
                        events.add(description);
                    }
                }
            } catch (AssertionError ex) {  }

        }

    }


    @SuppressLint("Range")
    public static long getCalendarId(Context context) {
        long calId = 0;

        Uri calendars;/*from w  w  w  .jav  a2 s  . c  o  m*/
            calendars = CalendarContract.Calendars.CONTENT_URI;


        Cursor managedCursor = context.getContentResolver()
                .query(calendars, new String[] { "_id", "name" }, null,
                        null, null);
        if (managedCursor.moveToFirst()) {
            calId = managedCursor.getLong(managedCursor.getColumnIndex("_id"));
        }
        managedCursor.close();

        return calId;
    }
 }
