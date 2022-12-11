package gameMechanics;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.Observable;
import java.util.Observer;

import Settings.Params;
import Settings.Player;

public class Songs extends Thread implements Observer {
    private final String TAG = Songs.class.getSimpleName();
    private Context context;
    private Player player;
    private MediaPlayer mp;

    public Songs() {
        Log.d(TAG, "Constructor");
        this.context = Player.getContext();
        mp = MediaPlayer.create(context, Params.SONGA);
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
    @Override
    public void update(Observable observable, Object arg) {
        player = (Player) observable;
        Log.d(TAG, "Observable: " + player.getPlay() + " / " + player.getPause());
        if(player.getPlay()){
            if(player.getPause()){
                mp.pause();
            }else{
                mp.start();
            }
        }
    }
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
}
