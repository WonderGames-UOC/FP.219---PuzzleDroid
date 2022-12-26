package com.example.puzzledroid;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
//import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import Settings.Params;


public class custom_dialog_menu {

    public interface returnDialogMenu {
        void Result(String username, int puzzres, int imgId);

    }

    private final returnDialogMenu intrfc;
    //public Intent intent = new Intent();




    public custom_dialog_menu(Context context, returnDialogMenu actividad) {

        intrfc = actividad;
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.setContentView(R.layout.custom_dialog_menu);

        final TextView userName = (TextView) dialog.findViewById(R.id.txt_UserEnter);
        ((TextView) dialog.findViewById(R.id.txt_UserEnter)).setTextColor(Color.BLACK); //Text color for white background.



        // Botones selección de imágenes
        Button imgDefault = (Button) dialog.findViewById(R.id.btnDefaultSelect);
        Button imgCamera = (Button) dialog.findViewById(R.id.btnCameraSelect);
        Button imgLibrary = (Button) dialog.findViewById(R.id.btnLibrarySelect);

        imgDefault.setOnClickListener(view -> {
            int puzzres = radioButtonCheck(dialog);
            if (userNameCheck(userName, context)) {
                intrfc.Result(userName.getText().toString(), puzzres, Params.DEFAULT);
                dialog.dismiss();
            }
        });
        imgCamera.setOnClickListener(view -> {
            int puzzres = radioButtonCheck(dialog);
            if (userNameCheck(userName, context)) {
                intrfc.Result(userName.getText().toString(), puzzres, Params.CAMERA);
                dialog.dismiss();
            }

        });
        imgLibrary.setOnClickListener(view -> {
            int puzzres = radioButtonCheck(dialog);
            if (userNameCheck(userName, context)) {
                intrfc.Result(userName.getText().toString(), puzzres, Params.GALLERY);
                dialog.dismiss();
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


}


