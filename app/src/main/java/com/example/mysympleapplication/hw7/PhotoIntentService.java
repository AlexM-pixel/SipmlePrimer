package com.example.mysympleapplication.hw7;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class PhotoIntentService extends IntentService {
    public static final String ACTION_MIRROR ="action_mirror";
    public static final String CHANGED_IMAGE="mirror_image";
    public static final String ACTION_UPDATE = "update";
    public static final String VALUE_UPDATE = "value_apdate";

    public PhotoIntentService() {
        super("name");
        Log.d("handle", " name constructor");
    }

    public void onCreate() {
        super.onCreate();
        Log.d("handle", "onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("handle", "onHandleIntent ");
        byte[] byteArray = intent.getByteArrayExtra(PhotoRedactorActivity.BITMAP_IMAGE);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);        // получил из интента
        Bitmap newBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);                     // сделал изменяемую копию
        Toast.makeText(getApplicationContext(), newBitmap.getHeight() + "111", Toast.LENGTH_SHORT).show();
        Intent intent1 = new Intent(ACTION_MIRROR);
        int pixel = 0;
        int width = newBitmap.getWidth();
        int height = newBitmap.getHeight();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width / 2; j++) {                         // а здесь зеркально отразил
                pixel = newBitmap.getPixel(width - 1 - j, i);
                newBitmap.setPixel(j, i, pixel);
            }
            Intent updateIntent = new Intent();
            updateIntent.setAction(ACTION_UPDATE);
           // updateIntent.addCategory(Intent.CATEGORY_DEFAULT);
            updateIntent.putExtra(VALUE_UPDATE, i);
            sendBroadcast(updateIntent);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArrayBitmap = stream.toByteArray();
        intent1.putExtra(CHANGED_IMAGE, byteArrayBitmap);
        sendBroadcast(intent1);
    }


}
