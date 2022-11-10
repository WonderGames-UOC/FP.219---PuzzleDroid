package com.example.puzzledroid;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class custom_dialog_menu {

    public interface returnDialogMenu {
        void Result(String username, int puzzres);

    }

    private final returnDialogMenu intrfc;

    public custom_dialog_menu(Context context, returnDialogMenu actividad) {
        intrfc = actividad;
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setContentView(R.layout.custom_dialog_menu);

        final TextView userName = (TextView) dialog.findViewById(R.id.txt_UserEnter);
        ImageView lvl1 = (ImageView) dialog.findViewById(R.id.imageView_Lvl1);
        ImageView lvl2 = (ImageView) dialog.findViewById(R.id.imageView_Lvl2);
        ImageView lvl3 = (ImageView) dialog.findViewById(R.id.imageView_Lvl3);


        lvl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userNameCheck(userName, context)) {
                    intrfc.Result(userName.getText().toString(), 8);
                    dialog.dismiss();
                }
            }
        });
        lvl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userNameCheck(userName, context)) {
                    intrfc.Result(userName.getText().toString(), 18);
                    dialog.dismiss();
                }

            }
        });
        lvl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userNameCheck(userName, context)) {
                    intrfc.Result(userName.getText().toString(), 32);
                    dialog.dismiss();

                }
            }
        });


        dialog.show();


    }

    public boolean userNameCheck(TextView userName, Context context) {
        if (userName.getText().toString().equals("")) {
            Toast.makeText(context, "Please, insert a valid user name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

}


