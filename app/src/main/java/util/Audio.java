package util;



// Track1: https://freemusicarchive.org/music/anemoia/demise/demise/

import android.media.MediaPlayer;

import com.example.puzzledroid.R;

public class Audio {

    public final int TRACK1 = R.raw.anemoia_demise;


    public void stopMusic(MediaPlayer mediaPlayer){
        mediaPlayer.release();
        mediaPlayer = null;
    }


}
