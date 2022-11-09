package com.example.puzzledroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;

import dbHelper.SQLiteHelper;



public class Game01Activity extends AppCompatActivity implements OnClickListener {
    SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "BD1_HighScores", null, 1);

    //declaraciones para el cronometro
    private Chronometer chronometer;
    //aqui guardamos el total de segundos que tardamos en resolver el puzzle
    private long pauseOffset;
    private String offsetString = pauseOffset+"";
    private boolean running;


    //¡¡NO BORRAR!! Etiqueta para el depurador.
    private final String TAG = "Game01Activity";

    //Game mechanics variables.
    private puzzlePieces puzzleBlocks = new puzzlePieces();
    private int rows, columns;
    private Selector selector = new Selector();
    private Counter counter = new Counter();

    //Sound variables.
    private SoundPool soundPool;
    private Sounds sounds;

    //Imagenes preseleccionadas de RESOURCES (app/res/drawable)
    protected int[] images = {
            R.drawable.level1,
            R.drawable.level2,
            R.drawable.level3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //¡¡NO BORRAR!! Registro para el depurador.
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game01);

        //Game sounds (Sounds config directly on the class).
        this.sounds = new Sounds(this);
        this.soundPool =  sounds.getSoundPool();


        //TODO: Obtener la imagen del nivel seleccionado
        //image = this.findViewById(R.id.imageView_game01Activity);

        //creacion de cronometro
        chronometer = findViewById(R.id.chronometer);

        //TODO: Eliminar botones y definir la división de la imagen en base al nivel seleccionado.
        Button bx4 = (Button) findViewById(R.id.button_x4);
        Button bx8 = (Button) findViewById(R.id.button_x8);
        Button bx16 = (Button) findViewById(R.id.button_x16);
        //Funciones onClick
        bx4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"buttonx4");
                int rndmNum = (int)(Math.random() * 3);
                genPuzzle(8, transformToBitmap(getDrawable(images[rndmNum])));
                imagePrinter(puzzleBlocks);
                resetTimer();
                counter.reset();
                startChronometer();
            }
        });
        bx8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"buttonx8");
                int rndmNum = (int)(Math.random() * 3);
                genPuzzle(18, transformToBitmap(getDrawable(images[rndmNum])));
                imagePrinter(puzzleBlocks);
                resetTimer();
                counter.reset();
                startChronometer();
            }
        });
        bx16.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"buttonx16");
                int rndmNum = (int)(Math.random() * 3);
                genPuzzle(32, transformToBitmap(getDrawable(images[rndmNum])));
                imagePrinter(puzzleBlocks);
                resetTimer();
                counter.reset();
                startChronometer();
            }
        });

    }
    public void onClick(View v){
        Log.d(TAG, "onClick: ");
        Log.d(TAG, (v.getId()) +" "+ v.getTag());

        int pos = Integer.parseInt(v.getTag().toString());  //Get the position value stored in the touched imageView
        selector(pos);                                      //Call selector function.
    }

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
                this.soundPool.play(this.sounds.getSwapSound(), 1, 1, 1, 0, (float) 1.5 );
                //Print the new order of blocks and reset the selector variable.
                imagePrinter(puzzleBlocks);
                if(this.puzzleBlocks.checkResult() > 0){
                    this.soundPool.play(this.sounds.getVictorySound(),1, 1, 3, 0, (float) 1.5 );
                    pauseChronometer();
                    //INSERT TIME IN DB
                }
                this.selector.resetSelection();
                break;
        }
    }

    //TODO: create class to work with any kind of image or drawable
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
    private void genPuzzle(int numOfPieces, Bitmap image){
        Log.d(TAG, "genPuzzle");
        //Divide the image
        imageDivider images = new imageDivider(numOfPieces, image);
        images.divideImageInSquares();
        rows = images.getRows();
        columns = images.getColumns();

        //Build the blocks
        this.puzzleBlocks.genPiecesCollection(images.getImages());

        //Shuffle the blocks
        this.puzzleBlocks.shuffle();

        //Print the blocks
        imagePrinter(this.puzzleBlocks);
    }

    private void imagePrinter(puzzlePieces blocks){
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

        //Instantiate the parent layout.
        LinearLayout mainLp = (LinearLayout) findViewById(R.id.puzzle_view);
        mainLp.setLayoutParams(mainLpParams);
        mainLp.removeAllViewsInLayout();


        //Define the wide & high of the imageview of each block based on the current wide and high of the parent layout.
        int imageViewWide = (int)(mainLp.getWidth() / columns);
        int imageViewHigh = (int) (mainLp.getHeight() / rows);

        //https://stackoverflow.com/questions/9685658/add-padding-on-view-programmatically
        float scale = getResources().getDisplayMetrics().density;
        int size = (int) (1*scale + 0.5f);

        LinearLayout childLp = new LinearLayout(this);
        ImageView imageView;
        int col = 0;
        for (puzzlePiece block :blocks.getPieces()
             ) {
            if(col == columns){ //New linear layout when last column reach.
                mainLp.addView(childLp, childLpParams);
                childLp = new LinearLayout(this);
                col = 0; //As a new row is started, the column counter is set to zero.
            }
            //Insert in the imageView the image of the block and set the padding.
            imageView = new ImageView(this);
            imageView.setTag(block.getPosition());
            imageView.setImageBitmap(block.getImage());
            imageView.setPadding(size,size,size,size);
            imageView.setOnClickListener(this);

            //Insert the imageView in the layout of the row.
            childLp.addView(imageView, imageViewWide, imageViewHigh);
            col++;
        }
        mainLp.addView(childLp, childLpParams);
        //TODO: remove background image.
        if (findViewById(R.id.puzzleDroid_imageView).getVisibility() != View.INVISIBLE){
            findViewById(R.id.puzzleDroid_imageView).setVisibility(View.INVISIBLE);
        }
    }

    public void startChronometer() {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
    }

    public void pauseChronometer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }
    public void resetTimer(){
        if(running){
            chronometer.stop();
            running = false;
        }
        pauseOffset = 0;
        offsetString = pauseOffset+"";
    }
}