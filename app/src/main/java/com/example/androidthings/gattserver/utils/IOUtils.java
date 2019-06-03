//package com.example.androidthings.gattserver.utils;
//
//import android.content.Context;
//import android.util.Log;
//
//
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//
//import static android.content.Context.MODE_PRIVATE;
//import static com.example.androidthings.gattserver.barrier.GattServer.TAG;
//
//public class IOUtils {
//    public static final String BARRIER_TYPE_ENTER = "enter";
//    public static final String BARRIER_TYPE_EXIT = "exit";
//
//    public static final String BARRIERTYPEFILENAME = "barrierType";
//
//    public static void writeBarrierTypeToFile(Context context, String barrierType)
//    {
//        try {
//            // отрываем поток для записи
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
//                    context.openFileOutput(BARRIERTYPEFILENAME, MODE_PRIVATE)));
//            // пишем данные
//            bw.write(barrierType);
//            // закрываем поток
//            bw.close();
//            Log.d(TAG, "barrierType wrote to file");
//        } catch (IOException e) {
//            Log.d(TAG, "IOException on writing barrierType to file");
//            Log.d(TAG, e.getMessage());
//        }
//    }
//
//    public static String readBarrierTypeFromFile(Context context) {
//        String barrierType = null;
//        try {
//            // открываем поток для чтения
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    context.openFileInput(BARRIERTYPEFILENAME)));
//            String str;
//            while ((str = br.readLine()) != null) {
//                barrierType = str;
//            }
//            br.close();
//
//        } catch (FileNotFoundException e) {
//            Log.d(TAG, "FileNotFoundException on reading barrierType to file");
//            Log.d(TAG, e.getMessage());
//        } catch (IOException e) {
//            Log.d(TAG, "IOException on reading barrierType to file");
//            Log.d(TAG, e.getMessage());
//        }
//        return barrierType;
//    }
//
//}
