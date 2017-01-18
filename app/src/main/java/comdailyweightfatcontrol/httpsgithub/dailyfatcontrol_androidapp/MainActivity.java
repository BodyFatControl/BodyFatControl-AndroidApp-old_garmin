package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp.adapter.IQDeviceAdapter;
import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.IQDeviceEventListener;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.IQDevice.IQDeviceStatus;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;
import com.garmin.android.connectiq.ConnectIQ.IQApplicationEventListener;
import com.garmin.android.connectiq.ConnectIQ.IQSendMessageListener;
import com.garmin.android.connectiq.ConnectIQ.IQMessageStatus;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


//public class MainActivity extends AppCompatActivity
//        implements NavigationView.OnNavigationItemSelectedListener {
public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener,
        OnChartGestureListener {

    private ConnectIQ mConnectIQ;
    private IQDevice mIQDevice;
    private IQDeviceAdapter mAdapter;
    private boolean mSdkReady = false;
    public static final String MY_APP = "3F3D83A85F584671A551EA1316623AD7";
    private IQApp mConnectIQApp = new IQApp(MY_APP);
    public static final int HISTORIC_HR_COMMAND = 104030201;
    private static final int USER_DATA_COMMAND = 204030201;
    private TextView mTextViewCaloriesCalc;
    private ListView listViewLogFoodList;
    private TextView mDateTitle;
    public static String PREFERENCES = "MainSharedPreferences";
    public static SharedPreferences Prefs;
    public static long mMidNightToday;
    private long mGraphInitialDate;
    private long mGraphFinalDate;
    public static final long SECONDS_24H = 24*60*60;
    private double mCaloriesEER = 0.0;
    private double mCaloriesActive = 0.0;
    private double mCaloriesConsumed = 0.0;
    DataBaseLogFoods mDataBaseLogFoods = new DataBaseLogFoods(this);
    ArrayList<Foods> mArrayListLogFood;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    static boolean mIsToday = true;
    private DataBaseUserProfile mDataBaseUserProfile = null;
    private UserProfile mUserProfile = null;

    public static SharedPreferences getPrefs() {
        return Prefs;
    }

    private ConnectIQ.IQDeviceEventListener mDeviceEventListener = new ConnectIQ.IQDeviceEventListener() {
        @Override
        public void onDeviceStatusChanged(IQDevice device, IQDevice.IQDeviceStatus status) {
//            if (status == IQDeviceStatus.CONNECTED) {
//                connectStatus.setText("connected");
//            } else if (status == IQDeviceStatus.NOT_CONNECTED) {
//                connectStatus.setText("not connected");
//            } else if (status == IQDeviceStatus.NOT_PAIRED) {
//                connectStatus.setText("not paired");
//            } else if (status == IQDeviceStatus.UNKNOWN) {
//                connectStatus.setText("unknown");
//            }

            device.setStatus(status);
        }
    };

    ConnectIQ.ConnectIQListener mListenerSDKInitialize = new ConnectIQ.ConnectIQListener() {
        @Override
        public void onInitializeError(ConnectIQ.IQSdkErrorStatus errStatus) {
//            if( null != mTextView )
//                mTextView.setText(R.string.initialization_error + errStatus.name());
            mSdkReady = false;
        }

        @Override
        public void onSdkReady() {
            mSdkReady = true;

            // verifiy if Garmin device is saved on SharedPreferences
            SharedPreferences mPrefs = getPrefs();
            Gson gson = new Gson();
            String json = mPrefs.getString("garmin_device", "");
            mIQDevice = gson.fromJson(json, IQDevice.class);

            if (mIQDevice != null) {
                // Get our instance of ConnectIQ.  Since we initialized it
                // in our MainActivity, there is no need to do so here, we
                // can just get a reference to the one and only instance.
                mConnectIQ = ConnectIQ.getInstance();
                try {
                    mConnectIQ.registerForDeviceEvents(mIQDevice, new IQDeviceEventListener() {

                        @Override
                        public void onDeviceStatusChanged(IQDevice device, IQDeviceStatus status) {
//                            if (status == IQDeviceStatus.CONNECTED) {
//                                connectStatus.setText("connected");
//                            } else if (status == IQDeviceStatus.NOT_CONNECTED) {
//                                connectStatus.setText("not connected");
//                            } else if (status == IQDeviceStatus.NOT_PAIRED) {
//                                connectStatus.setText("not paired");
//                            } else if (status == IQDeviceStatus.UNKNOWN) {
//                                connectStatus.setText("unknown");
//                            }
                        }
                    });
                } catch (InvalidStateException e) {
//                Log.wtf(TAG, "InvalidStateException:  We should not be here!");
                }

                try {
                    mConnectIQ.registerForDeviceEvents(mIQDevice, mDeviceEventListener);
                } catch (InvalidStateException e) {
                    // This generally means you forgot to call initialize(), but since
                    // we are in the callback for initialize(), this should never happen
                }

                // Let's check the status of our application on the device.
                try {
                    mConnectIQ.getApplicationInfo(MY_APP, mIQDevice, new ConnectIQ.IQApplicationInfoListener() {

                        @Override
                        public void onApplicationInfoReceived(IQApp app) {

                            //mConnectIQApp = app;

                            // This is a good thing. Now we can show our list of message options.
//                        String[] options = getResources().getStringArray(R.array.send_message_display);
//                        String[] options = null;
//
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DeviceActivity.this, android.R.layout.simple_list_item_1, options);
//                        setListAdapter(adapter);

                        }

                        @Override
                        public void onApplicationNotInstalled(String applicationId) {
                            // The Comm widget is not installed on the device so we have
                            // to let the user know to install it.
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle(R.string.missing_widget);
                            dialog.setMessage(R.string.missing_widget_message);
                            dialog.setPositiveButton(android.R.string.ok, null);
                            dialog.create().show();
                        }

                    });
                } catch (InvalidStateException e1) {
                } catch (ServiceUnavailableException e1) {
                }

                // Let's register to receive messages from our application on the device.
                try {
                    mConnectIQ.registerForAppEvents(mIQDevice, mConnectIQApp, new IQApplicationEventListener() {

                        @Override
                        public void onMessageReceived(IQDevice device, IQApp app, List<Object> message, ConnectIQ.IQMessageStatus status) {

                            // We know from our Comm sample widget that it will only ever send us strings, but in case
                            // we get something else, we are simply going to do a toString() on each object in the
                            // message list.
                            StringBuilder builder = new StringBuilder();

                            ArrayList<Integer> theMessage = new ArrayList<Integer>();
                            ArrayList<Measurement> measurementList = new ArrayList<Measurement>();

                            theMessage = (ArrayList<Integer>) message.get(0);
                            if(theMessage.get(0) == HISTORIC_HR_COMMAND) {
                                // Get the user data to store on each measurement
                                Iterator<Integer> iteratorTheMessage = theMessage.iterator();
                                iteratorTheMessage.next(); // command ID
                                iteratorTheMessage.next(); // random

                                while (iteratorTheMessage.hasNext()) {
                                    Measurement measurement = new Measurement();
                                    measurement.setDate(iteratorTheMessage.next());
                                    measurement.setHRValue(iteratorTheMessage.next());
                                    measurementList.add(measurement);
                                }

                                // reverse the list order, to get the values in date ascending order
                                Collections.reverse(measurementList);

                                // finally write the measurement list to database
                                new DataBaseHR(getApplication().getApplicationContext()).DataBaseWriteMeasurement(measurementList);

                                drawGraphs();

                            } else if (theMessage.get(0) == USER_DATA_COMMAND) {
                                // Store the UserProfile on database
                                Iterator<Integer> iteratorTheMessage = theMessage.iterator();
                                iteratorTheMessage.next(); // comdataBaseUserProfilemand ID
                                iteratorTheMessage.next(); // random
                                mUserProfile.setDate(mMidNightToday);
                                mUserProfile.setUserBirthYear(iteratorTheMessage.next());
                                mUserProfile.setUserGender(iteratorTheMessage.next());
                                mUserProfile.setUserHeight(iteratorTheMessage.next());
                                mUserProfile.setUserWeight(iteratorTheMessage.next());
                                mUserProfile.setUserActivityClass(iteratorTheMessage.next());
                                mDataBaseUserProfile.DataBaseUserProfileWrite(mUserProfile);

                                drawGraphs();
                            }

                            if (message.size() > 0) {
                                for (Object o : message) {
                                    builder.append(o.toString());
                                    builder.append("\r\n");
                                }
                            } else {
                                builder.append("Received an empty message from the application");
                            }

                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle(R.string.received_message);
                            dialog.setMessage(builder.toString());
                            dialog.setPositiveButton(android.R.string.ok, null);
                            dialog.create().show();
                        }
                    });

//                        // Start by sending the USER_DATA_COMMAND, which is needed to get the user information at app start
//                        ArrayList<Integer> command = new ArrayList<Integer>();
//                        command.add(USER_DATA_COMMAND);
//                        Random r = new Random();
//                        command.add(r.nextInt(2^30));
//                        sendMessage(command);

                } catch (InvalidStateException e) {
                    Toast.makeText(getApplication().getApplicationContext(), "ConnectIQ is not in a valid state", Toast.LENGTH_LONG).show();
                }
            } else {
                // Since there is no IQDevice saved on SharedPreferences, start the activity for user
                // select the IQDevice
                Intent intent = new Intent(getApplication().getApplicationContext(), ConnectActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onSdkShutDown() {
            mSdkReady = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Daily Fat Control");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the UserProfile from the database
        mDataBaseUserProfile = new DataBaseUserProfile(getApplication().getApplicationContext());
        mUserProfile = mDataBaseUserProfile.DataBaseUserProfileLast();
        if (mUserProfile == null) { // in the case there is no data on the database
            mUserProfile = new UserProfile();
            mUserProfile.setDate(0);
            mUserProfile.setUserBirthYear(0);
            mUserProfile.setUserGender(0);
            mUserProfile.setUserHeight(0);
            mUserProfile.setUserWeight(0);
            mUserProfile.setUserActivityClass(0);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication().getApplicationContext(), LogFoodMainActivity.class);
                startActivity(intent);
            }
        });

        Prefs = getSharedPreferences(MainActivity.PREFERENCES, MODE_PRIVATE);
        mConnectIQ = ConnectIQ.getInstance(this, ConnectIQ.IQConnectType.WIRELESS);

        // Initialize the SDK
        mConnectIQ.initialize(this, true, mListenerSDKInitialize);


        mDateTitle = (TextView) findViewById(R.id.date_title);
        mTextViewCaloriesCalc = (TextView) findViewById(R.id.calories_calc);
        listViewLogFoodList = (ListView) findViewById(R.id.log_food_list);
        listViewLogFoodList.setLongClickable(true);

        // Edit or delete a food from the list
        listViewLogFoodList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                final int position = pos;

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Manage logged food")
                        .setMessage("You can edit or delete this food.")
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, EditLoggedFoodActivity.class);
                                Foods food = (Foods) listViewLogFoodList.getItemAtPosition(position);
                                intent.putExtra("FOOD_ID", food.getId());
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Get the food object pointed by position, get the food ID to delete it
                                Foods food = (Foods) listViewLogFoodList.getItemAtPosition(position);
                                mDataBaseLogFoods.DataBaseLogFoodsDeleteFood(food.getId());

                                onResume(); // refresh the view by calling the onResume()
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .show();

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Calc and set graph initial and final dates (midnight today and rightnow)
        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
        long rightNowMillis = rightNow.getTimeInMillis() + offset;
        long sinceMidnightToday = rightNowMillis % (24 * 60 * 60 * 1000);
        long midNightToday = rightNowMillis - sinceMidnightToday;
        long now = rightNowMillis / 1000; // now in seconds
        midNightToday /= 1000; // now in seconds
        mMidNightToday = midNightToday;
        mGraphInitialDate = midNightToday;
        mGraphFinalDate = now;

        drawGraphs();
        drawListConsumedFoods();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        ArrayList<Integer> command = new ArrayList<Integer>();

        if (id == R.id.connect) {
            // Handle the connect action
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.update) {
            // If UserProfile is not updated on the database, send the command to get that information
            if (mUserProfile.getDate() < mMidNightToday)
            {
                command.add(USER_DATA_COMMAND);
                Random r = new Random();
                command.add(r.nextInt(2^30));
                sendMessage(command);
            }

            command.add(HISTORIC_HR_COMMAND);
            Random r = new Random();
            command.add(r.nextInt(2^30));
            long date = new DataBaseHR(getApplication().getApplicationContext()).DataBaseGetLastMeasurementDate();
            command.add((int) date);
            sendMessage(command);
            return true;

        } else if (id == R.id.user_profile) {
            command.add(USER_DATA_COMMAND);
            Random r = new Random();
            command.add(r.nextInt(2^30));
            sendMessage(command);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void sendMessage(ArrayList<Integer> command) {
        try {
            mConnectIQ.sendMessage(mIQDevice, mConnectIQApp, command, new IQSendMessageListener() {

                @Override
                public void onMessageStatus(IQDevice device, IQApp app, IQMessageStatus status) {
                }

            });
        } catch (InvalidStateException e) {
            Toast.makeText(this, "ConnectIQ is not in a valid state", Toast.LENGTH_SHORT).show();
        } catch (ServiceUnavailableException e) {
            Toast.makeText(this, "ConnectIQ service is unavailable. Is Garmin Connect Mobile installed and running?", Toast.LENGTH_LONG).show();
        }
    }

    void drawGraphs() {
        GraphData graphDataObj = new GraphData(getApplication().getApplicationContext());
        List<Entry> graphDataCaloriesEER = graphDataObj.prepareCaloriesEER(mGraphInitialDate, mGraphFinalDate, mUserProfile);
        mCaloriesEER = graphDataObj.getCaloriesEER();
        List<Entry> graphDataCaloriesActive = graphDataObj.prepareCaloriesActive(mGraphInitialDate, mGraphFinalDate, mCaloriesEER, mUserProfile);
        List<Entry> graphDataCaloriesConsumed = graphDataObj.prepareCaloriesConsumed(mGraphInitialDate, mGraphFinalDate, mCaloriesEER);


        if (graphDataCaloriesActive != null && graphDataCaloriesEER != null && graphDataCaloriesConsumed != null) {
            mCaloriesActive = graphDataObj.getCaloriesActive();
            mCaloriesConsumed = graphDataObj.getCaloriesConsumed();

            /** Draw the various calories sizes */
            SpannableStringBuilder builder = new SpannableStringBuilder();

            int caloriesSpent = (int) (mCaloriesEER + mCaloriesActive);
            SpannableString caloriesSpentString = new SpannableString(String.valueOf(caloriesSpent));
            caloriesSpentString.setSpan(new ForegroundColorSpan(Color.rgb(0, 204, 137)), 0, caloriesSpentString.length(), 0);
            builder.append(caloriesSpentString);
            builder.append(" - ");
            SpannableString caloriesConsumedString = new SpannableString(String.valueOf((int) mCaloriesConsumed));
            caloriesConsumedString.setSpan(new ForegroundColorSpan(Color.rgb(204, 204 , 0)), 0, caloriesConsumedString.length(), 0);
            builder.append(caloriesConsumedString);
            int caloriesResult = (int) (caloriesSpent - mCaloriesConsumed);
            if (caloriesResult < 0) {
                SpannableString caloriesResultString = new SpannableString(String.valueOf(caloriesResult));
                caloriesResultString.setSpan(new ForegroundColorSpan(Color.RED), 0, caloriesResultString.length(), 0);
                builder.append(" = ");
                builder.append(caloriesResultString);
            } else {
                builder.append(" = " + caloriesResult);
            }
            mTextViewCaloriesCalc.setText(builder, TextView.BufferType.SPANNABLE);

            // add entries to Calories Active dataset
            LineDataSet dataSetCaloriesActive = new LineDataSet(graphDataCaloriesActive, "Burned calories");
            dataSetCaloriesActive.setColor(Color.rgb(0, 204, 137));
            dataSetCaloriesActive.setCubicIntensity(1f);
            dataSetCaloriesActive.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            dataSetCaloriesActive.setFillColor(Color.rgb(0, 230, 154));
            dataSetCaloriesActive.setFillAlpha(127);
            dataSetCaloriesActive.setDrawFilled(true);
            dataSetCaloriesActive.setDrawHighlightIndicators(false);
            dataSetCaloriesActive.setHighlightLineWidth(2f);
            dataSetCaloriesActive.setDrawValues(false);
            dataSetCaloriesActive.setLineWidth(2f);
            dataSetCaloriesActive.setDrawCircles(false);

            // add entries to Calories consumed dataset
            LineDataSet dataSetCaloriesConsumed = new LineDataSet(graphDataCaloriesConsumed, "Consumed calories");
            dataSetCaloriesConsumed.setColor(Color.rgb(204, 204 , 0));
            dataSetCaloriesConsumed.setMode(LineDataSet.Mode.LINEAR);
            dataSetCaloriesConsumed.setFillColor(Color.rgb(230, 230, 0));
            dataSetCaloriesConsumed.setFillAlpha(255);
            dataSetCaloriesConsumed.setDrawFilled(true);
            dataSetCaloriesConsumed.setHighlightEnabled(false);
            dataSetCaloriesConsumed.setDrawValues(false);
            dataSetCaloriesConsumed.setLineWidth(2f);
            dataSetCaloriesConsumed.setDrawCircles(false);

//            // add entries to Calories EER dataset
//            LineDataSet dataSetCaloriesEER = new LineDataSet(graphDataCaloriesEER, "No active cals");
//            dataSetCaloriesEER.setColor(Color.rgb(0, 172 , 117));
//            dataSetCaloriesEER.setMode(LineDataSet.Mode.LINEAR);
//            dataSetCaloriesEER.setFillColor(Color.rgb(0, 172, 117));
//            dataSetCaloriesEER.setFillAlpha(240);
//            dataSetCaloriesEER.setDrawFilled(true);
//            dataSetCaloriesEER.setHighlightEnabled(false);
//            dataSetCaloriesEER.setDrawValues(false);
//            dataSetCaloriesEER.setLineWidth(0);
//            dataSetCaloriesEER.setDrawCircles(false);

            LineData lineData = new LineData(dataSetCaloriesActive, dataSetCaloriesConsumed);
//            LineData lineData = new LineData(dataSetCaloriesActive);

            // in this example, a LineChart is initialized from xml
            LineChart mChart = (LineChart) findViewById(R.id.chart_calories_active);

            Legend l = mChart.getLegend();
            l.setEnabled(true);

            // enable touch gestures
//            mChart.setTouchEnabled(true);
            mChart.setTouchEnabled(false);
//            mChart.setOnChartGestureListener(this);
//            mChart.setOnChartValueSelectedListener(this);

            mChart.setBackgroundColor(Color.WHITE);
            mChart.setDrawGridBackground(false);
            mChart.setDrawBorders(true);

            mChart.setDoubleTapToZoomEnabled(false);

            // enable scaling and dragging
//            chart.setDragEnabled(true);
//            chart.setScaleEnabled(true);
            mChart.setScaleXEnabled(false);     
            mChart.setScaleYEnabled(false);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.GRAY);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(true);
            xAxis.setAxisMinimum(0f);
            xAxis.setAxisMaximum(23.983f); //23h59m
            xAxis.setGranularity(0.016666667f); //1m
            xAxis.setValueFormatter(new IAxisValueFormatter() {

                private SimpleDateFormat mFormat = new SimpleDateFormat("H'h'mm");
                public String getFormattedValue(float value, AxisBase axis) {
                    if ((value % 1) == 0) {
                        mFormat = new SimpleDateFormat("H'h'");
                    } else {
                        mFormat = new SimpleDateFormat("H'h'mm");
                    }
                    return mFormat.format(new Date((long) ((value-1) *60*60*1000)));
                }
            });

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setEnabled(true);
            leftAxis.setGranularity(1);
            leftAxis.setAxisMinimum(0);
            leftAxis.setDrawTopYLabelEntry(true);

            // adjust max y axis value
//            if ((mCaloriesEER + mCaloriesActive) > 3000) {
//                leftAxis.resetAxisMaximum();
//            } else {
//                leftAxis.setAxisMaximum(800);
//            }

            leftAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value < (mCaloriesEER /10)) {
                        return "";
                    } else {
                        value = value - ((float) mCaloriesEER / 10);
                    }

                    return Integer.toString((int) value);
                }
            });


            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setEnabled(true);
            rightAxis.setGranularity(1);
            rightAxis.setAxisMinimum(0);
            rightAxis.setDrawTopYLabelEntry(true);

//            LimitLine ll1 = new LimitLine((float) mCaloriesEER/10, "");
//            ll1.setLineWidth(2f);
//            ll1.disableDashedLine();
//            leftAxis.addLimitLine(ll1);

            rightAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value < (mCaloriesEER /10)) {
                        value *= 10;
                    } else {
                        value = (value - (float) (mCaloriesEER /10)) + (float) mCaloriesEER;
                    }
                    return Integer.toString((int) value);
                }
            });

            // adjust max y axis value
//            if ((mCaloriesEER + mCaloriesActive) > 3000) {
//                rightAxis.resetAxisMaximum();
//            } else {
//                rightAxis.setAxisMaximum(800);
//            }


            // no description text
            mChart.getDescription().setEnabled(false);

            mChart.setAutoScaleMinMaxEnabled(false);
            mChart.setData(lineData);
            mChart.invalidate(); // refresh
        }
    }

    void drawListConsumedFoods () {
        // Populate the listview of logged foods
        // Start by getting the data from the database and then put on the array adapter, finally to the list
        mArrayListLogFood = mDataBaseLogFoods.DataBaseLogFoodsGetFoods(mGraphInitialDate*1000, mGraphFinalDate*1000);
        ArrayAdapter<Foods> arrayAdapterLogFoods = new LogFoodAdapter(this, mArrayListLogFood);
        listViewLogFoodList.setAdapter(arrayAdapterLogFoods);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        // write the selected value of Y value (calories on the point selected)

//        float date = e.getX();
//        SimpleDateFormat mFormat;
//        mFormat = new SimpleDateFormat("H'h'mm");
//        String dateString = mFormat.format(new Date((long) ((date-1) *60*60*1000)));
//
//        float value = 0;
//        if (e.getY() < (mCaloriesEER/10)) {
//            value = e.getY()*10;
//        } else {
////            value = (float) (e.getY()*10 - mCaloriesEER);
//            value = (float) (mCaloriesEER + (e.getY() - (mCaloriesEER / 10)));
////            value = value + (float) mCaloriesEER;
//        }
//
//        textViewCalories1.setText(dateString + " total calories " + Integer.toString((int) (value)));
//        textViewCalories2.setText("active calories " + Integer.toString(((int) value) - (int) (mCaloriesEER)));
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//        textViewCalories1.setText("total calories: " + Integer.toString((int) (mCaloriesEER + mCaloriesActive)));
//        textViewCalories2.setText("active calories: " + Integer.toString((int) mCaloriesActive));
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onNothingSelected() {}

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 < x1)
                    {
                        if (mIsToday == true) break;

                        mGraphInitialDate += SECONDS_24H; // seconds

                        if (mGraphInitialDate == mMidNightToday) { // today
                            mDateTitle.setText("today");

                            Calendar rightNow = Calendar.getInstance();
                            long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
                            long rightNowMillis = rightNow.getTimeInMillis() + offset;
                            mGraphFinalDate = rightNowMillis / 1000; // seconds

                            mIsToday = true;

                        } else if (mGraphInitialDate == (mMidNightToday - SECONDS_24H)) { // yesterday
                            mDateTitle.setText("yesterday");
                            mGraphFinalDate = mGraphInitialDate + SECONDS_24H; // seconds

                        } else { // other days
                            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");
                            String dateString = formatter.format(new Date(mGraphInitialDate * 1000L));
                            mDateTitle.setText(dateString);
                            mGraphFinalDate = mGraphInitialDate + SECONDS_24H; // seconds
                        }

                        drawGraphs();
                        drawListConsumedFoods();
                    }

                    // Right to left swipe action
                    else
                    {
                        mIsToday = false;

                        mGraphInitialDate -= SECONDS_24H; // seconds

                        if (mGraphInitialDate == (mMidNightToday - SECONDS_24H)) { // yesterday
                            mDateTitle.setText("yesterday");
                            mGraphFinalDate = mGraphInitialDate + SECONDS_24H; // seconds

                        } else { // other days
                            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");
                            String dateString = formatter.format(new Date(mGraphInitialDate * 1000L));
                            mDateTitle.setText(dateString);
                            mGraphFinalDate = mGraphInitialDate + SECONDS_24H; // seconds
                        }

                        drawGraphs();
                        drawListConsumedFoods();
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}

class LogFoodAdapter extends ArrayAdapter<Foods> {
    public LogFoodAdapter(Context context, ArrayList<Foods> foodsArrayList) {
        super(context, 0, foodsArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Foods food = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.logged_food, parent, false);
        }
        // Lookup view for data population
        TextView textViewFoodName = (TextView) convertView.findViewById(R.id.food_name);
        TextView textViewFoodBrand = (TextView) convertView.findViewById(R.id.food_brand);
        TextView textViewFoodCaloriesLogged = (TextView) convertView.findViewById(R.id.food_calories_logged);
        TextView textViewFoodUnitsLogged = (TextView) convertView.findViewById(R.id.food_units_logged);
        TextView textViewFoodUnitsType = (TextView) convertView.findViewById(R.id.food_units_type);
        // Populate the data into the template view using the data object
        textViewFoodName.setText(food.getName());
        textViewFoodBrand.setText(food.getBrand());
        textViewFoodCaloriesLogged.setText(Integer.toString(food.getCaloriesLogged()));

        // remove trailing zeros of units logged
        DecimalFormat df = new DecimalFormat();
        String units = df.format(food.getUnitsLogged());
        textViewFoodUnitsLogged.setText(units);

        textViewFoodUnitsType.setText(food.getUnitType());
        // Return the completed view to render on screen
        return convertView;
    }
}