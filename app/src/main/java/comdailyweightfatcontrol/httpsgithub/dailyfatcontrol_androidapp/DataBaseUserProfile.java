package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class DataBaseUserProfile extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database_user_profile.db";
    private static final String TABLE_NAME = "user_profile";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_USER_BIRTH_YEAR = "user_birth_year";
    private static final String COLUMN_USER_GENDER = "user_gender";
    private static final String COLUMN_USER_HEIGH = "user_birth_height";
    private static final String COLUMN_USER_WEIGH = "user_birth_weight";
    private static final String COLUMN_USER_ACTIVITY_CLASS = "user_activity_class";

    public DataBaseUserProfile(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " integer UNIQUE, " + /* UNIQUE means that there will not be duplicate entries with the same date */
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

    public UserProfile DataBaseUserProfileLast () {
        SQLiteDatabase db = this.getReadableDatabase();

        // build the query
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE + " DESC LIMIT 1";
        // open database
        Cursor cursor = db.rawQuery(query, null);

        UserProfile userProfile = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            userProfile = new UserProfile();
            userProfile.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            userProfile.setDate(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE)));
            userProfile.setUserBirthYear(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_BIRTH_YEAR)));
            userProfile.setUserGender(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_GENDER)));
            userProfile.setUserHeight(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_HEIGH)));
            userProfile.setUserWeight(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_WEIGH)));
            userProfile.setUserActivityClass(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ACTIVITY_CLASS)));
        }

        cursor.close();
        db.close(); // Closing database connection
        return userProfile;
    }

    public void DataBaseUserProfileWrite (UserProfile userProfile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, userProfile.getDate());
        values.put(COLUMN_USER_BIRTH_YEAR, userProfile.getUserBirthYear());
        values.put(COLUMN_USER_GENDER, userProfile.getUserGender());
        values.put(COLUMN_USER_HEIGH, userProfile.getUserHeight());
        values.put(COLUMN_USER_WEIGH, userProfile.getUserWeight());
        values.put(COLUMN_USER_ACTIVITY_CLASS, userProfile.getUserActivityClass());

        // Inserting Row: if there is one food with the same COLUMN_NAME, there new one will not be
        // inserted and will be ignored
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection
    }
}

