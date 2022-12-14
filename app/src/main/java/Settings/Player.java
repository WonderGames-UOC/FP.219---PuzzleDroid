package Settings;

import android.content.Context;

import java.util.Observable;

public final class Player extends Observable {

    public static Context context;
    public static int selectedSong = Params.SONGA;
    public static boolean isPlaying;

    public static Context getContext() {
        return context;
    }
    public static void setContext(Context context) {
        Player.context = context;
    }

    public static void setSelectedSong(int selectedSong) {
        Player.selectedSong = selectedSong;
    }
}
