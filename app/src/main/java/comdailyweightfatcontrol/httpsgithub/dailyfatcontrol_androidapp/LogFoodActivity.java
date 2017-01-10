package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFoodActivity extends AppCompatActivity {
    private Foods mFood;
    private EditText mEditTextServingSizeEntry = null;
    private TextView mTextViewCalories = null;
    private EditText mEditTextDate = null;
    private long mNowMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_food);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView textViewFoodName = (TextView) findViewById(R.id.food_name);
        final TextView textViewBrand = (TextView) findViewById(R.id.brand);
        final TextView textViewFoodUnityType = (TextView) findViewById(R.id.food_unity_type);
        mTextViewCalories = (TextView) findViewById(R.id.calories);
        mEditTextDate = (EditText) findViewById(R.id.date);
        mEditTextServingSizeEntry = (EditText) findViewById(R.id.serving_size_entry);

        Bundle extras = getIntent().getExtras();

        DataBaseFoods dataBaseFoods = new DataBaseFoods(this);
        mFood = dataBaseFoods.DataBaseGetFood(extras.getString("FOOD_NAME"));

        textViewFoodName.setText(mFood.getName());
        textViewBrand.setText(mFood.getBrand());
        textViewFoodUnityType.setText(mFood.getUnityType());
        mTextViewCalories.setText("");

        mEditTextServingSizeEntry.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (!string.isEmpty()) {
                    float value = Integer.parseInt(s.toString());
                    float calories = mFood.getCalories();
                    float unity = mFood.getUnity();
                    value = (1/unity) * calories * value;
                    mTextViewCalories.setText(Integer.toString((int) value));
                } else {
                    mTextViewCalories.setText("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        java.util.Calendar rightNow = java.util.Calendar.getInstance();
        long offset = rightNow.get(java.util.Calendar.ZONE_OFFSET) + rightNow.get(java.util.Calendar.DST_OFFSET);
        mNowMillis = rightNow.getTimeInMillis() + offset;
        SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy");
        mEditTextDate.setText(formater.format(new Date(mNowMillis)));

        mEditTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    java.util.Calendar rightNow = java.util.Calendar.getInstance();
                    rightNow.setTimeInMillis(mNowMillis);
                    int year = rightNow.get(Calendar.YEAR);
                    int month = rightNow.get(Calendar.MONTH);
                    int day = rightNow.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(LogFoodActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    java.util.Calendar rightNow = java.util.Calendar.getInstance();
                                    rightNow.clear();
                                    rightNow.set(year, monthOfYear, dayOfMonth);
                                    mNowMillis = rightNow.getTimeInMillis();

                                    SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy");
                                    mEditTextDate.setText(formater.format(new Date(mNowMillis)));
                                }
                            },
                            year,
                            month,
                            day);
                    dialog.show();
                }else {

                }
            }
        });
    }
}

