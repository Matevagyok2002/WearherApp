package sk.kasv.szaszak.weatherapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService {

    private static final String BASE_URL = "https://api.openweathermap.org/data/3.0/onecall?";
    private static final String API_KEY = "caf89d59e69d8d4a5a86b9ce806e31cb";

    private OkHttpClient client = new OkHttpClient();

    public static String requestData(String lat, String lon) throws IOException {

        Map<String, String> params = new HashMap<>();
        params.put("lat", lat);
        params.put("lon", lon);
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
