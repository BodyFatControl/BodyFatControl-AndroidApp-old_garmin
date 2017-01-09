package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp.Foods;

public class DataBaseFoods extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database_foods.db";
    private static final String TABLE_NAME = "foods";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BRAND = "brand";
    private static final String COLUMN_UNITY = "unity";
    private static final String COLUMN_UNITY_TYPE = "unity_type";
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
                COLUMN_UNITY + " integer, " +
                COLUMN_UNITY_TYPE + " text, " +
                COLUMN_CALORIES + " integer, " +
                COLUMN_LAST_USAGE_DATE + " integer, " +
                COLUMN_USAGE_FREQUENCY + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void DataBaseWriteFood (Foods food) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, food.getDate());
        values.put(COLUMN_NAME, food.getName());
        values.put(COLUMN_BRAND, food.getBrand());
        values.put(COLUMN_UNITY, food.getUnity());
        values.put(COLUMN_UNITY_TYPE, food.getUnityType());
        values.put(COLUMN_CALORIES, food.getCalories());

        // Inserting Row: if there is one food with the same COLUMN_NAME, there new one will not be
        // inserted and will be ignored
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        db.close(); // Closing database connection


    }
}

