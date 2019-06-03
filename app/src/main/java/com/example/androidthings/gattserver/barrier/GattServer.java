package com.example.androidthings.gattserver.barrier;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.BLUETOOTH_SERVICE;

public class GattServer {
    public static final String TAG = "GattServer";
    public final static byte COMMAND_OPEN_BARRIER = 1;
    public final static byte COMMAND_CLOSE_BARRIER = 2;

    public final static byte BARRIER_TYPE_ENTER = 0;
    public final static byte BARRIER_TYPE_EXIT = 1;
    /* Bluetooth API */
    private BluetoothManager mBluetoothManager;
    public static BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    /* Collection of notification subscribers */

    List<BluetoothDevice> myList = new ArrayList<>();


    private GattServerCallback GattServerCallback;

    Context mContext;

    AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: "+errorCode);
        }
    };

    public GattServer(Context context) {
        this.mContext = context;
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        // We can't continue without proper Bluetooth support
        if (!checkBluetoothSupport(bluetoothAdapter)) {
            Log.d(TAG, "!checkBluetoothSupport(bluetoothAdapter)");
        }
        GattServerCallback = new GattServerCallback(mContext);
    }

    public void startAdvertising()
    {
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.w(TAG, "Failed to get BluetoothAdapter");
            return;
        }

        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
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
                .addServiceUuid(new ParcelUuid(ServerProfile.CLIENT_SERVICE))
                .build();
        bluetoothAdapter.setName("Barrier");

        mBluetoothLeAdvertiser
                .startAdvertising(settings, data, mAdvertiseCallback);
    }

    public void stopAdvertising()
    {
        if (mBluetoothLeAdvertiser != null)
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }


    public void startServer()
    {
        if (mBluetoothManager == null)
        {
            Log.w(TAG, "Unable to create GATT server");
            return;
        }
        mBluetoothGattServer = mBluetoothManager.openGattServer(mContext, GattServerCallback);
        if (mBluetoothGattServer == null) {
            Log.w(TAG, "Unable to create GATT server");
            return;
        }

        mBluetoothGattServer.addService(ServerProfile.createClientService());
        Log.d(TAG, "Gatt server created");
    }

    public void stopServer()
    {
        if (mBluetoothGattServer != null){
//            myList = mBluetoothManager.getConnectedDevices(0);
           // mBluetoothGattServer.cancelConnection(mBluetoothManager.getConnectedDevices(0));

            mBluetoothGattServer.clearServices();
            mBluetoothGattServer.close();
        }




//        Log.d(TAG, "size:"+myList.size());




    }

//    public void openBarrier()
//    {
//        // TODO тут надо сделать посылку только нужному устройству
//        BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                mBluetoothGattServer
//                        .getService(ServerProfile.CLIENT_SERVICE)
//                        .getCharacteristic(ServerProfile.COMMAND);
//        byte []value = new byte[1];
//        value[0] = COMMAND_OPEN_BARRIER;
//        bluetoothGattCharacteristic.setValue(value);
//        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() != 0)
//            for (int i = 0; i < mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size(); i++) {
//                mBluetoothGattServer.notifyCharacteristicChanged(
//                        mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).get(i),
//                        bluetoothGattCharacteristic, true);
//            }
//        Log.d(TAG, "Open Barrier Command");
//
//    }
//
//    public void closeBarrier()
//    {
//        BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                mBluetoothGattServer
//                        .getService(ServerProfile.CLIENT_SERVICE)
//                        .getCharacteristic(ServerProfile.COMMAND);
//        byte []value = new byte[1];
//        value[0] = COMMAND_CLOSE_BARRIER;
//        bluetoothGattCharacteristic.setValue(value);
//        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() != 0)
//            for (int i = 0; i < mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size(); i++) {
//                mBluetoothGattServer.notifyCharacteristicChanged(
//                        mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).get(i),
//                        bluetoothGattCharacteristic, true);
//            }        Log.d(TAG, "Close Barrier Command");
//    }
//
//    public void sendDistance(Double distance)
//    {
//        // TODO сделать рассылку по всем подключенным устройствам
//        BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                mBluetoothGattServer.getService(ServerProfile.CLIENT_SERVICE)
//                .getCharacteristic(ServerProfile.DISTANCE);
//        Log.d(TAG, "distance = " + distance);
//
//        byte[] value = double2ByteArray(distance);
//
//        bluetoothGattCharacteristic.setValue(value);
//        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() != 0)
//            mBluetoothGattServer.notifyCharacteristicChanged(
//                    mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).get(0),
//                    bluetoothGattCharacteristic, true);
//        Log.d(TAG, "Sending distance");
//    }
//
//
//    public void SendZoneNumber(byte zoneNumber)
//    {
//        BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                mBluetoothGattServer.getService(ServerProfile.CLIENT_SERVICE)
//                        .getCharacteristic(ServerProfile.DISTANCE);
//        Log.d(TAG, "zoneNumber = " + zoneNumber);
//
//        byte[] value = new byte[1];
//        value[0] = zoneNumber;
//
//        bluetoothGattCharacteristic.setValue(value);
//        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() != 0)
//            for (int i = 0; i < mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size(); i++) {
//                mBluetoothGattServer.notifyCharacteristicChanged(
//                        mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).get(i),
//                        bluetoothGattCharacteristic, true);
//            }
//        Log.d(TAG, "Sending zone number");
//
//    }
//    public void sendBeaconValues()
//    {
//        // TODO сделать рассылку по всем подключенным устройствам
//        BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                mBluetoothGattServer.getService(ServerProfile.CLIENT_SERVICE)
//                        .getCharacteristic(ServerProfile.DISTANCE);
//
//        byte []value = {-11, -8, -90,
//                        -9, -10, -100};
//        bluetoothGattCharacteristic.setValue(value);
//        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() != 0)
//            mBluetoothGattServer.notifyCharacteristicChanged(
//                    mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).get(0),
//                    bluetoothGattCharacteristic, true);
//        Log.d(TAG, "Sending beacon values 1");
//    }
//
//    public void sendBeaconValues2()
//    {
//        // TODO сделать рассылку по всем подключенным устройствам
//        BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                mBluetoothGattServer.getService(ServerProfile.CLIENT_SERVICE)
//                        .getCharacteristic(ServerProfile.DISTANCE);
//
//        byte []value = {-110, -11, -7,
//                        -90, -9, -8};
//        bluetoothGattCharacteristic.setValue(value);
//        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() != 0)
//            mBluetoothGattServer.notifyCharacteristicChanged(
//                    mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).get(0),
//                    bluetoothGattCharacteristic, true);
//        Log.d(TAG, "Sending beacon values 2");
//    }
//
//    public void sendBeaconValues3()
//    {
//        // TODO сделать рассылку по всем подключенным устройствам
//        BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                mBluetoothGattServer.getService(ServerProfile.CLIENT_SERVICE)
//                        .getCharacteristic(ServerProfile.DISTANCE);
//
//        byte []value = {-100, -110, -90,
//                        -10, -9, -8};
//        bluetoothGattCharacteristic.setValue(value);
//        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() != 0)
//            mBluetoothGattServer.notifyCharacteristicChanged(
//                    mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).get(0),
//                    bluetoothGattCharacteristic, true);
//        Log.d(TAG, "Sending beacon values 3");
//    }
//
//    public void sendRegBeaconValues(int index, byte[] array)
//    {
//        // TODO сделать рассылку по всем подключенным устройствам
//        BluetoothGattCharacteristic bluetoothGattCharacteristic =
//                mBluetoothGattServer.getService(ServerProfile.CLIENT_SERVICE)
//                        .getCharacteristic(ServerProfile.DISTANCE);
//
//        bluetoothGattCharacteristic.setValue(array);
//        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() != 0)
//            mBluetoothGattServer.notifyCharacteristicChanged(
//                    mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).get(0),
//                    bluetoothGattCharacteristic, true);
//        Log.d(TAG, "Sending beacon values index = " + index);
//    }
    /**
     * Verify the level of Bluetooth support provided by the hardware.
     * @param bluetoothAdapter System {@link BluetoothAdapter}.
     * @return true if Bluetooth is properly supported, false otherwise.
     */
    private boolean checkBluetoothSupport(BluetoothAdapter bluetoothAdapter) {

        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported");
            return false;
        }

        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported");
            return false;
        }

        return true;
    }

//    public static byte[] float2ByteArray(float value)
//    {
//        return ByteBuffer.allocate(4).putFloat(value).array();
//    }

    public static byte[] double2ByteArray(double value) {
        return ByteBuffer.allocate(8).putDouble(value).array();
    }

//    public static float byteArray2Float(byte[] value)
//    {
//        float result = value[0]
//                    | (value[1] << 8)
//                    | (value[2] << 16)
//                    | (value[3] << 24);
//        return result;
//    }
}
