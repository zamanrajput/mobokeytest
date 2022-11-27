package com.roboart.mobokeylibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;

import com.roboart.mobokeylibrary.MKResponseListener.ScanResponseListner;

import java.util.ArrayList;
import java.util.List;

public class ScanMoboKey {
    private Context context;
    private int SCAN_PERIOD;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private List<ScanResult> scanResults;
    private BluetoothLeScanner mLEScanner;
    private ScanResponseListner scanResponseListner;

    public ScanMoboKey(Context context) {
        this.context = context;
        init();
    }

    private void init(){
        scanResults = new ArrayList<>();
        scanResponseListner = (ScanResponseListner) context;
    }

    private void _a_(int _1_ , String deviceName) {
        scanResults.clear();
        SCAN_PERIOD = _1_;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<ScanFilter>();
        filters.add(new ScanFilter.Builder().setDeviceName(deviceName).build());
        _a_1(true);
        return;
    }

    private void _a_1(final boolean enable) {
        if (enable) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(_a_2);
                    if (!scanResults.isEmpty()){
                        _a_3();
                        scanResponseListner.ScanResponse(1, scanResults);//Device Found
                    }else{//no device found
                        scanResponseListner.ScanResponse(0, scanResults);//Device not Found
                    }
                }
            }, SCAN_PERIOD);
            mLEScanner.startScan(filters, settings, _a_2);
        } else {
            mLEScanner.stopScan(_a_2);
        }
        return;
    }

    private ScanCallback _a_2 = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            scanResults.add(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
        }

        @Override
        public void onScanFailed(int errorCode) {
        }
    };

    private void _a_3(){
        if (!scanResults.isEmpty()) {
            List<ScanResult> temp = new ArrayList<>();
            BluetoothDevice bluetoothDevice;
            for(int i=0;i<scanResults.size(); i++) {
                bluetoothDevice = scanResults.get(i).getDevice();
                int j=0;
                boolean Notok = false;
                for (j = 0; j < temp.size(); j++) {
                    if (temp.get(j).getDevice().getAddress().equals(bluetoothDevice.getAddress())) {
                        Notok = true;
                        continue;
                    }
                }
                if (!Notok && j == temp.size()) {
                    temp.add(scanResults.get(i));
                }
            }
            scanResults = temp;
        }
    }

    public void ScanMKDevice(int period, String deviceName){
        _a_(period, deviceName);
        return;
    }

    public void ScanSpecficMKDevice(String mac, int period){
        scanResults.clear();
        SCAN_PERIOD = period;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<ScanFilter>();
        filters.add(new ScanFilter.Builder().setDeviceAddress(mac).build());
        _a_1(true);
        return;
    }
}
