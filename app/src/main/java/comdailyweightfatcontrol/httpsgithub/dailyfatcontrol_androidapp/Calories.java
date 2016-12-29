package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class Calories {
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

    public final int VIVOACTIVE_HR_SAMPLE_PERIOD = 95; // in seconds
    private final double VIVOACTIVE_HR_DT = 95.0/(60.0*60.0); // hour

    private Context mContext;

    public Calories(Context context) {
        mContext = context;
    }

    public ArrayList<Measurement> calcCalories (ArrayList<Measurement> measurementList) {

        Measurement lastMeasurement = new Measurement();
        DataBase dataBase = new DataBase(mContext);
        lastMeasurement = dataBase.DataBaseGetLastMeasurement();

        // loop through all the measurements
        for (Measurement measurement : measurementList) {

            // calc calories value for current measurement
            int hr = measurement.getHRValue();
            int calories;
            SharedPreferences mPrefs = MainActivity.getPrefs();
            int birthYear = mPrefs.getInt("BIRTH_YEAR", 0);
            if (birthYear == 0) return measurementList; // return due to wrong value
            int age = (Calendar.getInstance().get(Calendar.YEAR)) - birthYear;
            int gender = mPrefs.getInt("GENDER", 0);
            int height = mPrefs.getInt("HEIGHT", 0);
            if (height == 0) return measurementList; // return due to wrong value
            int weight = mPrefs.getInt("WEIGHT", 0);
            if (weight == 0) return measurementList; // return due to wrong value
            int activityClass = mPrefs.getInt("ACTIVITY_CLASS", 0);

            if (hr >= 90 && hr < 255) { // calculation based on formula without VO2max
                if (gender == 0) { // female
                    calories = (int) ((((-20.4022 + (0.4472*hr) - (0.1988*weight/1000) +
                            (0.2017*height/100)) / 4.184) * 60*VIVOACTIVE_HR_DT) * 1000);

                } else { // male
                    calories = (int) ((((-55.0969 + (0.6309*hr) - (0.1263*weight/1000) +
                            (0.074*height/100)) / 4.184) * 60*VIVOACTIVE_HR_DT) * 1000);
                }

            } else { // calculation based on Estimated Energy Requirements
                if (gender == 0) { // female
                    calories = (int) (((387 - (7.31*age) + (1.0*(10.9*weight/1000)) +
                            (660.7*height/100))) * 1000); // daily value
                    calories = (int) ((calories/24)*VIVOACTIVE_HR_DT); // value in VIVOACTIVE_HR_DT
                } else { // male
                    calories = (int) (((864 - (9.72*age) + (1.0*(14.2*weight/1000)) +
                            (503*height/100))) * 1000);
                    calories = (int) ((calories/24)*VIVOACTIVE_HR_DT);
                }
            }

            // Now verify if current measure date is after midnight
            // if is the first measure after midnight, do not sum otherwise do the sum
            Calendar rightNow = Calendar.getInstance();
            long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
            long sinceMidnightToday = (rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
            long midNightToday = (rightNow.getTimeInMillis() + offset) - sinceMidnightToday;
            midNightToday /= 1000; // value in milliseconds
            if ((lastMeasurement.getDate() - midNightToday) > VIVOACTIVE_HR_SAMPLE_PERIOD) {
                measurement.setCaloriesOutSum(calories + lastMeasurement.getCaloriesOutSum());
            }

            // now set all the values on the measurement
            measurement.setCaloriesOut(calories);
            measurement.setIsManualCalories(0);
            measurement.setUserBirthYear(birthYear);
            measurement.setUserGender(gender);
            measurement.setUserHeight(height);
            measurement.setUserWeight(weight);
            measurement.setUserWeight(weight);
            measurement.setUserActivityClass(activityClass);

            lastMeasurement = measurement; // we need to keep the last measurement calories to add to next one
        }

        return measurementList;
    }
}
