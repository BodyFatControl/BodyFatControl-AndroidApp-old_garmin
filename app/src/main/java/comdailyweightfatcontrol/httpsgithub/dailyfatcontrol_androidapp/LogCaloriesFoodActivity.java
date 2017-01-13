package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.icu.util.Calendar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Random;

public class LogCaloriesFoodActivity extends AppCompatActivity {
    private Foods mFood;
    private EditText mEditTextServingSizeEntry = null;
    private TextView mEditTextCalories = null;
    private EditText mEditTextDate = null;
    private EditText mEditTextTime = null;
    private Button mButtonLogThis = null;
    private RadioGroup mRadioGroup = null;
    java.util.Calendar mCalendarDate = java.util.Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Log quick calories");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_calories_food);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView textViewFoodName = (TextView) findViewById(R.id.food_name);
        final TextView textViewBrand = (TextView) findViewById(R.id.brand);
        final Spinner spinnerUnityType = (Spinner) findViewById(R.id.spinner_foods_unity_type);
        mEditTextCalories = (EditText) findViewById(R.id.calories);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_button_group);

        // set the radio button Meal Time depending on the current time
        int radioButtonNumber = utils.returnMealTimeRadioButtonNumber();
        RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(radioButtonNumber);
        radioButton.setChecked(true);

        mEditTextDate = (EditText) findViewById(R.id.date);
        mEditTextTime = (EditText) findViewById(R.id.time);
        mEditTextServingSizeEntry = (EditText) findViewById(R.id.serving_size_entry);
        mButtonLogThis = (Button) findViewById(R.id.button_log_this);

        // Set a custom spinner default value
        ArrayAdapter myAdap = (ArrayAdapter) spinnerUnityType.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition = myAdap.getPosition("piece");
        spinnerUnityType.setSelection(spinnerPosition);

        mEditTextDate.setText(mCalendarDate.get(Calendar.DAY_OF_MONTH) + "/" +
                (mCalendarDate.get(Calendar.MONTH)+1)  + "/" +
                mCalendarDate.get(Calendar.YEAR));

        mEditTextTime.setText(mCalendarDate.get(Calendar.HOUR_OF_DAY) + "h" +
                (mCalendarDate.get(Calendar.MINUTE)));

        mEditTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    DatePickerDialog dialog = new DatePickerDialog(LogCaloriesFoodActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    mCalendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    mCalendarDate.set(Calendar.MONTH, monthOfYear);
                                    mCalendarDate.set(Calendar.YEAR, year);
                                    mCalendarDate.set(Calendar.SECOND, 0);
                                    mCalendarDate.set(Calendar.MILLISECOND, 0);

                                    mEditTextDate.setText(mCalendarDate.get(Calendar.DAY_OF_MONTH) + "/" +
                                            (mCalendarDate.get(Calendar.MONTH)+1) + "/" +
                                            mCalendarDate.get(Calendar.YEAR));
                                }
                            },
                            mCalendarDate.get(Calendar.YEAR),
                            mCalendarDate.get(Calendar.MONTH),
                            mCalendarDate.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                }else {

                }
            }
        });

        mEditTextTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(LogCaloriesFoodActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            mCalendarDate.set(Calendar.HOUR_OF_DAY, selectedHour);
                            mCalendarDate.set(Calendar.MINUTE, selectedMinute);
                            mCalendarDate.set(Calendar.SECOND, 0);
                            mCalendarDate.set(Calendar.MILLISECOND, 0);

                            mEditTextTime.setText(mCalendarDate.get(Calendar.HOUR_OF_DAY) + "h" +
                                    (mCalendarDate.get(Calendar.MINUTE)));
                        }
                    }, mCalendarDate.get(Calendar.HOUR_OF_DAY), mCalendarDate.get(Calendar.MINUTE), true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                } else {

                }
            }
        });

        mButtonLogThis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Foods mFood = new Foods();
                mFood.setName(textViewFoodName.getText().toString());
                mFood.setBrand(textViewBrand.getText().toString());

                int tmpInt = Integer.parseInt(mEditTextServingSizeEntry.getText().toString());
                if (tmpInt > 0) {
                    mFood.setUnitsLogged(tmpInt);
                }

                mFood.setUnitType(spinnerUnityType.getSelectedItem().toString());
                mFood.setCaloriesLogged(Integer.parseInt(mEditTextCalories.getText().toString()));

                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radio_button_group);
                int selectedId = radiogroup.getCheckedRadioButtonId(); // get selected radio button from radioGroup
                RadioButton radioButton = (RadioButton) findViewById(selectedId); // find the radio button by returned id
                mFood.setMealTime(radioButton.getText().toString());

                mFood.setDate(mCalendarDate.getTimeInMillis());
                mFood.setIsCustomCalories(true);

                new DataBaseLogFoods(getApplication().getApplicationContext()).DataBaseLogFoodsWriteFood(mFood, false);

                finish(); // finish this activity
            }
        });
    }
}

