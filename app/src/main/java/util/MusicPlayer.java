package util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.puzzledroid.BuildConfig;

import util.Audio;

public class MusicPlayer extends Service {
    MediaPlayer mediaPlayer;
    Audio audio = new Audio();
    boolean isReady = false;
    String filePath = "android.resource://com.example.puzzledroid/raw/anemoia_demise.mp3";


    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isReady = true;
                Log.d("MediaPlayer", "MediaPlayer READY");
            }
        });

        try{
            mediaPlayer.setDataSource(filePath);
            Log.d("MP", String.valueOf(mediaPlayer.getDuration()));
            mediaPlayer.prepare();

        }catch (Exception e){

        }
        mediaPlayer.setLooping(true);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
            }
        });
    }

    /** Controles
     *
     * @return
     */
    public boolean audioPlay(){
        boolean ret = true;

        if (isReady) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                ret = false;
            } else {
                mediaPlayer.start();
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