package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBaseFoods extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database_foods.db";
    private static final String TABLE_NAME = "foods";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BRAND = "brand";
    private static final String COLUMN_UNITS = "units";
    private static final String COLUMN_UNIT_TYPE = "unit_type";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_LAST_USAGE_DATE = "last_usage_date";
    private static final String COLUMN_USAGE_FREQUENCY = "usage_frequency";

    public DataBaseFoods(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME + " text UNIQUE, " + /* UNIQUE means that there will not be duplicate entries with the same date */
                COLUMN_DATE + " integer, " +
                COLUMN_BRAND + " text, " +
                COLUMN_UNITS + " integer, " +
                COLUMN_UNIT_TYPE + " text, " +
                COLUMN_CALORIES + " integer, " +
                COLUMN_LAST_USAGE_DATE + " integer, " +
                COLUMN_USAGE_FREQUENCY + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void DataBaseFoodsWriteFood(Foods food) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, food.getDate());
        values.put(COLUMN_NAME, food.getName());
        values.put(COLUMN_BRAND, food.getBrand());
        values.put(COLUMN_UNITS, food.getUnits());
        values.put(COLUMN_UNIT_TYPE, food.getUnitType());
        values.put(COLUMN_CALORIES, food.getCalories());

        // Inserting Row: if there is one food with the same COLUMN_NAME, there new one will not be
        // inserted and will be ignored
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection
    }

    public ArrayList<String> DataBaseGetFoodsNames () {
        // Query to get all records
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        // Loop to put all the values to the ArrayList<Foods>
        cursor.moveToFirst();
        int counter = cursor.getCount();
        ArrayList<String> foodsNames = new ArrayList<>();
        for ( ; counter > 0; ) {
            if (cursor.isAfterLast()) break;

            String name;
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            foodsNames.add(name);

            cursor.moveToNext();
        }

        db.close(); // Closing database connection
        return foodsNames;
    }

    public void DataBaseDeleteFood (String name) {
        // Query to get all records
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, "name=?", new String[]{name});

        db.close(); // Closing database connection
    }

    public Foods DataBaseGetFood (String foodName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " LIKE '" + foodName + "'";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        Foods food = null;
        if (!cursor.isAfterLast()) {
            food = new Foods();
            food.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
            food.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            food.setBrand(cursor.getString(cursor.getColumnIndex(COLUMN_BRAND)));
            food.setUnits(cursor.getInt(cursor.getColumnIndex(COLUMN_UNITS)));
            food.setUnitType(cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_TYPE)));
            food.setCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES)));
            food.setLastUsageDate(cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_USAGE_DATE)));
            food.setUsageFrequency(cursor.getLong(cursor.getColumnIndex(COLUMN_USAGE_FREQUENCY)));
        }

        db.close(); // Closing database connection
        return food;
    }

    public ArrayList<Foods> DataBaseFoodsGetFoods () {
        // Query to get all the records starting at last midnight, ordered by date ascending
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE + " ASC";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int counter = cursor.getCount();
        ArrayList<Foods> foodsList = new ArrayList<>();
        for ( ; counter > 0; ) {
            if (cursor.isAfterLast()) break;
            Foods food = new Foods();
            food.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            food.setBrand(cursor.getString(cursor.getColumnIndex(COLUMN_BRAND)));
            food.setUnits(cursor.getInt(cursor.getColumnIndex(COLUMN_UNITS)));
            food.setUnitType(cursor.getString(cursor.getColumnIndex(COLUMN_UNIT_TYPE)));
            food.setCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES)));
            food.setLastUsageDate(cursor.getLong(cursor.getColumnIndex(COLUMN_LAST_USAGE_DATE)));
            food.setUsageFrequency(cursor.getInt(cursor.getColumnIndex(COLUMN_USAGE_FREQUENCY)));
            foodsList.add(food);
            cursor.moveToNext();
        }

        db.close(); // Closing database connection
        return foodsList;
    }
}

