package sk.kasv.szaszak.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import sk.kasv.szaszak.weatherapp.weather.WeatherData;

public class MainActivity extends AppCompatActivity {

    private TextView cityNameTextView;
    private TextView temperatureTextView;
    private TextView weatherDescriptionTextView;
    private ImageView weatherIconImageView;

    private Executor executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureTextView = findViewById(R.id.temperature);

        executor.execute(() -> {
            String result = null;
            try {
                result = WeatherService.requestData("11.00","-11.11");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String finalResult = result;
            runOnUiThread(() -> temperatureTextView.setText(jsonParser(finalResult) + "°C"));

        });
    }

    public String jsonParser(String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonObject currentObject = jsonObject.get("current").getAsJsonObject();
            return currentObject.get("temp").toString();
        } catch (Exception e)
        {
            return null;
        }
    }
}