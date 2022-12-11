package util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import gameMechanics.Songs;


public class Music extends Service {
    Handler HN = new Handler();
    /*
     There ara two class of services, the ones called by startService() and the ones bind to a component by bindService().
     startServices => runs until the task is completed or the destroy method is called.
     bindServices = > runs until the unBind() method is called and provides an interface to communicate with the binded component.
     */

    /*
        SERVICES need to be declared as components in the Android Manifest.
        <service
            android:name=".MyService" => name of the class that defines de service.
            android:enabled="true" => defines if it's possible to instantiate the service.
            android:exported="true" => defines if other apps components can start and interact with this service-
            >
        </service>
     */

    /* NOTES:
        stopSelf() => called inside the service, stops the service.
        By default a service execute itself in the main thread.
        Second threads launch on a service should be always kill inside the method onDestroy().
        Executing a service a second time create another instance of the service.
     */
    /* The subclass IntentService
        If there is only one thread inside the service, then the use of the subclass IntentService is recommended as it will manage the thread creation by itself.
     */
    private final String TAG = Music.class.getSimpleName();
    private SoundPool mPool;
    private Songs songs; //TODO: The songs must be placed in the SONGS class

    MusicServiceBroadCast msb = new MusicServiceBroadCast();

    public Music() {
        Log.d(TAG, "Constructor");
        songs = new Songs();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { //Call whenever the service is bind to a component with the method bindService().
        Log.d(TAG, "OnBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { //Call whenever the service receives a call by the method startService().
        Log.d(TAG, "onStartCommand");
        registerReceiver(msb, new IntentFilter("PAUSE"));
        registerReceiver(msb, new IntentFilter("PLAY"));
        play();
        return super.onStartCommand(intent, flags, startId);
        //return START_NOT_STICKY; //Indicates that the service should not be restarted after destruction. Not even if there is a task pending.
        //return START_STICKY; //Creates a new services after the destruction of this providing a null intent.
        //return  START_REDELIVER_INTENT; Same as START_STICKY but returns the last intent of the destroyed service.

    }

    public void play() {
        try {
            songs.playMusic();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void pause() {
        try {
            songs.pauseMusic();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onCreate() { //It's call whenever the service is created in memory.
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() { //It's important to kill all threads the service is managing in this method.
        Log.d(TAG, "onDestroy");
        unregisterReceiver(msb);
        super.onDestroy();
    }

    public class MusicServiceBroadCast extends BroadcastReceiver { //Receive instructions from Game01Activity
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, intent.getAction());
            if(intent.getAction().equals("PAUSE")){
                Log.d(TAG, "ACTION PAUSE");
                pause();
            }else {
                Log.d(TAG, "ACTION PLAY MUSIC");
                play();
            }
        }
    }
}
