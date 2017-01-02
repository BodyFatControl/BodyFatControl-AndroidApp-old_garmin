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

    public List<Entry> prepareCaloriesActive(long initialDate, long finalDate) {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Get the measurements from midnight today
        DataBase dataBase = new DataBase(mContext);
        ArrayList<Measurement> measurementList = dataBase.DataBaseGetMeasurements(initialDate, finalDate);

        // Calc calories on the measurement list
        Calories calories = new Calories();

        long date = 0;
        long endOfToday = MainActivity.SECONDS_24H - 1;
        long graphFinalDate = finalDate - initialDate;
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
                long measurementDate = (measurement.getDate() - initialDate);
                if (measurementDate < (date + 60)) { // means that measurement is in the interval of next minute
                    hr = measurement.getHRValue();
                    moveToNextMeasurement = true;
                }
            }

            if (date < graphFinalDate) { //  calc calories only until current date
                double tmp = calories.calcActiveCalories(hr);
                if (tmp > 0) {
                    caloriesSum += tmp;
                }
                graphDataEntriesList.add(new Entry((float) date/(60*60), (float) caloriesSum));
            }
        }

        return graphDataEntriesList;
    }

    public List<Entry> prepareCaloriesTotal(long initialDate, long finalDate) {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Get the measurements from midnight today
        DataBase dataBase = new DataBase(mContext);
        ArrayList<Measurement> measurementList = dataBase.DataBaseGetMeasurements(initialDate, finalDate);

        // Calc calories on the measurement list
        Calories calories = new Calories();

        long date = 0;
        long endOfToday = MainActivity.SECONDS_24H - 1;
        long graphFinalDate = finalDate - initialDate;
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
                long measurementDate = (measurement.getDate() - initialDate);
                if (measurementDate < (date + 60)) { // means that measurement is in the interval of next minute
                    hr = measurement.getHRValue();
                    moveToNextMeasurement = true;
                }
            }

            if (date < graphFinalDate) { //  calc calories only until current date
//                caloriesSum += calories.calcCalories(hr);
                caloriesSum = calories.calcCalories(hr);
//                if (caloriesSum < 90) caloriesSum = 0;
                graphDataEntriesList.add(new Entry((float) date/(60*60), (float) caloriesSum));
            }
        }

        return graphDataEntriesList;
    }

    public List<Entry> prepareHRHigher90(long initialDate, long finalDate) {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Get the measurements from midnight today
        DataBase dataBase = new DataBase(mContext);
        ArrayList<Measurement> measurementList = dataBase.DataBaseGetMeasurements(initialDate, finalDate);

        // Calc calories on the measurement list
        Calories calories = new Calories();

        long date = 0;
        long endOfToday = MainActivity.SECONDS_24H - 1;
        long graphFinalDate = finalDate - initialDate;
        Iterator measurementListIterator = measurementList.iterator();
        int hr = 0;
        boolean moveToNextMeasurement = true;
        Measurement measurement = null;
        double caloriesSum = 0;
        // Loop trough all the minutes starting from today midnight
        for ( ; date < endOfToday; date += 60) {

            if ((moveToNextMeasurement == true) && measurementListIterator.hasNext()) {
                measurement = (Measurement) measurementListIterator.next();
                moveToNextMeasurement = false;
            }

            if (measurement != null) {
                long measurementDate = (measurement.getDate() - initialDate);
                if (measurementDate < (date + 60)) { // means that measurement is in the interval of next minute
                    hr = measurement.getHRValue();
                    moveToNextMeasurement = true;
                }
            }

            if (date < graphFinalDate) { //  calc calories only until current date
                if (hr < 90) hr = 0;
                graphDataEntriesList.add(new Entry((float) date/(60*60), (float) hr));
            }
        }

        return graphDataEntriesList;
    }

    public List<Entry> prepareHR(long initialDate, long finalDate) {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Get the measurements from midnight today
        DataBase dataBase = new DataBase(mContext);
        ArrayList<Measurement> measurementList = dataBase.DataBaseGetMeasurements(initialDate, finalDate);

        // Calc calories on the measurement list
        Calories calories = new Calories();

        long date = 0;
        long endOfToday = MainActivity.SECONDS_24H - 1;
        long graphFinalDate = finalDate - initialDate;
        Iterator measurementListIterator = measurementList.iterator();
        int hr = 0;
        boolean moveToNextMeasurement = true;
        Measurement measurement = null;
        double caloriesSum = 0;
        // Loop trough all the minutes starting from today midnight
        for ( ; date < endOfToday; date += 60) {

            if ((moveToNextMeasurement == true) && measurementListIterator.hasNext()) {
                measurement = (Measurement) measurementListIterator.next();
                moveToNextMeasurement = false;
            }

            if (measurement != null) {
                long measurementDate = (measurement.getDate() - initialDate);
                if (measurementDate < (date + 60)) { // means that measurement is in the interval of next minute
                    hr = measurement.getHRValue();
                    moveToNextMeasurement = true;
                }
            }

            if (date < graphFinalDate) { //  calc calories only until current date
                graphDataEntriesList.add(new Entry((float) date/(60*60), (float) hr));
            }
        }

        return graphDataEntriesList;
    }
}
