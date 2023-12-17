package com.dy.app.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.dy.app.activity.MultiPlayerOnSameDeviceActivity;
import com.dy.app.gameplay.screenshot.ITakeScreenshot;

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
        //convert uri to file provider uri
        Uri pUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", new File(imageUri.getPath()));
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, pUri); // Uri of the screenshot
        activity.startActivity(Intent.createChooser(shareIntent, "gameplay.png"));
    }

    public static Bitmap getScreenShot(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //https://stackoverflow.com/questions/30789590/take-screenshot-from-layout-and-share-via-android
    public static void shareContent(Activity activity) {
        final Bitmap bitmap = getScreenShot(activity.getWindow().getDecorView().getRootView());
        String bitmapPath = MediaStore.Images.Media.insertImage(
                activity.getContentResolver(), bitmap, "result screenshot", "");
        Uri uri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Currently a new version of KiKi app is available.");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    public static void shareScreenShot(Activity activity, ITakeScreenshot driver, String title){
        final Bitmap bitmap = driver.getScreenshot();
        String bitmapPath = MediaStore.Images.Media.insertImage(
                activity.getContentResolver(), bitmap, title, "");
        //we now can remove the bitmap from memory
        bitmap.recycle();
        //process the uri
        Uri uri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "screenshot");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(shareIntent, title));
    }

    public static void shareBitmap(Activity activity, Bitmap bitmap){
        String bitmapPath = MediaStore.Images.Media.insertImage(
                activity.getContentResolver(), bitmap, "result screenshot", "");
        Uri uri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    public static Bitmap mergeBitmapCenter(Bitmap... bitmaps){
        int width = 0;
        int height = 0;

        //find max width and height
        for(Bitmap bitmap : bitmaps){
            width = Math.max(width, bitmap.getWidth());
            height = Math.max(height, bitmap.getHeight());
        }

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        for(Bitmap bitmap : bitmaps){
            int xOff = (width - bitmap.getWidth()) / 2;
            int yOff = (height - bitmap.getHeight()) / 2;
            canvas.drawBitmap(bitmap, xOff, yOff, null);
        }

        return result;
    }

    public static String getCurrentDate() {
        return java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
    }

    public static void shareFile(Activity activity, Uri currentSavedFileUri, String type) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(type);
        shareIntent.putExtra(Intent.EXTRA_STREAM, currentSavedFileUri);
        activity.startActivity(Intent.createChooser(shareIntent, "Share your PGN using ..."));
    }

    public static void openFile(Activity activity, String type, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type);
        activity.startActivityForResult(intent, requestCode);
    }

    public static String getTitle(long elo){
        if(elo < 1000){
            return "Beginner";
        }else if(elo < 1200){
            return "Novice";
        }else if(elo < 1400){
            return "Intermediate";
        }else if(elo < 1600){
            return "Advanced";
        }else if(elo < 1800){
            return "Expert";
        }else if(elo < 2000){
            return "Master";
        }else if(elo < 2200){
            return "Grandmaster";
        }else if(elo < 2400){
            return "International Master";
        }else if(elo < 2600){
            return "Grandmaster";
        }else {
            return "Super Grandmaster";
        }
    }

    public static String getWinRateDisplay(long win, long total){
        if(total == 0) {
            return "n/a";
        }else{
            return String.format("%.2f", (float)win/total * 100) + "%";
        }
    }

    public static String getAlphabetRating(long x, long max){
        float value = (float)x / max;
        if(value < 0.1){
            return "F";
        }else if(value < 0.2){
            return "E";
        }else if (value < 0.3){
            return "D";
        }else if (value < 0.4){
            return "C";
        }else if(value < 0.5){
            return "B";
        }else if(value < 0.6){
            return "A";
        }else if(value < 0.7){
            return "A+";
        }else if(value < 0.8){
            return "A++";
        }else if(value < 0.9){
            return "S";
        }else if(value>=1){
            return "SS";
        }else {
            return "S+";
        }
    }

    public static String getDate(Long aLong) {
        return java.text.DateFormat.getDateTimeInstance().format(new java.util.Date(aLong));
    }
}
