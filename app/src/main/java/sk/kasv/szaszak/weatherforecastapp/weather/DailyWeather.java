package sk.kasv.szaszak.weatherforecastapp.weather;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Date;

import sk.kasv.szaszak.weatherforecastapp.Util;

public class DailyWeather implements Serializable {

    private String date;
    private double temperature;
    private String summary;
    private String icon;

    public DailyWeather(JsonObject dailyObject) {

        try {

            JsonObject iconObject = dailyObject.get("weather").getAsJsonArray().get(0).getAsJsonObject();
            Date dt = new Date(dailyObject.get("dt").getAsLong() * 1000);

            this.date = Util.getDate(dt);
            this.temperature = Math.round(calcAverageTemp(dailyObject.get("temp").getAsJsonObject()));
            this.summary = dailyObject.get("summary").getAsString().replace("\"", "");
            this.icon = "large" + iconObject.get("icon").toString().replace("\"", "");

        } catch (Exception e)
        {
            //
        }
    }

    public String getDate() {
        return date;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }

    public String getSummary() {
        return summary;
    }

    public double calcAverageTemp(JsonObject tempObject) {

        double temp = 0;

        try {

            temp += tempObject.get("day").getAsDouble();
            temp += tempObject.get("night").getAsDouble();
            temp += tempObject.get("eve").getAsDouble();
            temp += tempObject.get("morn").getAsDouble();

        } catch (Exception e)
        {
            //
        }

        return temp / 4;
    }
}
