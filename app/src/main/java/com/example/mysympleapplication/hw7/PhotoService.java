package com.example.mysympleapplication.hw7;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.ByteArrayOutputStream;

public class PhotoService extends Service {
    Bitmap newBitmap;
    int progress = 0;
    Messenger messanger;
    Messenger toActivityMessenger;
    public static final int SET_COUNT = 3;
    IncomingHandler inHandler;
    HandlerThread hthread;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("1000", "onCreate");
        hthread = new HandlerThread("horizontalThread");
        hthread.start();
        inHandler = new IncomingHandler(hthread.getLooper());
        messanger = new Messenger(inHandler);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("1000", "onBind");
        byte[] byteArray = intent.getByteArrayExtra(PhotoRedactorActivity.BITMAP_IMAGE);        // получил из интента
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        newBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        return messanger.getBinder();
    }

    class IncomingHandler extends Handler {
        IncomingHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("1000", "handle Message");
            toActivityMessenger = msg.replyTo;
            inHandler.post(new Runnable() {
                @Override
                public void run() {
                    horizontal(newBitmap);
                }
            });
        }
    }

    @WorkerThread
    public void horizontal(Bitmap bitmap) {
        Message outMsg;
        Log.d("1000", bitmap.getHeight() + " hoeizontal()");
        newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int pixel;
        int width = newBitmap.getWidth();
        int height = newBitmap.getHeight();
        int[][] massivImage = new int[height][width];            //можно было без массива обойтись
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixel = newBitmap.getPixel(i, height - 1 - j);    // по горизонтали отразил
                massivImage[j][i] = pixel;
            }
            //  посылаю сообщение для progressbar
            outMsg = Message.obtain(inHandler, SET_COUNT);
            outMsg.arg1 = progress++;

            try {
                toActivityMessenger.send(outMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newBitmap.setPixel(i, j, massivImage[j][i]);
            }
            // посылаю сообщение для progressbar
            outMsg = Message.obtain(inHandler, SET_COUNT);
            outMsg.arg1 = progress++;
            try {
                toActivityMessenger.send(outMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArrayBitmap = stream.toByteArray();
        Intent intent1 = new Intent(PhotoIntentService.ACTION_MIRROR);
        intent1.putExtra(PhotoIntentService.CHANGED_IMAGE, byteArrayBitmap);
        sendBroadcast(intent1);
        hthread.quit();
    }
}

