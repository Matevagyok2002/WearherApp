package sk.kasv.szaszak.weatherforecastapp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Date;

public class CurrentWeather {

    private String city;
    private String date;
    private String time;
    private double temperature;
    private String description;
    private String icon;

    public CurrentWeather(String json, String city) {

        try {

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonObject currentObject = jsonObject.get("current").getAsJsonObject();
            JsonObject iconObject = currentObject.get("weather").getAsJsonArray().get(0).getAsJsonObject();
            Date dt = new Date(currentObject.get("dt").getAsLong() * 1000);
            String description = iconObject.get("description").toString().replace("\"", "");

            this.city = city;
            this.date = Util.getDate(dt);
            this.time = Util.getTime(dt);
            this.temperature = Math.round(currentObject.get("temp").getAsDouble());
            this.description = description.substring(0,1).toUpperCase() + description.substring(1);
            this.icon = "large" + iconObject.get("icon").toString().replace("\"", "");

        } catch (Exception e)
        {
            //
        }
    }

    public String getCity() {
        return city;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }
    public String getDescription() {return description;}

    public String getIcon() {
        return icon;
    }
}
