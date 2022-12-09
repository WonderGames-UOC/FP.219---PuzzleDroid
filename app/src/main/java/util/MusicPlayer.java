package util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.puzzledroid.BuildConfig;
import com.example.puzzledroid.Game01Activity;
import com.example.puzzledroid.R;

import util.Audio;

public class MusicPlayer extends Service {
    MediaPlayer mediaPlayer;
    Audio audio = new Audio();
    boolean isReady = false;
    //String filePath = "android.resource://com.example.puzzledroid/raw/anemoia_demise.mp3";


    @Override
    public void onCreate() {
        //mediaPlayer = new MediaPlayer();


        mediaPlayer.create(Game01Activity.context,R.raw.anemoia_demise);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isReady = true;
                Log.d("MediaPlayer", "MediaPlayer READY");
            }
        });

        //mediaPlayer.prepareAsync();
        mediaPlayer.setLooping(true);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //  No sé qué hacer todavía aquí habiendo loop
            }
        });
    }

    /** Controles
     *
     * @return
     */
    public boolean audioPlay(){
        boolean ret = false;

        if (isReady) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();

            } else {
                mediaPlayer.start();
                ret = true;
            }
        }
       return ret;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}