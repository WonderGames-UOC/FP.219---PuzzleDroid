package com.example.puzzledroid;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import dbHelper.SQLiteHelper;
import gameMechanics.Counter;
import gameMechanics.ImageDivider;
import gameMechanics.PuzzlePiece;
import gameMechanics.PuzzlePieces;
import gameMechanics.Selector;
import gameMechanics.Sounds;
import gameMechanics.Timer;
import util.HighScore;


public class Game01Activity extends AppCompatActivity implements OnClickListener {
    SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "BD1_HighScores", null, 1);

    //Game data
    private Chronometer chronometer;
    private final gameMechanics.Timer Timer = new Timer();
    int imgId, numBlocks;

    //¡¡NO BORRAR!! Etiqueta para el depurador.
    private final String TAG = "Game01Activity";

    //Game mechanics variables.
    private PuzzlePieces puzzleBlocks;
    private int rows, columns;
    private Selector selector;
    private Counter counter;
    private String userName;
    //TODO move this settings to DB or XML.
    private final int COLORGOOD = Color.WHITE;
    private final int COLORBAD = Color.GRAY;

    //Sound variables.
    private SoundPool soundPool;
    private Sounds sounds;

    //Print parameters
    DisplayMetrics dp;

    //CAMERA & GALLERY
    private  final int TAKE_PICTURE = 1;
    private  final int MEDIA_PICTURE = 0;

    private String currentPhotoPath;
    private Uri photoURI;
    private Bitmap bitmap;

    //This listener sets an observable that will launch the puzzle when the bitmap is loaded.
    MutableLiveData<Bitmap> listen = new MutableLiveData<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //¡¡NO BORRAR!! Registro para el depurador.
        Log.d(TAG, "onCreate");

        //https://stackoverflow.com/questions/2730855/prevent-screen-rotation-on-android
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Cancel screen rotation.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game01);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);


        //Game sounds (Sounds config directly on the class).
        this.sounds = new Sounds(this);
        this.soundPool =  sounds.getSoundPool();

        listen.observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap b) {
                selector = new Selector();
                counter = new Counter();
                startPuzzle(numBlocks, b);
                FullScreencall();
            }
        });

        //Gets the info selected by the user and stores it for the game start
        Bundle data = getIntent().getExtras();
        try{
            this.imgId = (int) data.getInt("imgId");
            this.numBlocks = (int)data.getInt("puzzres");
            userName = data.getString("userName");
            Log.i(TAG, "User selection: " + String.valueOf(imgId) + " / " + String.valueOf(numBlocks) + " / " + userName );
            switch (numBlocks){
                case 24:
                case 32:
                case 128:
                    Log.d(TAG, "Camera");
                    //Camera
                    cameraIntent();
                    break;
                default:
                    //Set initial background image
                    findViewById(R.id.puzzle_view).setBackground(getDrawable(imgId));

                    //Init variables
                    this.selector = new Selector();
                    this.counter = new Counter();

                    //Starts the puzzle
                    startPuzzle(numBlocks, getDrawable(imgId));
                    break;
            }
        }catch (Exception e) {
            Log.e(TAG, e.getMessage());

            onErrorLaunchErrPuzzle();
        }
    }
    private void onErrorLaunchErrPuzzle(){
        imgId = R.drawable.level1;
        numBlocks = 4;
        //Set initial background image
        findViewById(R.id.puzzle_view).setBackground(getDrawable(imgId));
        //Init variables
        this.selector = new Selector();
        this.counter = new Counter();
        //Starts the puzzle
        startPuzzle(numBlocks, getDrawable(imgId));
    }
    private void startPuzzle(int divisions, Drawable image){
        Log.d(TAG, "startPuzzle");
        this.puzzleBlocks = genPuzzle(divisions, transformToBitmap(image));
        imagePrinter(puzzleBlocks);
        try {
            //creacion de cronometro
            chronometer = findViewById(R.id.txtabTimer);
            //Starts the counter and crono.
            ((TextView) findViewById(R.id.txtabMoves)).setText("Movements: " + Integer.toString(counter.getMovements()));
            Timer.resetTimer(chronometer);
            this.counter.reset();
            Timer.startChronometer(chronometer);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
    private void startPuzzle(int divisions, Bitmap image){
        Log.d(TAG, "startPuzzle");
        this.puzzleBlocks = genPuzzle(divisions, image);
        imagePrinter(puzzleBlocks);
        try {
            //creacion de cronometro
            chronometer = findViewById(R.id.txtabTimer);
            //Starts the counter and crono.
            ((TextView) findViewById(R.id.txtabMoves)).setText("Movements: " + Integer.toString(counter.getMovements()));
            Timer.resetTimer(chronometer);
            this.counter.reset();
            Timer.startChronometer(chronometer);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * onClick event for puzzle blocks.
     * @param v = imageView
     */
    public void onClick(View v){
        Log.d(TAG, "onClick: ");
        Log.d(TAG, (v.getId()) +" "+ v.getTag());

        int pos = Integer.parseInt(v.getTag().toString());  //Get the position value stored in the touched imageView
        selector(pos);                                      //Call selector function.
    }

    /**
     * Puzzle blocks selector fn.
     * @param pos
     */
    private void selector(int pos){

        switch (selector.memoryLikeSelector(pos)){
            case -1: //If the same imageView is touched twice, selection is canceled.
                this.soundPool.play(this.sounds.getCancelSound(), 1,1, 1,0,(float) 1.5);
                break;
            case 0://If the touched imageView is first one.
                this.soundPool.play(this.sounds.getSelectSoundA(), 1, 1, 0, 0, 2 );
                break;
            case 1://If the touched imageView is the second.
                this.puzzleBlocks.swapPiecesById(this.selector.getSelBlockA(), this.selector.getSelBlockB());
                this.counter.add();
                try {
                    ((TextView) findViewById(R.id.txtabMoves)).setText("Movements: " + Integer.toString(counter.getMovements()));
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
                this.soundPool.play(this.sounds.getSwapSound(), 1, 1, 1, 0, (float) 1.5 );

                //Print the new order of blocks and reset the selector variable. Use light print for better performance.
                //imagePrinter(puzzleBlocks);
                lightPrint(this.selector.getSelBlockA(), this.selector.getSelBlockB());

                //Victoty condition check-
                if(this.puzzleBlocks.checkResult() > 0){
                    this.soundPool.play(this.sounds.getVictorySound(),1, 1, 3, 0, (float) 1.5 );
                    Timer.pauseChronometer(chronometer);
                    Log.d(TAG, Timer.offsetString);

                    //INSERT TIME AND COUNTER IN DB
                    String level = "";
                    switch (this.numBlocks){
                        case 24: level = "Easy";break;
                        case 32: level = "Medium";break;
                        case 128: level = "Hard";break;
                        default:
                            level = "Nightmare";break;
                    }
                    HighScore highScore = new HighScore(userName,
                            getDate(),
                            miliReturn(Integer.parseInt(String.valueOf(Timer.offsetString))),
                            String.valueOf(this.imgId),
                            String.valueOf(level),
                            String.valueOf(counter.getMovements()));
                    sqLiteHelper.insert_HS_Row(highScore);
                }
                this.selector.resetSelection();
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings_game:{
                exitGame();
                break;
            }
            case R.id.reset_game:{
                resetGame();
                break;
            }
            default:{
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Método para lanzar la pantalla de ayuda.
    private void exitGame() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Restarts the current puzzle game
     */
    private void resetGame(){
        this.puzzleBlocks.shuffle();
        Timer.resetTimer(this.chronometer);
        counter.reset();
        imagePrinter(this.puzzleBlocks);
        Timer.startChronometer(this.chronometer);
    }



    //TODO: create class to work with any kind of image or drawable
    /**
     * Transform a drawable ot a bitmap image. Needs improvement for product 2.
     * @param img
     * @return
     */
    private Bitmap transformToBitmap(Drawable img){
        try{
            BitmapDrawable bmDrawable = (BitmapDrawable) img;
            Bitmap bm = bmDrawable.getBitmap();
            return  bm;
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        return null;
    }

    /**
     * Returns an object puzzleBlocks made of the pieces of the image passed as parameter.
     * @param numOfPieces
     * @param image
     * @return a PuzzleOPieces object as a collection of puzzle pieces.
     */
    private PuzzlePieces genPuzzle(int numOfPieces, Bitmap image){
        Log.d(TAG, "genPuzzle");
        PuzzlePieces puzzleBlocks = new PuzzlePieces();

        //Get the display size
        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);

        //Get the high of the action bar bar high
        int barHigh = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)) {
            barHigh = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        //The action bar high is needed to ajust the screen high to one available, so is subtracted.

        Log.d(TAG, "ActionBar size: " + String.valueOf(barHigh) + "\nDp.H: " + dp.heightPixels + "\nDp.W: " + dp.widthPixels);

        //Divide the image
        ImageDivider images = new ImageDivider(numOfPieces, image);
        images.setBarHight(barHigh);
        images.setsHight(dp.heightPixels);
        images.setsWidth(dp.widthPixels);
        //images.divideImage();
        images.divideImageInSquares();
        //images.divideImageInSquares(dp);
        //images.divideImageInSquares((int)(dp.heightPixels*0.96), dp.widthPixels);
        this.rows = images.getRows();
        this.columns = images.getColumns();

        //Build the blocks
        puzzleBlocks.genPiecesCollection(images.getImages());

        //Shuffle the blocks
        puzzleBlocks.shuffle();
        return puzzleBlocks;
    }

    //TODO: move these functions to a class. All this need a huge refactor.
    private void imagePrinter(PuzzlePieces blocks){
        Log.d(TAG, "imagePrinter");

        //Definimos los atributos de los Linealayouts que conformarán la estructura.
        //TODO: Definir estos parámetros fura del método.
        LinearLayout.LayoutParams mainLpParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.VERTICAL);
        LinearLayout.LayoutParams childLpParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.HORIZONTAL);
        childLpParams.gravity = Gravity.CENTER;

        //Instantiate the parent layout.
        LinearLayout mainLp = (LinearLayout) findViewById(R.id.puzzle_view);
        mainLp.setLayoutParams(mainLpParams);
        mainLp.removeAllViewsInLayout(); //Remove all from mainLayout (allows reset)

        //Define the wide & high of the imageview of each block based on the current wide of screen.
        //https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android
        dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        float ratio = dp.heightPixels / dp.widthPixels;
        int imageViewWide = (int) ((dp.widthPixels / columns) *1 ); //TODO Base on the screen width (needs adjustment for screen rotation)
        if(ratio < 2){ //Adaptation to shorten screens
            imageViewWide = (int)(imageViewWide) * 1;

        }
        int imageViewHigh = imageViewWide; //Square blocks
        Log.d(TAG, "Wide: " + imageViewWide + " High: " + imageViewHigh);
        LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(
                imageViewWide,
                imageViewHigh
        );

        //Set left and right margins of the rows layouts to center the images in the screen.
        int sideMarginLayout = (dp.widthPixels - (imageViewWide * columns)) / 2;
        childLpParams.setMargins(sideMarginLayout,0,sideMarginLayout,0);


        //Padding based on display density.
        //https://stackoverflow.com/questions/9685658/add-padding-on-view-programmatically
        float scale = getResources().getDisplayMetrics().density;
        int size = (int) (1*scale + 0.5f);

        //Start of the painting loop.
        LinearLayout childLp = new LinearLayout(this);
        ImageView imageView;
        int col = 0;
        for (PuzzlePiece block :blocks.getPieces()
        ) {
            if(col == columns){ //New linear layout when last column reach.
                childLp.setBackgroundColor(Color.WHITE);
                mainLp.addView(childLp, childLpParams);
                childLp = new LinearLayout(this);
                col = 0; //As a new row is started, the column counter is set to zero.
            }
            //Insert in the imageView the image of the block and set the padding.
            imageView = new ImageView(this);
            imageView.setId(View.generateViewId()); //Autogenerated unique ID. (API 17). https://stackoverflow.com/questions/8460680/how-can-i-assign-an-id-to-a-view-programmatically
            imageView.setBackgroundColor(COLORBAD);
            imageView.setTag(block.getPosition());
            imageView.setImageBitmap(block.getImage());
            if(col == 0){
                imageView.setPadding(size,0,size,size); //Padding on the right, left and bottom for each block.
            }else{
                imageView.setPadding(0,0,size,size); //Padding on the right and bottom of each block.
            }
            imageView.setOnClickListener(this);
            if(blocks.checkPiece(block.getPosition())){
                //imageView.setColorFilter( new LightingColorFilter(3,2));
                imageView.setBackgroundColor(COLORGOOD);
            }


            //Insert the imageView in the layout of the row.
            childLp.addView(imageView,imgViewParams);
            col++;
        }
        childLp.setBackgroundColor(Color.WHITE); //Better contrast for the separation lines.
        mainLp.addView(childLp, childLpParams);
        mainLp.setBackgroundResource(0);    //Background image off.
        mainLp.setBackgroundColor(Color.GRAY); //Black background smooths image downsize on sides.
    }
    private void lightPrint(int posA, int posB){
        int viewA, viewB; //stores the views ID

        //Instantiate the swapped puzzle pieces.
        PuzzlePiece pieceA = this.puzzleBlocks.getPieceA();
        PuzzlePiece pieceB = this.puzzleBlocks.getPieceB();

        //Obtain the view by its tag
        viewA = getImageViewByTag(String.valueOf(pieceA.getPosition()));
        viewB = getImageViewByTag(String.valueOf(pieceB.getPosition()));

        //Substitute pieceA values with those of pieceB
        ImageView vA = findViewById(viewA);
        vA.setImageBitmap(pieceB.getImage());
        vA.setTag(pieceB.getPosition());
        vA.setBackgroundColor(COLORBAD);

        //Substitute pieceB values with those of pieceA
        ImageView vB = findViewById(viewB);
        vB.setImageBitmap(pieceA.getImage());
        vB.setTag(pieceA.getPosition());
        vB.setBackgroundColor(COLORBAD);

        //SetBackground color if piece is in the correct position.
        if(puzzleBlocks.checkPiece(posB)){
            vA.setBackgroundColor(COLORGOOD);
        }
        if(puzzleBlocks.checkPiece(posA)){
            vB.setBackgroundColor(COLORGOOD);
        }
    }
    private int getImageViewByTag(String tag){
        int id = -1;
        LinearLayout mainLp = (LinearLayout) findViewById(R.id.puzzle_view);
        for(int i = 0; i < mainLp.getChildCount(); i++){
            LinearLayout rowLayout = (LinearLayout) mainLp.getChildAt(i);
            for(int j = 0; j < rowLayout.getChildCount();j++){
                View v = rowLayout.getChildAt(j);
                String vTag = v.getTag().toString();
                if(vTag.equals(tag)) {
                    id = v.getId();
                    return id;
                }
            }
        }
        return id; //TODO Error control from this return.
    }

    public String getDate(){
        String ret;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        ret = String.valueOf(dateFormat.format(calendar.getTime()));
        return ret;

    }

    public String miliReturn(int millis){
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

    /****************************************************
     *
     * CAMERA & GALLERY RANDOM
     *
     ****************************************************/
    /*
     * This function creates a unique file name and sets a file path for the camera image.
     * FILE ACCESS PERMISSIONS are defined in the AndroidManifest.xml
     * FILE path DIRECTORY_PICTURES is defined in @XML/file_paths.xml
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "JPEG" + timeStamp + "_";
        //File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //Directory of the app.
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return  image;
    }
    /*
     *This fn prepares and stats the camera intent.
     */
    private void cameraIntent(){
        //Set the intent for the camera "MediaStore.ACTION_IMAGE_CAPTURE"
        Intent launchCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Create media file to store the image get with the camera.
        if(launchCamera.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (IOException e){
                Log.e(TAG, e.getMessage());
            }
            if(photoFile != null){
                //Get and store the URI of the image file.
                photoURI = FileProvider.getUriForFile(this, "WonderGames.fileprovider",photoFile);
                //I pass the URI of the file to the intent to allow the camera store the picture there.
                launchCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //Callback - start the intent and wait for the result => onActivityResult- TAKE_PICTURE (INT) is the ID.
                startActivityForResult(launchCamera, TAKE_PICTURE);
            }
        }
    }
    /*
     * This method receives the result of the intent launched by startActivityForResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    try {
                        MediaScannerConnection.scanFile(this, new String[]{currentPhotoPath}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String s, Uri uri) {
                                        Log.d(TAG, currentPhotoPath);
                                        bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                                        listen.postValue(BitmapFactory.decodeFile(currentPhotoPath));
                                    }
                                });
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(this, "Error obteniendo la imagen...", Toast.LENGTH_SHORT);
                        toast.show();
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                case MEDIA_PICTURE:

                    break;
                default:
                    break;
            }
        }else{
            Log.e(TAG, "onActivity RESULT NOT OK");
            onErrorLaunchErrPuzzle();
        }
    }
    //Hide navigation bar.
    //https://stackoverflow.com/a/27804505
    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}