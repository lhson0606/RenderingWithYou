package com.dy.app.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static int randomInt(int min, int max) {
        return (int)(Math.random() * (max - min + 1) + min);
    }

    public static String toLocalCapitalize(String str){
        final String parts[] = str.split(" ");
        String result = "";

        for(String part : parts){
            result += part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase() + " ";
        }

        return result;
    }

    public static byte[] serialize(Object obj){
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserialize(byte[] bytes){
        try {
            return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static int long2int(long longValue){
        int intValue;

        try{
            intValue = Math.toIntExact(longValue);
        }catch (ArithmeticException e){
            throw new RuntimeException("long value is too large to convert to int");
        }

        return intValue;

    }

    public static void askForFileLocation(Activity activity, String title, String initialPath, String allowedExtensions, int requestCode){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(initialPath));
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.setType(allowedExtensions);
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        intent.putExtra("android.content.extra.FANCY", true);
        intent.putExtra("android.content.extra.SHOW_FILESIZE", true);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void verifyPermission(Activity activity, String permission, int requestCode){
        if(activity.checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{permission}, requestCode);
        }
    }

    public static void verifyWriteStoragePermission(Activity activity, int requestCode){
        verifyPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode);
    }

    public static void verifyReadStoragePermission(Activity activity, int requestCode){
        verifyPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE, requestCode);
    }

    public static void takeAScreenshot(Activity activity, int requestCode){
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        activity.startActivityForResult(intent, requestCode);
    }

    public static void shareImageMedia(Activity activity, Uri imageUri){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri); // Uri of the screenshot
        activity.startActivity(Intent.createChooser(shareIntent, "gameplay.png"));
    }

    //see https://stackoverflow.com/questions/30196965/how-to-take-a-screenshot-of-a-current-activity-and-then-share-it
    public static void takeCurrentScreenShotAndShare(Activity activity){
        Bitmap bitmap = getScreenShot(activity);
        File screenShotFile = storeBitmap(bitmap, "gameplay.png");
        shareFile(activity, screenShotFile);
    }

    private void shareFileWithType(Activity activity, File file, String type){
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType(type);

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            activity.startActivity(Intent.createChooser(intent, "Share file"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    private static void shareFile(Activity activity, File file){
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            activity.startActivity(Intent.createChooser(intent, "Share file"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap getScreenShot(Activity activity){
        View screenView = activity.getWindow().getDecorView().getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static File storeBitmap(Bitmap bm, String filename){
        File dir = new File(DyConst.dirPath);

        if(!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(dir, filename);
        try{
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }
}
