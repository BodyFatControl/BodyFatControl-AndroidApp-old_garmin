package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class DataBaseLogFoods extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_DIR = "body_fat_control";
    private static final String DATABASE_NAME = "database_log_foods.db";
    private static final String TABLE_NAME = "log_foods";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BRAND = "brand";
    private static final String COLUMN_UNITS = "units";
    private static final String COLUMN_UNIT_TYPE = "unit_type";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_UNITS_LOGGED = "units_logged";
    private static final String COLUMN_CALORIES_LOGGED = "calories_logged";
    private static final String COLUMN_MEAL_TIME = "meal_time";
    private static final String COLUMN_IS_CUSTOM_CALORIES = "is_custom_calories";

    public DataBaseLogFoods(Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + DATABASE_DIR
                + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " text, " +
                COLUMN_DATE + " integer, " +
                COLUMN_BRAND + " text, " +
                COLUMN_UNITS + " real, " +
                COLUMN_UNIT_TYPE + " text, " +
                COLUMN_CALORIES + " integer, " +
                COLUMN_UNITS_LOGGED + " real, " +
                COLUMN_CALORIES_LOGGED + " integer, " +
                COLUMN_MEAL_TIME + " integer, " +
                COLUMN_IS_CUSTOM_CALORIES + " boolean)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void DataBaseLogFoodsWriteFood(Foods food, boolean replace) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (replace == true) { // for replacing we need to use the same ID of the database table
            values.put(COLUMN_ID, food.getId());
        }
        values.put(COLUMN_DATE, food.getDate());
        values.put(COLUMN_NAME, food.getName());
        values.put(COLUMN_BRAND, food.getBrand());
        values.put(COLUMN_UNITS, food.getUnits());
        values.put(COLUMN_UNIT_TYPE, food.getUnitType());
        values.put(COLUMN_CALORIES, food.getCalories());
        values.put(COLUMN_UNITS_LOGGED, food.getUnitsLogged());
        values.put(COLUMN_CALORIES_LOGGED, food.getCaloriesLogged());
        values.put(COLUMN_MEAL_TIME, food.getMealTime());
        values.put(COLUMN_IS_CUSTOM_CALORIES, food.getIsCustomCalories());

        if (replace == true) {
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } else {
            db.insert(TABLE_NAME, null, values);
        }

        db.close(); // Closing database connection
    }

    public void DataBaseLogFoodsDeleteFood (int id) {
        // Query to get all records
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, "_id=?", new String[]{Integer.toString(id)});

        db.close(); // Closing database connection
    }

    public ArrayList<Foods> DataBaseLogFoodsGetFoods (long initialDate, long finalDate) {
        // Query to get all the records starting at last midnight, ordered by date ascending
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " BETWEEN " +
                + initialDate + " AND " + finalDate + " ORDER BY " + COLUMN_DATE +
                " ASC";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        int counter = cursor.getCount();
        ArrayList<Foods> foodsList = new ArrayList<>();
        for ( ; counter > 0; ) {
            if (cursor.isAfterLast()) break;
            Foods food = new Foods();
            food.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            food.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
            food.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            food.setBrand(cursor.getString(cursor.getColumnIndex(COLUMN_BRAND)));
            food.setUnitsLogged(cursor.getFloat(cursor.getColumnIndex(COLUMN_UNITS_LOGGED)));
            food.setCaloriesLogged(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES_LOGGED)));
            food.setUnits(cursor.getFloat(cursor.getColumnIndex(COLUMN_UNITS)));
            food.setUnitType(cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_TYPE)));
            food.setCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES)));
            food.setMealTime(cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_TIME)));
            food.setIsCustomCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CUSTOM_CALORIES)) == 1);
            foodsList.add(food);
            cursor.moveToNext();
        }

        cursor.close();
        db.close(); // Closing database connection
        return foodsList;
    }

    public ArrayList<String> DataBaseLogFoodsGetNames (long initialDate, long finalDate) {
        // Query to get all the records starting at initialDate up to finalDate, ordered by date ascending
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " BETWEEN " +
                + initialDate*1000 + " AND " + finalDate*1000 + " ORDER BY " + COLUMN_DATE +
                " ASC";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        int counter = cursor.getCount();
        ArrayList<String> foodsNames = new ArrayList<>();
        for (; counter > 0; ) {
            if (cursor.isAfterLast()) break;

            String name;
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            foodsNames.add(name);

            cursor.moveToNext();
        }

        cursor.close();
        db.close(); // Closing database connection
        return foodsNames;
    }

    public Foods DataBaseLogFoodsGetFood(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + id;

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        Foods food = new Foods();
        food.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        food.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
        food.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        food.setBrand(cursor.getString(cursor.getColumnIndex(COLUMN_BRAND)));
        food.setUnits(cursor.getFloat(cursor.getColumnIndex(COLUMN_UNITS)));
        food.setUnitType(cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_TYPE)));
        food.setCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES)));
        food.setUnitsLogged(cursor.getFloat(cursor.getColumnIndex(COLUMN_UNITS_LOGGED)));
        food.setCaloriesLogged(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES_LOGGED)));
        food.setMealTime(cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_TIME)));
        food.setIsCustomCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CUSTOM_CALORIES)) == 1);

        cursor.close();
        db.close(); // Closing database connection
        return food;
    }

    public ArrayList<Object> DataBaseLogFoodsGetFoodsAndMeals (long initialDate, long finalDate) {
        // Query to get all the records starting at last midnight, ordered by date ascending
        SQLiteDatabase db = this.getWritableDatabase();

        String mealsTime[] = {MealTime.BREAKFAST, MealTime.MORNING_SNACK, MealTime.LUNCH,
                MealTime.AFTERNOON_SNACK, MealTime.DINNER, MealTime.EVENING_SNACK, MealTime.ANYTIME};

        ArrayList<Object> foodsAndMealsList = new ArrayList<>();
        int foodsAndMealsListPosition = 0;
        int foodsAndMealsListStartPosition = 0;
        for (String mealTime : mealsTime) {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " BETWEEN " +
                    +initialDate + " AND " + finalDate + " AND " + COLUMN_MEAL_TIME + " LIKE " +
                    "'" + mealTime + "'" + " ORDER BY " + COLUMN_DATE + " ASC";

            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            int counter = cursor.getCount();
            int calories = 0;
            int mealCalories = 0;
            boolean foodAdded = false;
            for (; counter > 0; ) {
                if (cursor.isAfterLast()) break;
                Foods food = new Foods();
                food.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                food.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
                food.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                food.setBrand(cursor.getString(cursor.getColumnIndex(COLUMN_BRAND)));
                food.setUnitsLogged(cursor.getFloat(cursor.getColumnIndex(COLUMN_UNITS_LOGGED)));

                // keep track of total calories on this MealTime
                calories = cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES_LOGGED));
                mealCalories += calories;
                food.setCaloriesLogged(calories);
                food.setUnits(cursor.getFloat(cursor.getColumnIndex(COLUMN_UNITS)));
                food.setUnitType(cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_TYPE)));
                food.setCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES)));
                food.setMealTime(cursor.getString(cursor.getColumnIndex(COLUMN_MEAL_TIME)));
                food.setIsCustomCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CUSTOM_CALORIES)) == 1);
                foodsAndMealsList.add(food);
                foodAdded = true;
                foodsAndMealsListPosition++;
                cursor.moveToNext();
            }

            if (foodAdded == true) {
                // add to begin of list the MealTime
                foodsAndMealsList.add(foodsAndMealsListStartPosition, new MealTime(mealTime, mealCalories));
                foodsAndMealsListPosition++;
                foodsAndMealsListStartPosition = foodsAndMealsListPosition;
            }
            cursor.close();
        }

        db.close(); // Closing database connection
        return foodsAndMealsList;
    }
}

class MealTime {
    public static final String BREAKFAST = "Breakfast";
    public static final String MORNING_SNACK = "Morning snack";
    public static final String LUNCH = "Lunch";
    public static final String AFTERNOON_SNACK = "Afternoon snack";
    public static final String DINNER = "Dinner";
    public static final String EVENING_SNACK = "Evening snack";
    public static final String ANYTIME = "Anytime";

    private int mCalories = 0;
    private String mMealTimeName = null;

    public MealTime() {
    }

    public MealTime(String mMealTimeName, int mCalories) {
        this.mCalories = mCalories;
        this.mMealTimeName = mMealTimeName;
    }

    public int getCalories() {

        return mCalories;
    }

    public void setCalories(int calories) {
        this.mCalories = calories;
    }

    public String getMealTimeName() {
        return mMealTimeName;
    }

    public void setMealTimeName(String mMealTimeName) {
        this.mMealTimeName = mMealTimeName;
    }
}
