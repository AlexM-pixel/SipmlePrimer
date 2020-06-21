package com.example.mysympleapplication.hw8;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class GeoService extends IntentService {
    WeatherService weatherService;
    public static final String SPAIN_OBJECT="spain";
    public static final String SEND_RESPONSE_BODY = "responseBody";
    String lat;
    String lon;
    public GeoService() {
        super("nameGeoService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Call<SampleWeather> call = weatherService.getWheatherNow(lat, lon);
        try {
            Response<SampleWeather> response= call.execute();
            SampleWeather forecasts = response.body();
            if (forecasts != null) {
                Intent intent1= new Intent(SEND_RESPONSE_BODY);
                intent1.putExtra(SPAIN_OBJECT, (Parcelable) forecasts);
//                textView_Minsk.setText(String.valueOf(forecasts.fact.temp));
//                textView_Ispan.setText(forecasts.fact.condition);
                Log.d("!!!", response.code() + " ," + response.message() + " ," + forecasts.nowTime);
            } else {
//                textView_Minsk.setText("fail!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
