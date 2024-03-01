package sk.kasv.szaszak.weatherforecastapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import sk.kasv.szaszak.weatherforecastapp.weather.CurrentWeather;
import sk.kasv.szaszak.weatherforecastapp.weather.WeatherForecast;
import sk.kasv.szaszak.weatherforecastapp.weather.WeatherService;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private GestureDetector gestureDetector;

    private TextView cityTextView;
    private TextView temperatureTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private ImageView iconImageView;

    private boolean foundExactLocation = true;
    private LatLng location;
    private WeatherForecast weatherForecast;
    private Geocoder geocoder;

    private final ActivityResultLauncher<Intent> mapLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                location = data.getParcelableExtra("selected_location");
                loadWeatherData();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTextView = findViewById(R.id.city);
        dateTextView = findViewById(R.id.date);
        timeTextView = findViewById(R.id.time);
        temperatureTextView = findViewById(R.id.temperature);
        descriptionTextView = findViewById(R.id.description);
        iconImageView = findViewById(R.id.icon);

        findViewById(R.id.selectLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("location", location);
                mapLauncher.launch(intent);
            }
        });

        findViewById(R.id.showHourlyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HourlyActivity.class);
                intent.putExtra("hourlyWeatherList", (Serializable) weatherForecast.getHourlyWeather());
                startActivity(intent);
            }
        });

        findViewById(R.id.showDailyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DailyActivity.class);
                intent.putExtra("dailyWeatherList", (Serializable) weatherForecast.getDailyWeather());
                startActivity(intent);
            }
        });

        geocoder = new Geocoder(this, Locale.getDefault());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                getLocationPermission();
                return true;
            }
        });

        findViewById(R.id.mainCardView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            setLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            setLocation();
        }
    }

    public void setLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location userLocation) {
                            if (userLocation != null) {
                                foundExactLocation = true;
                                location = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                            }
                            else {
                                location = getLocationByLocale();
                            }
                            loadWeatherData();
                        }
                    });
        }
        else {
            location = getLocationByLocale();
            loadWeatherData();
        }
    }

    public String getLocationName(LatLng location) {
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
             geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                String country = address.getCountryName();

                if (foundExactLocation)
                    return city != null ? city + ", " + country : country;
                else
                    return country;
            }
            else {
                return "Unknown location";
            }
        } catch (IOException e) {
            return "Unknown location";
        }
    }

    public LatLng getLocationByLocale() {
        foundExactLocation = false;
        LocaleList currentLocales = getResources().getConfiguration().getLocales();
        String locationName = currentLocales.get(0).getDisplayCountry();

        try {

            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
            else {
                return new LatLng(0, 0);
            }
        } catch (IOException e) {
            return new LatLng(0, 0);
        }
    }

    public void loadWeatherData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            String result = null;
            try {
                result = WeatherService.requestData(location);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String finalResult = result;
            weatherForecast = new WeatherForecast(finalResult, getLocationName(location));
            foundExactLocation = true;
            CurrentWeather currentWeather = weatherForecast.getCurrentWeather();
            runOnUiThread(() -> {
                cityTextView.setText(weatherForecast.getLocation());
                temperatureTextView.setText(String.valueOf(currentWeather.getTemperature()));
                dateTextView.setText(currentWeather.getDate());
                timeTextView.setText(currentWeather.getTime());
                descriptionTextView.setText(currentWeather.getDescription());

                int resourceId = getResources().getIdentifier(currentWeather.getIcon(), "drawable", getPackageName());
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
                Picasso.get().load(uri).into(iconImageView);
            });

        });
    }

}