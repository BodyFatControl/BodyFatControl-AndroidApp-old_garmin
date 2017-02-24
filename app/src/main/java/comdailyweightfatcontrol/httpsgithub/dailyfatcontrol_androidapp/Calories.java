package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class Calories {
    private static int EERCalories = 0;
    private static int EERCaloriesPerMinute = 0;

    public Calories() {
    }

    public int getEERCaloriesPerMinute() {
        return EERCaloriesPerMinute;
    }

    public void setEERCaloriesPerMinute(int EERCaloriesPerMinute) {
        this.EERCaloriesPerMinute = EERCaloriesPerMinute;
    }

    public static int getEERCalories() {
        return EERCalories;
    }

    public static void setEERCalories(int EERCalories) {
        Calories.EERCalories = EERCalories;
    }
}
