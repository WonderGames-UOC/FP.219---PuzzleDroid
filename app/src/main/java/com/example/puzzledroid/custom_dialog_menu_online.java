package com.example.puzzledroid;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Settings.Params;


public class custom_dialog_menu_online {

    public interface returnDialogMenuOnline {
        void ResultOnline(String email, int puzzres, int imgId,  String Id);
    }

    private final String TAG = this.getClass().getSimpleName();
    private final returnDialogMenuOnline intrfc;
    public Intent intent = new Intent();
    private Context context;




    public custom_dialog_menu_online(Context context, returnDialogMenuOnline actividad, String id, String email) {
        this.context = context;
        intrfc = actividad;
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.custom_dialog_menu_online);

        final TextView userName = (TextView) dialog.findViewById(R.id.txt_UserEnter);
        userName.setTextColor(Color.BLACK);//Text color for white background.
        userName.setText(email);




        // Botones selección de imágenes
        //Button imgDefault = (Button) dialog.findViewById(R.id.btnDefaultSelect);
        Button play = (Button) dialog.findViewById(R.id.btnCameraSelect);
        Button back = (Button) dialog.findViewById(R.id.btnLibrarySelect);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "play.onClick");
                int puzzres = radioButtonCheck(dialog);
                intrfc.ResultOnline(email, puzzres, Params.DEFAULT, id);
                dialog.dismiss();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "back.onClick");
                try{
                    dialog.dismiss();
                }catch (Throwable t){
                    Log.e(TAG, t.getMessage());
                }
            }
        });
        dialog.show();
    }
    // Función de comprobación del nombre de ususario.
    public boolean userNameCheck(TextView userName, Context context) {
        if (userName.getText().toString().equals("")) {
            Toast.makeText(context, "Please, enter a valid user name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    // Función que devuelve la resolución del puzzle según los radio buttons.
    public int radioButtonCheck (Dialog dialog){
        int puzzres = Params.EASY;
        // Botones seleccción de Dificultad
        RadioButton radioButtonMedium = (RadioButton) dialog.findViewById(R.id.rbtnMediu);
        RadioButton radioButtonHard = (RadioButton) dialog.findViewById(R.id.rbtnHard);

        if (radioButtonMedium.isChecked()) {
            puzzres = Params.MEDIUM;
        }

        if (radioButtonHard.isChecked()) {
            puzzres = Params.HARD;
        }

        return puzzres;
    }

    // Función  que devuelve una imagen aleatoria de las propuestas inicialmente para el juego.
    public int imageRandomReturn(){
        List<Integer> image = Arrays.asList(R.drawable.level1, R.drawable.level2, R.drawable.level3);
        Random random = new Random();

        return image.get(random.nextInt(image.size()));
    }





}


