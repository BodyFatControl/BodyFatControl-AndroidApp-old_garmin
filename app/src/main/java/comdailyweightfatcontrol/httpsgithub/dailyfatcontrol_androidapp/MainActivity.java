package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp.adapter.IQDeviceAdapter;
import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.ConnectIQListener;
import com.garmin.android.connectiq.ConnectIQ.IQConnectType;
import com.garmin.android.connectiq.ConnectIQ.IQDeviceEventListener;
import com.garmin.android.connectiq.ConnectIQ.IQSdkErrorStatus;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.IQDevice.IQDeviceStatus;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;
import com.garmin.android.connectiq.ConnectIQ.IQApplicationEventListener;

import com.google.gson.Gson;

import java.util.List;

import static comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp.DeviceActivity.MY_APP;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ConnectIQ mConnectIQ;
    private IQDevice mIQDevice;
    private IQDeviceAdapter mAdapter;
    private boolean mSdkReady = false;
    private IQApp mConnectIQApp = null;
    public static final String MY_APP = "a3421feed289106a538cb9547ab12095";

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
    }

    @Override
    public void onResume() {
        super.onResume();

        // verifiy if Garmin device is saved on SharedPreferences
        SharedPreferences mPrefs = getSharedPreferences("MainSharedPreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("garmin_device", "");
        IQDevice mIQDevice = gson.fromJson(json, IQDevice.class);

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

            // Let's check the status of our application on the device.
            try {
                mConnectIQ.getApplicationInfo(MY_APP, mIQDevice, new ConnectIQ.IQApplicationInfoListener() {

                    @Override
                    public void onApplicationInfoReceived(IQApp app) {

                        mConnectIQApp = app;

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
                Toast.makeText(this, "ConnectIQ is not in a valid state", Toast.LENGTH_LONG).show();
            }
        } else {
            // Since there is no IQDevice saved on SharedPreferences, start the activity for user
            // select the IQDevice
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);
        }
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.connect) {
            // Handle the connect action
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);

        } else if (id == R.id.about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
