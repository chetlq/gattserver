package com.example.androidthings.gattserver.barrier;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


//import com.example.androidthings.gattserver.TimeProfile;
import com.example.androidthings.gattserver.interfaces.GetDistance;
import com.example.androidthings.gattserver.interfaces.UpdateStatus;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;


public class GattServerCallback extends BluetoothGattServerCallback {
    private static final String TAG = "GattServerCallback";
    private Context mContext;
     Set<BluetoothDevice> devices = new HashSet<>();
    public GattServerCallback(Context context) {
        mContext = context;
        Log.d(TAG, "GattServerCallback constructor");
    }

    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        Log.d(TAG, "GattServerCallback onConnectionStateChange...");
        switch (newState){
            case (BluetoothProfile.STATE_CONNECTED):
              //  ((UpdateStatus)mContext).UpdateStatus("GattCallback STATE_CONNECTED..." + device.getAddress());

                Log.d(TAG, "GattCallback STATE_CONNECTED..." + device.getAddress());
                break;
            case (BluetoothProfile.STATE_DISCONNECTED):
            //    ((UpdateStatus)mContext).UpdateStatus("GattCallback STATE_DISCONNECTED..." + device.getAddress());
                Log.d(TAG, "GattCallback STATE_DISCONNECTED..." + device.getAddress());
                break;
        }
    }

    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattCharacteristic characteristic,
                                             boolean preparedWrite, boolean responseNeeded,
                                             int offset, byte[] value) {
        Log.d(TAG, "GattServerCallback onCharacteristicWriteRequest...");
//        if (characteristic.getUuid().equals(ServerProfile.RESULT_REQUEST))
//        {
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("Receiving resultRequest = ");
//            for (int i = 0; i < value.length; i++)
//            {
//                stringBuilder.append(value[i]).append(" ");
//            }
//            Log.d(TAG, "RESULT_REQUEST>> "+stringBuilder.toString());
//
////            ((UpdateResultRequest)mContext).UpdateResultRequest(value[0]);
//            if ((value.length > 1) & (value.length == 5))
//            {
//                final Integer receivedAmount =
//                         value[1]|
//                        (value[2] << 8)|
//                        (value[3] << 16)|
//                        (value[4] << 24);
//                Log.d(TAG, "receivedAmount>> "+receivedAmount.toString());
////                ((DeviceScanActivity)(mContext)).runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        Toast.makeText(mContext, "Amount = " + receivedAmount, Toast.LENGTH_SHORT).show();
////                    }
////                });
//
//            }
//        }
//        else
//        if (characteristic.getUuid().equals(ServerProfile.BARRIER_TYPE))
//        {
//            Log.d(TAG, "Receiving barrier type = " + value[0]);
//            switch (value[0])
//            {
//                case (GattServer.BARRIER_TYPE_ENTER):
//                    Log.d(TAG, "Barrier type enter");
////                    ((UpdateBarrierStatus)mContext).updateBarrierStatus("Barrier type enter");
//                    break;
//                case (GattServer.BARRIER_TYPE_EXIT):
//                    Log.d(TAG, "Barrier type exit");
////                    ((UpdateBarrierStatus)mContext).updateBarrierStatus("Barrier type exit");
//                    break;
//            }
//
////            updateBarrierStatus
//        }

        if (responseNeeded)
        {
            GattServer.mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }
    }

    @Override
    public void onMtuChanged(BluetoothDevice device, int mtu) {
        super.onMtuChanged(device, mtu);
        Log.d(TAG, "onMtuChanged");

    }

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "GattServerCallback onCharacteristicReadRequest...");
        UUID uuid = characteristic.getUuid();
        byte[] value = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};

        String testToken = "920187952898da0c7d17157a405bccf0b806e2cdacb4453dae65cdef387525da";
//        String testToken = "4b821fd17c2953cec72281842e73b3a67d1f522b8928a00118b78d9ad78e112a";
//        String testToken = ((GetChosenToken)mContext).getChosenToken();

        try {
            characteristic.setValue(testToken.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        if (ServerProfile.UUID_BATTERY_LEVEL_UUID.equals(characteristic.getUuid()))
//        {
//            Log.d(TAG, "BATTERY sended");
////            try {
////                GattServer.mBluetoothGattServer.sendResponse(device, requestId, GATT_SUCCESS, offset, testToken.getBytes("UTF-8"));
////                Log.d(TAG, "Token sended");
////            } catch (UnsupportedEncodingException e) {
////                e.printStackTrace();
////            }
//        }
//        if (ServerProfile.ACCOUNT_TOKEN.equals(characteristic.getUuid()))
//        {
//            try {
//                GattServer.mBluetoothGattServer.sendResponse(device, requestId, GATT_SUCCESS, offset, testToken.getBytes("UTF-8"));
//                Log.d(TAG, "Token sended");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        if (ServerProfile.COMMAND.equals(characteristic.getUuid()))
//        {
//            Log.d(TAG, "Reading command characteristic");
//            GattServer.mBluetoothGattServer.sendResponse(device, requestId, GATT_SUCCESS, offset, null);
//        }
//        if (ServerProfile.DISTANCE.equals(characteristic.getUuid()))
//        {
//            Log.d(TAG, "Reading distance characteristic");
//            GattServer.mBluetoothGattServer.sendResponse(device, requestId,
//                    GATT_SUCCESS, offset, GattServer.double2ByteArray(((GetDistance)mContext).getDistance()));
//        }
    }

    @Override
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        super.onDescriptorReadRequest(device, requestId, offset, descriptor);
        Log.d(TAG, "onDescriptorReadRequest");
    }

//    @Override
//    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
//        super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
//        Log.d(TAG, "onDescriptorWriteRequest");
//    }

            @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded,
                                             int offset,  byte[] value) {
                Log.d(TAG, "onDescriptorReadRequest");
//            if (ServerProfile.ACCOUNT_TOKEN.equals(descriptor.getUuid())) {
//                Log.d(TAG, "onDescriptorWriteRequest: WIFI_STAT_CONFIG");
//                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
//                    Log.d(TAG, "Subscribe device to notifications: " + device);
//                    devices.add(device);
//                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
//                    Log.d(TAG, "Unsubscribe device from notifications: " + device);
//                    devices.remove(device);
//                }
//
//
//        }
    };
}
