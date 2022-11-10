package com.example.puzzledroid;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

public class Timer {
    private long pauseOffset;
    public String offsetString;


    public void startChronometer(Chronometer chronometer) {
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
    }

    public void pauseChronometer(Chronometer chronometer) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            offsetString = pauseOffset+"";
    }

    public void resetTimer(Chronometer chronometer){
        chronometer.stop();

        pauseOffset = 0;
        offsetString = pauseOffset+"";
    }
}
