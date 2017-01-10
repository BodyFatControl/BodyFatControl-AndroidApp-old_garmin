package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

public class LogFoodActivity extends AppCompatActivity {
    private Foods mFood;
    private EditText mEditTextServingSizeEntry = null;
    private TextView mTextViewCalories = null;
    private EditText mEditTextDate = null;
    private EditText mEditTextTime = null;
    private Button mButtonLogThis = null;
    java.util.Calendar mCalendarDate = java.util.Calendar.getInstance();

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
        mEditTextTime = (EditText) findViewById(R.id.time);
        mEditTextServingSizeEntry = (EditText) findViewById(R.id.serving_size_entry);
        mButtonLogThis = (Button) findViewById(R.id.button_log_this);

        Bundle extras = getIntent().getExtras();

        DataBaseFoods dataBaseFoods = new DataBaseFoods(this);
        mFood = dataBaseFoods.DataBaseGetFood(extras.getString("FOOD_NAME"));

        textViewFoodName.setText(mFood.getName());
        textViewBrand.setText(mFood.getBrand());
        textViewFoodUnityType.setText(mFood.getUnitType());
        mTextViewCalories.setText("");

        mEditTextServingSizeEntry.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (!string.isEmpty()) {
                    float value = Integer.parseInt(s.toString());
                    float calories = mFood.getCalories();
                    float unity = mFood.getUnit();
                    value = (1/unity) * calories * value;
                    mTextViewCalories.setText(Integer.toString((int) value));
                } else {
                    mTextViewCalories.setText("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        mEditTextDate.setText(mCalendarDate.get(Calendar.DAY_OF_MONTH) + "/" +
                                (mCalendarDate.get(Calendar.MONTH)+1)  + "/" +
                                mCalendarDate.get(Calendar.YEAR));

        mEditTextTime.setText(mCalendarDate.get(Calendar.HOUR) + "h" +
                (mCalendarDate.get(Calendar.MINUTE)));

        mEditTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    DatePickerDialog dialog = new DatePickerDialog(LogFoodActivity.this,
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
                    mTimePicker = new TimePickerDialog(LogFoodActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            mCalendarDate.set(Calendar.HOUR, selectedHour);
                            mCalendarDate.set(Calendar.MINUTE, selectedMinute);
                            mCalendarDate.set(Calendar.SECOND, 0);
                            mCalendarDate.set(Calendar.MILLISECOND, 0);

                            mEditTextTime.setText(mCalendarDate.get(Calendar.HOUR) + "h" +
                                    (mCalendarDate.get(Calendar.MINUTE)));
                        }
                    }, mCalendarDate.get(Calendar.HOUR), mCalendarDate.get(Calendar.MINUTE), true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }else {

                }
            }
        });

        mButtonLogThis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Start by getting a food from the database, with the same name
                DataBaseFoods dataBaseFoods = new DataBaseFoods(getApplication().getApplicationContext());
                Foods originalFood = dataBaseFoods.DataBaseGetFood(textViewFoodName.getText().toString());

                Foods newFood = originalFood; // make a copy

                int tmpInt = Integer.parseInt(mEditTextServingSizeEntry.getText().toString());
                if (tmpInt > 0) {
                    newFood.setUnitsLogged(tmpInt);
                }

                newFood.setCaloriesLogged(Integer.parseInt(mTextViewCalories.getText().toString()));

                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radio_button_group);
                int selectedId = radiogroup.getCheckedRadioButtonId(); // get selected radio button from radioGroup
                RadioButton radioButton = (RadioButton) findViewById(selectedId); // find the radio button by returned id
                newFood.setMealTime(radioButton.getText().toString());

                newFood.setDate(mCalendarDate.getTimeInMillis());
                newFood.setIsCustomCalories(false);

                new DataBaseLogFoods(getApplication().getApplicationContext()).DataBaseLogFoodsWriteFood(newFood);

                // update stats
                originalFood.setLastUsageDate(mCalendarDate.getTimeInMillis());
                originalFood.setUsageFrequency(originalFood.getUsageFrequency() + 1);
                dataBaseFoods.DataBaseFoodsWriteFood(originalFood);

                finish(); // finish this activity
            }
        });
    }
}

