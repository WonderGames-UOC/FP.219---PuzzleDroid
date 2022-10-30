package com.example.puzzledroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.SplittableRandom;

public class Game01Activity extends AppCompatActivity {

    //¡¡NO BORRAR!! Etiqueta para el depurador.
    protected String tag = "Game01Activity";

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
                imagePrinter(imageDivider(4), 4);
            }
        });
        bx8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(tag,"buttonx8");
                imagePrinter(imageDivider(9),9);

            }
        });
        bx16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(tag,"buttonx16");
                imagePrinter(imageDivider(16),16);
            }
        });
    }
    //Funciones dividir y mostrar
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
    private void imagePrinter(ArrayList<Bitmap> trozos, int columnas){
        Log.d(tag, "imagePrinter");

        //Definimos los atributos de los Linealayouts que conformarán la estructura.
        LinearLayout.LayoutParams mainLpParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.VERTICAL);
        LinearLayout.LayoutParams childLpParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.HORIZONTAL);

        //Instanciamos el layout padre
        LinearLayout mainLp = (LinearLayout) findViewById(R.id.puzzle_view);
        mainLp.setLayoutParams(mainLpParams);
        int ancho = (int)(mainLp.getWidth() / Math.sqrt(columnas));
        int alto = (int) (mainLp.getHeight() / Math.sqrt(columnas));

        //Creamos el primer LinarLayout child
        LinearLayout childLp = new LinearLayout(this);

        //Iniciamos el loop para insertar las imagenes
        ImageView imageView;
        int col = 0; //Iniciamos el contador de columnas
        Log.d(tag, "Núm. trozos: " + String.valueOf(trozos.size()));
        for (Bitmap trozo: trozos
             ) {
            if(col == Math.sqrt(columnas)){ //Máx imagenes por fila
                mainLp.addView(childLp, childLpParams); //Insertamos el layout con las N imágenes en el layout padre.
                childLp = new LinearLayout(this); //Creamos un nuevo layout.
                col = 0; //Ponemos a 0 el contador de imágenes por fila.
            }
            imageView = new ImageView(this);
            imageView.setImageBitmap(trozo);
            //https://stackoverflow.com/questions/9685658/add-padding-on-view-programmatically
            float scale = getResources().getDisplayMetrics().density;
            int size = (int) (1*scale + 0.5f);
            imageView.setPadding(size,size,size,size);
            childLp.addView(imageView, ancho, alto); //Insertamos la imagen en el layout hijo.
            col++;
        }
        mainLp.addView(childLp, childLpParams); //Insertamos el últimno layout hijo.
        findViewById(R.id.puzzleDroid_imageView).setVisibility(View.INVISIBLE); //Ocultamos el fondo
    }
    private void launchActivity(ArrayList<Bitmap> trozos){
        Intent intent = new Intent(this, imgChunks.class);
        intent.putParcelableArrayListExtra("Morceaux", trozos);
        startActivity(intent);
    }
}