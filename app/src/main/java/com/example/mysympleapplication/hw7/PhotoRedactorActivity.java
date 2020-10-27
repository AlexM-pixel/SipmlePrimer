package com.example.mysympleapplication.hw7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.mysympleapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static java.lang.StrictMath.round;

public class PhotoRedactorActivity extends AppCompatActivity {
    ServiceConnection sConn;
    private boolean bound;
    private ImageView imageView;
    private final int Pick_image = 1;
    Bitmap bitmapImage;
    Bitmap newBitmap;
    ProgressBar progressBar;
    PhotoChangerReceiver receiver;
    UpdatetReceiver updatetReceiver;
    public static final String BITMAP_IMAGE = "image";
    final Messenger messenger = new Messenger(new IncomingHandler());
    Messenger toServiceMessenger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_redactor);
        imageView = findViewById(R.id.imagePhotoPick);
        progressBar = findViewById(R.id.progressBar);
        receiver = new PhotoChangerReceiver();
        updatetReceiver = new UpdatetReceiver();
        registerReceiver(receiver, new IntentFilter(PhotoIntentService.ACTION_MIRROR));
        registerReceiver(updatetReceiver, new IntentFilter(PhotoIntentService.ACTION_UPDATE));
        Button buttonChangeImage = findViewById(R.id.buttonImege);
        sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("1000", "MainActivity onServiceConnected");
                bound = true;
                toServiceMessenger = new Messenger(service);
                Message msg = Message.obtain(null, PhotoService.SET_COUNT);
                msg.replyTo = messenger;
                msg.arg1 = 0; //наш счетчик
                try {
                    toServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("1000", " onServiceDisconnected");
                bound = false;
            }
        };
        buttonChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Pick_image);
            }
        });
        Button buttontransform = findViewById(R.id.buttonHorizontal);
        buttontransform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeHorizontalChange();
            }
        });
        Button buttonMirror = findViewById(R.id.buttonMirror);
        buttonMirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mirrorsEffect();
            }
        });
    }

    //Обрабатываем результат выбора в галерее:

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == Pick_image && resultCode == RESULT_OK) {
            try {
                //Получаем URI изображения, преобразуем его в Bitmap
                //объект и отображаем в элементе ImageView нашего интерфейса:
                final Uri imageUri = imageReturnedIntent.getData();
                if (imageUri != null) {
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    bitmapImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(bitmapImage);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void mirrorsEffect() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        newBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        newBitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Intent intent = new Intent(this, PhotoIntentService.class);
        intent.putExtra(BITMAP_IMAGE, byteArray);
        progressBar.setProgress(0);
        progressBar.setMax(newBitmap.getHeight());
        progressBar.setVisibility(ProgressBar.VISIBLE);
        startService(intent);
        Log.d("1000", newBitmap.getHeight() + "method_mirrorsEffect()");
    }

    public void makeHorizontalChange() {
        newBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Intent intent = new Intent(this, PhotoService.class);
        intent.putExtra(BITMAP_IMAGE, byteArray);
        bindService(intent, sConn, Context.BIND_AUTO_CREATE);
        Log.d("1000", "method_Horizontal()");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        progressBar.setProgress(0);
        progressBar.setMax(newBitmap.getWidth() * 2);
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    class PhotoChangerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] byteArray = intent.getByteArrayExtra(PhotoIntentService.CHANGED_IMAGE);
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);        // получил из интента
            imageView.setImageBitmap(bmp);
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (bound) {
                unbindService(sConn);
                bound = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(updatetReceiver);
    }

    public class UpdatetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(PhotoIntentService.VALUE_UPDATE, 0);
            progressBar.setProgress(progress);
        }
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == PhotoService.SET_COUNT) {
                int progress = msg.arg1;
                progressBar.setProgress(progress);
            }
            Log.d("1000", "UpdateHandler" + msg.arg1);
        }
    }
}

