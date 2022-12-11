package gameMechanics;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import Settings.Player;


public class Songs extends Thread{
    private final String TAG = Songs.class.getSimpleName();
    private Context context;
    private Player player;
    private MediaPlayer mp;

    public Songs() {
        Log.d(TAG, "Constructor");
        this.context = Player.context;
        mp = MediaPlayer.create(context, Player.selectedSong);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setLooping(true);
    }
    public Songs(Context context) {
        Log.d(TAG, "Constructor");
        this.context = context;
        mp = MediaPlayer.create(context, Player.selectedSong);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setLooping(true);
    }

    @Override
    public void run(){
        mp.start();
    }
    public void playMusic(){
        mp.start();
    }
    public void pauseMusic(){
        mp.pause();
    }
    public void stopMusic(){
        mp.stop();
    }
    public void release(){
        stopMusic();
        mp.release();
    }
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
}
