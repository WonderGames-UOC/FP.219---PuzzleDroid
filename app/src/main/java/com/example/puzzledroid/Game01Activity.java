package com.example.puzzledroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class Game01Activity extends AppCompatActivity {

    //¡¡NO BORRAR!! Etiqueta para el depurador.
    private final String tag = "Game01Activity";
    private puzzlePieces puzzleBlocks = new puzzlePieces();
    private int rows, columns;

    //Imagenes preseleccionadas de RESOURCES (app/res/drawable)
    protected int[] images = {
            R.drawable.level1,
            R.drawable.level2,
            R.drawable.level3
    };
    private ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //¡¡NO BORRAR!! Registro para el depurador.
        Log.d(tag, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game01);

        //Obtener la imagen del nivel seleccionado
        //image = this.findViewById(R.id.imageView_game01Activity);


        //TODO: Eliminar botones y definir la división de la imagen en base al nivel seleccionado.
        Button bx4 = (Button) findViewById(R.id.button_x4);
        Button bx8 = (Button) findViewById(R.id.button_x8);
        Button bx16 = (Button) findViewById(R.id.button_x16);

        //Funciones onClick
        bx4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(tag,"buttonx4");
                genPuzzle(4, transformToBitmap(getDrawable(R.drawable.level1)));
                imagePrinter(puzzleBlocks);
            }
        });
        bx8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(tag,"buttonx8");
                genPuzzle(8, transformToBitmap(getDrawable(R.drawable.level1)));
                imagePrinter(puzzleBlocks);
            }
        });
        bx16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(tag,"buttonx16");
                genPuzzle(32, transformToBitmap(getDrawable(R.drawable.level1)));
                imagePrinter(puzzleBlocks);
            }
        });
    }
    //TODO: create method to work with any kind of image or drawable
    private Bitmap transformToBitmap(Drawable img){
        try{
            BitmapDrawable bmDrawable = (BitmapDrawable) img;
            Bitmap bm = bmDrawable.getBitmap();
            return  bm;
        }catch (Exception e){
            Log.d(tag,e.getMessage());
        }
        return null;
    }

    private void genPuzzle(int numOfPieces, Bitmap image){
        Log.d(tag, "genPuzzle");
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
        Log.d(tag, "imagePrinter");

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
            imageView.setImageBitmap(block.getImage());
            imageView.setPadding(size,size,size,size);

            //Insert the imageView in the layout of the row.
            childLp.addView(imageView, imageViewWide, imageViewHigh);
            col++;
        }
        mainLp.addView(childLp, childLpParams);
        //TODO: remove background image.
        findViewById(R.id.puzzleDroid_imageView).setVisibility(View.INVISIBLE);
    }

    //Funciones dividir y mostrar. DEPRECATED
    private ArrayList<Bitmap> imageDivider(int denominador){
        Log.d(tag,"imageDivider");

        //Filas y columnas que obtendremos
        int filas, columnas;

        //Alto y ancho de cada trozo
        int altoTrozo, anchoTrozo;

        /*TODO: Los trozos de la imagen deberán almacenarse como colección de objetos con atributos:
            coordenas original
            coordenadas actuales
            bitmap de la imagen
         */
        //ArrayList donde guardaremos cada trozo. Tipo bitmap. El número de trozos sera un parámetro de entrada del método.
        ArrayList<Bitmap> trozos = new ArrayList<Bitmap>(denominador);

        /*Determinar la escala de la imagen, el alto y el ancho. Usar la clase BitmapDrawable
          https://developer.android.com/reference/android/graphics/Bitmap
        */
        //Creamos un bitmap de la imagen que nos permitirá determinar su ancho y alto-
        BitmapDrawable bmDrawable = (BitmapDrawable) getDrawable(R.drawable.level1);
        Bitmap bm = bmDrawable.getBitmap();
        //Creamos una nueva imagen con las dimensiones escogidas. createSaledBitmap(source, width, high, filter)
        Bitmap bmEscalado = Bitmap.createScaledBitmap(bm,bm.getWidth(),bm.getHeight(),true);
        Log.d(tag, "Tamaño img: " + bmEscalado.getWidth() + "x"+bmEscalado.getHeight());
        //TODO: crear método para determinar núm. filas y columas que generen trozos cuadrados.
        //Definimos el mismo número de filas y columnas.
        filas = columnas = (int) Math.sqrt(denominador);
        Log.d(tag, String.valueOf(filas));
        //Calculamos el ancho de cada trozo en base al ancho y alto de la imagen original y función del número de filas y columnas definidos.
        altoTrozo = bm.getHeight() / filas; //Definimos la altura de cada trozo
        anchoTrozo = bm.getWidth() / filas; //Definimos el ancho de cada trozo
        Log.d(tag, String.valueOf(altoTrozo) + " x " + String.valueOf(anchoTrozo));

        //Recorremos nuestra imagen de arriba a abajo (o viceversa) en saltos iguales ancho y alto definidos para cado trozo trozo.
        //Usaremos la funcion Bitmap.createBitmap(bmEscalado,coordX,coordY, ancho, alto) para crear los trozos.
        //Estos se almacenarán en el ArrayList definido anteriormente.
        int x, y = 0; //Iniciamos las coordenadas que marcaran el comienzo de los trozos.
        for(int i = 0; i < filas; i++){//El primer loop recorrerá las filas
            x=0; //Volvemos a poner a 0 la coordenada de las columnas en cada iteración del loop filas.
            for(int j = 0; j < columnas; j++){//El segundo loop las columnas
                //Añadimos el trozo al array.
                trozos.add(Bitmap.createBitmap(bmEscalado, x, y, anchoTrozo, altoTrozo));
                x+=anchoTrozo; //Saltamos a la siguiente coordenada.
            }
            y+=altoTrozo; //saltamos a la siguiente coordenada
        }
        //Devolvemos el arrayList con las imagenes
        return trozos;
    };
}