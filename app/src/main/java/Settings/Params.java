package Settings;

import android.graphics.Bitmap;

import com.example.puzzledroid.R;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class Params {

    //MODE
    public  static final int GALLERY = 2;
    public static final int CAMERA = 1;
    public static final int DEFAULT = 0;

    //DIFFICULTY
    public static final  int EASY = 4;
    public static final  int MEDIUM = 6;
    public static final  int HARD = 8;
    public static final  int NIGHTMARE = 10;

    //OPTIONS
    public final int IMAGE_FITSCREENWIDE = 0;
    public final int IMAGE_FITSCREENHIGH = 1;

    //MUSIC
    //https://freemusicarchive.org
    public static final int SONGA = R.raw.mind_reader_blues;
    public static final int SONGB = R.raw.baby_please_dont_go;
    public static final int SONGC = R.raw.somebody_knockin;
    private static int songPos = 0;

    // FIREBASE PATHS
    public static String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/puzzledroid-2605b.appspot.com/o/PuzzleDroidImages%2FLevel2.png?alt=media";

    public static int nextSongReturn(){
        List<Integer> ret = Arrays.asList(SONGA, SONGB, SONGC);
        songPos++;
        if (songPos >= ret.size()){
            songPos = 0;
        }
        return ret.get(songPos);
    }

    // Función  que devuelve una imagen aleatoria de las propuestas inicialmente para el juego.
    public static int imageRandomReturn(){
        List<Integer> image = Arrays.asList(R.drawable.level1, R.drawable.level2, R.drawable.level3);
        Random random = new Random();

        return image.get(random.nextInt(image.size()));
    }
}
