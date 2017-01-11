package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphData {
    private Context mContext;
    private double mMaxCaloriesEER = 0;
    private double mMaxCaloriesActive = 0;
    private double mMaxCaloriesConsumed = 0;

    public GraphData(Context context) {
        mContext = context;
    }

    public List<Entry> prepareCaloriesEER(long initialDate, long finalDate) {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Calc calories on the measurement list
        Calories caloriesObject = new Calories();
        double calories = 0;

        long date = 0;
        long endOfToday = MainActivity.SECONDS_24H - 1;
        long graphFinalDate = finalDate - initialDate;
        calories = caloriesObject.calcCaloriesEER(initialDate, finalDate);
        // Loop trough all the minutes starting from today midnight
        for ( ; date < endOfToday; date += 60) {
            if (date < graphFinalDate) { //  calc calories only until current date
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) calories
                        / 10)); // divide by 10 for the graph scale
            }
        }

        mMaxCaloriesEER = calories;
        return graphDataEntriesList;
    }

    public List<Entry> prepareCaloriesActive(long initialDate, long finalDate, double CaloriesEER) {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Get the measurements from midnight today
        DataBaseHR dataBase = new DataBaseHR(mContext);
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
                if (true/*tmp > 0*/) {
                    caloriesSum += tmp;
                    graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (caloriesSum + (CaloriesEER/10))));
                } else if (tmp == 0 && date == 0) { // very first value should be added to the graph
                    graphDataEntriesList.add(new Entry(0, -2));
                }
            }

            if (date == (endOfToday - 59) && (initialDate < MainActivity.mMidNightToday)) { //  last point
                    graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (caloriesSum + (CaloriesEER/10))));
            }
        }

        mMaxCaloriesActive = caloriesSum;
        return graphDataEntriesList;
    }

    public double getmMaxCaloriesEER() {
        return mMaxCaloriesEER;
    }

    public double getmMaxCaloriesActive() {
        return mMaxCaloriesActive;
    }

    public List<Entry> prepareCaloriesConsumed(long initialDate, long finalDate) {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Get the measurements from midnight today
        DataBaseLogFoods dataBaseLogFoods = new DataBaseLogFoods(mContext);
        ArrayList<Foods> foodsList = dataBaseLogFoods.DataBaseLogFoodsGetFoods(initialDate*1000, finalDate*1000);

        long date = 0;
        long foodDate = 0;
        long endOfToday = MainActivity.SECONDS_24H - 1;
        Iterator foodsListIterator = foodsList.iterator();
        Foods food = null;
        double caloriesSum = 0;
        double previousCaloriesSum = 0;
        double foodCalories;
        boolean moveToNextFood = true;
        // Loop trough all the minutes starting from today midnight
        for ( ; date < endOfToday; date += 60) {

            if ((moveToNextFood == true) && foodsListIterator.hasNext()) {
                food = (Foods) foodsListIterator.next();
                foodDate = (food.getDate() / 1000) - initialDate;
                moveToNextFood = false;
            }

            while (foodDate >= date && foodDate < (date + 60)) { // food is in this interval time
                if (food == null) break;
                foodCalories = food.getCaloriesLogged();
                previousCaloriesSum = caloriesSum;
                caloriesSum += foodCalories;
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (previousCaloriesSum)));
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (caloriesSum)));

                if (foodsListIterator.hasNext()) { // iterate on the foods in the same interval
                    food = (Foods) foodsListIterator.next();
                    foodDate = (food.getDate() / 1000) - initialDate;
                } else  {
                    break;
                }
            }

            if (date == 0) { // very first value should be added to the graph
                graphDataEntriesList.add(new Entry(0, -2));
            }

            if (date == (endOfToday - 59) && (initialDate < MainActivity.mMidNightToday)) { //  last point
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (caloriesSum)));
            }
        }

        mMaxCaloriesConsumed = caloriesSum;
        return graphDataEntriesList;
    }
}
