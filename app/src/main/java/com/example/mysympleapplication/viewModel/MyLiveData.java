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

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.mysympleapplication.model.MyRetrofit;
import com.example.mysympleapplication.model.SampleWeather;
import com.example.mysympleapplication.model.WeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLiveData extends LiveData<Integer> {
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
        if (current_weather.getValue() != null && spain_weather.getValue() != null) {
            MediatorLiveData<Pair<SampleWeather, SampleWeather>> pairMediatorLiveData = new MediatorLiveData<>();
            pairMediatorLiveData.addSource(current_weather, currentValue -> pairMediatorLiveData.setValue(Pair.create(currentValue, spain_weather.getValue())));
            pairMediatorLiveData.addSource(spain_weather, spainValue -> pairMediatorLiveData.setValue(Pair.create(current_weather.getValue(), spainValue)));
            Transformations.switchMap(pairMediatorLiveData, temp -> getTemp(temp.first.fact.temp, temp.second.fact.temp));
        }
    }

    private LiveData<Integer> getTemp(int currentTemp, int spainTemp) {
        LiveData<Integer> liveData = new LiveData<Integer>() {
            @Override
            protected void setValue(Integer value) {
                super.setValue(Math.max(currentTemp, spainTemp));
            }
        };

        return liveData;
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
                current_weather.setValue(weather);
                Log.e("AScs", "onFailure ," + weather.fact.temp);
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
