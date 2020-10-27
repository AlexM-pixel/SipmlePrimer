package com.example.mysympleapplication.hw8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysympleapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Main8Activity extends AppCompatActivity {
    TextView textView_Minsk;
    TextView textView_Ispan;
    SampleWeather spain;
    SampleWeather minsk;
    LocationManager locationManager;
    Location myLocation;
    WeatherService weatherService;
    WeatherSpainReceiver weatherSpainReceiver;
    List<Address> addressList;
    Button buttonSv;
    public static final int RUN_PERMISSION_REQUEST = 125;
    public static final String LOCATION_LAT = "lat_location";
    public static final String LOCATION_LON = "lon_location";
    boolean minskGet;
    boolean getGeocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        weatherService = MyRetrofit.getInstance().create(WeatherService.class);
        textView_Minsk = findViewById(R.id.textView_Minsk);
        textView_Ispan = findViewById(R.id.textView_Ispan);
        buttonSv = findViewById(R.id.button_svalivator);
        Button button_UpdateWeather = findViewById(R.id.button_UpdateWither);
        button_UpdateWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Main8Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(Main8Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main8Activity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, RUN_PERMISSION_REQUEST);
                    Log.d("!!!", "checkSelfPermission");
                    return;
                }
                myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Toast.makeText(Main8Activity.this, "onClick", Toast.LENGTH_SHORT).show();
                if (myLocation != null) {
                    textView_Minsk.setText("ждем...");
                    Toast.makeText(Main8Activity.this, "myLocation != null", Toast.LENGTH_SHORT).show();
                    final String lat = String.valueOf(myLocation.getLatitude());
                    final String lon = String.valueOf(myLocation.getLongitude());
                    getWeather(lat, lon);
                    ExecutorService service = Executors.newFixedThreadPool(1);
                    service.submit(new Runnable() {
                        @Override
                        public void run() {
                            Geocoder geocoder = new Geocoder(Main8Activity.this);
                            try {
                                Log.d("!!!", "geocoder_RUN");
                                addressList = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    service.shutdown();
                    try {
                        getGeocoder=  service.awaitTermination(2,TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intentSpain = new Intent(Main8Activity.this, GeoService.class);
                    intentSpain.putExtra(LOCATION_LAT, "41.23");
                    intentSpain.putExtra(LOCATION_LON, "2.10");
                    startService(intentSpain);
                }
                //   String lat = "53.89079";
                //  String lon = "27.525773";
            }
        });
        if (ActivityCompat.checkSelfPermission(Main8Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Main8Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 10, locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            myLocation = location;
            Log.d("!!!", "onLocationChanged");
            Toast.makeText(Main8Activity.this, "onLocationChanged", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onStart() {
        super.onStart();
        weatherSpainReceiver = new WeatherSpainReceiver();
        registerReceiver(weatherSpainReceiver, new IntentFilter(GeoService.SEND_RESPONSE_BODY));
    }


    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);             // без этого метода продолжает работать при выходе из приложения
        if (weatherSpainReceiver != null) {
            unregisterReceiver(weatherSpainReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RUN_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                Log.d("!!!", "onRequestPermissionResult");
                Toast.makeText(this, "thank you", Toast.LENGTH_SHORT).show();
            } else {
                // permission denied
            }
        }
    }

    public void getWeather(String lat, String lon) {
        Call<SampleWeather> call = weatherService.getWheatherNow(lat, lon);
        call.enqueue(new Callback<SampleWeather>() {
            @Override
            public void onResponse(Call<SampleWeather> call, Response<SampleWeather> response) {
                minsk = response.body();
                if (minsk != null) {
                    minskGet = true;
                    Log.d("!!!", response.code() + " ," + response.message() + " ," + minsk.nowTime);
                } else {
                    Toast.makeText(Main8Activity.this, response.code() + " !!!", Toast.LENGTH_SHORT).show();
                    textView_Minsk.setText("fail!");
                }

            }

            @Override
            public void onFailure(@NonNull Call<SampleWeather> call, @NonNull Throwable t) {
                Log.d("!!!", "onFailure ," + t.getMessage());

            }
        });
    }


    public void startingSvalivator() {
        Uri uri = Uri.parse("yandexnavi://build_route_on_map?lat_to=41.3926&lon_to=2.14445");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("ru.yandex.yandexnavi");
// Проверяет, установлено ли приложение.
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
//Запускает Яндекс.Навигатор.
            startActivity(intent);
        } else {
// Открывает страницу Яндекс.Навигатора в Google Play.
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
            startActivity(intent);
        }
    }

    class WeatherSpainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            spain = (SampleWeather) intent.getSerializableExtra(GeoService.SPAIN_OBJECT);
            comparisonTemp();
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void comparisonTemp() {
       do {
           Log.d("!!!", "do " + " , " + getGeocoder);
           if (minskGet && addressList != null) {
               Toast.makeText(this, "if " + getGeocoder + "  , " + minskGet, Toast.LENGTH_SHORT).show();
               // textView_Minsk.setText("в Минске сейчас: " + minsk.fact.temp + " градусов");
                textView_Minsk.setText("в " + addressList.get(0).getCountryName() + " " + addressList.get(0).getAdminArea() + " " + minsk.fact.temp + " градусов");
                textView_Ispan.setText("а в Испании сейчас: " + spain.fact.temp + " градусов");
                Log.d("!!!", "minskGet && addressList != null" + " , " + getGeocoder);
                if (spain.fact.temp < 32 && minsk.fact.temp < 20) {
                    buttonSv.setVisibility(View.VISIBLE);
                    buttonSv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startingSvalivator();
                        }
                    });
                }
            }
        } while (!getGeocoder);
    }
}
