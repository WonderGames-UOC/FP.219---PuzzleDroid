package util;



// Track1: https://freemusicarchive.org/music/anemoia/demise/demise/

import android.media.MediaPlayer;
import android.view.MenuItem;

import com.example.puzzledroid.R;

public class Audio {

    public final int TRACK1 = R.raw.anemoia_demise;
    public final String musicON = "Music ON";
    public final String musicOFF = "Music OFF";




    public void stopMusic(MediaPlayer mediaPlayer){
        mediaPlayer.release();
        mediaPlayer = null;
    }


}
