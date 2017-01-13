package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class LogFoodMainActivity extends AppCompatActivity {
    ArrayList<Foods> mArrayListLogFood;
    DataBaseFoods mDataBaseFoods = new DataBaseFoods(this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_food_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        ArrayList<Integer> command = new ArrayList<Integer>();

        if (id == R.id.create_food) {
            // Handle the connect action
            Intent intent = new Intent(this, CreateFoodActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Log food");
        setContentView(R.layout.activity_log_food_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication().getApplicationContext(), LogCaloriesFoodActivity .class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ListView listViewFoodsList = (ListView) findViewById(R.id.foods_list);
        listViewFoodsList.setLongClickable(true);

        // Add a food from the list
        listViewFoodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                   Intent intent = new Intent(LogFoodMainActivity.this, LogFoodActivity.class);
                   Foods food = (Foods) listViewFoodsList.getItemAtPosition(position);
                   intent.putExtra("FOOD_NAME", food.getName());
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
                                Foods food = (Foods) listViewFoodsList.getItemAtPosition(position);
                                intent.putExtra("FOOD_NAME", food.getName());
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Foods food = (Foods) listViewFoodsList.getItemAtPosition(position);
                                mDataBaseFoods.DataBaseDeleteFood(food.getName());
                                onResume(); // refresh the view by calling the onResume()
                            }

                        })
                        .setNeutralButton("Cancel", null)
                        .show();

                return true;
            }
        });

        // Populate the listview of logged foods
        // Start by getting the data from the database and then put on the array adapter, finally to the list
        mArrayListLogFood = mDataBaseFoods.DataBaseFoodsGetFoods();
        if (!mArrayListLogFood.isEmpty()) {
            ArrayAdapter<Foods> arrayAdapterAvailableFoods = new AvailableFoodAdapter(this, mArrayListLogFood);
            listViewFoodsList.setAdapter(arrayAdapterAvailableFoods);
        }
    }
}

class AvailableFoodAdapter extends ArrayAdapter<Foods> {
    public AvailableFoodAdapter(Context context, ArrayList<Foods> foodsArrayList) {
        super(context, 0, foodsArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Foods food = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.available_food, parent, false);
        }
        // Lookup view for data population
        TextView textViewFoodName = (TextView) convertView.findViewById(R.id.food_name);
        TextView textViewFoodBrand = (TextView) convertView.findViewById(R.id.food_brand);
        TextView textViewFoodCalories = (TextView) convertView.findViewById(R.id.food_calories);
        TextView textViewFoodUnits = (TextView) convertView.findViewById(R.id.food_units);
        TextView textViewFoodUnitsType = (TextView) convertView.findViewById(R.id.food_units_type);
        // Populate the data into the template view using the data object
        textViewFoodName.setText(food.getName());
        textViewFoodBrand.setText(food.getBrand());
        textViewFoodCalories.setText(Integer.toString(food.getCalories()));
        textViewFoodUnits.setText(Integer.toString(food.getUnits()));
        textViewFoodUnitsType.setText(food.getUnitType());
        // Return the completed view to render on screen
        return convertView;
    }
}
