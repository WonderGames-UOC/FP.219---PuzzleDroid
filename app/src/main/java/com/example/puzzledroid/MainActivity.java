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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dbHelper.SQLiteHelper;
import util.RandomImageSelector;


import Settings.FIREBASE_PATHS;
import activities.OnlineScores;
import apirest.RestRetrofit;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements custom_dialog_menu.returnDialogMenu, custom_dialog_menu_online.returnDialogMenuOnline {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Context context = this;
    Button play, hs, rs, online, writedb, querydb, topScores;
    private static String TAG = MainActivity.class.getSimpleName();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseFirestore dbfs = FirebaseFirestore.getInstance();

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
        writedb = findViewById(R.id.write_db);
        querydb = findViewById(R.id.query_db);
        topScores = findViewById(R.id.onlineHighScoresButton);

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

        //Exist session
        if(session()){
            SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
            String email = prefs.getString("email", null);
            String id = prefs.getString("id", null);
            createUpdateUser(id,email);
            setup();
        }
    }
    //Save google credentials as preferences
    private void saveUserData(String email, String id){
        SharedPreferences.Editor prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit();
        prefs.putString("email", email);
        prefs.putString("id", id);
        prefs.apply();
    }
    //Check if there is valid credentials in the app.
    private boolean session(){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);
        String id = prefs.getString("id", null);
        Log.d(TAG, "Stored email: " + email);
        if(email != null && id != null){
            //writedb.setVisibility(View.VISIBLE);
            online.setVisibility(View.VISIBLE);
            return true;
        }
            //writedb.setVisibility(View.INVISIBLE);
            online.setVisibility(View.GONE);
        return false;
    }
    private boolean isNewUser(String id){
        final boolean[] userExist = {false};
        Log.d(TAG, "isNewUser: " + id);
        DatabaseReference ref = db.getReference();
        ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                userExist[0] = snapshot.exists();
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                userExist[0] = false;
                Log.d(TAG, "isNewUser.onCancell: " + error.getMessage());
            }
        });
        return userExist[0];
    }
    //Registers a new user or updates an existing one in the database.
    private void createUpdateUser(String id, String email){
        Log.d(TAG, "newUser");
        Boolean userExist = isNewUser(id);
        DatabaseReference ref = db.getReference();
        if(!userExist){
            ref.child(FIREBASE_PATHS.USERS).child(id).child(FIREBASE_PATHS.EMAIL).setValue(email);
        }
        ref.child(FIREBASE_PATHS.USERS).child(id).child(FIREBASE_PATHS.LASTUPDATE).setValue(FIREBASE_PATHS.getCurrentDateTime());
    }
    private void logInFirebase(){
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

    //Setups all the online methods and buttons.
    private void setup(){
        SharedPreferences sp = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email = sp.getString("email", null);
        String id = sp.getString("id", null);
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d(TAG, "PLay Online.onClick");
                new custom_dialog_menu_online(context, MainActivity.this, id, email);
            }
        });

        topScores.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.d(TAG, "TopScores onClick");
                 onlineScores();
             }
         });
        writedb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = sp.getString("email", null);
                String id = sp.getString("id",null);
                Log.d(TAG, "writeDB button");

                DatabaseReference ref = db.getReference();
                String ts = String.valueOf(System.currentTimeMillis());
                ref.child("Users").child(id).child("Email").setValue(email);
                ref.child("Users").child(id).child("LastUpdate").setValue(ts);
                ref.child("Users")
                        .child(id)
                        .child("Scores").child(String.valueOf(System.currentTimeMillis())).setValue(System.currentTimeMillis());
                Map<String, String> img = new HashMap<String, String>();
                img.put("Image1", "URL1");
                img.put("Image2", "URL2");
                img.put("Image3", "URL3");

                ref.child("Users").child(id).child("ImagesSeen").setValue(img);

                entities.HighScores hs  = new entities.HighScores();
                hs.Top1 = 9999;
                hs.Top2 = 8888;
                hs.Top3 = 3333;

                try {
                    RestRetrofit retrofit = new RestRetrofit();
                    Call<entities.HighScores> postTopScores = retrofit.postTopScores.createPost(hs);
                    postTopScores.enqueue(new Callback<entities.HighScores>() {
                        @Override
                        public void onResponse(Call<entities.HighScores> call, Response<entities.HighScores> response) {
                            Log.d(TAG,
                                    "\nMessage: " + response.message()
                                    //+"\nHeaders: " + response.headers()
                                    +"\nResponse: " + response.body()
                                    );
                        }

                        @Override
                        public void onFailure(Call<entities.HighScores> call, Throwable t) {
                            Log.d(TAG, t.getMessage());
                        }
                    });
                }catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }

            }//onClick(View close)
        });
        querydb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "querydb button");

                //User data
                SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
                // Get a reference to the Realtime Database
                FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();// inicia o objeto mFirebaseAuth
                String id = prefs.getString("id", null);
                if(id != null){
                    String path = "Users/"+ id + "/ImagesSeen";
                    //db.getReference().child(path);
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    Log.d(TAG, "mFirebaseAuth: " + mFirebaseAuth.toString());

                    // Define the function that will read data from the database and return the result as a Single
                    Observable<DataSnapshot> observable = Observable.create(new ObservableOnSubscribe<DataSnapshot>() {
                        @Override
                        public void subscribe(@NonNull ObservableEmitter<DataSnapshot> emitter) throws Throwable {
                            DatabaseReference ref = db.getReference(path);

                            //Add a ValueEventListener
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Log.d(TAG, "onDataChange");
                                    //Emmit the result
                                    emitter.onNext(snapshot);
                                }

                                @Override
                                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                                    Log.e(TAG, "onCancelled: " + error.getMessage());
                                    emitter.onError(error.toException());
                                }
                            });
                        }
                    });
                    observable.safeSubscribe(new Observer<DataSnapshot>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            Log.d(TAG, "subscriber.onSubscribre: " + d.toString());
                        }

                        @Override
                        public void onNext(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "Subscriber onMext: " + dataSnapshot.toString());
                            try{
                                String jsonString = dataSnapshot.getValue().toString();
                                Log.d(TAG, "jsonString: " + jsonString);
                                Gson gson = new Gson();
                                Object imagesSeen = gson.fromJson(jsonString, Object.class);
                                Map<String, String> imageMap = (Map<String, String>) imagesSeen;
                                Log.d(TAG, ((Map<?, ?>) imagesSeen).get("Image2").toString());

                                //for(Image image:imagesSeen.getImages()){Log.d(TAG, image.getName() + " " + image.getUrl());

                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                            }
                        }
                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.e(TAG, "subscriber.onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                }
            }//onClick(View close)
        });
/*
        db.getReference().child("Users").child(sp.getString("id",null)).addListenerForSingleValueEvent(new ValueEventListener(){
           @Override
           public void onDataChange(DataSnapshot dataSnapshot){
               if(dataSnapshot.exists()){
                   Log.d(TAG, dataSnapshot.child("Scores").getValue().toString());
               }else{
                   Log.d(TAG, "User not found");
               }
           }
           @Override public void onCancelled(DatabaseError databaseError){
               Log.e(TAG, databaseError.getMessage());
           }
        });
*/

    }

    //RequestCode 100 is for Google login response.
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
        session();
    }



    // Método para lanzar la pantalla de juego.
    private void startGame(String userName, int puzzres, int imgId) {
        Intent i = new Intent(this, Game01Activity.class);
        i.putExtra("userName", userName.toString());
        i.putExtra("puzzres", puzzres);
        i.putExtra("imgId", imgId);
        startActivity(i);
    }
    private void startGame(String email, int puzzres, int imgId, String id) {
        Intent i = new Intent(this, Game01Activity.class);
        i.putExtra("email", email.toString());
        i.putExtra("puzzres", puzzres);
        i.putExtra("imgId", imgId);
        i.putExtra("Id", id);
        startActivity(i);
    }
    // Método para lanzar la pantalla de puntuaciones.
    private void hScores(){
        Intent j = new Intent(this, HighScores.class);
        startActivity(j);
    }
    private void onlineScores() {
        Intent intent = new Intent(this, OnlineScores.class);
        startActivity(intent);
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
    @Override
    public void ResultOnline(String email, int puzzres, int imgId, String Id){
        startGame(email, puzzres, imgId, Id);
    }
}