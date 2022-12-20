package com.example.puzzledroid;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements custom_dialog_menu.returnDialogMenu {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Context context = this;
    Button play, hs, rs, online;
    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        play = findViewById(R.id.play_button);
        hs = findViewById(R.id.button2);
        rs = findViewById(R.id.recentScoresButton);
        online = findViewById(R.id.online_button);


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

        //Exist session
        session();
        setup();
    }
    private void saveUserData(String email, String id){
        SharedPreferences.Editor prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
        prefs.putString("email", email);
        prefs.putString("id", id);
        prefs.apply();
    }
    private void session(){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);
        Log.d(TAG, "Stored email: " + email);
    }
    private void setup(){
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d(TAG, "online button");
                //Setup the request
                GoogleSignInOptions googleConf = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestId()
                        .build();
                //Setup the auth client
                GoogleSignInClient googleClient = GoogleSignIn.getClient(context, googleConf);
                startActivityForResult(googleClient.getSignInIntent(), 100 ); //Start login activity
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent); //Signin task.
            try {
                GoogleSignInAccount account = task.getResult(); //Signin task result
                if(account != null){
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null); //Google token
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task1 -> { //Signin Firebase with the google credential.
                        if(task1.isSuccessful()){
                            saveUserData(account.getEmail(), account.getId()); //Get email and id

                            new AlertDialog.Builder(context)
                                    .setTitle("Firebase Login")
                                    .setMessage("ID: " + account.getId() + "\n Email: " + account.getEmail())
                                    .setIcon(R.drawable.puzzledroid_icon)
                                    .setNegativeButton("GREAT", null)
                                    .show();
                        }else{
                            new AlertDialog.Builder(context)
                                    .setTitle("Firebase Login Fail")
                                    .setMessage("ID: null\n Email: null")
                                    .setIcon(R.drawable.puzzledroid_icon)
                                    .setNegativeButton("OK", null)
                                    .show();
                        }
                    });
                }
            }catch (Exception e){
                Log.d(TAG, e.getMessage());
                new AlertDialog.Builder(context)
                        .setTitle("Account error")
                        .setMessage("\n" + e.getMessage())
                        .setIcon(R.drawable.puzzledroid_icon)
                        .setNegativeButton("OK", null)
                        .show();
            }
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