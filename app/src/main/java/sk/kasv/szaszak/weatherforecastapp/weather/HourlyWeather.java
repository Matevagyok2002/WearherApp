package sk.kasv.szaszak.weatherforecastapp.weather;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Date;

import sk.kasv.szaszak.weatherforecastapp.Util;

public class HourlyWeather implements Serializable {

    private String time;
    private double temperature;
    private String icon;

    public HourlyWeather(JsonObject hourlyObject) {

        try {

            JsonObject iconObject = hourlyObject.get("weather").getAsJsonArray().get(0).getAsJsonObject();
            Date dt = new Date(hourlyObject.get("dt").getAsLong() * 1000);

            this.time = Util.getTime(dt);
            this.temperature = Math.round(hourlyObject.get("temp").getAsDouble());
            this.icon = "large" + iconObject.get("icon").toString().replace("\"", "");

        } catch (Exception e)
        {
            //
        }
    }

    public String getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }
}
