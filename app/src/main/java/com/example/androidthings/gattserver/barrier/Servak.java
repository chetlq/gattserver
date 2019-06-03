package com.example.androidthings.gattserver.barrier;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidthings.gattserver.R;
import com.example.androidthings.gattserver.TimeProfile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Servak  extends Activity {
    public static final String TAG = "Servak";
    /* Local UI */
    private TextView mLocalTimeView;
    private GattServer mGattServer;
    /* Bluetooth API */
    private BluetoothManager bluetoothManager;
    private BluetoothGattServer bluetoothGattServer;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    /* Collection of notification subscribers */
    Set<BluetoothDevice> devices = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        final Button button = (Button)findViewById(R.id.button);

//        mGattServer = new GattServer(this);
//        mGattServer.startAdvertising();
//        mGattServer.startServer();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BluetoothDevice s : devices) {
                    bluetoothGattServer.cancelConnection(s);
                }

//                BluetoothDevice bluetoothDevice = devices.iterator().next();

//                mGattServer.cancelConnection(device);
            }
        });
        initBt(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mGattServer.stopAdvertising();
        mGattServer.stopServer();
    }
//}

    private void initBt(Context context) {

        bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "initBt: Adapter null!");
            return;
        }

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bluetoothReceiver, filter);

        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "initBt: enabling Bluetooth");
            bluetoothAdapter.enable();
        } else {
            Log.d(TAG, "initBt: starting services");
            startAdvertising();
            startServer();
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    startAdvertising();
                    startServer();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopServer();
                    stopAdvertising();
                    break;
                default:
                    // Do nothing
            }
        }
    };

    public void startAdvertising() {
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothAdapter.setName("Servak");
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (bluetoothLeAdvertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(TimeProfile.TIME_SERVICE))
                .build();

        bluetoothLeAdvertiser
                .startAdvertising(settings, data, advertiseCallback);
    }
    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: "+errorCode);
        }
    };
    private void stopAdvertising() {
        if (bluetoothLeAdvertiser == null) return;

        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
    }

    private void startServer() {
        bluetoothGattServer = bluetoothManager.openGattServer(this, bluetoothGattServerCallback);
        if (bluetoothGattServer == null) {
            Log.w(TAG, "Unable to create GATT server");
            return;
        }

        bluetoothGattServer.addService(TimeProfile.createTimeService());
//        bluetoothGattServer.addService(BusStopProfile.createNetworkService());
    }
    private void stopServer() {
        if (bluetoothGattServer == null) return;

        bluetoothGattServer.close();
    }

//    public static BluetoothGattService createConfigService() {
//        BluetoothGattService service = new BluetoothGattService(CONFIG_SERVICE,
//                BluetoothGattService.SERVICE_TYPE_PRIMARY);
//
//        // read / write the bus stop number
//        BluetoothGattCharacteristic configCharacteristic = new BluetoothGattCharacteristic(STOP_NUMBER,
//                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_WRITE,
//                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
//
//        service.addCharacteristic(configCharacteristic);
//
//        return service;
//    }

    private BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: " + device);
                devices.add(device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device);
                //Remove device from any active subscriptions
                devices.remove(device);
            }
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
            Log.d(TAG, "onExecuteWrite: " );
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
            Log.d(TAG, "onMtuChanged:  " );
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);

                        if (TimeProfile.LOCAL_TIME_INFO.equals(characteristic.getUuid())) {
                            String stop = new String(value);
                Log.d(TAG, "onCharacteristicWriteRequest: LOCAL_TIME_INFO "+stop );
                            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
            } else
            if (TimeProfile.CURRENT_TIME.equals(characteristic.getUuid())) {
                String stop = new String(value);
                Log.d(TAG, "onDescriptorWriteRequest: Stop number " + stop);
                } else {
                Log.w(TAG, "Unknown descriptor write request");
                if (responseNeeded) {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
                }
            }


        }

        //        @Override
//        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
//                                                 BluetoothGattCharacteristic characteristic,
//                                                 boolean preparedWrite, boolean responseNeeded,
//                                                 int offset, byte[] value) {
//            long now = System.currentTimeMillis();
//            if (TimeProfile.LOCAL_TIME_INFO.equals(characteristic.getUuid())) {
//                Log.d(TAG, "onDescriptorWriteRequest: LOCAL_TIME_INFO " );
//            } else
//            if (TimeProfile.CURRENT_TIME.equals(characteristic.getUuid())) {
//                String stop = new String(value);
//                Log.d(TAG, "onDescriptorWriteRequest: Stop number " + stop);
//
////                if (listener != null) {
////                    listener.onStopUpdated(stop);
////                }
//
//                if (responseNeeded) {
//                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, TimeProfile.getExactTime(now, TimeProfile.ADJUST_NONE));
//                }
//
////            } else if (BusStopProfile.WIFI_SSID.equals(characteristic.getUuid())) {
////                Log.d(TAG, "onDescriptorWriteRequest: SSID " + new String(value));
////
////                if (responseNeeded) {
////                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, TimeProfile.getExactTime(now, TimeProfile.ADJUST_NONE));
////                }
////            } else if (BusStopProfile.WIFI_PASS.equals(characteristic.getUuid())) {
////                Log.d(TAG, "onDescriptorWriteRequest: PASS " + new String(value));
//            } else {
//                Log.w(TAG, "Unknown descriptor write request");
//                if (responseNeeded) {
//                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
//                }
//            }
//        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            long now = System.currentTimeMillis();
            if (TimeProfile.CURRENT_TIME.equals(characteristic.getUuid())) {
                Log.i(TAG, "Read CurrentTime");
//                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, stop.getBytes());
                bluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,new byte[]{0,1,3,4,5,7,8,9,15});
//                        TimeProfile.getExactTime(now, TimeProfile.ADJUST_NONE));
//            } else if (TimeProfile.LOCAL_TIME_INFO.equals(characteristic.getUuid())) {
//                Log.i(TAG, "Read LocalTimeInfo");
//                bluetoothGattServer.sendResponse(device,
//                        requestId,
//                        BluetoothGatt.GATT_SUCCESS,
//                        0,
//                        TimeProfile.getLocalTimeInfo(now));
            } else {
                // Invalid characteristic
                Log.w(TAG, "Invalid Characteristic Read: " + characteristic.getUuid());
                bluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null);
            }
//
//            if (BusStopProfile.STOP_NUMBER.equals(characteristic.getUuid())) {
//                Log.d(TAG, "Config descriptor read");
//                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, stop.getBytes());
//            } else if (BusStopProfile.WIFI_SSID.equals(characteristic.getUuid())) {
//                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0,
//                        BusStopProfile.getCurrentSsid(context).getBytes());
//            } else if (BusStopProfile.WIFI_STAT.equals(characteristic.getUuid())) {
//                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0,
//                        BusStopProfile.isConnected(context) ? new byte[]{1} : new byte[]{0});
//            } else {
//                // Invalid characteristic
//                Log.w(TAG, "Invalid Characteristic Read: " + characteristic.getUuid());
//                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
//            }
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset,
                                            BluetoothGattDescriptor descriptor) {
            if (TimeProfile.CLIENT_CONFIG.equals(descriptor.getUuid())) {
                Log.d(TAG, "Config descriptor read");
                byte[] returnValue;
                if (devices.contains(device)) {
                    returnValue = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                } else {
                    returnValue = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                }
                Log.d(TAG, "returnValue: "+returnValue);
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, returnValue);
            } else {
                Log.w(TAG, "Unknown descriptor read request");
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            }
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded,
                                             int offset,  byte[] value) {
            if (TimeProfile.CLIENT_CONFIG.equals(descriptor.getUuid())) {
                Log.d(TAG, "onDescriptorWriteRequest: WIFI_STAT_CONFIG");
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Subscribe device to notifications: " + device);
//                    devices.add(device);
                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Unsubscribe device from notifications: " + device);
//                    devices.remove(device);
                }

                if (responseNeeded) {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
                }
            } else {
                Log.w(TAG, "Unknown descriptor write request");
                if (responseNeeded) {
                    bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
                }
            }
        }
    };
}
