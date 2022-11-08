package com.example.puzzledroid;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class puzzlePieces {
    final String TAG = "puzzlePieces";
    private ArrayList<puzzlePiece> pieces;
    private puzzlePiece pieceA;
    private puzzlePiece pieceB;

    //Constructors
    public puzzlePieces(){}
    public puzzlePieces(ArrayList<puzzlePiece> pieces) {
        this.pieces = pieces;
    }
    //Getters & Setters
    public puzzlePiece getPieceA() {
        return pieceA;
    }
    public void setPieceA(puzzlePiece pieceA) {
        this.pieceA = pieceA;
    }
    public puzzlePiece getPieceB() {
        return pieceB;
    }
    public void setPieceB(puzzlePiece pieceB) {
        this.pieceB = pieceB;
    }
    public ArrayList<puzzlePiece> getPieces(){
        return this.pieces;
    }

    /**
     * Generates the collections puzzlePieces from an arrayList<Bitmap>
     * @param images:  Bitmap collection to build the puzzle.
     * @return returns 0 if no error. Otherwise returns 1.
     */
    public int genPiecesCollection(ArrayList<Bitmap> images){
        Log.d(TAG, "genPiecesCollection");
        try{
            this.pieces = new ArrayList<puzzlePiece>();
            if(images.size() < 2){
                return 1;
            }
            puzzlePiece piece = new puzzlePiece();
            int pos = 0;
            for(Bitmap img:images
            ){
                //puzzlePiece(int orgX, int orgY, Bitmap image, int currentX, int currentY, int position){}
                piece = new puzzlePiece(img, pos);
                this.pieces.add(piece);
                pos++;
            }
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
            return 1;
        }
        return 0;
    }

    /**
     * Returns a puzzlePiece of the pieces collection.
     * @param pos: index position on the collection of the puzzlePiece.
     * @return a puzzlePiece of the pieces collection of position pos or an empty puzzlePiece if there is an error.
     */
    public puzzlePiece getPieceByPos(int pos){
        Log.d(TAG, "getPieceByPos");
        try {
            pieces.get(pos);
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
        return new puzzlePiece();
    }

    /**
     * Swap position between two element in the collection.
     * @param posA index position on the collection of the pieceA.
     * @param posB index position on the collection of the pieceA.
     * @return returns 0 if no error. Otherwise returns 1.
     */
    private int swapPieces(int posA, int posB){
        Log.d(TAG, "swapPieces");
        try {
            this.pieceA = pieces.get(posA);
            this.pieceB = pieces.get(posB);
            pieces.set(posA, pieceB);
            pieces.set(posB, pieceA);
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
            return 1;
        }
        return 0; //Success.
    }

    /**
     *
     * @param idA
     * @param idB
     */
    public void swapPiecesById(int idA, int idB){
        Log.d(TAG, "swapPiecesById");
        int posA, posB = 0;
        posA = getPieceIndexById(idA);
        posB = getPieceIndexById(idB);
        if(posA < 0 | posB < 0){
            Log.d(TAG, Integer.toString(posA) +" "+ Integer.toString(posB) );
            return;
        }
        swapPieces(posA, posB);
    }

    /**
     *
     * @param id
     * @return
     */
    private int getPieceIndexById(int id){
        Log.d(TAG, "getPieceById");
        for(int i = 0; i < this.pieces.size(); i++){
            if(pieces.get(i).getPosition() == id){
                return i;
            }
        }
        Log.d(TAG, Integer.toString(id) + " not found.");
        return -1;
    }

    /**
     * Verifies if all the puzzlePieces are o
     * @return 1 for success, 0 for fail.
     */
    public int checkResult(){
        Log.d(TAG,"checkresult");
        try{
            int pos = 0;
            for(puzzlePiece piece:pieces
            ){
                if(piece.getPosition() != pos){
                    return 0;
                }
                pos++;
            }
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
            return  -1;
        }
        return 1;
    }

    /**
     * Randomize the puzzlePieces order of the pieces collection.
     */
    public void shuffle(){
        Log.d(TAG,"shuflle");
        int passes = this.pieces.size() * 10;
        try {
            for(int i = passes; i>=0; i--){
                int posA = ThreadLocalRandom.current().nextInt(0, this.pieces.size());
                int posB = ThreadLocalRandom.current().nextInt(0, this.pieces.size());
                swapPieces(posA,posB);
            }
            if(checkResult() > 0){
                shuffle();
            }
        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
    }
}
