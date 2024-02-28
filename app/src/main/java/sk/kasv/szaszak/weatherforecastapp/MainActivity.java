package sk.kasv.szaszak.weatherforecastapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_SELECT_LOCATION = 100;
    private FusedLocationProviderClient fusedLocationClient;

    private TextView cityTextView;
    private TextView temperatureTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private ImageView iconImageView;

    private LatLng location = new LatLng(0, 0);

    private final Executor executor = Executors.newSingleThreadExecutor();

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

        Button selectLocationButton = findViewById(R.id.selectLocationButton);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();


        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
                mapLauncher.launch(mapIntent);
            }
        });

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).title(getLocationName(latLng))
                        .draggable(true));
                MainActivity.this.location = latLng;
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_LOCATION && resultCode == Activity.RESULT_OK && data != null) {
            LatLng selectedLocation = data.getParcelableExtra("selected_location");
            // Handle the selected location
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
                                location = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                            }
                            loadWeatherData();
                        }
                    });
        }
        else
            loadWeatherData();
    }

    public String getLocationName(LatLng location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
             geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                String country = address.getCountryName();

                return city != null ? city + ", " + country : country;
            }
            else {
                return "Unknown location";
            }
        } catch (IOException e) {
            return "Unknown location";
        }
    }

    public void loadWeatherData() {
        executor.execute(() -> {
            String result = null;
            try {
                result = WeatherService.requestData(location);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String finalResult = result;
            CurrentWeather currentWeather = new CurrentWeather(finalResult, getLocationName(location));
            runOnUiThread(() -> {
                cityTextView.setText(currentWeather.getCity());
                temperatureTextView.setText(String.valueOf(currentWeather.getTemperature()));
                dateTextView.setText(currentWeather.getDate());
                timeTextView.setText(currentWeather.getTime());
                descriptionTextView.setText(currentWeather.getDescription());

                int resourceId = getResources().getIdentifier(currentWeather.getIcon(), "drawable", getPackageName());

// Convert the resource ID to a URI
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);

// Load the image using Picasso
                Picasso.get().load(uri).into(iconImageView);
            });

        });
    }
}