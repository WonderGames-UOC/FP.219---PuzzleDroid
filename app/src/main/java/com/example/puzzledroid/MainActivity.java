package com.example.puzzledroid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.puzzledroid.databinding.ActivityMainBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dbHelper.SQLiteHelper;
import util.RandomImageSelector;

public class MainActivity extends AppCompatActivity implements custom_dialog_menu.returnDialogMenu {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Context context = this;
    Button play, hs, rs;
    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        play = findViewById(R.id.button);
        hs = findViewById(R.id.button2);
        rs = findViewById(R.id.recentScoresButton);

        Log.d(TAG, "Check files table exist.");
        SQLiteHelper db = new SQLiteHelper(this, "BD1_HighScores", null, 1);
        db.checkTableFilesExist();
        if(db.countFilesInDb(-1) < 1){ //Check there is at least one picture not seen
            if(db.countFilesInDb() < 1){
                Log.d(TAG, "Loading files path in DB.");
                ExecutorService executorService = Executors.newFixedThreadPool(2);
                RandomImageSelector rsi = new RandomImageSelector(getApplicationContext(), executorService);
                rsi.loadFilesInDb();
            }else{
                Log.d(TAG, "Reset all files to be used again.");
                db.resetFiles();
            }
        }

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "onClick");
                new custom_dialog_menu(context, MainActivity.this);
            }
        });

        hs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"High Scores Click");
                hScores();
            }
        });

        rs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Recent Scores Click");
                rScores();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notifChannel", "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
// Método para lanzar la pantalla de juego.
    private void startGame(String userName, int puzzres, int imgId) {
        Intent i = new Intent(this, Game01Activity.class);
        i.putExtra("userName", userName.toString());
        i.putExtra("puzzres", puzzres);
        i.putExtra("imgId", imgId);
        startActivity(i);
    }
    // Método para lanzar la pantalla de puntuaciones.
    private void hScores(){
        Intent j = new Intent(this, HighScores.class);
        startActivity(j);
    }

    private void rScores(){
        Intent r = new Intent(this, CalendarScores.class);
        startActivity(r);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showHelpPage();
        }
        return super.onOptionsItemSelected(item);
    }
    // Método para lanzar la pantalla de ayuda.
    private void showHelpPage() {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
    }

    @Override
    public void Result(String username, int puzzres, int imgId) {
        startGame(username, puzzres, imgId);
    }
}