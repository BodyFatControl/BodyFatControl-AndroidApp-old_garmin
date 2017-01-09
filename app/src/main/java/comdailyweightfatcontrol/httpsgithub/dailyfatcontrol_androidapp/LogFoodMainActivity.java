package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class LogFoodMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_food_main);

        final Button buttonCreateFood = (Button) findViewById(R.id.button_create_food);

        buttonCreateFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication().getApplicationContext(), CreateFoodActivity.class);
                startActivity(intent);
            }
        });
    }
}
