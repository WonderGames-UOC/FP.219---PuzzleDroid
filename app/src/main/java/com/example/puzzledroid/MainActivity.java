package com.example.puzzledroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.puzzledroid.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements custom_dialog_menu.returnDialogMenu {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Context context = this;
    Button play, hs;
    String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(tag, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        play = findViewById(R.id.button);
        hs = findViewById(R.id.button2);

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d(tag, "onClick");
                new custom_dialog_menu(context, MainActivity.this);
                //startGame();
            }
        });

        hs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(tag,"High Scores Click");
                hScores();
            }
        });
    }
// Método para lanzar la pantalla de juego.
    private void startGame(String userName, int puzzres) {
        Intent i = new Intent(this, Game01Activity.class);
        i.putExtra("userName", userName.toString());
        i.putExtra("puzzres", puzzres);
        startActivity(i);
    }
    // Método para lanzar la pantalla de puntuaciones.
    private void hScores(){
        Intent j = new Intent(this, HighScores.class);
        startActivity(j);
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
    public void Result(String username, int puzzres) {
        startGame(username, puzzres);
    }
}