package util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class RandomImageSelector {
    //https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media
    public RandomImageSelector(Context context, Executor executor){
        this.context = context;
        this.executor = executor;
    }
    private final String TAG = "RandomImageSelector";
    private Context context;
    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private File folder;
    private File[] files;
    private ArrayList<String> allFiles = new ArrayList<String>();
    private String[] folders = {Environment.DIRECTORY_DCIM};
    private String path;

    private final Executor executor;

    RIS_Callback callback;


    private ArrayList<String> getAllImages(){
        ArrayList<String> imagesPath= new ArrayList<String>();
        for (File f: files
                 ) {
                imagesPath.add(f.getPath());
            }
        return imagesPath;
    }
    public void setCallback(RIS_Callback callback){
        this.callback = callback;
    }

    public ArrayList<String> alternative(){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = context.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
            for (String s: listOfAllImages
                 ) {
                Log.d(TAG, s + "\n");
            }

        }
        return listOfAllImages;
    }

    public String rndImg2(){
        Log.d(TAG, "rndImg2");
        dataCollector();
        int max, sel;
        max = allFiles.size();
        sel = (int) (Math.random()*(max-0));
        path = allFiles.get(sel);
        return path;
    }
    public void rndImg(){
        Log.d(TAG, "rndImg");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                Log.d(TAG, "onPostExecute");
                callback.onReturnImagePath(aVoid);
            }

            @Override
            protected String doInBackground(Void... voids) {
                Log.d(TAG, "doInBackground");
                String value = rndImg2();
                return value;
            }
        };
    }
    public void rndImgAlt() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String rData = rndImg2();
                callback.onReturnImagePath(rData);
            }
        });
    }


    private ArrayList<String> dataCollector(){
        Log.d(TAG, "dataCollector");
        //String[] nFolder = {"/"};
        //this.folders = nFolder;
        ArrayList<String> filesPath = new ArrayList<String>();
        for (String fldr: folders
             ) {
            folder = Environment.getExternalStoragePublicDirectory(fldr);
            //folder = Environment.getExternalStorageDirectory();
            if(folder != null){
                files = folder.listFiles();
                try{
                    for (File f:files
                    ) {
                        if(f.isDirectory()){
                            File subFolderFiles = new File(f.getCanonicalPath());
                            try {
                                filesPath = getDirFiles(subFolderFiles, filesPath);
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                            }
                        }else{
                            try {
                                if(f.getName().contains(".jpg")) {
                                    Log.d(TAG, "\n File: " + f.getCanonicalPath());
                                    filesPath.add(f.getAbsolutePath());
                                }
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        allFiles = filesPath;
        return filesPath;
    }
    public ArrayList<String> getDirFiles(File file, ArrayList<String> nPaths){
        ArrayList<String> paths = new ArrayList<>();
        if(nPaths != null) {
            paths.addAll(nPaths);
        }

        File[] files = file.listFiles();
        for (File f: files
             ) {
            if(f.isDirectory()){
                try{
                    getDirFiles(new File(f.getPath()), paths);
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }else{
                try {
                    if(f.getName().contains(".jpg")){
                        paths.add(f.getAbsolutePath());
                        Log.d(TAG, "\n File: " + f.getCanonicalPath());
                    }
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return paths;
    }
    public interface RIS_Callback{
        void onReturnImagePath(String path);
    }
}


