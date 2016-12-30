package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GraphData {
    private Context mContext;
    private HashMap<Long, Double> graphData = new HashMap<Long, Double>();

    public GraphData(Context context) {
        mContext = context;
    }
    public HashMap<Long, Double> prepare() {
        // Get the measurements from midnight today
        DataBase dataBase = new DataBase(mContext);
        ArrayList<Measurement> measurementList = dataBase.DataBaseGetLastDayMeasurements();

        // Calc calories on the measurement list
        Calories calories = new Calories();

        // Get the MidNightToday and RightNow date values
        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
        long rightNowMillis = rightNow.getTimeInMillis() + offset;
        long sinceMidnightToday = rightNowMillis % (24 * 60 * 60 * 1000);
        long midNightToday = rightNowMillis - sinceMidnightToday;
        rightNowMillis /= 1000; // now in seconds
        midNightToday /= 1000; // now in seconds

        long date = midNightToday;
        Iterator measurementListIterator = measurementList.iterator();
        int hr;
        boolean moveToNextMeasurement = true;
        Measurement measurement = null;
        double caloriesSum = 0;
        // Loop trough all the minutes starting from today midnight
        for ( ; date < rightNowMillis; date += 60) {

            if ((moveToNextMeasurement == true) && measurementListIterator.hasNext()) {
                measurement = (Measurement) measurementListIterator.next();
                moveToNextMeasurement = false;
            }

            int measurementDate = measurement.getDate();
            hr = 0;
            if (measurementDate < (date + 60)) { // means that measurement is in the interval of next minute
                hr = measurement.getHRValue();
                moveToNextMeasurement = true;
            }

            caloriesSum += calories.calcCalories(hr);

            graphData.put(date, caloriesSum);
        }

        return graphData;
    }
}
