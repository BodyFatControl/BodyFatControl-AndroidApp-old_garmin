package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBaseLogFoods extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "database_log_foods.db";
    private static final String TABLE_NAME = "log_foods";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BRAND = "brand";
    private static final String COLUMN_UNIT = "unit";
    private static final String COLUMN_UNIT_TYPE = "unit_type";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_UNITS_LOGGED = "units_logged";
    private static final String COLUMN_CALORIES_LOGGED = "calories_logged";
    private static final String COLUMN_MEAL_TIME = "meal_time";
    private static final String COLUMN_IS_CUSTOM_CALORIES = "is_custom_calories";

    public DataBaseLogFoods(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME + " text, " +
                COLUMN_DATE + " integer, " +
                COLUMN_BRAND + " text, " +
                COLUMN_UNIT + " integer, " +
                COLUMN_UNIT_TYPE + " text, " +
                COLUMN_CALORIES + " integer, " +
                COLUMN_UNITS_LOGGED + " text, " +
                COLUMN_CALORIES_LOGGED + " integer, " +
                COLUMN_MEAL_TIME + " integer, " +
                COLUMN_IS_CUSTOM_CALORIES + " boolean)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void DataBaseLogFoodsWriteFood(Foods food) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, food.getDate());
        values.put(COLUMN_NAME, food.getName());
        values.put(COLUMN_BRAND, food.getBrand());
        values.put(COLUMN_UNIT, food.getUnit());
        values.put(COLUMN_UNIT_TYPE, food.getUnitType());
        values.put(COLUMN_CALORIES, food.getCalories());
        values.put(COLUMN_UNITS_LOGGED, food.getUnitsLogged());
        values.put(COLUMN_CALORIES_LOGGED, food.getCaloriesLogged());
        values.put(COLUMN_MEAL_TIME, food.getMealTime());
        values.put(COLUMN_IS_CUSTOM_CALORIES, food.getIsCustomCalories());

        // Inserting Row: replace food if already exists
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public void DataBaseLogFoodsDeleteFood (String name) {
        // Query to get all records
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, "name=?", new String[]{name});

        db.close(); // Closing database connection
    }

    public ArrayList<Foods> DataBaseLogFoodsGetFoods (long initialDate, long finalDate) {
        // Query to get all the records starting at last midnight, ordered by date ascending
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " BETWEEN " +
                + initialDate + " AND " + finalDate + " ORDER BY " + COLUMN_DATE +
                " ASC";

        Cursor cursor = db.rawQuery(query, null);

        // Loop to put all the values to the ArrayList<Measurement>
        cursor.moveToFirst();
        int counter = cursor.getCount();
        int date;
        int hr;
        ArrayList<Foods> foodsList = new ArrayList<>();
        for ( ; counter > 0; ) {
            if (cursor.isAfterLast()) break;
            Foods food = new Foods();
            food.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
            food.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            food.setBrand(cursor.getString(cursor.getColumnIndex(COLUMN_BRAND)));
            food.setCaloriesLogged(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES_LOGGED)));
            food.setUnitType(cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_TYPE)));
            food.setUnit(cursor.getInt(cursor.getColumnIndex(COLUMN_UNIT)));
            foodsList.add(food);
            cursor.moveToNext();
        }

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

        db.close(); // Closing database connection
        return foodsNames;
    }
}

