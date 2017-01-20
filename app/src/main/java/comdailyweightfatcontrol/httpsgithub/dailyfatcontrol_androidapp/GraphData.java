package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphData {
    private Context mContext;
    private double mCurrentCaloriesEER = 0;
    private double mCaloriesEER = 0;
    private double mCaloriesActive = 0;
    private double mCaloriesConsumed = 0;
    private long mInitialDate = 0;
    private long mFinalDate = 0;
    private UserProfile mUserProfile = null;

    public GraphData(Context context, long initialDate, long finalDate, UserProfile userProfile) {
        mContext = context;
        mInitialDate = initialDate;
        mFinalDate = finalDate;
        mUserProfile = userProfile;

        Calories caloriesObject = new Calories(mContext);
        mCaloriesEER = caloriesObject.calcCaloriesEER(mUserProfile);
        mCurrentCaloriesEER = caloriesObject.calcCaloriesEER(mInitialDate, mFinalDate, mUserProfile);
    }

    public List<Entry> prepareCurrentCaloriesEER() {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Calc calories on the measurement list
        Calories caloriesObject = new Calories(mContext);
        double calories = 0;

        long date = 0;
        long endOfToday = MainActivity.SECONDS_24H - 1;
        long graphFinalDate = mFinalDate - mInitialDate;
        calories = caloriesObject.calcCaloriesEER(mInitialDate, mFinalDate, mUserProfile);
        // Loop trough all the minutes starting from today midnight
        for ( ; date < endOfToday; date += 60) {
            if (date < graphFinalDate) { //  calc calories only until current date
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) calories
                        / 10)); // divide by 10 for the graph scale
            }
        }

        mCurrentCaloriesEER = calories;
        return graphDataEntriesList;
    }

    public List<Entry> prepareCaloriesActive() {
        List<Entry> graphDataEntriesList = new ArrayList<Entry>();

        // Get the measurements from midnight today
        DataBaseHR dataBase = new DataBaseHR(mContext);
        ArrayList<Measurement> measurementList = dataBase.DataBaseGetMeasurements(mInitialDate, mFinalDate);

        // Calc calories on the measurement list
        Calories calories = new Calories(mContext);

        long date = 0;
        long endOfToday = MainActivity.SECONDS_24H - 1;
        long graphFinalDate = mFinalDate - mInitialDate;
        double caloriesEERPerVivoactiveHRSample = mCaloriesEER / ((24*60*60)/95); // Vivoactive HR samples HR at each 95s
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
                long measurementDate = (measurement.getDate() - mInitialDate);
                if (measurementDate < (date + 60)) { // means that measurement is in the interval of next minute
                    hr = measurement.getHRValue();
                    measurement = null;
                    moveToNextMeasurement = true;
                }
            }

            if (date < graphFinalDate) { //  calc calories only until current date
                double caloriesValue = calories.calcActiveCalories(hr, mUserProfile);
                if (caloriesValue > 1) { // subtract EER value
                    mCurrentCaloriesEER -= caloriesEERPerVivoactiveHRSample;
                }
                caloriesSum += caloriesValue;
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (caloriesSum + (mCurrentCaloriesEER/10))));
            }

            if (date == (endOfToday - 59) && (mInitialDate < MainActivity.mMidNightToday)) { //  last point
                graphDataEntriesList.add(new Entry((float) date / (60 * 60), (float) (caloriesSum + (mCurrentCaloriesEER/10))));
            }
        }

        mCaloriesActive = caloriesSum;
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
