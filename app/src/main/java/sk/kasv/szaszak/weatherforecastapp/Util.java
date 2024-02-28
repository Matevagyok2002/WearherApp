package sk.kasv.szaszak.weatherforecastapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static String getDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        return formatter.format(date);
    }

    public static String getTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return formatter.format(date);
    }
}
