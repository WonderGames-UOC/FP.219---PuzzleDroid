package com.example.puzzledroid;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dbHelper.SQLiteHelper;



public class Game01Activity extends AppCompatActivity implements OnClickListener {
    SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "BD1_HighScores", null, 1);

    //Game data
    private Chronometer chronometer;
    private Timer Timer = new Timer();

    //¡¡NO BORRAR!! Etiqueta para el depurador.
    private final String TAG = "Game01Activity";

    //Game mechanics variables.
    private PuzzlePieces puzzleBlocks;
    private int rows, columns;
    private Selector selector = new Selector();
    private Counter counter = new Counter();
    private String userName;

    //Sound variables.
    private SoundPool soundPool;
    private Sounds sounds;

    DisplayMetrics displayMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //¡¡NO BORRAR!! Registro para el depurador.
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game01);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);


        //Game sounds (Sounds config directly on the class).
        this.sounds = new Sounds(this);
        this.soundPool =  sounds.getSoundPool();


        //TODO: Obtener la imagen del nivel seleccionado
        //Receive data from custom_dialog_menu
        Bundle data = getIntent().getExtras();
        int imgId, numBlocks;
        try{
            imgId = (int) data.getInt("imgId");
            numBlocks = (int)data.getInt("puzzres");
            userName = data.getString("userName");
        }catch (Exception e){
            imgId = R.drawable.level1;
            numBlocks = 4;
        }

        //Set initial background image
        findViewById(R.id.puzzle_view).setBackground(getDrawable(imgId));

        //TODO: Eliminar botones y definir la división de la imagen en base al nivel seleccionado.
        this.puzzleBlocks = genPuzzle(numBlocks, transformToBitmap(getDrawable(imgId)));
        imagePrinter(puzzleBlocks);
        try {
            //creacion de cronometro
            chronometer = findViewById(R.id.txtabTimer);
            ((TextView) findViewById(R.id.txtabMoves)).setText("Moves: " + Integer.toString(counter.getMovements()));

            Timer.resetTimer(chronometer);
            counter.reset();
            Timer.startChronometer(chronometer);
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
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
                    ((TextView) findViewById(R.id.txtabMoves)).setText("Moves: " + Integer.toString(counter.getMovements()));
                }catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }
                this.soundPool.play(this.sounds.getSwapSound(), 1, 1, 1, 0, (float) 1.5 );
                //Print the new order of blocks and reset the selector variable.
                imagePrinter(puzzleBlocks);
                if(this.puzzleBlocks.checkResult() > 0){
                    this.soundPool.play(this.sounds.getVictorySound(),1, 1, 3, 0, (float) 1.5 );
                    Timer.pauseChronometer(chronometer);
                    //para acceder al tiempo hacer Timer.offsetString, aparecera en milisegundos

                    Log.d(TAG, Timer.offsetString);
                    //INSERT TIME AND COUNTER IN DB
                }
                this.selector.resetSelection();
                break;
        }
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
            Log.d(TAG,e.getMessage());
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

        //Divide the image
        ImageDivider images = new ImageDivider(numOfPieces, image);
        images.divideImageInSquares();
        this.rows = images.getRows();
        this.columns = images.getColumns();

        //Build the blocks
        puzzleBlocks.genPiecesCollection(images.getImages());

        //Shuffle the blocks
        puzzleBlocks.shuffle();
        return puzzleBlocks;
    }

    //TODO: move this function to a class.
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
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int imageViewWide = (int) ((displayMetrics.widthPixels / columns) * 0.93); //TODO Base on the screen width (needs adjustment for screen rotation)
        int imageViewHigh = imageViewWide; //Square blocks
        Log.d(TAG, "Wide: " + imageViewWide + " High: " + imageViewHigh);
        LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(
                imageViewWide,
                imageViewHigh
        );

        //Set left and right margins of the rows layouts to center the images in the screen.
        int sideMarginLayout = (displayMetrics.widthPixels - (imageViewWide * columns)) / 2;
        childLpParams.setMargins(sideMarginLayout,0,sideMarginLayout,0);


        //Padding based on display density.
        //https://stackoverflow.com/questions/9685658/add-padding-on-view-programmatically
        float scale = getResources().getDisplayMetrics().density;
        int size = (int) (1*scale + 0.5f);

        //Start of the painting loop.
        LinearLayout childLp = new LinearLayout(this);
        ImageView imageView;
        int col = 0;
        for (puzzlePiece block :blocks.getPieces()
        ) {
            if(col == columns){ //New linear layout when last column reach.
                childLp.setBackgroundColor(Color.WHITE);
                mainLp.addView(childLp, childLpParams);
                childLp = new LinearLayout(this);
                col = 0; //As a new row is started, the column counter is set to zero.
            }
            //Insert in the imageView the image of the block and set the padding.
            imageView = new ImageView(this);
            imageView.setTag(block.getPosition());
            imageView.setImageBitmap(block.getImage());
            if(col == 0){
                imageView.setPadding(size,0,size,size); //Padding on the right, left and bottom for each block.
            }else{
                imageView.setPadding(0,0,size,size); //Padding on the right and bottom of each block.
            }
            imageView.setOnClickListener(this);

            //Insert the imageView in the layout of the row.
            childLp.addView(imageView,imgViewParams);
            col++;
        }
        childLp.setBackgroundColor(Color.WHITE); //Better contrast for the separation lines.
        mainLp.addView(childLp, childLpParams);
        mainLp.setBackgroundResource(0);    //Background image off.
        mainLp.setBackgroundColor(Color.GRAY); //Black background smooths image downsize on sides.
    }
}