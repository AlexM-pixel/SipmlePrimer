package com.example.mysympleapplication.viewModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mysympleapplication.model.MyRetrofit;
import com.example.mysympleapplication.model.dataWeather.SampleWeather;
import com.example.mysympleapplication.model.WeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLiveData extends MediatorLiveData<Pair<SampleWeather, SampleWeather>> {
    LocationManager locationManager;
    LocationListener locationListener;
    Context context;
    public static final String SPAIN_LAT = "41.23";
    public static final String SPAIN_LON = "2.10";

    public MyLiveData(Context context) {
        this.context = context;
    }

    MutableLiveData<SampleWeather> current_weather = new MutableLiveData<>();
    MutableLiveData<SampleWeather> spain_weather = new MutableLiveData<>();
    WeatherService weatherService;

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        super.onActive();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            startLocationUpdates();
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1, 10, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null && hasActiveObservers()) {
                getCurrentWeather(location);             // пока не получин обновленные данные по локации получаю погоду по последним сохраненным
                getOtherWeather(SPAIN_LAT, SPAIN_LON);    // и параллельно запускаю метод получения погоды в другой стране
            }
        }
        addSource(current_weather, curWeather-> {
                Log.e("AScs","current weather onChanged " + curWeather.getFact().getTemp());
                setValue(Pair.create(curWeather, spain_weather.getValue()));
        });
        addSource(spain_weather, spainWeather -> {
            Log.e("AScs","spain weather onChanged " + spainWeather.getFact().getTemp());
            setValue(Pair.create(current_weather.getValue(), spainWeather));
        });

    }


    private void getCurrentWeather(Location location) {       // получаю местную погоду и заношу значение в первую лайвдату
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        weatherService = MyRetrofit.getInstance().create(WeatherService.class);
        Call<SampleWeather> currentCall = weatherService.getWheatherNow(lat, lon);
        currentCall.enqueue(new Callback<SampleWeather>() {
            @Override
            public void onResponse(Call<SampleWeather> call, Response<SampleWeather> response) {
                SampleWeather weather = response.body();
                if (weather != null) {
                    current_weather.setValue(weather);
                    Log.e("AScs", response.message());
                } else {
                    Log.e("AScs", "current_weather = null" + response.toString());
                }
            }

            @Override
            public void onFailure(Call<SampleWeather> call, Throwable t) {
                Log.e("!!!", "onFailure ," + t.getMessage());
            }
        });
    }

    private void getOtherWeather(String lat, String lon) {      // получаю погоду в Испании и заношу значение во вторую лайвдату
        weatherService = MyRetrofit.getInstance().create(WeatherService.class);   // будет создаваться каждый раз при обновлении локейшена
        Call<SampleWeather> spainCall = weatherService.getWheatherNow(lat, lon);
        spainCall.enqueue(new Callback<SampleWeather>() {
            @Override
            public void onResponse(Call<SampleWeather> call, Response<SampleWeather> response) {
                SampleWeather weather = response.body();
                spain_weather.setValue(weather);
            }

            @Override
            public void onFailure(Call<SampleWeather> call, Throwable t) {
                Log.d("!!!", "onFailure ," + t.getMessage());
            }
        });

    }

    @Override
    protected void onInactive() {
        super.onInactive();
        locationManager.removeUpdates(locationListener);
    }

    private void startLocationUpdates() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {   // при обновлении локейшена будут запускаться два метода по получению погоды
                getCurrentWeather(location);
                getOtherWeather(SPAIN_LAT, SPAIN_LON);
                Toast.makeText(context.getApplicationContext(), "locationChanged", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }
}
