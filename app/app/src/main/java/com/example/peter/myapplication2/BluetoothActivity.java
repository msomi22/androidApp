package com.example.peter.myapplication2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private ListView mLvDevices;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mLvDevices = (ListView) findViewById(R.id.bluetooth_list);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBtReceiver, filter);

        //get
        displayDevices();

        if (mBtAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        // Getting the Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter != null) {
            mBtAdapter.startDiscovery();
            Toast.makeText(this, "Starting discovery...", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Bluetooth disabled or not available.", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        unregisterReceiver(mBtReceiver);
    }


    private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName()); // device.getAddress() + ", " +

                //add device to db
                if(mDeviceList != null){

                    if(!db.deviceExist(device.getName())) {
                        db.addDevice(device.getName());
                        displayDevices();
                    }

                }else{

                    mBtAdapter.startDiscovery();
                }



            }
        }
    };


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Intent intent = new Intent(BluetoothActivity.this,ScheduleActivity.class);
                startActivity(intent);
                finish();



                return true;

            case R.id.action_search:
                // User chose the "Search" action,


                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_exit:
                // User chose the "exit" item , exit the app
                finish();
                System.exit(0);
                return  true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void displayDevices(){
        //get
        if (mLvDevices != null) {
            ArrayList<String> devicesList = db.getDevices();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, devicesList);
            mLvDevices.setAdapter(adapter);
        }
    }

}
