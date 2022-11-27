package com.roboart.mobokeylibrary.MKResponseListener;

import android.bluetooth.le.ScanResult;

import java.util.List;

public interface ScanResponseListner {
    public void ScanResponse(int response, List<ScanResult> scanResultList);
}
