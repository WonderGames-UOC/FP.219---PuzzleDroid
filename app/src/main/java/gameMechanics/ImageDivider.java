package gameMechanics;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;

public class ImageDivider {
    final private String TAG = "imageDivider";
    private int rows, columns, high, wide, denominator;
    private Bitmap image;
    private ArrayList<Bitmap> images;

    //Constructors
    public ImageDivider(){
    }
    public ImageDivider(int denominator, Bitmap image){
        this.image = image;
        this.denominator = denominator;
        validDenominator();
    }

    /**
     * It will return a number of columns an rows in case we ask for a number of pieces.
     */
    private void validDenominator(){
        Log.d(TAG, "validDenominator");
        try{
            if(this.denominator < 2){
                this.denominator = 2;
            }
            double res = Math.sqrt(this.denominator);
            this.rows = this.columns = (int) Math.round(res);
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Fills the images ArrayList with the blocks of the provided image base on the rows, columns, wide and high provided.
     * @param image Scaled Bitmap of the image to divide.
     */
    private void imageDivider(Bitmap image){
        Log.d(TAG, "imageDivider");
        /*
            Read the image from the {0,0} position, from left to right and up to down,
            in steps equal to the wide and high set for the pieces.
         */
        Bitmap block;
        int x, y = 0;
        for(int i = 0; i < rows; i++){
            x = 0;
            for( int j = 0; j < columns; j++){
                try {
                    block= Bitmap.createBitmap(image, x, y, wide, high);
                    images.add(block);
                    x+=wide;
                }catch (Exception e){
                    Log.d(TAG, e.getMessage());
                    return;
                }
            }
            y+=high;
        }
    }

    /**
     * Divides a Bitmap image in the indicated number of blocks.
     */
    public void divideImage(){
        Log.d(TAG, "divideImage");

        //Empty image arrayList.
        images = new ArrayList<Bitmap>();

        //Set the images pieces high and wide.
        wide = image.getWidth() / columns;
        high = image.getHeight() / rows;

        //ScaledBitmap will allow us tu cut the image in pieces of exact wide and high.
        Bitmap scaledBm = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), true);

        imageDivider(scaledBm);
    }

    /**
     * Divides a Bitmap image in the indicated number of blocks based on a specific image high and wide.
     * @param high
     * @param wide
     */
    public void divideImage(int high, int wide){
        Log.d(TAG, "divideImage");

        //Empty image arrayList.
        images = new ArrayList<Bitmap>();

        //Set the images pieces high and wide.
        this.wide = wide / columns;
        this.high = high / rows;

        //ScaledBitmap will allow us tu cut the image in pieces of exact wide and high.
        Bitmap scaledBm = Bitmap.createScaledBitmap(image, wide, high, true);

        imageDivider(scaledBm);
    }

    /**
     * Divides the image provided in the number indicated of square blocks.
     */
    public void divideImageInSquares(){
        Log.d(TAG, "divideImageInSqueares");

        //Empty image arrayList.
        images = new ArrayList<Bitmap>();

        //Calculate the area of the square blocks.
        int imageArea = image.getHeight() * image.getWidth();
        int partArea = imageArea / denominator;
        this.wide = this.high = (int) Math.floor(Math.sqrt(partArea));

        //Calculate the number of rows and columns base on the size of the image and the size of the squares blocks.
        this.columns = (int) Math.floor(image.getWidth()/this.wide);
        this.rows = (int) Math.floor(image.getHeight()/this.high);

        //Scalate the original image based on the size and number of the square blocks.
        int scaledWide = (this.wide * columns);
        int scaledHigh = (this.high * rows);
        Bitmap scaledImage = Bitmap.createScaledBitmap(image, scaledWide, scaledHigh, true);

        imageDivider(scaledImage);
    }

    /**
     * Divides the image provided in the number indicated of square blocks based on a specific image High and Wide.
     * @param high
     * @param wide
     */
    public void divideImageInSquares(int high, int wide){
        Log.d(TAG, "divideImageInSquares");
        Log.d(TAG, "Wide: " + wide + " High: "+ high);
        //Empty image arrayList.
        images = new ArrayList<Bitmap>();

        //Calculate the area of the square blocks.
        Log.d(TAG, "ImageH: " + image.getHeight() + " ImageW: " + image.getWidth());
        int imageArea = high * wide;
        int partArea = imageArea / denominator;
        this.wide = this.high = (int) Math.floor(Math.sqrt(partArea));

        //Calculate the number of rows and columns base on the size of the image and the size of the squares blocks.
        this.columns = (int) Math.floor(wide/this.wide);
        this.rows = (int) Math.floor(high/this.high);
        Log.d(TAG, "Pieces: " + this.denominator  +" Cols: " +columns + " Rows: " + rows);

        //Scalate the original image based on the size and number of the square blocks.
        int scaledWide = (this.wide * columns);
        int scaledHigh = (this.high * rows);
        Bitmap scaledImage = Bitmap.createScaledBitmap(image, scaledWide, scaledHigh, true);
        imageDivider(scaledImage);
    }

    /**
     * Divides the image provided in the number indicated of square blocks based on a specific image High and Wide.
     * @param dp
     */
    public void divideImageInSquares(DisplayMetrics dp){
        Log.d(TAG, "divideImageInSquares");
        Log.d(TAG, "Wide: " + wide + " High: "+ high);
        //Empty image arrayList.
        images = new ArrayList<Bitmap>();

        //Calculate the area of the square blocks.
        Log.d(TAG, "ImageH: " + image.getScaledHeight(dp.heightPixels) + " ImageW: " + image.getScaledWidth(dp.widthPixels));
        Log.d(TAG, "dpH: " + dp.heightPixels + " dpW: " + dp.widthPixels);
        int imageArea = image.getScaledHeight(dp.densityDpi) * image.getScaledWidth(dp.densityDpi);
        int partArea = imageArea / denominator;
        this.wide = this.high = (int) Math.floor(Math.sqrt(partArea));

        //Calculate the number of rows and columns base on the size of the image and the size of the squares blocks.
        this.columns = (int) Math.floor(image.getScaledWidth(dp.densityDpi)/this.wide);
        this.rows = (int) Math.floor(image.getScaledHeight(dp.densityDpi)/this.high);
        Log.d(TAG, "Pieces: " + this.denominator  +" Cols: " +columns + " Rows: " + rows);

        //Scalate the original image based on the size and number of the square blocks.
        int scaledWide = (this.wide * columns);
        int scaledHigh = (this.high * rows);
        Bitmap scaledImage = Bitmap.createScaledBitmap(image, scaledWide, scaledHigh, true);
        imageDivider(scaledImage);
    }
    /**
     * MCD Euclid's method
     * @param x integer A while A > B
     * @param y integer B
     * @return MCD of the two integers.
     */
    private int mCD(int x, int y){
        Log.d(TAG, "divideImageInSquares");
        if(y == 0){
            return x;
        }
        return mCD(y,x % y);
    }



    //Getters & Setters
    public int getRows() {
        return rows;
    }
    public void setRows(int rows) {
        this.rows = rows;
    }
    public int getColumns() {
        return columns;
    }
    public void setColumns(int columns) {
        this.columns = columns;
    }
    public int getHigh() {
        return high;
    }
    public void setHigh(int high) {
        this.high = high;
    }
    public int getWide() {
        return wide;
    }
    public void setWide(int wide) {
        this.wide = wide;
    }
    public int getDenominator() {
        return denominator;
    }
    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    public ArrayList<Bitmap> getImages() {
        return images;
    }
}
