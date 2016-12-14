/**
 * Copyright (C) 2015 Garmin International Ltd.
 * Subject to Garmin SDK License Agreement and Wearables Application Developer Agreement.
 */
package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.IQApplicationEventListener;
import com.garmin.android.connectiq.ConnectIQ.IQApplicationInfoListener;
import com.garmin.android.connectiq.ConnectIQ.IQDeviceEventListener;
import com.garmin.android.connectiq.ConnectIQ.IQMessageStatus;
import com.garmin.android.connectiq.ConnectIQ.IQSendMessageListener;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.IQDevice.IQDeviceStatus;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.List;

public class DeviceActivity extends ListActivity {

    public static final String IQDEVICE = "IQDevice";
    public static final String MY_APP = "a3421feed289106a538cb9547ab12095";

    private static final String TAG = DeviceActivity.class.getSimpleName();

    private TextView mDeviceName;
    private TextView mDeviceStatus;
    private ConnectIQ mConnectIQ;
    private IQDevice mDevice;
    private IQApp mMyApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_device);

        Intent intent = getIntent();
        mDevice = (IQDevice)intent.getParcelableExtra(IQDEVICE);
        mMyApp = new IQApp("a3421feed289106a538cb9547ab12095");

//        mDeviceName = (TextView)findViewById(R.id.devicename);
//        mDeviceStatus = (TextView)findViewById(R.id.devicestatus);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mDevice != null) {
            mDeviceName.setText(mDevice.getFriendlyName());
            mDeviceStatus.setText(mDevice.getStatus().name());

            // Get our instance of ConnectIQ.  Since we initialized it
            // in our MainActivity, there is no need to do so here, we
            // can just get a reference to the one and only instance.
            mConnectIQ = ConnectIQ.getInstance();
            try {
                mConnectIQ.registerForDeviceEvents(mDevice, new IQDeviceEventListener() {

                    @Override
                    public void onDeviceStatusChanged(IQDevice device, IQDeviceStatus status) {
                        // Since we will only get updates for this device, just display the status
                        mDeviceStatus.setText(status.name());
                    }

                });
            } catch (InvalidStateException e) {
                Log.wtf(TAG, "InvalidStateException:  We should not be here!");
            }

            // Let's check the status of our application on the device.
            try {
                mConnectIQ.getApplicationInfo(MY_APP, mDevice, new IQApplicationInfoListener() {

                    @Override
                    public void onApplicationInfoReceived(IQApp app) {
                        // This is a good thing. Now we can show our list of message options.
//                        String[] options = getResources().getStringArray(R.array.send_message_display);
                        String[] options = null;

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DeviceActivity.this, android.R.layout.simple_list_item_1, options);
                        setListAdapter(adapter);
                    }

                    @Override
                    public void onApplicationNotInstalled(String applicationId) {
                        // The Comm widget is not installed on the device so we have
                        // to let the user know to install it.
                        AlertDialog.Builder dialog = new AlertDialog.Builder(DeviceActivity.this);
//                        dialog.setTitle(R.string.missing_widget);
//                        dialog.setMessage(R.string.missing_widget_message);
                        dialog.setPositiveButton(android.R.string.ok, null);
                        dialog.create().show();
                    }

                });
            } catch (InvalidStateException e1) {
            } catch (ServiceUnavailableException e1) {
            }

            // Let's register to receive messages from our application on the device.
            try {
                mConnectIQ.registerForAppEvents(mDevice, mMyApp, new IQApplicationEventListener() {

                    @Override
                    public void onMessageReceived(IQDevice device, IQApp app, List<Object> message, IQMessageStatus status) {

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

                        AlertDialog.Builder dialog = new AlertDialog.Builder(DeviceActivity.this);
//                        dialog.setTitle(R.string.received_message);
                        dialog.setMessage(builder.toString());
                        dialog.setPositiveButton(android.R.string.ok, null);
                        dialog.create().show();
                    }

                });
            } catch (InvalidStateException e) {
                Toast.makeText(this, "ConnectIQ is not in a valid state", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mDevice != null) {
            // It is a good idea to unregister everything and shut things down to
            // release resources and prevent unwanted callbacks.
            try {
                mConnectIQ.unregisterForDeviceEvents(mDevice);

                if (mMyApp != null) {
                    mConnectIQ.unregisterForApplicationEvents(mDevice, mMyApp);
                }
            } catch (InvalidStateException e) {
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

//        Object message = MessageFactory.getMessage(this, position);
//        try {
//            mConnectIQ.sendMessage(mDevice, mMyApp, message, new IQSendMessageListener() {
//
//                @Override
//                public void onMessageStatus(IQDevice device, IQApp app, IQMessageStatus status) {
//                    Toast.makeText(DeviceActivity.this, status.name(), Toast.LENGTH_LONG).show();
//                }
//
//            });
//        } catch (InvalidStateException e) {
//            Toast.makeText(this, "ConnectIQ is not in a valid state", Toast.LENGTH_LONG).show();
//        } catch (ServiceUnavailableException e) {
//            Toast.makeText(this, "ConnectIQ service is unavailable.   Is Garmin Connect Mobile installed and running?", Toast.LENGTH_LONG).show();
//        }
    }
}
