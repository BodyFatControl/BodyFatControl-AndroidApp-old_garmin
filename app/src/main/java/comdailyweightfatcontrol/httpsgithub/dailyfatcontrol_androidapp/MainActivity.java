package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener,
        OnChartGestureListener, NavigationView.OnNavigationItemSelectedListener {

    private Tracker mTracker;
    private ConnectIQ mConnectIQ;
    private IQDevice mIQDevice;
    private IQDeviceAdapter iqDeviceAdapter;
    private boolean mSdkReady = false;
    public static final String MY_APP = "3F3D83A85F584671A551EA1316623AD7";
    private IQApp mConnectIQApp = new IQApp(MY_APP);
    public static final int ALIVE_COMMAND = 154030201;
    public static final int HISTORIC_CALS_COMMAND = 104030201;
    private static final int USER_DATA_COMMAND = 204030201;
    private static final int CALORIES_CONSUMED_COMMAND = 304030201;
    NavigationView navigationView;
    private CustomDrawerLayout mDrawerLayout;
    private TextView mTextViewCaloriesCalc;
    private ListView listViewLogFoodList;
    private TextView mDateTitle;
    private TextView textViewLastUpdateDate;
    private ImageView mImageViewConnect;
    public static String PREFERENCES = "MainSharedPreferences";
    public static SharedPreferences Prefs;
    public static long mMidNightToday;
    private long mGraphInitialDate;
    private long mGraphFinalDate;
    private long mLastUpdateDate = 0;
    public static final long SECONDS_24H = 24*60*60;
    public static double mUserCaloriesEER = 0.0;
    public static double userCaloriesEERPerMinute = 0.0;
    private double mCurrentCaloriesEER = 0.0;
    private double mCaloriesActive = 0.0;
    private double mCaloriesConsumed = 0.0;
    DataBaseLogFoods mDataBaseLogFoods = new DataBaseLogFoods(this);
    ArrayList<Object> mArrayListLogFood;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    static boolean mIsToday = true;
    private DataBaseUserProfile mDataBaseUserProfile = null;
    private UserProfile mUserProfile = null;
    private Context mContext;

    public static SharedPreferences getPrefs() {
        return Prefs;
    }

    private ConnectIQ.IQDeviceEventListener mDeviceEventListener = new ConnectIQ.IQDeviceEventListener() {
        @Override
        public void onDeviceStatusChanged(IQDevice device, IQDevice.IQDeviceStatus status) {
            if (status == IQDeviceStatus.CONNECTED) {
                mImageViewConnect.setVisibility(View.VISIBLE);
            } else if (status == IQDeviceStatus.NOT_CONNECTED) {
                mImageViewConnect.setVisibility(View.INVISIBLE);
            } else if (status == IQDeviceStatus.NOT_PAIRED) {
                mImageViewConnect.setVisibility(View.INVISIBLE);
            } else if (status == IQDeviceStatus.UNKNOWN) {
                mImageViewConnect.setVisibility(View.INVISIBLE);
            }

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
                            if (status == IQDeviceStatus.CONNECTED) {
                                mImageViewConnect.setVisibility(View.VISIBLE);
                            } else if (status == IQDeviceStatus.NOT_CONNECTED) {
                                mImageViewConnect.setVisibility(View.INVISIBLE);
                            } else if (status == IQDeviceStatus.NOT_PAIRED) {
                                mImageViewConnect.setVisibility(View.INVISIBLE);
                            } else if (status == IQDeviceStatus.UNKNOWN) {
                                mImageViewConnect.setVisibility(View.INVISIBLE);
                            }
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
                            if(theMessage.get(0) == ALIVE_COMMAND) {
                                // The app is ready to receive commands, send command to ask for
                                // historic HR
                                ArrayList<Integer> command = new ArrayList<>();
                                // If UserProfile is not updated on the database, send the command to get that information
                                if (mUserProfile.getDate() < mMidNightToday)
                                {
                                    command.add(USER_DATA_COMMAND);
                                    sendMessage(command);
                                }

                                command.add(HISTORIC_CALS_COMMAND);
                                long date = new DataBaseCalories(mContext).DataBaseGetLastMeasurementDate();
                                command.add((int) date / 60);
                                sendMessage(command);

                            } else if(theMessage.get(0) == HISTORIC_CALS_COMMAND) {
                                // Get the user data to store on each measurement
                                Iterator<Integer> iteratorTheMessage = theMessage.iterator();
                                iteratorTheMessage.next(); // command ID
                                long date = (iteratorTheMessage.next() * 60);
                                while (iteratorTheMessage.hasNext()) {
                                    Measurement measurement = new Measurement();
                                    measurement.setDate((int) date);
                                    date -= 60;
                                    measurement.setCalories(iteratorTheMessage.next());
                                    measurementList.add(measurement);
                                }

                                // reverse the list order, to get the values in date ascending order
                                Collections.reverse(measurementList);

                                // finally write the measurement list to database
                                new DataBaseCalories(mContext).DataBaseWriteMeasurement(measurementList);

                                // Set current date as LastUpdateDate
                                Calendar rightNow = Calendar.getInstance();
                                long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
                                long rightNowMillis = rightNow.getTimeInMillis() + offset;
                                long now = rightNowMillis / 1000; // now in seconds

                                    long s = now % 60;
                                    long m = (now / 60) % 60;
                                    long h = (now / (60 * 60)) % 24;
                                    String string = String.format("%dh%02dm%02ds", h, m, s);
                                    textViewLastUpdateDate.setText(string);

                                mLastUpdateDate = rightNowMillis;

                                drawGraphs();

                            } else if (theMessage.get(0) == USER_DATA_COMMAND) {
                                // Store the UserProfile on database
                                Iterator<Integer> iteratorTheMessage = theMessage.iterator();
                                iteratorTheMessage.next(); // command ID
                                mUserProfile.setDate(mMidNightToday);
                                mUserProfile.setUserBirthYear(iteratorTheMessage.next());
                                mUserProfile.setUserGender(iteratorTheMessage.next());
                                mUserProfile.setUserHeight(iteratorTheMessage.next());
                                mUserProfile.setUserWeight(iteratorTheMessage.next());
                                mUserProfile.setUserActivityClass(iteratorTheMessage.next());
                                mUserProfile.setUserEERCaloriesPerMinute(iteratorTheMessage.next());
                                mDataBaseUserProfile.DataBaseUserProfileWrite(mUserProfile);

                                userCaloriesEERPerMinute = mUserProfile.getUserEERCaloriesPerMinute();

                                drawGraphs();
                            }
                        }
                    });

                } catch (InvalidStateException e) {
                    Toast.makeText(mContext, "ConnectIQ is not in a valid state", Toast.LENGTH_LONG).show();
                }
            } else {
                // Since there is no IQDevice saved on SharedPreferences, start the activity for user
                // select the IQDevice
                Intent intent = new Intent(mContext, ConnectActivity.class);
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
        setTitle("Daily Fat Control - v0.7");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (CustomDrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(Color.BLACK);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mContext = getApplication().getApplicationContext();

        // [START shared_tracker]
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        // [END shared_tracker]

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("onCreate")
                .build());

        // Get the UserProfile from the database
        mDataBaseUserProfile = new DataBaseUserProfile(mContext);
        mUserProfile = mDataBaseUserProfile.DataBaseUserProfileLast();
        if (mUserProfile == null) { // in the case there is no data on the database
            mUserProfile = new UserProfile();
            mUserProfile.setDate(0);
            mUserProfile.setUserBirthYear(0);
            mUserProfile.setUserGender(0);
            mUserProfile.setUserHeight(0);
            mUserProfile.setUserWeight(0);
            mUserProfile.setUserActivityClass(0);
            mUserProfile.setUserEERCaloriesPerMinute(0);
        }
        userCaloriesEERPerMinute = mUserProfile.getUserEERCaloriesPerMinute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("LogFoodMainActivity")
                        .build());

                Intent intent = new Intent(mContext, LogFoodMainActivity.class);
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
        textViewLastUpdateDate = (TextView) findViewById(R.id.last_update_date);
        mImageViewConnect = (ImageView) findViewById(R.id.connect);
        mImageViewConnect.setVisibility(View.INVISIBLE);

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

    //@SuppressWarnings("StatementWithEmptyBody")
    //@Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.ic_menu_home){

        } else if (id == R.id.ic_menu_connect){
            // Handle the connect action
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("ConnectActivity")
                    .build());
        } else if (id == R.id.ic_menu_about){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("onResume")
                .build());

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
        GraphData graphDataObj = new GraphData(mContext, mGraphInitialDate, mGraphFinalDate);
        List<Entry> graphDataCalories = graphDataObj.prepareCalories();
        mCurrentCaloriesEER = graphDataObj.getCurrentCaloriesEER();
        List<Entry> graphDataCaloriesConsumed = graphDataObj.prepareCaloriesConsumed();

        if (graphDataCalories != null && graphDataCaloriesConsumed != null) {
            final int caloriesSpentColor = ContextCompat.getColor(mContext, R.color.graphCaloriesSpent);
            final int caloriesSpentLineColor = ContextCompat.getColor(mContext, R.color.graphCaloriesSpentLine);
            final int caloriesConsumedColor = ContextCompat.getColor(mContext, R.color.graphCaloriesConsumed);
            final int caloriesConsumedLineColor = ContextCompat.getColor(mContext, R.color.graphCaloriesConsumedLine);

            mCaloriesActive = graphDataObj.getCaloriesActive();
            mCaloriesConsumed = graphDataObj.getCaloriesConsumed();

            /** Draw the various calories sizes */
            SpannableStringBuilder builder = new SpannableStringBuilder();

            int caloriesSpent = (int) (mCurrentCaloriesEER + mCaloriesActive);
            SpannableString spannableString = new SpannableString(Integer.toString((int) mCaloriesActive));
            spannableString.setSpan(new ForegroundColorSpan(caloriesSpentLineColor), 0, spannableString.length(), 0);
            builder.append(spannableString);
            builder.append(" + ");
            spannableString = new SpannableString(Integer.toString((int) mCurrentCaloriesEER));
            spannableString.setSpan(new ForegroundColorSpan(caloriesSpentLineColor), 0, spannableString.length(), 0);
            builder.append(spannableString);
            builder.append(" - ");
            spannableString = new SpannableString(String.valueOf((int) mCaloriesConsumed));
            spannableString.setSpan(new ForegroundColorSpan(caloriesConsumedLineColor), 0, spannableString.length(), 0);
            builder.append(spannableString);
            int caloriesResult = (int) (caloriesSpent - mCaloriesConsumed);
            if (caloriesResult < 0) {
                spannableString = new SpannableString(String.valueOf(caloriesResult));
                spannableString.setSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(mContext, R.color.caloriesResultValue)), 0, spannableString.length(), 0);
                builder.append(" = ");
                builder.append(spannableString);
            } else {
                builder.append(" = " + caloriesResult);
            }
            mTextViewCaloriesCalc.setText(builder, TextView.BufferType.SPANNABLE);

            // add entries to Calories Active dataset
            LineDataSet dataSetCaloriesActive = new LineDataSet(graphDataCalories, "Burned calories");
            dataSetCaloriesActive.setColor(caloriesSpentLineColor);
            dataSetCaloriesActive.setCubicIntensity(1f);
            dataSetCaloriesActive.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            dataSetCaloriesActive.setFillColor(caloriesSpentColor);
            dataSetCaloriesActive.setFillAlpha(127);
            dataSetCaloriesActive.setDrawFilled(true);
            dataSetCaloriesActive.setDrawHighlightIndicators(false);
            dataSetCaloriesActive.setHighlightLineWidth(2f);
            dataSetCaloriesActive.setDrawValues(false);
            dataSetCaloriesActive.setLineWidth(2f);
            dataSetCaloriesActive.setDrawCircles(false);

            // add entries to Calories consumed dataset
            LineDataSet dataSetCaloriesConsumed = new LineDataSet(graphDataCaloriesConsumed, "Consumed calories");
            dataSetCaloriesConsumed.setColor(caloriesConsumedLineColor);
            dataSetCaloriesConsumed.setMode(LineDataSet.Mode.LINEAR);
            dataSetCaloriesConsumed.setFillColor(caloriesConsumedColor);
            dataSetCaloriesConsumed.setFillAlpha(255);
            dataSetCaloriesConsumed.setDrawFilled(true);
            dataSetCaloriesConsumed.setHighlightEnabled(false);
            dataSetCaloriesConsumed.setDrawValues(false);
            dataSetCaloriesConsumed.setLineWidth(2f);
            dataSetCaloriesConsumed.setDrawCircles(false);

            LineData lineData = new LineData(dataSetCaloriesActive, dataSetCaloriesConsumed);

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
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
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
//            if ((mCurrentCaloriesEER + mCaloriesActive) > 3000) {
//                leftAxis.resetAxisMaximum();
//            } else {
//                leftAxis.setAxisMaximum(800);
//            }

            leftAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value < (mCurrentCaloriesEER /10)) {
                        return "";
                    } else {
                        value = value - ((float) mCurrentCaloriesEER / 10);
                    }

                    return Integer.toString((int) value);
                }
            });


            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setEnabled(true);
            rightAxis.setGranularity(1);
            rightAxis.setAxisMinimum(0);
            rightAxis.setDrawTopYLabelEntry(true);

//            LimitLine ll1 = new LimitLine((float) mCurrentCaloriesEER/10, "");
//            ll1.setLineWidth(2f);
//            ll1.disableDashedLine();
//            leftAxis.addLimitLine(ll1);

            rightAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value < (mCurrentCaloriesEER /10)) {
                        value *= 10;
                    } else {
                        value = (value - (float) (mCurrentCaloriesEER /10)) + (float) mCurrentCaloriesEER;
                    }
                    return Integer.toString((int) value);
                }
            });

            // adjust max y axis value
//            if ((mCurrentCaloriesEER + mCaloriesActive) > 3000) {
//                rightAxis.resetAxisMaximum();
//            } else {
//                rightAxis.setAxisMaximum(800);
//            }w


            // no description text
            mChart.getDescription().setEnabled(false);

            mChart.setAutoScaleMinMaxEnabled(false);
            mChart.setData(lineData);
            mChart.invalidate(); // refresh

            // Send the calories balance value (but only for today)
            if (mGraphInitialDate == mMidNightToday) { // today
                ArrayList<Integer> command = new ArrayList<>();
                command.add(CALORIES_CONSUMED_COMMAND);
                command.add((int) mCaloriesConsumed);
                sendMessage(command);
            }
        }
    }

    void drawListConsumedFoods () {
        // Populate the listview of logged foods
        // Start by getting the data from the database and then put on the array adapter, finally to the list
        mArrayListLogFood = mDataBaseLogFoods.DataBaseLogFoodsGetFoodsAndMeals(mGraphInitialDate*1000, mGraphFinalDate*1000);
        ArrayAdapter<Object> arrayAdapterLogFoods = new LogFoodAdapter(this, mArrayListLogFood);
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
//        if (e.getY() < (mCurrentCaloriesEER/10)) {
//            value = e.getY()*10;
//        } else {
////            value = (float) (e.getY()*10 - mCurrentCaloriesEER);
//            value = (float) (mCurrentCaloriesEER + (e.getY() - (mCurrentCaloriesEER / 10)));
////            value = value + (float) mCurrentCaloriesEER;
//        }
//
//        textViewCalories1.setText(dateString + " total calories " + Integer.toString((int) (value)));
//        textViewCalories2.setText("active calories " + Integer.toString(((int) value) - (int) (mCurrentCaloriesEER)));
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//        textViewCalories1.setText("total calories: " + Integer.toString((int) (mCurrentCaloriesEER + mCaloriesActive)));
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

class LogFoodAdapter extends ArrayAdapter<Object> {
    public static final int TYPE_FOOD  = 0;
    public static final int TYPE_MEALTIME  = 1;

    private ArrayList<Object> mFoodsArrayList;

    public LogFoodAdapter(Context context, ArrayList<Object> foodsArrayList) {
        super(context, 0, foodsArrayList);

        this.mFoodsArrayList = foodsArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        Object object = mFoodsArrayList.get(position);
        if (object instanceof Foods) return TYPE_FOOD;
        else return TYPE_MEALTIME;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Object object = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            if (getItemViewType(position) == TYPE_FOOD) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.logged_food, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.logged_food_mealtime, parent, false);
            }
        }

        if (getItemViewType(position) == TYPE_FOOD) {
            // Lookup view for data population
            TextView textViewFoodName = (TextView) convertView.findViewById(R.id.food_name);
            TextView textViewFoodCaloriesLogged = (TextView) convertView.findViewById(R.id.food_calories_logged);
            // Populate the data into the template view using the data object
            // Get the data item for this position
            Foods food = (Foods) object;
            textViewFoodName.setText(food.getName());
            textViewFoodCaloriesLogged.setText(Integer.toString(food.getCaloriesLogged()));

            // remove trailing zeros of units logged
            DecimalFormat df = new DecimalFormat();
            String units = df.format(food.getUnitsLogged());
            convertView.setClickable(false);
            convertView.setFocusable(false);
        } else {
            // Lookup view for data population
            TextView textViewMealTime = (TextView) convertView.findViewById(R.id.mealtime);
            TextView textViewMealTimeCalories = (TextView) convertView.findViewById(R.id.calories);
            // Populate the data into the template view using the data object
            // Get the data item for this position
            MealTime mealTime = (MealTime) object;
            textViewMealTime.setText(mealTime.getMealTimeName());
            // remove trailing zeros of units logged
            DecimalFormat df = new DecimalFormat();
            String units = df.format(mealTime.getCalories());
            textViewMealTimeCalories.setText(units);
            convertView.setClickable(true);
            convertView.setFocusable(true);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}

