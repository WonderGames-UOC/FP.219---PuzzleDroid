package gameMechanics;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;

public class ImageDivider {
    final private String TAG = "imageDivider";
    private int rows, columns, high, wide, denominator, barHight, sHight, sWidth;
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
        //Rows are always double of columns.
        //Denominator variable sets the num of rows.
        // Starts dividing the screen in squares. If screen hight(wide)/width (narrow) ratio < 2, then the size of the square is reduced to fit all the blocks in the hight of the screen,
        // if the ratio > 2, then is reduced to fit all the blocks in the width of the screen.
        //Image will be first cut to match the ratio of the screen, then scaled and divided.
        //Image is rotated to better fit the screen.

        //Empty image arrayList.
        images = new ArrayList<Bitmap>();

        //Numbre of blocks
        switch (this.denominator){ //Limits the denominator values.
            case 4:
            case 8:
            case 6:
            case 10:
                break;
            default:
                this.denominator = 6;
        }
        int numOfBlocks = (int) (Math.pow(denominator,2)/2);
        this.rows = denominator;
        this.columns = denominator / 2;

        //Screen wide and narrow dimensions
        DisplayMetrics dp = new DisplayMetrics();
        int sHight = this.sHight - barHight; //Action bar high.
        int sWidth = this.sWidth;
        float sRatio = (float)sHight/(float)sWidth;

        Log.d(TAG, "Current Screen dimension:"
                + "\nsRatio: " + sRatio +
                "\n sHight: " + sHight +
                "\n sWidth: " + sWidth);

        //Divide screen in squares Nx2N format.
        if(sRatio < 2){//Screen width need adjustment
            float correctionFactor = 1 - (2-sRatio) /2;

            sWidth = (int) Math.round((float) sWidth *  correctionFactor);
        }
        if(sRatio > 2){//Screen high needs adjustment
            float correctionFactor = 1 - (sRatio - 2) /2;
            sHight = (int) Math.round((float) sHight * correctionFactor);
        }
        sRatio = (float)sHight / (float) sWidth;

        //Calculate the area of the square blocks.
        int sArea = sHight * sWidth;
        int blockArea = (int) Math.round(Math.sqrt(sArea/numOfBlocks));
        int blockSize = (int) Math.floor(blockArea / 2);

        Log.d(TAG, "Blocks dimensions:"
                +"\nScreen Area: " + sArea +
                "\n Block Area: " + Math.round(Math.sqrt(sArea/numOfBlocks)) +
                "\n (cast) Block Area: " + blockArea +
                "\n Block Size: " + blockSize);

        //block size and target image width and hight
        this.high = this.wide = blockSize;
        int iNewWidth = this.columns * blockSize;
        int iNewHight = this.rows * blockSize;

        //IMAGE TREATMENT
        //Image wide and narrow dimensiones
        int iWide = image.getWidth();
        int iNarrow = image.getHeight();
        int iOffsetW = 0;
        int iOffsetN = 0;

        //Colums must be the narrow part of the image.
        if(iNarrow > iWide){
            iWide = iNarrow;
            iNarrow = image.getWidth();
        }
        float iRatio = (float)iWide / (float)iNarrow;

        Log.d(TAG, "Original Image Dimension:" +
                "\niRatio: " + iRatio +
                "\n iWide: " + iWide +
                "\n iNarrow: " + iNarrow);


        //Adjust image to fit screen
        if(iRatio > sRatio){
            Log.d(TAG, "Adjust image wide to scree:" +
                    "\niRatio: " + iRatio +" > " + sRatio);
            //Fix image narrow to screen width
            //Cut image wide to match (image narrow * screen Ratio).
            int newIWide = (int) Math.floor(iNarrow * sRatio);
            iOffsetW = (int) Math.floor((iWide - newIWide)/2);
            iWide = newIWide;
        }else{
            Log.d(TAG, "Adjust image narrow:" +
                    "\niRatio: " + iRatio + "<= " + sRatio);
            //Fix image wide to screen width
            //Cut image narrow match (image Wide / Screen Ratio)
            int newINarrow = (int) Math.floor(iWide / sRatio);
            iOffsetN = (int) Math.round((iNarrow-newINarrow)/2);
            iNarrow = newINarrow;
        }

        //Get an image chunk that match the screen proportions.
        Bitmap bm;
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        //Bitmap.createBitmap(image, x, y, wide, high);
        Log.d(TAG, "Image chuck that fits the screen dimensions: "+
                "\nNarrow: " + (iOffsetN + iNarrow-1) +
                "\n Wide: " + (iOffsetW + iWide));
        if(image.getHeight() > image.getWidth()){
            Log.d(TAG, "Image cut orientation:" + "\niWide vs image.Hight: " + (iWide+iOffsetW) + " vs " + image.getHeight() +
                    "\n iNarrow vs image.Width: " + (iNarrow+iOffsetN) + " vs " + image.getWidth());
            bm = Bitmap.createBitmap(this.image, iOffsetN+0, iOffsetW+0, iNarrow+0, iWide+0);
        }else {
            Log.d(TAG, "Image cut orientation:" + "\niWide vs image.Width: " + (iWide+iOffsetW) + " vs " + image.getWidth() +
                    "\n iNarrow vs image.Hight: " + (iNarrow+iOffsetN) + " vs " + image.getHeight());
            bm = Bitmap.createBitmap(this.image,  iOffsetW+0,iOffsetN+0, iWide+0, iNarrow-0);
            bm = Bitmap.createBitmap(this.image, 0,0, bm.getWidth(),bm.getHeight(),matrix, true);
        }
        this.image = bm;

        //Scalate the image based on the screen size and number of the square blocks.
        Bitmap scaledImage = Bitmap.createScaledBitmap(image, iNewWidth, iNewHight, true);
        imageDivider(scaledImage);
    }

    /**
     * Divides the image provided in the number indicated of square blocks based on a specific image High and Wide.
     * @param high
     * @param wide
     */
    public void divideImageInSquares(int high, int wide){
        Log.d(TAG, "divideImageInSquares: ");
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

    public int getBarHight() {
        return barHight;
    }

    public void setBarHight(int barHight) {
        this.barHight = barHight;
    }

    public int getsHight() {
        return sHight;
    }

    public void setsHight(int sHight) {
        this.sHight = sHight;
    }

    public int getsWidth() {
        return sWidth;
    }

    public void setsWidth(int sWidth) {
        this.sWidth = sWidth;
    }
}
