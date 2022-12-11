package Settings;

import android.content.Context;

import java.util.Observable;

public final class Player extends Observable {
    public static boolean pause = false;
    public static boolean play = true;
    public static Context context;

    public void setPause(boolean pause){
        synchronized (this){
            this.pause = pause;
            setChanged();
            notifyObservers();
        }
    }
    public void setPlay(boolean play){
        synchronized (this){
            this.play = play;
            setChanged();
            notifyObservers();
        }
    }
    public synchronized boolean getPause(){
        return pause;
    }
    public synchronized boolean getPlay(){
        return play;
    }
    public static Context getContext() {
        return context;
    }
    public static void setContext(Context context) {
        Player.context = context;
    }
}
