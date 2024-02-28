package sk.kasv.szaszak.weatherforecastapp;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class WeatherService {

    private static final String BASE_URL = "https://api.openweathermap.org/data/3.0/onecall?";
    private static final String API_KEY = "caf89d59e69d8d4a5a86b9ce806e31cb";

    private OkHttpClient client = new OkHttpClient();

    public static String requestData(LatLng location) throws IOException {

        Map<String, String> params = new HashMap<>();
        params.put("lat", String.valueOf(location.latitude));
        params.put("lon", String.valueOf(location.longitude));
        params.put("units", "metric");
        params.put("appid", API_KEY);

        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(urlBuilder.toString()).build();

            return client.newCall(request).execute().body().string();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
