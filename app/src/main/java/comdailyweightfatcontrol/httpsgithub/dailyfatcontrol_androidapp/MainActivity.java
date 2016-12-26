package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ConnectIQ mConnectIQ;
    private IQDevice mIQDevice;
    private IQDeviceAdapter mAdapter;
    private boolean mSdkReady = false;
    public static final String MY_APP = "3F3D83A85F584671A551EA1316623AD7";
    private IQApp mConnectIQApp = new IQApp(MY_APP);
    public static final int HISTORIC_HR_COMMAND = 104030201;
    public static final int USER_DATA_COMMAND = 204030201;
    private TextView mTextView;

    private ConnectIQ.IQDeviceEventListener mDeviceEventListener = new ConnectIQ.IQDeviceEventListener() {

        @Override
        public void onDeviceStatusChanged(IQDevice device, IQDevice.IQDeviceStatus status) {
            mAdapter.updateDeviceStatus(device, status);
        }

    };

    private ConnectIQ.ConnectIQListener mListenerSDKInitialize = new ConnectIQ.ConnectIQListener() {
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
            SharedPreferences mPrefs = getSharedPreferences("MainSharedPreferences", MODE_PRIVATE);
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
                            // Since we will only get updates for this device, just display the status
//                        mDeviceStatus.setText(status.name());
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mTextView = (TextView)findViewById(R.id.text_view);

        // Here we are specifying that we want to use a WIRELESS bluetooth connection.
        // We could have just called getInstance() which would by default create a version
        // for WIRELESS, unless we had previously gotten an instance passing TETHERED
        // as the connection type.
        mConnectIQ = ConnectIQ.getInstance(this, ConnectIQ.IQConnectType.WIRELESS);

        // Initialize the SDK
        mConnectIQ.initialize(this, true, mListenerSDKInitialize);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        ArrayList<Integer> command = new ArrayList<Integer>();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.connect) {
            // Handle the connect action
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);

        } else if (id == R.id.about) {

        } else if (id == R.id.user_profile) {
            command.add(USER_DATA_COMMAND);
            Random r = new Random();
            command.add(r.nextInt(2^30));

            sendMessage(command);

        } else if (id == R.id.hr_5m) {
            command.add(HISTORIC_HR_COMMAND);
            Random r = new Random();
            command.add(r.nextInt(2^30));
            int millis = (int) (System.currentTimeMillis() / 1000);
            command.add(millis - (5 * 60));
            sendMessage(command);

        } else if (id == R.id.hr_10m) {
            command.add(HISTORIC_HR_COMMAND);
            Random r = new Random();
            command.add(r.nextInt(2^30));
            int millis = (int) (System.currentTimeMillis() / 1000);
            command.add(millis - (10 * 60));
            sendMessage(command);

        } else if (id == R.id.hr_20m) {
            command.add(HISTORIC_HR_COMMAND);
            Random r = new Random();
            command.add(r.nextInt(2^30));
            int millis = (int) (System.currentTimeMillis() / 1000);
            command.add(millis - (20 * 60));
            sendMessage(command);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}