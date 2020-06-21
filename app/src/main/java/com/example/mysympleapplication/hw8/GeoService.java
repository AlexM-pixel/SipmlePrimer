package com.example.mysympleapplication.hw8;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Serializable;

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
        lat=intent.getStringExtra(Main8Activity.LOCATION_LAT);
        lon=intent.getStringExtra(Main8Activity.LOCATION_LON);
        weatherService = MyRetrofit.getInstance().create(WeatherService.class);
        Call<SampleWeather> call = weatherService.getWheatherNow(lat, lon);
        try {
            Response<SampleWeather> response= call.execute();
            SampleWeather forecasts = response.body();
            if (forecasts != null) {
                Intent intent1= new Intent(SEND_RESPONSE_BODY);
                intent1.putExtra(SPAIN_OBJECT,forecasts);
                Log.d("!!!", response.code() + " ," + response.message() + " ," + forecasts.nowTime);
                sendBroadcast(intent1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
