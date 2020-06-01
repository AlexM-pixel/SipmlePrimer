package com.example.mysympleapplication.hw7;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class PhotoService extends Service {
    Bitmap newBitmap;
    private final MyBinder myBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    class MyBinder extends Binder {
        PhotoService getService() {
            return PhotoService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void horizontal(Bitmap bitmap) {
        newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        HandlerThread hthread = new HandlerThread("horizontalThread");
        hthread.start();
        Handler handler = new Handler(hthread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                int pixel;
                int progress = 0;
                int width = newBitmap.getWidth();
                int height = newBitmap.getHeight();
                int[][] massivImage = new int[height][width];            //можно было без массива обойтись
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        pixel = newBitmap.getPixel(i, height - 1 - j);    // по горизонтали отразил
                        massivImage[j][i] = pixel;
                    }
                    Intent updateIntent = new Intent();
                    updateIntent.setAction(PhotoIntentService.ACTION_UPDATE);
                    updateIntent.putExtra(PhotoIntentService.VALUE_UPDATE, progress++);
                    sendBroadcast(updateIntent);

                }
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        newBitmap.setPixel(i, j, massivImage[j][i]);
                    }
                    Intent updateIntent = new Intent();
                    updateIntent.setAction(PhotoIntentService.ACTION_UPDATE);
                    updateIntent.putExtra(PhotoIntentService.VALUE_UPDATE, progress++);
                    sendBroadcast(updateIntent);

                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArrayBitmap = stream.toByteArray();
                Intent intent1 = new Intent(PhotoIntentService.ACTION_MIRROR);
                intent1.putExtra(PhotoIntentService.CHANGED_IMAGE, byteArrayBitmap);
                sendBroadcast(intent1);
            }
        });
    }
}
