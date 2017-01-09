package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class LogFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_food);

        final TextView editTextFoodName = (TextView) findViewById(R.id.food_name);
        final TextView editTextBrand = (TextView) findViewById(R.id.brand);
        final TextView editTextFoodUnityType = (TextView) findViewById(R.id.food_unity_type);

        Bundle extras = getIntent().getExtras();

        DataBaseFoods dataBaseFoods = new DataBaseFoods(this);
        Foods food = dataBaseFoods.DataBaseGetFood(extras.getString("FOOD_NAME"));

        editTextFoodName.setText(food.getName());
        editTextBrand.setText(food.getBrand());
        editTextFoodUnityType.setText(food.getUnityType());

//        ublic class MainActivity extends Activity  {
//
//            private Calendar cal;
//            private int day;
//            private int month;
//            private int year;
//            private EditText et;
//
//            @Override
//            protected void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                setContentView(R.layout.activity_main);
//
//                et= (EditText) findViewById(R.id.edittext1);
//                cal = Calendar.getInstance();
//                day = cal.get(Calendar.DAY_OF_MONTH);
//                month = cal.get(Calendar.MONTH);
//                year = cal.get(Calendar.YEAR);
//
//
//                et.setText(day+"/"+month+"/"+"/"+year);
//
//
//
//                et.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//
//                        DateDialog();
//
//                    }
//                });
//            }
//
//
//            public void DateDialog(){
//
//                OnDateSetListener listener=new OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth)
//                    {
//
//                        et.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
//
//                    }};
//
//                DatePickerDialog dpDialog=new DatePickerDialog(this, listener, year, month, day);
//                dpDialog.show();
//
//            }
    }
}
