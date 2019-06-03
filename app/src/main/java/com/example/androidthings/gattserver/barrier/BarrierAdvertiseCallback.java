//package com.example.androidthings.gattserver.barrier;
//
//import android.bluetooth.le.AdvertiseCallback;
//import android.bluetooth.le.AdvertiseSettings;
//import android.util.Log;
//
//import static com.example.androidthings.gattserver.barrier.GattServer.TAG;
//
//
//public class BarrierAdvertiseCallback extends AdvertiseCallback {
//
//    @Override
//    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//        Log.i(TAG, "LE Advertise Started.");
//    }
//
//    @Override
//    public void onStartFailure(int errorCode) {
//        switch (errorCode)
//        {
//            case (AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE):
//                Log.e(TAG, "Advertising error: ADVERTISE_FAILED_DATA_TOO_LARGE");
////                Toast.makeText(context, "Advertising error: ADVERTISE_FAILED_DATA_TOO_LARGE", Toast.LENGTH_SHORT).show();
//                break;
//            case (AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS):
//                Log.e(TAG, "Advertising error: ADVERTISE_FAILED_TOO_MANY_ADVERTISERS");
////                Toast.makeText(context, "Advertising error: ADVERTISE_FAILED_TOO_MANY_ADVERTISERS", Toast.LENGTH_SHORT).show();
//                break;
//            case (AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED):
//                Log.e(TAG, "Advertising error: ADVERTISE_FAILED_ALREADY_STARTED");
////                Toast.makeText(context, "Advertising error: ADVERTISE_FAILED_ALREADY_STARTED", Toast.LENGTH_SHORT).show();
//                break;
//            case (AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR):
//                Log.e(TAG, "Advertising error: ADVERTISE_FAILED_INTERNAL_ERROR");
////                Toast.makeText(context, "Advertising error: ADVERTISE_FAILED_INTERNAL_ERROR", Toast.LENGTH_SHORT).show();
//                break;
//            case (AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED):
//                Log.e(TAG, "Advertising error: ADVERTISE_FAILED_FEATURE_UNSUPPORTED");
////                Toast.makeText(context, "Advertising error: ADVERTISE_FAILED_FEATURE_UNSUPPORTEDE", Toast.LENGTH_SHORT).show();
//                break;
//        }
//        Log.w(TAG, "LE Advertise Failed: "+errorCode);
//    }
//
//}
