package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphData {
    private Context mContext;
    private double mCurrentCaloriesEER = 0;
    private double mCaloriesActive = 0;
    private double mCaloriesConsumed = 0;
    private long mInitialDate = 0;
    private long mFinalDate = 0;

    public GraphData(Context context, long initialDate, long finalDate) {
        mContext = context;
        mInitialDate = initialDate;
        mFinalDate = finalDate;
    }

    public List<Entry> prepareCalories() {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Get the measurements from midnight today
        DataBaseCalories dataBase = new DataBaseCalories(mContext);
        ArrayList<Measurement> measurementList = dataBase.DataBaseGetMeasurements(mInitialDate, mFinalDate);

        long date = 0;
        long endOfToday = (MainActivity.SECONDS_24H - 1) / 60;
        long graphFinalDate = (mFinalDate - mInitialDate) / 60; // in minutes
        double caloriesEERPerMinute = MainActivity.userCaloriesEERPerMinute;
        double caloriesEER = 0;
        Iterator measurementListIterator = measurementList.iterator();
        Measurement measurement = null;
        double caloriesActiveSum = 0;
        double calories = 0;

        // ***************************************************
        // First calc the EER calories (subtracting the active calories
        ArrayList<Measurement> measurementList1 = measurementList; // make a copy
        Iterator measurementListIterator1 = measurementList1.iterator();
        for ( ; date < endOfToday; date++) { // Loop trough all the minutes starting from today midnight
            if (date <= graphFinalDate) { //  calc calories only until current date
                if (measurement == null && measurementListIterator1.hasNext() ) { // read new measurement if wasn't done before
                    measurement = (Measurement) measurementListIterator1.next();
                }
                if (measurement != null) {
                    if (measurement.getCalories() <= caloriesEERPerMinute) { // means that we don't have active calories here
                        caloriesEER += caloriesEERPerMinute; // default value of calories for this minute
                    }
                    measurement = null;
                }
            }
        }
        mCurrentCaloriesEER = caloriesEER;
        // ***************************************************

        // ***************************************************
        // Now calc the active calories and prepare the graph data
        date = 0;
        for ( ; date < endOfToday; date++) { // Loop trough all the minutes starting from today midnight
            if (date <= graphFinalDate) { //  calc calories only until current date
                if (measurement == null && measurementListIterator.hasNext() ) { // read new measurement if wasn't done before
                    measurement = (Measurement) measurementListIterator.next();
                }

                if (measurement != null) {
                    calories = measurement.getCalories();
                    if (calories > caloriesEERPerMinute) { // means that we have active calories here
                        caloriesActiveSum += calories;
                    }
                    measurement = null;
                }

                graphDataEntriesList.add(new Entry((float) date / 60, (float) (caloriesActiveSum/1000 + (mCurrentCaloriesEER/10/1000))));
            }

            if (date == endOfToday && (mInitialDate < (MainActivity.mMidNightToday/60))) { //  last point
                graphDataEntriesList.add(new Entry((float) date / 60, (float) (caloriesActiveSum/1000 + (mCurrentCaloriesEER/10/1000))));
            }
        }

        mCurrentCaloriesEER /= 1000;
        mCaloriesActive = caloriesActiveSum/1000;
        return graphDataEntriesList;
    }

    public double getCurrentCaloriesEER() {
        return mCurrentCaloriesEER;
    }

    public double getCaloriesActive() {
        return mCaloriesActive;
    }

    public double getCaloriesConsumed() {
        return mCaloriesConsumed;
    }

    public List<Entry> prepareCaloriesConsumed() {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Get the measurements from midnight today
        DataBaseLogFoods dataBaseLogFoods = new DataBaseLogFoods(mContext);
        ArrayList<Foods> foodsList = dataBaseLogFoods.DataBaseLogFoodsGetFoods(mInitialDate*1000, mFinalDate*1000);

        long date = 0;
        long foodDate = 0;
        long endOfToday = MainActivity.SECONDS_24H - 1;
        long graphFinalDate = mFinalDate - mInitialDate;
        Iterator foodsListIterator = foodsList.iterator();
        Foods food = null;
        double caloriesSum = 0;
        double previousCaloriesSum = 0;
        double previewCaloriesSum_1_10 = 0;
        double foodCalories;
        boolean moveToNextFood = true;
        mCaloriesConsumed = 0;
        // Loop trough all the minutes starting from today midnight
        for ( ; date < endOfToday; date += 60) {

            if ((moveToNextFood == true) && foodsListIterator.hasNext()) {
                food = (Foods) foodsListIterator.next();
                foodDate = (food.getDate() / 1000) - mInitialDate;
                moveToNextFood = false;
            }

            final double caloriesEER_1_10 = mCurrentCaloriesEER/10;
            while (foodDate >= date && foodDate < (date + 60)) { // food is in this interval time
                if (food == null) break;
                foodCalories = food.getCaloriesLogged();
                mCaloriesConsumed += foodCalories;
                previewCaloriesSum_1_10 = caloriesSum + (foodCalories/10);

                if (caloriesSum > caloriesEER_1_10) { // we are already over 1/10 interval
                    caloriesSum = caloriesSum + foodCalories;

                } else if (previewCaloriesSum_1_10 <= caloriesEER_1_10) { // we will be in the 1/10 interval
                    caloriesSum += (foodCalories / 10);

                } else { // we have some part in and other over 1/10 interval
                    double value = caloriesEER_1_10 - caloriesSum;
                    foodCalories = (foodCalories/10) - value;
                    caloriesSum = caloriesEER_1_10 + foodCalories*10;
                }

                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (previousCaloriesSum)));
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (caloriesSum)));
                previousCaloriesSum = caloriesSum;

                if (foodsListIterator.hasNext()) { // iterate on the foods in the same interval
                    food = (Foods) foodsListIterator.next();
                    foodDate = (food.getDate() / 1000) - mInitialDate;
                } else  {
                    break;
                }
            }

            if (graphFinalDate >= date && graphFinalDate < (date + 60)) { // add a point at the current date
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (caloriesSum)));
            }

            if (date == 0) { // very first value should be added to the graph
                graphDataEntriesList.add(new Entry(0, -2));
            }

            if (date == (endOfToday - 59) && (mInitialDate < MainActivity.mMidNightToday)) { //  last point
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (caloriesSum)));
            }
        }

        return graphDataEntriesList;
    }
}
