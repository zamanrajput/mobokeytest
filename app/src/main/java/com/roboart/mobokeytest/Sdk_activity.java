package com.roboart.mobokeytest;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.roboart.mobokeylibrary.MKDevice;
import com.roboart.mobokeylibrary.MKResponseListener.ConnectionResponseListener;
import com.roboart.mobokeylibrary.MKResponseListener.OperationsResponseListener;
import com.roboart.mobokeylibrary.MKResponseListener.RssiResponseListner;
import com.roboart.mobokeylibrary.MKResponseListener.ScanResponseListner;
import com.roboart.mobokeylibrary.ScanMoboKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Sdk_activity extends AppCompatActivity implements ConnectionResponseListener,
        ScanResponseListner, OperationsResponseListener, RssiResponseListner {
    Button btn_1, btn_2,disconnect, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_10, btn_11, btn_12,
            btn_13, btn_14, btn_15, btn_16, btn_17, btn_18, btn_19, btn_20, btn_21 , acc , pairBtn , authBtn
            , pustTypeStartStop;
    EditText et;
    TextView tv, tvDeviceInfo , showrssi;
    MKDevice obj;
    ScanMoboKey scanMoboKey;
    private List<ScanResult> scanResults;
    private boolean isConnected;
    private String code;
    ProgressDialog progressDialog1;
    EditText deviceNameEt;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        scanMoboKey = new ScanMoboKey(this);
        obj = new MKDevice(this);
        scanResults = new ArrayList<>();
        //et = findViewById(R.id.timer_input);
        tv = findViewById(R.id.tv);
        tvDeviceInfo = findViewById(R.id.tv_info);
        showrssi = findViewById(R.id.showrssi);
        btn_1 = findViewById(R.id.btnScan);
        btn_2 = findViewById(R.id.btnConnect);
        pairBtn = findViewById(R.id.btnPair);
        authBtn = findViewById(R.id.btnAuthenticate);
        disconnect=findViewById(R.id.btndisConnect);
        deviceNameEt = findViewById(R.id.devicename);
        btn_3 = findViewById(R.id.btnLock);
        btn_4 = findViewById(R.id.btnUnlock);
        btn_5 = findViewById(R.id.btnTrunk);
        acc = findViewById(R.id.btnAcc);
        btn_6 = findViewById(R.id.btnPower);
        btn_7 = findViewById(R.id.btnStart);
        btn_8 = findViewById(R.id.btnStop);
        btn_9 = findViewById(R.id.btnEKOn);
        btn_10 = findViewById(R.id.btnEKOff);
        btn_11 = findViewById(R.id.btnAutoLockOn);
        btn_12 = findViewById(R.id.btnAutoLockOff);
        btn_13 = findViewById(R.id.btnSecurityOn);
        btn_14 = findViewById(R.id.btnSecurityOff);
        btn_15 = findViewById(R.id.btnCarTypePush);
        btn_16 = findViewById(R.id.btnCarTypeSelf);
        btn_17 = findViewById(R.id.btnPushPowerOn);
        btn_18 = findViewById(R.id.btnPushPowerOff);
        pustTypeStartStop = findViewById(R.id.btnPushCarStartStop);
//        btn_19 = findViewById(R.id.btnSetEkTime);
//        btn_20 = findViewById(R.id.btnSetSelftTime);
//        btn_21 = findViewById(R.id.btnRssi);

        progressDialog1 = new ProgressDialog(Sdk_activity.this);
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.initListner(Sdk_activity.this, Sdk_activity.this, Sdk_activity.this, Sdk_activity.this);

                try {
                    if(deviceNameEt.getText().toString().length() != 0) {
                        scanMoboKey.ScanMKDevice(2000 , deviceNameEt.getText().toString());
                        progressDialog1.setMessage("Scanning...");
                        progressDialog1.show();
                        tv.setText("Searching...");
                    }else {
                        Toast.makeText(Sdk_activity.this, "Please enter valid device name", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    Toast.makeText(Sdk_activity.this, "Kindly on your Bluetooth and Gps location", Toast.LENGTH_SHORT).show();
                }
            }
        });



        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected ) {
//
                   // tv.setText("Trying Connect...");
                    Log.i("Connection Status", "trying...");
                    progressDialog1.setMessage("Initializing Connection");
                    progressDialog1.show();
                    Log.d("Test", "onClick: " + scanResults.get(0).getDevice().getName());
                  boolean che = obj.ConnectWithMK(scanResults.get(0).getDevice(), true,scanResults.get(0).getDevice().getName() );//Connect with Device
//                    Toast.makeText(MainActivity.this, "" + che , Toast.LENGTH_SHORT).show();
                }
//                else {
//                    obj.MKDisconnect(false);
//                }
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnected) {
                    progressDialog1.setMessage("Disconnecting...");
                    progressDialog1.show();

                    obj.MKDisconnect();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            progressDialog1.dismiss();
//                            tv.setText("Disconnected.");
//                            tvDeviceInfo.setText("Device Model/Firmware");
//
//                        }
//                    },3000);
//                    isConnected=false;

                }
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKGetLock();

            }
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKGetUnlock();

            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKTrunk();
            }
        });

        acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKAccOn();
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKPowerOn();
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKStart();
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               obj.MKPushPowerOff();
            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    obj.MKEngineDeadOn();
            }
        });
        btn_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    obj.MKEngineDeadOff();
            }
        });
        btn_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKAutoLockOn();
            }
        });
        btn_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKAutoLockOff();
            }
        });
        btn_13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKCarTypePush();
            }
        });
        btn_16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj.MKCarTypeSelf();
            }
        });
        pairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBleDevicePaired(scanResults.get(0).getDevice())){
                    Toast.makeText(Sdk_activity.this, "Device Already Paired", Toast.LENGTH_SHORT).show();
                }else {
                    obj.MKPair();
                }

            }
        });
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj.MKAuthenticate();
            }
        });

        btn_17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // obj.MKPowerOn();
                obj.MKAccOn();
            }
        });
        btn_18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    obj.MKPushPowerOff();
                obj.MKAccOff();
            }
        });

        pustTypeStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obj.MKPushPowerOn();
            }
        });
//        btn_19.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                obj.MKEngineKillTimer(1);//Pass Engine Kill timer int 1-10
//
//            }
//        });
//        btn_20.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                obj.MKSelfStartTimer(3);// Pass the int Self Start Timer 0-9
//            }
//        });
//        btn_21.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("RSSI: " ,obj.GetRssi());//Get the distance form devie in Hight, Medium, Low
//            }
//        });
    }

    @Override
    public void ConnectionResponse(int response, String device) {
        switch (response) {
            case 0:// Feedback when Disconnect with device
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Handler handler= new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText("Disconnected.");
                                progressDialog1.dismiss();
                                tvDeviceInfo.setText("Device Model/Firmware");
                            }
                        },3000);
                        isConnected = false;

                        Log.i("Connection status: ", "Disconnected");
                    }
                });
                break;
            case 1:// Feedback when connecting with device
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Connection status: ", "Connecting");
                       // tv.setText("Connecting...");
//                        progressDialog1.setMessage("Connecting...");
//                        progressDialog1.show();
                        //When you receive connecting feedback check for regular key other wise connection won't be complete
                        obj.MKCheckMasterKeyForConnection("1010");
                    }
                });
                break;
            case 2:// Feedback When connected with device
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isConnected = true;
                        tv.setText("Connected: "+ scanResults.get(0).getDevice().getName());
                       // progressDialog1.dismiss();
                        tvDeviceInfo.setText("MAC : " + scanResults.get(0).getDevice().getAddress() + "\n \n" + obj.getSerialNumber() + "\n" + obj.getFirmware() + "\n"    );
                        Log.i("Connection status: ", "Connected");
                        obj.RegisterRssi();
                    }
                });
                break;
            case 11: // FeedBack when connects and services discovered
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog1.dismiss();
                        tvDeviceInfo.setText("MAC : " + scanResults.get(0).getDevice().getAddress() + "\n\nDevice Connected ");


                    }
                });
                break;


        }
    }

    @Override
    public void ScanResponse(int response, List<ScanResult> scanResults) {
        Log.i("Scan Response: ", response + "");
        if (response == 1)//Device Found Feedback
        {
            this.scanResults = scanResults; //Get Scan Results
            tv.setText("Device Found: " + scanResults.get(0).getDevice().getName() );
            tvDeviceInfo.setText("MAC : " + scanResults.get(0).getDevice().getAddress());
            progressDialog1.dismiss();
            Log.i("Scan Result: ", scanResults.size() + "");
            //if Device is connecting first time pairing process will be initiate
        } else if (response == 0) {//No Device Found Feedback
            tv.setText("No Device Found");
            progressDialog1.dismiss();
        }
    }

    @Override
    public void OperationsResponse(final int response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (response) {
                    case 0:// Invalid RK Feedback
                        tv.setText("RK Invalid...");
                        Log.i("RK status: ", "Invalid");
                        break;
                    case 1://valid RK Feedback
                        tv.setText("RK Valid...");
                        Log.i("RK status: ", "Valid");
//                        obj.MKStatus();
                        break;
                    case 2:// Invalid MK Feedback
                        Log.i("MK status: ", "Invalid");
                        break;
                    case 3://valid MK Feedback
                        Log.i("MK status: ", "Valid");
                        obj.MKStatus();
                        break;
                    case 4:// RK Changed Feedback
                        Log.i("RK Change: ", "Valid");
                        break;
                    case 5://RK Change Error Feedback
                        Log.i("RK Change: ", "Invalid");
                        break;
                    case 6:// MK Changed Feedback
                        Log.i("MK Change: ", "Valid");
                        break;
                    case 7://MK Change Error Feedback
                        Log.i("MK Change: ", "Invalid");
                        break;
                    case 8:// Lock Feedback
                        Log.i("Operation: ", "Locked");
                        break;
                    case 9:// Unlock Feedback
                        Log.i("Operation: ", "Unlocked");
                        break;
                    case 10:// Engine Kill On Feedback
                        Log.i("Operation: ", "Engine Kill On");
                        break;
                    case 11:// Engine Kill Off Feedback
                        Log.i("Operation: ", "Engine Kill Off");
                        break;
                    case 12:// Acc on Feedback
                        Log.i("Operation: ", "Acc on");
                        break;
                    case 13:// ACc off Feedback
                        Log.i("Operation: ", "Acc off");
                        break;
                    case 14:// Power on Feedback
                        Log.i("Operation: ", "Power on");
                        break;
                    case 15:// Power off Feedback
                        Log.i("Operation: ", "Power off");
                        break;
                    case 16:// Start Feedback
                        Log.i("Operation: ", "Start");
                        break;
                    case 17:// Push Power on Feedback
                        Log.i("Operation: ", "Push Power On");
                        break;
                    case 18:// Push Power off Feedback
                        Log.i("Operation: ", "Push Power Off");
                        break;
                    case 19:// Timer Feedback
                        Log.i("Operation: ", "Timer Changed");

                        break;
                    case 21:// AutoLock on Feedback
                        Log.i("Operation: ", "AutoLock On");
                        break;
                    case 22:// AutoLock off Feedback
                        Log.i("Operation: ", "AutoLock Off");
                        break;
                    case 23:// Security On Feedback
                        Log.i("Operation: ", "Security On");
                        break;
                    case 24:// Security Off Feedback
                        Log.i("Operation: ", "Security Off");
                        break;
                    case 25:// Car Type Push Feedback
                        Log.i("Operation: ", "Push Start Car");
                        break;
                    case 26:// Car Type Self Feedback
                        Log.i("Operation: ", "Self Start Car");
                        break;
                    case 27:// Proximity Lock Unlock On
                        Log.i("Operation: ", "ProxLU On");
                        break;
                    case 28:// Proximity Lock Unlock Off
                        Log.i("Operation: ", "ProxLU Off");
                        break;
                    case 29:// Proximity Start Stop On
                        Log.i("Operation: ", "ProxSS Car");
                        break;
                    case 30:// Proximity Lock Unlock On
                        Log.i("Operation: ", "ProxSS Car");
                        break;
                }
            }
        });
    }

    public boolean requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void RssiResponse(int i) {
        Log.d("rssi", "RssiResponse: " + i);
        showrssi.setText(String.valueOf(i));
    }

    private boolean isBleDevicePaired(BluetoothDevice bleDevice) {
        if (bleDevice != null) {
            Set<BluetoothDevice> paired = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            for (BluetoothDevice b : paired) {
                if (b.getName() != null && !b.getName().isEmpty())
                    if (b.getAddress().equals(bleDevice.getAddress())) {
                        return true;
                    }
            }
            return false;
        }
        return false;
    }

}
