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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Settings.FIREBASE_PATHS;
import Settings.Params;


public class custom_dialog_menu_online {

    public interface returnDialogMenuOnline {
        void ResultOnline(String email, int puzzres, int imgId, String img ,String Id);
    }

    private final String TAG = this.getClass().getSimpleName();
    private final returnDialogMenuOnline intrfc;
    public Intent intent = new Intent();
    private Context context;
    private String id;
    private String img;
    private String email;
    private int puzzres;




    public custom_dialog_menu_online(Context context, returnDialogMenuOnline actividad, String id, String email) {
        this.context = context;
        intrfc = actividad;
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.setContentView(R.layout.custom_dialog_menu_online);
        final TextView userName = (TextView) dialog.findViewById(R.id.txt_UserEnter);
        userName.setTextColor(Color.BLACK);//Text color for white background.
        userName.setText(email);
        this.id = id;
        this.email = email;

        // Botones selección de imágenes
        //Button imgDefault = (Button) dialog.findViewById(R.id.btnDefaultSelect);
        Button play = (Button) dialog.findViewById(R.id.btnCameraSelect);
        Button back = (Button) dialog.findViewById(R.id.btnLibrarySelect);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "play.onClick");
                puzzres = radioButtonCheck(dialog);
                //String img = Params.returnFirebaseImage();
                getRandomImageFromStorage();
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

    //ONLINE METHODS
    private void excludedImages(){
        Log.d(TAG, "excludedImages");
        List<String> images = new ArrayList<String>();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        ref.child(FIREBASE_PATHS.USERS).child(id).child(FIREBASE_PATHS.IMAGESSEEN).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange");
                for(DataSnapshot child : snapshot.getChildren()){
                    String imageName = child.getValue(String.class);
                    images.add(imageName);
                }
                storageImagesList(images);
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                Log.d(TAG, "isNewUser.onCancell: " + error.getMessage());
                //TODO: handle error.
            }
        });
    }
    private void storageImagesList(List<String> excludedImages){
        Log.d(TAG, "storageImagesList");
        List<String> newImages = new ArrayList<String>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference(FIREBASE_PATHS.STORAGE_IMG);
        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        if(!excludedImages.contains(item.getName())){
                            Log.d(TAG,"storageImagesList\nName: "+ item.getName()
                                    +"\nPath: " + item.getPath()
                                    + "\nBucket: " + item.getBucket()
                            );
                            newImages.add(item.getName());
                        }
                    }
                    if(excludedImages.size() > 0 && newImages.size() < 1){
                        restoreImagesSeen();//Call back restoreImages.
                    }else{
                        getDownloadUrl(randomImageFromList(newImages));//Call next method.
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, e.getMessage());
                    //TODO: handle error.
                    getDownloadUrl(randomImageFromList(newImages));//Call next method.
                });
    }
    private String randomImageFromList(List<String> images){
        String fileName = "";
        Random random = new Random();
        if(images.size() > 0){
            int pos = random.nextInt(0 + images.size());
            fileName = images.get(pos);
        }
        return  fileName;
    }
    private void getDownloadUrl(String fileName){
        Log.d(TAG, "getDownloadUrl: " + fileName);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(FIREBASE_PATHS.STORAGE_IMG).child(fileName);

        imageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    img = uri.toString();
                    launchOnlineGame();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, e.getMessage());
                });
    }
    private void getRandomImageFromStorage(){
        Log.d(TAG, "getRandomImageFromStorage");
        excludedImages();
    }
    private void restoreImagesSeen(){
        Log.d(TAG, "restoreImagesSeen");
        List<String> images = new ArrayList<String>();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        ref.child(FIREBASE_PATHS.USERS).child(id).child(FIREBASE_PATHS.IMAGESSEEN).removeValue();
        excludedImages();
    }
    private void launchOnlineGame(){
        intrfc.ResultOnline(email, puzzres, Params.DEFAULT, img, id);
    }

}


