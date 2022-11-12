package gameMechanics;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class PuzzlePieces {
    final String TAG = "puzzlePieces";
    private ArrayList<PuzzlePiece> pieces;
    private PuzzlePiece pieceA;
    private PuzzlePiece pieceB;

    //Constructors
    public PuzzlePieces(){}
    public PuzzlePieces(ArrayList<PuzzlePiece> pieces) {
        this.pieces = pieces;
    }
    //Getters & Setters
    public PuzzlePiece getPieceA() {
        return pieceA;
    }
    public void setPieceA(PuzzlePiece pieceA) {
        this.pieceA = pieceA;
    }
    public PuzzlePiece getPieceB() {
        return pieceB;
    }
    public void setPieceB(PuzzlePiece pieceB) {
        this.pieceB = pieceB;
    }
    public ArrayList<PuzzlePiece> getPieces(){
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
            this.pieces = new ArrayList<PuzzlePiece>();
            if(images.size() < 2){
                return 1;
            }
            PuzzlePiece piece = new PuzzlePiece();
            int pos = 0;
            for(Bitmap img:images
            ){
                //puzzlePiece(int orgX, int orgY, Bitmap image, int currentX, int currentY, int position){}
                piece = new PuzzlePiece(img, pos);
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
    public PuzzlePiece getPieceByPos(int pos){
        Log.d(TAG, "getPieceByPos");
        try {
            pieces.get(pos);
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
        return new PuzzlePiece();
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
     * Verifies if all the puzzlePieces are ok
     * @return 1 for success, 0 for fail.
     */
    public int checkResult(){
        Log.d(TAG,"checkresult");
        try{
            int pos = 0;
            for(PuzzlePiece piece:pieces
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
     * Returns true if the piece is in the correct pos.
     * @param id
     * @return
     */
    public boolean checkPiece(int id){
        int pos = getPieceIndexById(id);
        if(pos == id){
            return true;
        }
        return false;
    }

    /**
     * Randomize the puzzlePieces order of the pieces collection.
     */
    public void shuffle(){
        //Log.d(TAG,"shuflle");
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
