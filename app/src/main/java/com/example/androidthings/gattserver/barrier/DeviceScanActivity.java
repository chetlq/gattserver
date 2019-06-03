///*
// * Copyright (C) 2013 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.example.androidthings.gattserver.barrier;
//
//import android.Manifest;
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanFilter;
//import android.bluetooth.le.ScanSettings;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.RadioButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.example.androidthings.gattserver.R;
//import com.example.androidthings.gattserver.interfaces.GetChosenToken;
//import com.example.androidthings.gattserver.interfaces.GetDistance;
//import com.example.androidthings.gattserver.interfaces.UpdateBarrierStatus;
//import com.example.androidthings.gattserver.interfaces.UpdateResultRequest;
//import com.example.androidthings.gattserver.interfaces.UpdateStatus;
//
//import java.nio.ByteBuffer;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.UUID;
//
//
//
///**
// * Activity for scanning and displaying available Bluetooth LE devices.
// */
//public class DeviceScanActivity extends Activity
//        implements AdapterView.OnItemClickListener, UpdateStatus, UpdateResultRequest, GetDistance, UpdateBarrierStatus, GetChosenToken {
//
//    public static final String STATUS_CONNECTED = "Device connected";
//    public static final String STATUS_DISCONNECTED = "Device disconnected";
//
//    public final static byte RESULT_REQUEST_SUCCESS_CODE = 0;
//    public final static byte RESULT_REQUEST_BAD_TOKEN    = 1;
//    public final static byte RESULT_REQUEST_BARRIER_BUSY = 2;
//    public final static byte RESULT_REQUEST_DOUBLE_ENTER = 3;
//    public final static byte RESULT_REQUEST_DOUBLE_EXIT  = 4;
//    public final static byte RESULT_REQUEST_BAD_DISTANCE = 5;
//    public final static byte RESULT_REQUEST_GOOD_DISTANCE = 6;
//    public final static byte RESULT_REQUEST_FROD_DISTANCE = 7;
//    public final static byte RESULT_REQUEST_NO_BINDINGS = 8;
//    public final static byte RESULT_REQUEST_ERROR_WHILE_PAYMENT = 9;
//    public final static byte RESULT_REQUEST_ERROR_NO_CONNECT_TO_SERVER = 10;
//    public final static byte RESULT_REQUEST_ERROR_NO_CONNECT_TO_PAYMENT_SERVER = 11;
//
//    public final static String RESULT_REQUEST_SUCCESS = "success";
//    public final static String ERROR_RESULT_BAD_ACCOUNT_TOKEN = "Error. Bad account token";
//    public final static String ERROR_RESULT_BARRIER_BUSY = "Error. Barrier is busy";
//    public static final String ERROR_DOUBLE_ACCOUNT_ENTERING = "Error. Account already entered!! Alarm!!!!";
//    public static final String ERROR_DOUBLE_ACCOUNT_EXIT = "Error. Account not in parking!";
//    public static final String ERROR_BAD_DISTANCE = "Car in other zone or not in any zone";
//    public static final String RESULT_GOOD_DISTANCE = "Car in right zone";
//    public static final String ERROR_FROD_DISTANCE = "FROD: More than one car in this zone";
//    public final static String ERROR_RESULT_NO_CORRECT_BINDINGS = "Error. No correct bindings";
//    public static final String ERROR_RESULT_WHILE_PAYMENT = "Error while payment";
//    public static final String ERROR_RESULT_NO_CONNECT_TO_SERVER = "No connection to server";
//    public static final String ERROR_RESULT_NO_CONNECT_TO_PAYMENT_SERVER = "No connection to payment server";
//
//    public static UUID BARRIER_SERVICE = UUID.fromString("AC021218-F7C6-4ABB-9DC0-600A487BAB80");
//    public static UUID BEACON_SERVICE_UUID = UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e");
//    private LeDeviceListAdapter mLeDeviceListAdapter;
//    private BluetoothAdapter mBluetoothAdapter;
//    private boolean mScanning;
//    private Handler mHandler;
//
//    private static final int REQUEST_ENABLE_BT = 1;
//    // Stops scanning after 10 seconds.
//    private static final long SCAN_PERIOD = 10000;
//
//    private GattServer mGattServer;
//    private ListView listView;
//    private TextView statusTextView;
//    private TextView resultRequestTextView;
//    private EditText distanceEditText;
//    private TextView barrierTypeTextView;
//    private TextView requiredBarrierTypeTextView;
//
//    private Button startServerButton;
//    private Button stopServerButton;
//    private Button openBarrierButton;
//    private Button closeBarrierButton;
//    private Button sendDistanceButton;
//    private Button switchBarrierTypeButton;
//
//    private RadioButton radioButtonToken1, radioButtonToken2;
//    private static Boolean timerIsSet = false;
//    private static final int TIMERPERIOD = 1000;
//    private static Timer sendRegDataTimer;
//    private static int currentRegIndex = 0;
//
//    private String requiredBarrierType;
//
//    private Context context;
//    private BluetoothLeScanner bluetoothLeScanner;
//    private ScanCallback scanCallback;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_activity);
//
//        getActionBar().setTitle(R.string.title_devices);
//        context = this;
//
//        mHandler = new Handler();
//
//        // Use this check to determine whether BLE is supported on the device.  Then you can
//        // selectively disable BLE-related features.
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
//        // BluetoothAdapter through BluetoothManager.
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        // Checks if Bluetooth is supported on the device.
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//            // Запросим разрешения
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    333
//            );
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//            // Запросим разрешения
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    333
//            );
//        }
//        listView = findViewById(R.id.device_list);
//        listView.setOnItemClickListener(this);
//
//        statusTextView = findViewById(R.id.status_connection_text);
//        resultRequestTextView = findViewById(R.id.result_request_text);
//
//        distanceEditText = findViewById(R.id.editDistance);
//        startServerButton = findViewById(R.id.buttonStartServer);
//        startServerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UpdateStatus("");
//                clearResultStatus();
//                updateBarrierStatus("");
//                mGattServer.startAdvertising();
//                mGattServer.startServer();
//            }
//        });
//
//
//        stopServerButton = findViewById(R.id.buttonStopServer);
//        stopServerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UpdateStatus("");
//                clearResultStatus();
//                updateBarrierStatus("");
//                if (sendRegDataTimer != null)
//                    sendRegDataTimer.cancel();
//                timerIsSet = false;
//                mGattServer.stopAdvertising();
//                mGattServer.stopServer();
//                Log.d(TAG, "Stopping GATT server");
//            }
//        });
//
//        openBarrierButton = findViewById(R.id.buttonOpenBarrier);
//        openBarrierButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clearResultStatus();
//                mGattServer.openBarrier();
//            }
//        });
//        closeBarrierButton = findViewById(R.id.buttonCloseBarrier);
//        closeBarrierButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clearResultStatus();
//                mGattServer.closeBarrier();
//            }
//        });
//
//        sendDistanceButton = findViewById(R.id.buttonDistance);
//        sendDistanceButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Double distance = Double.parseDouble(distanceEditText.getText().toString());
////                mGattServer.sendDistance(distance);
//                mGattServer.SendZoneNumber(Byte.parseByte(distanceEditText.getText().toString()));
////                mGattServer.sendBeaconValues();
//            }
//        });
//
////        Button sendDistance2 = findViewById(R.id.buttonDistance2);
////        sendDistance2.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                mGattServer.sendBeaconValues2();
////            }
////        });
////
////        Button sendDistance3 = findViewById(R.id.buttonDistance3);
////        sendDistance3.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                mGattServer.sendBeaconValues3();
////            }
////        });
//        barrierTypeTextView = findViewById(R.id.barrier_type_text);
//
//        Button sendRegDataButton1 = findViewById(R.id.send_reg_data_button1);
//        sendRegDataButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!timerIsSet)
//                {
//                    timerIsSet = true;
//                    currentRegIndex = 0;
//                    sendRegDataTimer = new Timer();
//                    sendRegDataTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            mGattServer.sendRegBeaconValues(currentRegIndex, RegData.inFirstZone[currentRegIndex]);
//                            currentRegIndex++;
//                            if (currentRegIndex == RegData.inFirstZone.length)
//                            {
//                                sendRegDataTimer.cancel();
//                                timerIsSet = false;
//                                Log.d(TAG, "Sending registration data finished");
//                            }
//                        }
//                    }, 0, TIMERPERIOD);
//                }
//            }
//        });
//
//        Button sendRegDataButoon2 = findViewById(R.id.send_reg_data_button2);
//        sendRegDataButoon2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!timerIsSet)
//                {
//                    timerIsSet = true;
//                    currentRegIndex = 0;
//                    sendRegDataTimer = new Timer();
//                    sendRegDataTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            mGattServer.sendRegBeaconValues(currentRegIndex, RegData.inOtherZone[currentRegIndex]);
//                            currentRegIndex++;
//                            if (currentRegIndex == RegData.inOtherZone.length)
//                            {
//                                sendRegDataTimer.cancel();
//                                timerIsSet = false;
//                                Log.d(TAG, "Sending registration data finished");
//                            }
//                        }
//                    }, 0, TIMERPERIOD);
//                }
//            }
//        });
//
//        Button sendRegDataButton3 = findViewById(R.id.send_reg_data_button3);
//        sendRegDataButton3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!timerIsSet)
//                {
//                    timerIsSet = true;
//                    currentRegIndex = 0;
//                    sendRegDataTimer = new Timer();
//                    sendRegDataTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            mGattServer.sendRegBeaconValues(currentRegIndex, RegData.inFrontFirstZone[currentRegIndex]);
//                            currentRegIndex++;
//                            if (currentRegIndex == RegData.inFrontFirstZone.length)
//                            {
//                                sendRegDataTimer.cancel();
//                                timerIsSet = false;
//                                Log.d(TAG, "Sending registration data finished");
//                            }
//                        }
//                    }, 0, TIMERPERIOD);
//                }
//            }
//        });
//
//        Button sendRegDataButton4 = findViewById(R.id.send_reg_data_button4);
//        sendRegDataButton4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!timerIsSet)
//                {
//                    timerIsSet = true;
//                    currentRegIndex = 0;
//                    sendRegDataTimer = new Timer();
//                    sendRegDataTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            mGattServer.sendRegBeaconValues(currentRegIndex, RegData.inFrontSecondZone[currentRegIndex]);
//                            currentRegIndex++;
//                            if (currentRegIndex == RegData.inFrontSecondZone.length)
//                            {
//                                sendRegDataTimer.cancel();
//                                timerIsSet = false;
//                                Log.d(TAG, "Sending registration data finished");
//                            }
//                        }
//                    }, 0, TIMERPERIOD);
//                }
//            }
//        });
//
//        Button sendRegDataButton5 = findViewById(R.id.send_reg_data_button5);
//        sendRegDataButton5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!timerIsSet)
//                {
//                    timerIsSet = true;
//                    currentRegIndex = 0;
//                    sendRegDataTimer = new Timer();
//                    sendRegDataTimer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            mGattServer.sendRegBeaconValues(currentRegIndex, RegData.farFromZones[currentRegIndex]);
//                            currentRegIndex++;
//                            if (currentRegIndex == RegData.farFromZones.length)
//                            {
//                                sendRegDataTimer.cancel();
//                                timerIsSet = false;
//                                Log.d(TAG, "Sending registration data finished");
//                            }
//                        }
//                    }, 0, TIMERPERIOD);
//                }
//            }
//        });
//        radioButtonToken1 = findViewById(R.id.radio_token1);
//        radioButtonToken2 = findViewById(R.id.radio_token2);
//
//        requiredBarrierType = IOUtils.readBarrierTypeFromFile(context);
//        if (requiredBarrierType == null) // По умолчанию пусть будет требоваться шлагбаум на въезд
//        {
//            requiredBarrierType = IOUtils.BARRIER_TYPE_ENTER;
//            IOUtils.writeBarrierTypeToFile(context, requiredBarrierType);
//        }
//        requiredBarrierTypeTextView = findViewById(R.id.requiered_barrier_type_text);
//        requiredBarrierTypeTextView.setText("Required barrier type: " + requiredBarrierType);
//        switchBarrierTypeButton = findViewById(R.id.switch_barrier_type_button);
//        switchBarrierTypeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // TODO
//                if (requiredBarrierType.equals(IOUtils.BARRIER_TYPE_ENTER))
//                    requiredBarrierType = IOUtils.BARRIER_TYPE_EXIT;
//                else
//                    requiredBarrierType = IOUtils.BARRIER_TYPE_ENTER;
//                IOUtils.writeBarrierTypeToFile(context, requiredBarrierType);
//                requiredBarrierTypeTextView.setText("Required barrier type: " + requiredBarrierType);
//            }
//        });
//
//        mGattServer = new GattServer(context);
//        mGattServer.startAdvertising();
//        mGattServer.startServer();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy");
//        mGattServer.stopAdvertising();
//        mGattServer.stopServer();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        if (!mScanning) {
//            menu.findItem(R.id.menu_stop).setVisible(false);
//            menu.findItem(R.id.menu_scan).setVisible(true);
//            menu.findItem(R.id.menu_refresh).setActionView(null);
//        } else {
//            menu.findItem(R.id.menu_stop).setVisible(true);
//            menu.findItem(R.id.menu_scan).setVisible(false);
//            menu.findItem(R.id.menu_refresh).setActionView(
//                    R.layout.actionbar_indeterminate_progress);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_scan:
//                mLeDeviceListAdapter.clear();
//                scanLeDevice(true);
//                break;
//            case R.id.menu_stop:
//                scanLeDevice(false);
//                break;
//        }
//        return true;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
//        // fire an intent to display a dialog asking the user to grant permission to enable it.
//        if (!mBluetoothAdapter.isEnabled()) {
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//        }
//
//        // Initializes list view adapter.
//        mLeDeviceListAdapter = new LeDeviceListAdapter();
//        listView.setAdapter(mLeDeviceListAdapter);
////        scanLeDevice(true);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // User chose not to enable Bluetooth.
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            finish();
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
////        scanLeDevice(false);
//        mLeDeviceListAdapter.clear();
//    }
//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            // Stops scanning after a pre-defined scan period.
////            mHandler.postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    mScanning = false;
////                    stopBleScan();
//////                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
////                    invalidateOptionsMenu();
////                }
////            }, SCAN_PERIOD);
//
//            mScanning = true;
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
////            startBleScan();
//
//        } else {
//            mScanning = false;
////            stopBleScan();
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//        invalidateOptionsMenu();
//    }
//    private void startBleScan()
//    {
//
////        [2, 21, -9, -126, 109, -90, 79, -94, 78, -104, -128, 36, -68, 91, 113, -32, -119, 62, -34, 46, -126, -121, -77]
//        byte[] manData = {2, 21, -9, -126, 109, -90, 79, -94, 78, -104, -128, 36, -68, 91, 113, -32, -119, 62, -34, 46, -126, -121, -77};
//        bluetoothLeScanner =  BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
//        scanCallback = new GattScanCallback();
//        ScanFilter scanFilter = new ScanFilter.Builder()
////                .setManufacturerData(76, manData)
//                .build();
//        List<ScanFilter> scanFilterList = new ArrayList<>();
//        ScanSettings scanSettings = new ScanSettings.Builder().build();
//        scanFilterList.add(scanFilter);
//        if (bluetoothLeScanner != null)
//            bluetoothLeScanner.startScan(scanFilterList, scanSettings, scanCallback);
//        else
//            Log.d(TAG, "Failed to start scan");
//    }
//
//    private void stopBleScan()
//    {
//        if (bluetoothLeScanner != null)
//        {
//            bluetoothLeScanner.stopScan(scanCallback);
//        }
//    }
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(i);
//        if (device == null) return;
//        final Intent intent = new Intent(this, DeviceControlActivity.class);
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//        if (mScanning) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            mScanning = false;
//        }
//        startActivity(intent);
//    }
//
//    @Override
//    public void UpdateStatus(final String newStatus) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                statusTextView.setText(newStatus);
//            }
//        });
//    }
//
//
//    public void clearResultStatus()
//    {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                resultRequestTextView.setText("");
////                barrierTypeTextView.setText("");
//            }
//        });
//    }
//
//    @Override
//    public void UpdateResultRequest(final byte result) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                switch (result)
//                {
//                    case (RESULT_REQUEST_SUCCESS_CODE):
//                        resultRequestTextView.setText(RESULT_REQUEST_SUCCESS);
//                        break;
//                    case (RESULT_REQUEST_BAD_TOKEN):
//                        resultRequestTextView.setText(ERROR_RESULT_BAD_ACCOUNT_TOKEN);
//                        break;
//                    case (RESULT_REQUEST_BARRIER_BUSY):
//                        resultRequestTextView.setText(ERROR_RESULT_BARRIER_BUSY);
//                        break;
//                    case (RESULT_REQUEST_DOUBLE_ENTER):
//                        resultRequestTextView.setText(ERROR_DOUBLE_ACCOUNT_ENTERING);
//                        break;
//                    case (RESULT_REQUEST_DOUBLE_EXIT):
//                        resultRequestTextView.setText(ERROR_DOUBLE_ACCOUNT_EXIT);
//                        break;
//                    case (RESULT_REQUEST_BAD_DISTANCE):
//                        resultRequestTextView.setText(ERROR_BAD_DISTANCE);
//                        break;
//                    case (RESULT_REQUEST_GOOD_DISTANCE):
//                        resultRequestTextView.setText(RESULT_GOOD_DISTANCE);
//                        break;
//                    case (RESULT_REQUEST_FROD_DISTANCE):
//                        resultRequestTextView.setText(ERROR_FROD_DISTANCE);
//                        break;
//                    case (RESULT_REQUEST_NO_BINDINGS):
//                        resultRequestTextView.setText(ERROR_RESULT_NO_CORRECT_BINDINGS);
//                        break;
//                    case (RESULT_REQUEST_ERROR_WHILE_PAYMENT):
//                        resultRequestTextView.setText(ERROR_RESULT_WHILE_PAYMENT);
//                        break;
//                    case (RESULT_REQUEST_ERROR_NO_CONNECT_TO_SERVER):
//                        resultRequestTextView.setText(ERROR_RESULT_NO_CONNECT_TO_SERVER);
//                        break;
//                    case (RESULT_REQUEST_ERROR_NO_CONNECT_TO_PAYMENT_SERVER):
//                        resultRequestTextView.setText(ERROR_RESULT_NO_CONNECT_TO_PAYMENT_SERVER);
//                        break;
//                    default:
//                        resultRequestTextView.setText("Result = " + result);
//                        break;
//                }
//
//            }
//        });
//    }
//
//    @Override
//    public Double getDistance() {
//        return Double.parseDouble(distanceEditText.getText().toString());
//    }
//
//    @Override
//    public void updateBarrierStatus(final String newStatus) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                barrierTypeTextView.setText(newStatus);
//            }
//        });
//    }
//
//
//    @Override
//    public String getChosenToken() {
//        if (radioButtonToken1.isChecked())
//            return ServerProfile.token1;
//        else
//        if (radioButtonToken2.isChecked())
//            return ServerProfile.token2;
//        return null;
//    }
//
//    // Adapter for holding devices found through scanning.
//    private class LeDeviceListAdapter extends BaseAdapter {
//        private ArrayList<BluetoothDevice> mLeDevices;
//        private ArrayList<Integer> mRSSIvalues;
//        private LayoutInflater mInflator;
//
//        public LeDeviceListAdapter() {
//            super();
//            mLeDevices = new ArrayList<BluetoothDevice>();
//            mRSSIvalues = new ArrayList<>();
//            mInflator = DeviceScanActivity.this.getLayoutInflater();
//        }
//
//        public void addDevice(BluetoothDevice device, Integer rssi) {
////            if(!mLeDevices.contains(device)) {
//                mLeDevices.add(device);
//                mRSSIvalues.add(rssi);
////            }
//        }
//
//        public BluetoothDevice getDevice(int position) {
//            return mLeDevices.get(position);
//        }
//
//        public void clear() {
//            mLeDevices.clear();
//        }
//
//        @Override
//        public int getCount() {
//            return mLeDevices.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return mLeDevices.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            ViewHolder viewHolder;
//            // General ListView optimization code.
//            if (view == null) {
//                view = mInflator.inflate(R.layout.listitem_device, null);
//                viewHolder = new ViewHolder();
//                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
//                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
//                viewHolder.deviceRSSI = view.findViewById(R.id.device_RSSI);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
//
//            BluetoothDevice device = mLeDevices.get(i);
//            final String deviceName = device.getName();
//            if (deviceName != null && deviceName.length() > 0)
//                viewHolder.deviceName.setText(deviceName);
//            else
//                viewHolder.deviceName.setText(R.string.unknown_device);
//            viewHolder.deviceAddress.setText(device.getAddress());
//
//            viewHolder.deviceRSSI.setText("rssi: " + mRSSIvalues.get(i).toString() + " dbm");
//
//            return view;
//        }
//    }
//
//    // Device scan callback.
//    private BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//
//        public byte[] getIdAsByte(java.util.UUID uuid)
//        {
//            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
//            bb.putLong(uuid.getMostSignificantBits());
//            bb.putLong(uuid.getLeastSignificantBits());
//            return bb.array();
//        }
//        public final int constMajor = 56878;
//        public final int constMinor = 33415;
////        public final int constMajor = 9;
////        public final int constMinor = 6;
//
//        @Override
//        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                    if (device.getName() != null && device.getName().equals("Barrier")) {
////                    mLeDeviceListAdapter.addDevice(device, new Integer(rssi));
////                    mLeDeviceListAdapter.notifyDataSetChanged();
////                    }
//                    byte[] uuid = getIdAsByte(BEACON_SERVICE_UUID);
//
//
////                    Integer minor = (scanRecord[25] & 0xFF ) | ((scanRecord[24] & 0xFF) << 8);
////                    Integer major = (scanRecord[23] & 0xFF ) | ((scanRecord[22] & 0xFF) << 8);
//                    int minor = (scanRecord[28] & 0xFF ) | ((scanRecord[27] & 0xFF) << 8);
//                    int major = (scanRecord[26] & 0xFF ) | ((scanRecord[25] & 0xFF) << 8);
//
////                    Log.d(TAG, "Device = " + device.getAddress() + " " + Arrays.toString(scanRecord)
////                            + " minor = " + minor + " major = " + major + " rssi = " + rssi);
//                    if ((minor == constMinor) & (major == constMajor)) {
////                        Log.d(TAG, "Device = " + device.getAddress() + " " + Arrays.toString(scanRecord));
//                        Log.d(TAG, "Device = " + device.getAddress() + " minor = " + minor + " major = " + major + " rssi = " + rssi);
////                        mLeDeviceListAdapter.addDevice(device, new Integer(rssi));
////                        mLeDeviceListAdapter.notifyDataSetChanged();
//                    }
////                            break;
//
////                        }
////                    }
//                }
//            });
//        }
//    };
//
//    static class ViewHolder {
//        TextView deviceName;
//        TextView deviceAddress;
//        TextView deviceRSSI;
//    }
//}