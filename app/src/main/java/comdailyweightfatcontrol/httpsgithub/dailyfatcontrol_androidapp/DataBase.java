package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class DataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "database.db";
    private static final String TABLE_NAME = "calories_out";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_HR_VALUE = "hr_value";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_CALORIES_SUM = "calories_sum";
    private static final String COLUMN_MANUAL_CALORIES = "manual_calories";
    private static final String COLUMN_USER_BIRTH_YEAR = "user_birth_year";
    private static final String COLUMN_USER_GENDER = "user_gender";
    private static final String COLUMN_USER_HEIGH = "user_birth_height";
    private static final String COLUMN_USER_WEIGH = "user_birth_weight";
    private static final String COLUMN_USER_ACTIVITY_CLASS = "user_activity_class";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_DATE + " integer UNIQUE, " + /* UNIQUE means that there will not be duplicate entries with the same date */
                COLUMN_HR_VALUE + " integer, " +
                COLUMN_CALORIES + " integer, " +
                COLUMN_CALORIES_SUM + " integer, " +
                COLUMN_MANUAL_CALORIES + " integer, " +
                COLUMN_USER_BIRTH_YEAR + " integer, " +
                COLUMN_USER_GENDER + " integer, " +
                COLUMN_USER_HEIGH + " integer, " +
                COLUMN_USER_WEIGH + " integer, " +
                COLUMN_USER_ACTIVITY_CLASS + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void DataBaseWriteMeasurement (ArrayList<Measurement> measurementList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (Measurement measurement : measurementList) {
            values.put(COLUMN_DATE, measurement.getDate());
            values.put(COLUMN_HR_VALUE, measurement.getHRValue());
            values.put(COLUMN_CALORIES, measurement.getCaloriesOut());
            values.put(COLUMN_CALORIES_SUM, measurement.getCaloriesOutSum());
            values.put(COLUMN_MANUAL_CALORIES, measurement.getIsManualCalories());
            values.put(COLUMN_USER_BIRTH_YEAR, measurement.getUserBirthYear());
            values.put(COLUMN_USER_GENDER, measurement.getUserGender());
            values.put(COLUMN_USER_HEIGH, measurement.getUserHeight());
            values.put(COLUMN_USER_WEIGH, measurement.getUserWeight());
            values.put(COLUMN_USER_ACTIVITY_CLASS, measurement.getUserActivityClass());

            // Inserting Row
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }

        db.close(); // Closing database connection
    }

    public ArrayList<Measurement> DataBaseGetLastDayMeasurements () {

        // Get the MidNightToday and RightNow date values
        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
        long rightNowMillis = rightNow.getTimeInMillis() + offset;
        long sinceMidnightToday = rightNowMillis % (24 * 60 * 60 * 1000);
        long midNightToday = rightNowMillis - sinceMidnightToday;
        rightNowMillis /= 1000; // now in seconds
        midNightToday /= 1000; // now in seconds

        // Query to get all the records starting at last midnight, ordered by date ascending
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " BETWEEN " +
                + midNightToday + " AND " + rightNowMillis + " ORDER BY " + COLUMN_DATE +
                " ASC";

        Cursor cursor = db.rawQuery(query, null);

        // Loop to put all the values to the ArrayList<Measurement>
        cursor.moveToFirst();
        int counter = cursor.getCount();
        int date;
        int hr;
        ArrayList<Measurement> measurementList = new ArrayList<Measurement>();
        for ( ; counter > 0; ) {
            if (cursor.isAfterLast()) break;
            date = cursor.getInt(cursor.getColumnIndex(COLUMN_DATE));
            hr = cursor.getInt(cursor.getColumnIndex(COLUMN_HR_VALUE));
            cursor.moveToNext();

            Measurement measurement = new Measurement();
            measurement.setDate(date);
            measurement.setHRValue(hr);
            measurementList.add(measurement);
        }

        db.close(); // Closing database connection
        return measurementList;
    }

    public Measurement DataBaseGetLastMeasurement () {
        Measurement measurement = new Measurement();
        SQLiteDatabase db = this.getWritableDatabase();
        // build the query
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE + " DESC LIMIT 1";
        // open database
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            measurement.setDate(cursor.getInt(cursor.getColumnIndex(COLUMN_DATE)));
            measurement.setHRValue(cursor.getInt(cursor.getColumnIndex(COLUMN_HR_VALUE)));
            measurement.setCaloriesOut(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES)));
            measurement.setCaloriesOutSum(cursor.getInt(cursor.getColumnIndex(COLUMN_CALORIES_SUM)));
            measurement.setIsManualCalories(cursor.getInt(cursor.getColumnIndex(COLUMN_MANUAL_CALORIES)));
            measurement.setUserBirthYear(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_BIRTH_YEAR)));
            measurement.setUserGender(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_GENDER)));
            measurement.setUserHeight(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_HEIGH)));
            measurement.setUserWeight(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_WEIGH)));
            measurement.setUserActivityClass(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ACTIVITY_CLASS)));
        }

        db.close(); // Closing database connection
        return measurement;
    }
}

