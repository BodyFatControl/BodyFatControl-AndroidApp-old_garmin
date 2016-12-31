package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GraphData {
    private Context mContext;
    private HashMap<Long, Double> graphData = new HashMap<Long, Double>();

    public GraphData(Context context) {
        mContext = context;
    }

    public List<Entry> prepareCaloiresActive() {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

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
        midNightToday /= 1000; // now in seconds

        long date = 0;
        long endOfToday = 24*60*60;
        Iterator measurementListIterator = measurementList.iterator();
        int hr;
        boolean moveToNextMeasurement = true;
        Measurement measurement = null;
        double caloriesSum = 0;
        // Loop trough all the minutes starting from today midnight
        for ( ; date < endOfToday; date += 60) {

            if ((moveToNextMeasurement == true) && measurementListIterator.hasNext()) {
                measurement = (Measurement) measurementListIterator.next();
                moveToNextMeasurement = false;
            }midNightToday /= 1000; // now in seconds

            hr = 0;
            if (measurement != null) {
                long measurementDate = (measurement.getDate() - midNightToday);
                if (measurementDate < (date + 60)) { // means that measurement is in the interval of next minute
                    hr = measurement.getHRValue();
                    moveToNextMeasurement = true;
                }
            }

            if (date < rightNowMillis) { //  calc calories only until current date
                caloriesSum += calories.calcActiveCalories(hr);
            }

            graphDataEntriesList.add(new Entry((float) date/(60*60), (float) caloriesSum));
        }

        return graphDataEntriesList;
    }

    public List<Entry> prepareCaloriesTotal() {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

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
        midNightToday /= 1000; // now in seconds
        rightNowMillis /= 1000; // now in seconds
        rightNowMillis -= midNightToday;

        long date = 0;
        long endOfToday = 24*60*60;
        Iterator measurementListIterator = measurementList.iterator();
        int hr;
        boolean moveToNextMeasurement = true;
        Measurement measurement = null;
        double caloriesSum = 0;
        // Loop trough all the minutes starting from today midnight
        for ( ; date < endOfToday; date += 60) {

            if ((moveToNextMeasurement == true) && measurementListIterator.hasNext()) {
                measurement = (Measurement) measurementListIterator.next();
                moveToNextMeasurement = false;
            }

            hr = 0;
            if (measurement != null) {
                long measurementDate = (measurement.getDate() - midNightToday);
                if (measurementDate < (date + 60)) { // means that measurement is in the interval of next minute
                    hr = measurement.getHRValue();
                    moveToNextMeasurement = true;
                }
            }

            if (date < rightNowMillis) { //  calc calories only until current date
                caloriesSum += calories.calcCalories(hr);
            }

            graphDataEntriesList.add(new Entry((float) date/(60*60), (float) caloriesSum));
        }

        return graphDataEntriesList;
    }
}
