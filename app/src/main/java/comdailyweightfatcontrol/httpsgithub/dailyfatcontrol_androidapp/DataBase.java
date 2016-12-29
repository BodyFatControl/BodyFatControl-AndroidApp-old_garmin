package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "database.db";
    private static final String TABLE_NAME = "CALORIES_OUT";
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

            // Inserting Row
//            db.insert(TABLE_NAME, null, values);
            db.replace(TABLE_NAME, null, values);
        }

        db.close(); // Closing database connection
    }
}

