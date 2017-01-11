package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class LogFoodMainActivity extends AppCompatActivity {
    ArrayList<String> mFoodsNames;
    DataBaseFoods mDataBaseFoods = new DataBaseFoods(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_log_food_main);

        final Button buttonCreateFood = (Button) findViewById(R.id.button_create_food);
        final ListView listViewFoodsList = (ListView) findViewById(R.id.foods_list);
        listViewFoodsList.setLongClickable(true);

        buttonCreateFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication().getApplicationContext(), CreateFoodActivity.class);
                startActivity(intent);
            }
        });

        // Add a food from the list
        listViewFoodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                   Intent intent = new Intent(LogFoodMainActivity.this, LogFoodActivity.class);
                   intent.putExtra("FOOD_NAME", listViewFoodsList.getItemAtPosition(position).toString());
                   startActivity(intent);
               }
           });

        // Edit or delete a food from the list
        listViewFoodsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                final int position = pos;

                new AlertDialog.Builder(LogFoodMainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Manage food")
                        .setMessage("You can edit or delete this food.")
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(LogFoodMainActivity.this, EditFoodActivity.class);
                                intent.putExtra("FOOD_NAME", listViewFoodsList.getItemAtPosition(position).toString());
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDataBaseFoods.DataBaseDeleteFood(listViewFoodsList.getItemAtPosition(position).toString());
                                onResume(); // refresh the view by calling the onResume()
                            }

                        })
                        .setNeutralButton("Cancel", null)
                        .show();

                return true;
            }
        });


        mFoodsNames = mDataBaseFoods.DataBaseGetFoodsNames();
        ArrayAdapter<String> arrayAdapterFoodsList =  new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mFoodsNames);
        listViewFoodsList.setAdapter(arrayAdapterFoodsList);
    }
}
