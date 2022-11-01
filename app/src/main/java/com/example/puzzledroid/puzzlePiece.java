package com.example.puzzledroid;

import android.graphics.Bitmap;

public class puzzlePiece {
    final String tag = "puzzlePiece";
    private int orgX, orgY, currentX, currentY, position;
    private Bitmap image;

    //Constructors
    public puzzlePiece(){}
    public puzzlePiece(Bitmap image, int pos){
        this.image = image;
        this.position = pos;
    }
    public puzzlePiece(int orgX, int orgY, Bitmap image){
        this.orgX = orgX;
        this.orgY = orgY;
        this.image = image;
    }
    public puzzlePiece(int orgX, int orgY, Bitmap image, int currentX, int currentY){
        this.orgX = orgX;
        this.orgY = orgY;
        this.image = image;
    }
    public puzzlePiece(int orgX, int orgY, Bitmap image, int currentX, int currentY, int position){
        this.orgX = orgX;
        this.orgY = orgY;
        this.image = image;
        this.position = position;
    }


    //Getters & setters
    public int getOrgX() {
        return orgX;
    }
    public void setOrgX(int orgX) {
        this.orgX = orgX;
    }
    public int getOrgY() {
        return orgY;
    }
    public void setOrgY(int orgY) {
        this.orgY = orgY;
    }
    public int getCurrentX() {
        return currentX;
    }
    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }
    public int getCurrentY() {
        return currentY;
    }
    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
}
