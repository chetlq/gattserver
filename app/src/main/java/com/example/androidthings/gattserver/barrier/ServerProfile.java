package com.example.androidthings.gattserver.barrier;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class ServerProfile {

    public static UUID CLIENT_SERVICE = UUID.fromString("e7b525b0-9dbb-4429-87a2-b12c46b0d197");
//    public static UUID CLIENT_SERVICE = UUID.fromString("e7b525b0-9dbb-4429-87a2-b12c46b0d199");
//    public static UUID ACCOUNT_TOKEN      = UUID.fromString("5AA21D1D-0467-44B4-9606-089ADD9ABD2B");
//    public static UUID DISTANCE = UUID.fromString("E1E5889B-897F-4F43-8F00-6C739551B3E5");
//    public static UUID DISTANCE_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static UUID UUID_BATTERY_LEVEL_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static UUID COMMAND          = UUID.fromString("099c484d-4bec-4bd2-b91f-14ecdd691aa7");
//    public static UUID COMMAND_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
//    public static UUID RESULT_REQUEST   = UUID.fromString("bc371eac-3253-4134-98c3-b685efc0b5c4");
//    public static UUID BARRIER_TYPE     = UUID.fromString("06040312-d71f-445d-87f9-ec8b7714e5d6");

    public static String token1 = "920187952898da0c7d17157a405bccf0b806e2cdacb4453dae65cdef387525da";
    public static String token2 = "4b821fd17c2953cec72281842e73b3a67d1f522b8928a00118b78d9ad78e112a";

    public static BluetoothGattService createClientService() {
        BluetoothGattService service = new BluetoothGattService(CLIENT_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

//        BluetoothGattCharacteristic accountTokenCharacteristic = new BluetoothGattCharacteristic(ACCOUNT_TOKEN,
//                BluetoothGattCharacteristic.PROPERTY_READ,
//                BluetoothGattCharacteristic.PERMISSION_READ);
//        service.addCharacteristic(accountTokenCharacteristic);

//        BluetoothGattCharacteristic distanceCharacteristic = new BluetoothGattCharacteristic(DISTANCE,
//                BluetoothGattCharacteristic.PROPERTY_READ| BluetoothGattCharacteristic.PROPERTY_NOTIFY,
//                BluetoothGattCharacteristic.PERMISSION_READ);
//
//        BluetoothGattDescriptor distanceConfigDescriptor = new BluetoothGattDescriptor(DISTANCE_DESCRIPTOR, 0);
//        distanceCharacteristic.addDescriptor(distanceConfigDescriptor);
//        service.addCharacteristic(distanceCharacteristic);
//
        BluetoothGattCharacteristic commandCharacteristic = new BluetoothGattCharacteristic(COMMAND,
                BluetoothGattCharacteristic.PROPERTY_READ| BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);

//        BluetoothGattDescriptor commandConfigDescriptor = new BluetoothGattDescriptor(COMMAND_DESCRIPTOR, 0);
//        commandCharacteristic.addDescriptor(commandConfigDescriptor);
//        service.addCharacteristic(commandCharacteristic);
//
//        BluetoothGattCharacteristic resultRequestCharacteristic = new BluetoothGattCharacteristic(RESULT_REQUEST,
//                BluetoothGattCharacteristic.PROPERTY_WRITE,
//                BluetoothGattCharacteristic.PERMISSION_WRITE);
//        service.addCharacteristic(resultRequestCharacteristic);
//
//        BluetoothGattCharacteristic barrierTypeCharacteristic = new BluetoothGattCharacteristic(BARRIER_TYPE,
//                BluetoothGattCharacteristic.PROPERTY_WRITE,
//                BluetoothGattCharacteristic.PERMISSION_WRITE);
//        service.addCharacteristic(barrierTypeCharacteristic);

        BluetoothGattCharacteristic batteryTypeCharacteristic = new BluetoothGattCharacteristic(UUID_BATTERY_LEVEL_UUID,

                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(batteryTypeCharacteristic);
        service.addCharacteristic(commandCharacteristic);

        return service;
    };
}
