package com.example.mysympleapplication.hw7;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class SearchService extends Service {
    int cound;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String[] allFiles=intent.getStringArrayExtra("");
        for (String fileName : allFiles) {
//            if (fileName.contains("." + nameFile.getText())) {
//                cound++;
//                Log.d("onTextChanged", "i = " + cound);
//            }
        }
        return null;
    }
}