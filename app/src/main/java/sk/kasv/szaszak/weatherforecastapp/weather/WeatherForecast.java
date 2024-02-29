package sk.kasv.szaszak.weatherforecastapp.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class WeatherForecast {

    private final String location;
    private final CurrentWeather currentWeather;
    private final List<HourlyWeather> hourlyWeather;
    private final List<DailyWeather> dailyWeather;


    public WeatherForecast(String jsonString, String location) {

        List<HourlyWeather> hourlyWeather = new ArrayList<>();
        List<DailyWeather> dailyWeather = new ArrayList<>();
        CurrentWeather currentWeather = null;

        try {

            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            JsonObject currentObject = jsonObject.get("current").getAsJsonObject();
            JsonArray hourlyArray = jsonObject.get("hourly").getAsJsonArray();
            JsonArray dailyArray = jsonObject.get("daily").getAsJsonArray();

            currentWeather = new CurrentWeather(currentObject);

            for (int i = 0; i < 24; i++) {
                hourlyWeather.add(new HourlyWeather(hourlyArray.get(i).getAsJsonObject()));
            }

            for (int i = 0; i < 7; i++) {
                dailyWeather.add(new DailyWeather(dailyArray.get(i).getAsJsonObject()));
            }

        } catch (Exception e) {
            //
        }

        this.location = location;
        this.currentWeather = currentWeather;
        this.hourlyWeather = hourlyWeather;
        this.dailyWeather = dailyWeather;
    }

    public String getLocation() {
        return location;
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public List<DailyWeather> getDailyWeather() {
        return dailyWeather;
    }

    public List<HourlyWeather> getHourlyWeather() {
        return hourlyWeather;
    }
}
