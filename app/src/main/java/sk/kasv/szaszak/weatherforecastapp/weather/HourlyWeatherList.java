package sk.kasv.szaszak.weatherforecastapp.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class HourlyWeatherList {

    private final List<HourlyWeather> hourly;

    public List<HourlyWeather> getHourly() {
        return hourly;
    }

    public HourlyWeatherList(String json) {

        List<HourlyWeather> hourly = new ArrayList<>();
        try {



        } catch (Exception e)
        {
            //
        }

        this.hourly = hourly;
    }
}
