package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class Calories {
    private Context mContext;

    /*
    EER:
    Your EER (Estimated Energy Requirements) are the number of estimated  calories that you burn
    based on your BMR plus calories from a typical  non-exercise day, such as getting ready for
    work, working at a desk job  for 8 hours, and stopping by the store on the way home. EER is
    based on a  formula published by the FDA and used by other government agencies to  estimate the
    calories required by an individual based on their age,  height, weight, and gender. Your EER is
    greater than your BMR since your  BMR only takes into account the calories burned by your body
    just for  it to exist.
    MALE: EER = 864 - 9.72 x age(years) + 1.0 x (14.2 x weight(kg) + 503 x height(meters))
    FEMALE: EER = 387 - 7.31 x age(years) + 1.0 x (10.9 x weight(kg) + 660.7 x height(meters))

    Calories over and including 90 HR:
    This is the Formula when you don't know the VO2max (Maximal oxygen consumption):
    Male:((-55.0969 + (0.6309 x HR) + (0.1988 x W) + (0.2017 x A))/4.184) x 60 x T
    Female:((-20.4022 + (0.4472 x HR) - (0.1263 x W) + (0.074 x A))/4.184) x 60 x T
    HR = Heart rate (in beats/minute)
    W = Weight (in kilograms)
    A = Age (in years)
    T = Exercise duration time (in hours)

    With VO2max known you can calculate the calories burned like this:
﻿   Male:((-95.7735 + (0.634 x HR) + (0.404 x VO2max) + (0.394 x W) + (0.271 x A))/4.184) x 60 x T
    Female:((-59.3954 + (0.45 x HR) + (0.380 x VO2max) + (0.103 x W) + (0.274 x A))/4.184) x 60 x T
    */

    public Calories(Context context) {
        mContext = context;
    }

    public double calcCaloriesEER(UserProfile userProfile) {

        int birthYear = 0;
        int age = 0;
        int gender = 0;
        double height = 0;
        double weight = 0;
        int activityClass = 0;
        if (userProfile != null) {
            birthYear = userProfile.getUserBirthYear();
            age = (Calendar.getInstance().get(Calendar.YEAR)) - birthYear;
            gender = userProfile.getUserGender();
            height = (double) userProfile.getUserHeight();
            weight = (double) userProfile.getUserWeight();
            activityClass = userProfile.getUserActivityClass();
        }

        double calories;
        if (gender == 0) { // female
            calories = ((387 - (7.31*age) + (1.0*(10.9*weight/1000)) +
                    (660.7*height/100))); // daily value
        } else { // male
            calories = ((864 - (9.72*age) + (1.0*(14.2*weight/1000)) +
                    (503*height/100)));
        }

        return calories;
    }

    public double calcCaloriesEER(long initialDate, long finalDate, UserProfile userProfile) {

        int birthYear = 0;
        int age = 0;
        int gender = 0;
        double height = 0;
        double weight = 0;
        int activityClass = 0;
        if (userProfile != null) {
            birthYear = userProfile.getUserBirthYear();
            age = (Calendar.getInstance().get(Calendar.YEAR)) - birthYear;
            gender = userProfile.getUserGender();
            height = (double) userProfile.getUserHeight();
            weight = (double) userProfile.getUserWeight();
            activityClass = userProfile.getUserActivityClass();
        }

        double calories;
        if (gender == 0) { // female
            calories = ((387 - (7.31*age) + (1.0*(10.9*weight/1000)) +
                    (660.7*height/100))); // daily value
            double temp = (finalDate - initialDate);
            temp = temp / MainActivity.SECONDS_24H;
            calories = calories * temp;

        } else { // male
            calories = ((864 - (9.72*age) + (1.0*(14.2*weight/1000)) +
                    (503*height/100)));
            double temp = (finalDate - initialDate);
            temp = temp / MainActivity.SECONDS_24H;
            calories = calories * temp;
        }

        return calories;
    }

    public double calcActiveCalories(int hr_value, UserProfile userProfile) {

        int birthYear = 0;
        int age = 0;
        int gender = 0;
        double height = 0;
        double weight = 0;
        int activityClass = 0;
        if (userProfile != null) {
            birthYear = userProfile.getUserBirthYear();
            age = (Calendar.getInstance().get(Calendar.YEAR)) - birthYear;
            gender = userProfile.getUserGender();
            height = (double) userProfile.getUserHeight();
            weight = (double) userProfile.getUserWeight();
            activityClass = userProfile.getUserActivityClass();
        }

        double hr = (double) hr_value;
        double calories;
        if (hr >= 90 && hr < 255) { // calculation based on formula without VO2max
            if (gender == 0) { // female
                calories = (-20.4022 + (0.4472*hr) - (0.1263*weight/1000) +
                        (0.074*age));
                calories = calories / 4.184;

            } else { // male
                calories = (-55.0969 + (0.6309*hr) + (0.1988*weight/1000) +
                        (0.2017*age));
                calories = calories / 4.184;
            }
        } else { // here, calculation based on Estimated Energy Requirements
            calories = 0;
        }

        return calories;
    }
}
