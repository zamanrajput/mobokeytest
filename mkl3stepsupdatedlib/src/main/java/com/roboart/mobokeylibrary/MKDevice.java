package com.roboart.mobokeylibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.util.Log;

import com.roboart.mobokeylibrary.MKResponseListener.ConnectionResponseListener;
import com.roboart.mobokeylibrary.MKResponseListener.OperationsResponseListener;
import com.roboart.mobokeylibrary.MKResponseListener.RssiResponseListner;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MKDevice {
    private Context context;
    private int[] outOfRange;
    private boolean autoConnect, isSelfStart, PushStartNotify;
    private BluetoothDevice bleDevice;
    private BluetoothGatt bluetoothGatt;
    private List<WriteModel> WriteQueue;
    private RssiResponseListner rssiResponseListner;
    private ScheduledExecutorService scheduleTask4Rsii;
    private List<BluetoothGattCharacteristic> ReadQueue;
    private List<CharacteristicsModel> characteristicsModels;
    private List<CharacteristicsModel> devicInfoCharacteristicsList;
    private ConnectionResponseListener bleConnectionStateListener;
    private OperationsResponseListener operationsResponseListener;
    private String RK1, RK2, newRK1, newRK2, MK1, MK2, newMK1, newMK2, KeyChange, serialNumber, firmware, connectedWith, strConnectionReqSended;
    private int timer_1, timer_2, ReadQueueIndex, outOfRangeCounter, WriteQueueIndex;
    private boolean CheckRK, readStates, OperationFeedback, CheckMK, isValidRk, ChangeRK, ChangeMK, ReadRssi, reconnect, MultipleWrite,
            isEngineKill, isSecurity, dummyConnection, WritePushStartON, WritePushStartOFF, isConnectionStatusValid, isDisconnected,
            successfulConnection, closeConnection, isLockingMode, isLockUnlockOperation, isConnectionReqSended , pairingCharac;

    private void init() {
        bleDevice = null;
        RK1 = "";
        RK2 = "";
        MK1 = "";
        MK2 = "";
        newMK1 = "";
        newMK2 = "";
        newRK1 = "";
        newRK2 = "";
        KeyChange = "";
        serialNumber = "";
        firmware = "";
        outOfRangeCounter = 0;
        WriteQueueIndex = 0;
        WriteQueue = new ArrayList<>();
        outOfRange = new int[300];
        ChangeMK = false;
        ChangeRK = false;
        isLockUnlockOperation = false;
        closeConnection = false;
        successfulConnection = false;
        CheckMK = false;
        isDisconnected = false;
        dummyConnection = false;
        isEngineKill = false;
        isSecurity = false;
        isLockingMode = false;
        WritePushStartOFF = false;
        CheckRK = false;
        reconnect = false;
        isConnectionStatusValid = false;
        WritePushStartON = false;
        MultipleWrite = false;
        PushStartNotify = false;
        isSelfStart = false;
        isValidRk = false;
        readStates = false;
        ReadRssi = false;
        ReadQueueIndex = 0;
        OperationFeedback = false;
        devicInfoCharacteristicsList = new ArrayList<>();
        ReadQueue = new ArrayList<>();
        characteristicsModels = new ArrayList<>();
    }

    public boolean _c_(BluetoothDevice _1_, boolean _2_, String devicename) {
        if (bluetoothGatt == null) {
            this.autoConnect = _2_;
            if (_1_ != null && _1_.getName().equals(devicename)) {
                bleDevice = _1_;
                bluetoothGatt = _1_.connectGatt(context, _2_, _b_1);
                reconnect = true;
                return true;
            }
        } else {
            this.autoConnect = _2_;
            init();
            reconnect = false;
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bleDevice = null;
            bluetoothGatt = null;
            bleDevice = _1_;
            if (_1_ != null && _1_.getName().equals(devicename)) {
                bluetoothGatt = _1_.connectGatt(context, autoConnect, _b_1);
            }
        }
        return false;
    }

    private BluetoothGattCallback _b_1 = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothGatt.STATE_CONNECTED:
//                    if(!dummyConnection && characteristicsModels.isEmpty()) {
                    bleDevice = gatt.getDevice();
                    bluetoothGatt = gatt;
                    gatt.discoverServices();
//                    }else{
//                        dummyConnection = false;
//                        bleConnectionStateListener.ConnectionResponse(3, "dummyResponse");
//                    }
                    break;
                case BluetoothGatt.STATE_DISCONNECTED:
                    isConnectionReqSended = false;
                    isDisconnected = true;
                    if (!successfulConnection) {
                        bluetoothGatt = null;
                    }
                    if (ReadRssi) {
                        scheduleTask4Rsii.shutdown();
                    }
                    if (reconnect) {
                        reconnect = false;
                        gatt.connect();
                        init();
//                        dummyConnection = true;
                    }
                    if (closeConnection) {
                        closeConnection = false;
                        gatt.disconnect();
                        gatt.close();
                        bluetoothGatt = null;
                    }
                    bleConnectionStateListener.ConnectionResponse(0, gatt.getDevice().getAddress());//Disconnect
                    break;
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            characteristicsModels.clear();
            try {
                for (BluetoothGattCharacteristic c : gatt.getServices().get(2).getCharacteristics()) {
                    devicInfoCharacteristicsList.add(new CharacteristicsModel(false, c, "0", 0));
                }
            } catch (IndexOutOfBoundsException e) {
                Log.e("MK_EXCEPTION", e.getMessage());
            }
            try {
                Log.d("fourthservice", "onServicesDiscovered: " + gatt.getServices().get(3).getUuid().toString());
                for (BluetoothGattCharacteristic c : gatt.getServices().get(3).getCharacteristics()) {
                    characteristicsModels.add(new CharacteristicsModel(false, c, "0", 0));
                }
            } catch (IndexOutOfBoundsException e) {
                Log.e("MK_EXCEPTION", e.getMessage());
            }
            bleConnectionStateListener.ConnectionResponse(11, gatt.getDevice().getAddress()); // services discovered
           // setNotificationCharacter();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            String value = Arrays.toString(characteristic.getValue());
            if(pairingCharac){
                pairingCharac = false;
                if (characteristic.getUuid().equals
                        (devicInfoCharacteristicsList.get(2).getBluetoothGattCharacteristic().getUuid())) {
                    byte[] bytes = characteristic.getValue();
                    try {
                        String data = new String(bytes, "UTF-8");
//                        serialNumber = data;

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (readStates) {
                if (characteristic.getUuid().equals
                        (devicInfoCharacteristicsList.get(2).getBluetoothGattCharacteristic().getUuid())) {
                    byte[] bytes = characteristic.getValue();
                    try {
                        String data = new String(bytes, "UTF-8");
                        serialNumber = data;

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                if (characteristic.getUuid().equals
                        (devicInfoCharacteristicsList.get(3).getBluetoothGattCharacteristic().getUuid())) {
                    byte[] bytes = characteristic.getValue();
                    try {
                        String data = new String(bytes, "UTF-8");
                        firmware = data;

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if (characteristic.getUuid().equals
                        (characteristicsModels.get(3).getBluetoothGattCharacteristic().getUuid())) {
                    String str = _p_c_(characteristic.getValue());
                    if (str.substring(1, 2).equals("3")) {
                        operationsResponseListener.OperationsResponse(8);
                    } else if (str.substring(1, 2).equals("4")) {
                        operationsResponseListener.OperationsResponse(9);
                    }
                    if (str.substring(0, 1).equals("3")) {//Engine Kill on
                        isEngineKill = true;
                        operationsResponseListener.OperationsResponse(10);
                    } else if (str.substring(0, 1).equals("4")) {//Engine Kill off
                        isEngineKill = false;
                        operationsResponseListener.OperationsResponse(11);
                    }
                    characteristicsModels.get(3).setFeedBackValue(str);
                } else if (characteristic.getUuid().equals
                        (characteristicsModels.get(4).getBluetoothGattCharacteristic().getUuid())) {
                    String str = _p_c_(characteristic.getValue());
                    if (isSelfStart) {
                        if (str.substring(1, 2).equals("5")) {//Acc on
                            operationsResponseListener.OperationsResponse(12);
                        } else if (str.substring(1, 2).equals("6")) {//Acc off
                            operationsResponseListener.OperationsResponse(13);
                        } else if (str.substring(1, 2).equals("7")) {//Power on
                            operationsResponseListener.OperationsResponse(14);
                        } else if (str.substring(1, 2).equals("8")) {//Power off
                            operationsResponseListener.OperationsResponse(15);
                        } else if (str.substring(1, 2).equals("9")) {//Start
                            operationsResponseListener.OperationsResponse(16);
                        }
                    } else {
                        if (str.substring(1, 2).equals("5")) {//smart Key on
                            if (str.substring(0, 1).equals("1") || str.substring(0, 1).equals("3")) {//smart key power on
                                operationsResponseListener.OperationsResponse(17);
                            } else if (str.substring(0, 1).equals("2")) {//smart key on power off
                                operationsResponseListener.OperationsResponse(12);
                            }
                        } else if (str.substring(1, 2).equals("6")) {//smart key off
                            operationsResponseListener.OperationsResponse(13);
                        }

                    }
                    characteristicsModels.get(4).setFeedBackValue(str);
                } else if (characteristic.getUuid().equals(characteristicsModels.get(5).getBluetoothGattCharacteristic().getUuid())) {
                    String str = _p_c_(characteristic.getValue());
                    characteristicsModels.get(5).setFeedBackValue(str);
                    operationsResponseListener.OperationsResponse(19);
                } else if (characteristic.getUuid().equals(characteristicsModels.get(6).getBluetoothGattCharacteristic().getUuid())) {
                    String str = toBinary(characteristic.getValue());
                    if (str.substring(7, 8).equals("1")) {//AutoLock on
                        operationsResponseListener.OperationsResponse(21);
                    } else if (str.substring(7, 8).equals("0")) {//AutoLock off
                        operationsResponseListener.OperationsResponse(22);
                    }
                    if (str.substring(6, 7).equals("1")) {//Security on
                        isSecurity = true;
                        operationsResponseListener.OperationsResponse(23);
                    } else if (str.substring(6, 7).equals("0")) {//Security off
                        isSecurity = false;
                        operationsResponseListener.OperationsResponse(24);
                    }
                    if (str.substring(4, 5).equals("1")) {//Car Type Push
                        isSelfStart = false;
                        operationsResponseListener.OperationsResponse(25);
                    } else if (str.substring(4, 5).equals("0")) {//Car Type Self
                        isSelfStart = true;
                        operationsResponseListener.OperationsResponse(26);
                    }
                    if (str.substring(3, 4).equals("1")) {//ProxLU on
                        operationsResponseListener.OperationsResponse(27);
                    } else if (str.substring(3, 4).equals("0")) {//ProxLU off
                        operationsResponseListener.OperationsResponse(28);
                    }
                    if (str.substring(2, 3).equals("1")) {//ProxSS on
                        operationsResponseListener.OperationsResponse(29);
                    } else if (str.substring(2, 3).equals("0")) {//ProxSS off
                        operationsResponseListener.OperationsResponse(30);
                    }
                    if (str.substring(1, 2).equals("1")) {//Single Wire
                        isLockingMode = false;
                        operationsResponseListener.OperationsResponse(31);
                    } else if (str.substring(1, 2).equals("0")) {//Double Wire
                        isLockingMode = true;
                        operationsResponseListener.OperationsResponse(32);
                    }
                    characteristicsModels.get(6).setFeedBackValue(str);
                }

                ReadQueue.remove(ReadQueue.get(ReadQueueIndex));
                if (ReadQueue.size() >= 0) {
                    ReadQueueIndex--;
                    if (ReadQueueIndex == -1) {
                        readStates = false;

//                        if (isEngineKill || isSecurity) {
//                            _p_w_(3, "20");
//                        }
                        successfulConnection = true;
                        if (!isConnectionReqSended) {
                            isConnectionReqSended = true;
                            bleConnectionStateListener.ConnectionResponse(2, gatt.getDevice().getAddress());//Connected
                            reconnect = false;
                        }
                    } else {
                        p_r_2_();
                    }
                }
            }

            if (CheckRK) {
                if (value.equals("[1]")) {
                    _p_w_(1, RK1);
                } else if (value.equals("[11]")) {
                    _p_w_(1, RK2);
                } else if (value.equals("[12]")) {
                    CheckRK = false;
                    isConnectionStatusValid = true;
                    operationsResponseListener.OperationsResponse(1);//RK Valid
                } else if (value.equals("[0]")) {
                    CheckRK = false;
                    isConnectionStatusValid = false;
                    operationsResponseListener.OperationsResponse(0);//RK invalid
                } else {
                    CheckRK = false;
                    isConnectionStatusValid = false;
                    operationsResponseListener.OperationsResponse(-1);//RK Write fail
                }
            }

            if (PushStartNotify) {
                PushStartNotify = false;
                if (characteristic.getUuid().equals(characteristicsModels.get(4).bluetoothGattCharacteristic.getUuid())) {
                    String str = _p_c_(characteristic.getValue());
                    if (str.substring(0, 1).equals("1")) {//Push Start
                        operationsResponseListener.OperationsResponse(17);//Push Power On
                    }
                    if (str.substring(0, 1).equals("2")) {
                        if (WritePushStartOFF) {
                            WritePushStartOFF = false;
                            if (characteristicsModels.get(4).getFeedBackValue().substring(1, 2).equals("5")) {
                                _p_w_(4, "15");
                                operationsResponseListener.OperationsResponse(13);//Acc off
                            }
                        } else
                            operationsResponseListener.OperationsResponse(18);//Push Power off
                    }
                }
            }

            if (WritePushStartON) {
                WritePushStartON = false;
                String str = _p_c_(characteristic.getValue());
                if (str.substring(1, 2).equals("5")) {
                    _p_w_(4, "40");

                    operationsResponseListener.OperationsResponse(12);//ACc on
                }
            }

            if (OperationFeedback) {
                if (characteristic.getUuid().equals(characteristicsModels.get(3).getBluetoothGattCharacteristic().getUuid())) {
                    String str = _p_c_(characteristic.getValue());
                    int x = characteristicsModels.get(3).getSubIndex();
                    if (!isLockUnlockOperation) {
                        if (str.substring(0, 1).equals("3")) {//Engine Kill on
                            isEngineKill = true;
                            operationsResponseListener.OperationsResponse(10);
                        } else if (str.substring(0, 1).equals("4")) {//Engine Kill off
                            isEngineKill = false;
                            operationsResponseListener.OperationsResponse(11);
                        }
                    }

                    if (isLockUnlockOperation) {
                        if (x == 1 && str.substring(1, 2).equals("3")) {//Locked
                            operationsResponseListener.OperationsResponse(8);
                        } else if (x == 2 && str.substring(1, 2).equals("4")) {//Unlocked
                            operationsResponseListener.OperationsResponse(9);
                        }
                        isLockUnlockOperation = false;
                    }
                    OperationFeedback = false;
                    characteristicsModels.get(3).setOperation(false);
                    characteristicsModels.get(3).setFeedBackValue(str);
                } else if (characteristic.getUuid().equals(characteristicsModels.get(5).getBluetoothGattCharacteristic().getUuid())) {
                    String str = _p_c_(characteristic.getValue());
                    OperationFeedback = false;
                    characteristicsModels.get(5).setOperation(false);
                    characteristicsModels.get(5).setFeedBackValue(str);
                    operationsResponseListener.OperationsResponse(19);
                } else if (characteristic.getUuid().equals(characteristicsModels.get(6).getBluetoothGattCharacteristic().getUuid())) {
                    String str = toBinary(characteristic.getValue());
                    int x = characteristicsModels.get(6).getSubIndex();
                    if (x == 65 && str.substring(7, 8).equals("1")) {//AutoLock on
                        operationsResponseListener.OperationsResponse(21);
                    } else if (x == 75 && str.substring(7, 8).equals("0")) {//AutoLock off
                        operationsResponseListener.OperationsResponse(22);
                    } else if (x == 66 && str.substring(6, 7).equals("1")) {//Security on
                        operationsResponseListener.OperationsResponse(23);
                    } else if (x == 76 && str.substring(6, 7).equals("0")) {//Security off
                        operationsResponseListener.OperationsResponse(24);
                    } else if (x == 68 && str.substring(4, 5).equals("1")) {//Car Type Push
                        isSelfStart = false;
                        operationsResponseListener.OperationsResponse(25);
                    } else if (x == 78 && str.substring(4, 5).equals("0")) {//Car Type Self
                        isSelfStart = true;
                        operationsResponseListener.OperationsResponse(26);
                    } else if (x == 69 && str.substring(3, 4).equals("1")) {//ProxLU on
                        operationsResponseListener.OperationsResponse(27);
                    } else if (x == 79 && str.substring(3, 4).equals("0")) {//ProxLU off
                        operationsResponseListener.OperationsResponse(28);
                    } else if (x == 70 && str.substring(2, 3).equals("1")) {//ProxSS on
                        operationsResponseListener.OperationsResponse(29);
                    } else if (x == 80 && str.substring(2, 3).equals("0")) {//ProxSS off
                        operationsResponseListener.OperationsResponse(30);
                    } else if (x == 72 && str.substring(1, 2).equals("1")) {//Single Wire
                        isLockingMode = false;
                        operationsResponseListener.OperationsResponse(31);
                    } else if (x == 82 && str.substring(1, 2).equals("1")) {//Single Wire
                        isLockingMode = false;
                        operationsResponseListener.OperationsResponse(31);
                    } else if (x == 81 && str.substring(1, 2).equals("0")) {//Double Wire
                        isLockingMode = true;
                        operationsResponseListener.OperationsResponse(32);
                    }
                    OperationFeedback = false;
                    characteristicsModels.get(6).setOperation(false);
                    characteristicsModels.get(6).setFeedBackValue(str);
                } else if (characteristic.getUuid().equals(characteristicsModels.get(4).getBluetoothGattCharacteristic().getUuid())) {
                    String str = _p_c_(characteristic.getValue());
                    int x = characteristicsModels.get(4).getSubIndex();
                    if (x == 10 && str.substring(1, 2).equals("5")) {//Acc on
                        if (!isSelfStart) {
                            if (str.substring(1, 2).equals("5")) {//smart Key on
                                operationsResponseListener.OperationsResponse(12);
                            } else if (str.substring(1, 2).equals("6")) {//smart key off
                                operationsResponseListener.OperationsResponse(13);
                            }
                        } else {
                            operationsResponseListener.OperationsResponse(12);
                        }
                    } else if (x == 15 && str.substring(1, 2).equals("6")) {//Acc off
                        operationsResponseListener.OperationsResponse(13);
                    } else if (x == 20 && str.substring(1, 2).equals("7")) {//Power on
                        operationsResponseListener.OperationsResponse(14);
                    } else if (x == 25 && str.substring(1, 2).equals("8")) {//Power off
                        operationsResponseListener.OperationsResponse(15);
                    } else if (x == 30 && str.substring(1, 2).equals("9")) {//Start
                        operationsResponseListener.OperationsResponse(16);
                    }
                    OperationFeedback = false;
                    characteristicsModels.get(4).setOperation(false);
                    characteristicsModels.get(4).setFeedBackValue(str);
                }
            }

            if (MultipleWrite) {
                WriteQueue.remove(WriteQueue.get(WriteQueueIndex));
                if (WriteQueue.size() >= 0) {
                    WriteQueueIndex--;
                    if (WriteQueueIndex == -1) {
                        MultipleWrite = false;
                        WriteQueue.clear();
                        WriteQueueIndex = -1;
                    } else {
                        m_w();
                    }
                }
            }

            if (ChangeRK) {
                if (value.equals("[15]")) {
                    _p_w_(1, newRK2);
                } else if (value.equals("[16]")) {
                    ChangeRK = false;
                    RK1 = newRK1;
                    RK2 = newRK2;
                    operationsResponseListener.OperationsResponse(4);
                } else {
                    ChangeRK = false;
                    operationsResponseListener.OperationsResponse(5);
                }
            }

            if (ChangeMK) {
                if (value.equals("[17]")) {
                    _p_w_(0, newMK2);
                } else if (value.equals("[18]")) {
                    ChangeMK = false;
                    operationsResponseListener.OperationsResponse(6);
                } else {
                    ChangeMK = false;
                    operationsResponseListener.OperationsResponse(7);
                }
            }

            if (CheckMK) {
                if (value.equals("[3]") || value.equals("[2]") || value.equals("[4]")) {
                    KeyChange = value.substring(1, 2);
                    _p_w_(0, MK1);
                } else if (value.equals("[13]")) {
                    _p_w_(0, MK2);
                } else if (value.equals("[14]")) {
                    CheckMK = false;
                    if (KeyChange.equals("2") || KeyChange.equals("3") || KeyChange.equals("4")) {
                        //for regular key change //for master key change // for owner connection
                        isValidRk = true;
                        operationsResponseListener.OperationsResponse(3);
                    }
                } else if (value.equals("[0]")) {
                    isValidRk = false;
                    CheckMK = false;
                    operationsResponseListener.OperationsResponse(2);
                } else {
                    CheckRK = false;
                    isConnectionStatusValid = false;
                    operationsResponseListener.OperationsResponse(-2);//MK Write fail
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (CheckRK || ChangeRK || CheckMK || ChangeMK) {
                _p_r_(2);
            } else if (characteristic.getUuid().equals(characteristicsModels.get(3).getBluetoothGattCharacteristic().getUuid())) {
                OperationFeedback = true;
                _p_r_(3);
            } else if (characteristic.getUuid().equals(characteristicsModels.get(4).getBluetoothGattCharacteristic().getUuid())) {
                OperationFeedback = true;
                _p_r_(4);
            } else if (characteristic.getUuid().equals(characteristicsModels.get(5).getBluetoothGattCharacteristic().getUuid())) {
                OperationFeedback = true;
                _p_r_(5);
            } else if (characteristic.getUuid().equals(characteristicsModels.get(6).getBluetoothGattCharacteristic().getUuid())) {
                OperationFeedback = true;
                _p_r_(6);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (ReadRssi) {
                if (outOfRange[0] == 0) {
                    outOfRange[outOfRangeCounter++] = rssi;
                }

                if (rssi < -95) {
                    outOfRange[outOfRangeCounter++] = rssi;

                    if (outOfRangeCounter == 100) {
//                        ReadRssi = false;
//                        scheduleTask4Rsii.shutdown();
//                        closeConnection = true;
//                        gatt.disconnect();
                    }
                } else {
                    outOfRangeCounter = 0;
                }
                rssiResponseListner.RssiResponse(rssi);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (descriptor.getCharacteristic().getUuid().equals(characteristicsModels.get(7).bluetoothGattCharacteristic.getUuid())) {
                isDisconnected = false;
                bleConnectionStateListener.ConnectionResponse(1, gatt.getDevice().getAddress());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            String value = Arrays.toString(characteristic.getValue());
            if (value.equals("[2]")) {
                if (isConnectionStatusValid) {
                    CheckRK = true;
                    _p_w_(2, "1");
                } else {
                    reconnect = false;
                }
            } else if (value.equals("[3]") && !isSelfStart) {
                PushStartNotify = true;
                bluetoothGatt.readCharacteristic(characteristicsModels.get(4).bluetoothGattCharacteristic);
            } else if (value.equals("[4]")) {
                characteristicsModels.get(4).setOperation(false);
//                characteristicsModels.get(4).setOperation(false);
                operationsResponseListener.OperationsResponse(15);
            }
        }
    };

    private void _p_w_(int _1_, String _2_) {
        if (!characteristicsModels.isEmpty() && bluetoothGatt != null) {
            characteristicsModels.get(_1_).setOperation(true);
            characteristicsModels.get(_1_).setSubIndex(Integer.parseInt(_2_));
            int val = Integer.parseInt(_2_);
            byte[] byte_value = new byte[1];
            byte_value[0] = (byte) (val & 0xFF);
            characteristicsModels.get(_1_).getBluetoothGattCharacteristic().setValue(byte_value);
            bluetoothGatt.writeCharacteristic(characteristicsModels.get(_1_).getBluetoothGattCharacteristic());
        }
    }

    private void _p_r_(int _1_) {
        if (!isDisconnected)
            bluetoothGatt.readCharacteristic(characteristicsModels.get(_1_).getBluetoothGattCharacteristic());
    }

    private String _p_c_(byte[] _1_) {
        StringBuilder sb = new StringBuilder(_1_.length * 2);
        for (byte b : _1_)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private void p_r_2_() {
        readStates = true;
        bluetoothGatt.readCharacteristic(ReadQueue.get(ReadQueueIndex));
    }

    private void p_m_(String _1_) {
        CheckMK = true;
        _p_w_(2, _1_);
    }

    private boolean _d_(String _1_) {
        if (_1_ != null && _1_.length() == 4 && _1_.matches("[0-9]+")) {
            RK1 = _1_.substring(0, 2);
            RK2 = _1_.substring(2, 4);
            CheckRK = true;
            _p_w_(2, "1");
        }
        return false;
    }

    private void _e_() {
        if(characteristicsModels != null && !characteristicsModels.isEmpty() && ReadQueue != null) {
            ReadQueue.add(characteristicsModels.get(5).bluetoothGattCharacteristic);
            ReadQueue.add(characteristicsModels.get(4).bluetoothGattCharacteristic);
            ReadQueue.add(characteristicsModels.get(3).bluetoothGattCharacteristic);
            ReadQueue.add(characteristicsModels.get(6).bluetoothGattCharacteristic);
            ReadQueue.add(devicInfoCharacteristicsList.get(2).bluetoothGattCharacteristic);
            ReadQueue.add(devicInfoCharacteristicsList.get(3).bluetoothGattCharacteristic);
            ReadQueueIndex = 5;
            p_r_2_();
        }
    }

    public void setNotificationCharacter() {
        bluetoothGatt.setCharacteristicNotification(characteristicsModels.get(7).bluetoothGattCharacteristic, true);
        BluetoothGattDescriptor descriptor = characteristicsModels.get(7).getBluetoothGattCharacteristic().getDescriptors().get(0);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
    }

    private void _h_() {
        _p_w_(3, "1");
    }

    private void _i_() {
        _p_w_(3, "2");
    }

    private void m_w() {
        _p_w_(WriteQueue.get(WriteQueueIndex).getIndex(), WriteQueue.get(WriteQueueIndex).getValue());
    }

    private void m_w_q(int proxLU, int proxSS, int secureLU, int secureSS) {
        WriteQueue.clear();
        WriteQueueIndex = -1;
        if (proxLU == 1) {
            WriteQueue.add(new WriteModel(6, "69"));
            WriteQueueIndex++;
            if (characteristicsModels.get(6).getFeedBackValue().substring(7, 8).equals("0")) {
                WriteQueue.add(new WriteModel(6, "65"));
                WriteQueueIndex++;
            }
        } else if (proxLU == 0) {
            WriteQueue.add(new WriteModel(6, "79"));
            WriteQueueIndex++;
        }

        if (proxSS == 1) {
            WriteQueue.add(new WriteModel(6, "70"));
            WriteQueueIndex++;
            if (characteristicsModels.get(6).getFeedBackValue().substring(6, 7).equals("0")) {
                WriteQueue.add(new WriteModel(6, "66"));
                WriteQueueIndex++;
            }
        } else if (proxSS == 0) {
            WriteQueue.add(new WriteModel(6, "80"));
            WriteQueueIndex++;
        }

        if (secureLU == 1) {
            WriteQueue.add(new WriteModel(6, "65"));
            WriteQueueIndex++;
        } else if (secureLU == 0) {
            WriteQueue.add(new WriteModel(6, "75"));
            WriteQueueIndex++;
        }

        if (secureSS == 1) {
            WriteQueue.add(new WriteModel(6, "66"));
            WriteQueueIndex++;
        } else if (secureSS == 0) {
            WriteQueue.add(new WriteModel(6, "76"));
            WriteQueueIndex++;
        }
        MultipleWrite = true;
        m_w();
    }

    private String toBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }

    private void AccPowerStartOper(int index) {
        if (isSelfStart) {
            if (index == 1) {//Turn Acc on
                _p_w_(4, "10");
            } else if (index == 2) {//Turn Acc off
                _p_w_(4, "15");
            } else if (index == 3) {//turn Power on
                _p_w_(4, "20");
            } else if (index == 4) {//turn Power off
                _p_w_(4, "25");
            } else if (index == 5) {//turn Start on
                _p_w_(4, "30");
            } else if (index == 6) {//turn proximity Stop for self
                _p_w_(4, "35");
            } else if (index == 7) {//turn Proximity Stop Disable for self
                _p_w_(4, "36");
            }
        } else {
            if (index == 1) {//Turn Acc on
                _p_w_(4, "10");
            } else if (index == 2) {//Turn Acc off
                _p_w_(4, "15");
            } else if (index == 3) {
                if (characteristicsModels.get(4).getFeedBackValue().substring(1, 2).equals("5")) {
                    _p_w_(4, "40");
                } else {
                    WritePushStartON = true;
                    _p_w_(4, "10");
                }
            } else if (index == 4) {
                _p_w_(4, "50");
            } else if (index == 8) {//turn Proximity Stop for push
                _p_w_(4, "51");
            } else if (index == 9) {//turn Proximity Stop Disable for push
                _p_w_(4, "52");
            }
        }
    }

    private String ProvideTimer(int index) {
        if (index == 1) {//Security timer
            String str = characteristicsModels.get(5).getFeedBackValue().substring(0, 1);
            if (str.equals("a"))
                str = "10";
            return (Integer.parseInt(str) * 10) + "";
        } else if (index == 2) {//Self Start timmer
            String str = characteristicsModels.get(5).getFeedBackValue().substring(1, 2);
            if (str.equals("a"))
                str = "10";
            return progressConvertertoFloat(str) + "";
        }
        return "";
    }

    private void FunctionalityOper(int index) {
        switch (index) {
            case 0://EngineDead on
                _p_w_(3, "10");
                break;
            case 1://EngineDed off
                _p_w_(3, "20");
                break;
            case 2://AutoLock On
                _p_w_(6, "65");
                break;
            case 3://AutoLock off
                _p_w_(6, "75");
                break;
            case 4://Security on
                _p_w_(6, "66");
                break;
            case 5://Security off
                _p_w_(6, "76");
                break;
            case 6://Car Type Push
                _p_w_(6, "68");
                break;
            case 7://Car Type Self
                _p_w_(6, "78");
                break;
            case 8://Self Start Timer
                _p_w_(5, timer_1 + "");
                break;
            case 9://EK Timer
                _p_w_(5, (timer_2 + 9) + "");
                break;
            case 10://Proximity L/U On
                _p_w_(6, "69");
                break;
            case 11://Proximity L/U Off
                _p_w_(6, "79");
                break;
            case 12://Proximity S/S On
                _p_w_(6, "70");
                break;
            case 13://Proximity S/S Off
                _p_w_(6, "80");
                break;
            case 14://Trunk
                _p_w_(3, "5");
                break;
            case 15://Locking Mode Single Wire unlocking feedback
                _p_w_(6, "72");
                break;
            case 16://Locking Mode Single Wire Lock feedback
                _p_w_(6, "82");
                break;
            case 17://locking Mode Double Wire
                _p_w_(6, "81");
                break;
        }
    }

    private float progressConvertertoFloat(String value) {
        switch (value) {
            case "1":
                return (float) 0.2;
            case "2":
                return (float) 0.5;
            case "3":
                return (float) 0.7;
            case "4":
                return (float) 1.0;
            case "5":
                return (float) 1.2;
            case "6":
                return (float) 1.5;
            case "7":
                return (float) 1.7;
            case "8":
                return (float) 2.0;
            case "9":
                return (float) 2.2;
            case "10":
                return (float) 2.5;
        }
        return (float) 0.7;
    }

    private boolean _f_(String _1_) {
        if (isValidRk && _1_ != null && _1_.length() == 4 && _1_.matches("[0-9]+")) {
            newRK1 = _1_.substring(0, 2);
            newRK2 = _1_.substring(2, 4);
            ChangeRK = true;
            _p_w_(1, newRK1);
            return true;
        }
        return false;
    }

    private boolean _g_(String _1_) {
        if (isValidRk && _1_ != null && _1_.length() == 4 && _1_.matches("[0-9]+")) {
            newMK1 = _1_.substring(0, 2);
            newMK2 = _1_.substring(2, 4);
            ChangeMK = true;
            _p_w_(0, newMK1);
            return true;
        }
        return false;
    }

    private class CharacteristicsModel {
        boolean operation;
        String FeedBackValue;
        int subIndex;
        BluetoothGattCharacteristic bluetoothGattCharacteristic;

        private CharacteristicsModel(boolean operation, BluetoothGattCharacteristic bluetoothGattCharacteristic, String FeedBackValue, int subIndex) {
            this.subIndex = subIndex;
            this.operation = operation;
            this.FeedBackValue = FeedBackValue;
            this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
        }

        private int getSubIndex() {
            return subIndex;
        }

        private void setSubIndex(int subIndex) {
            this.subIndex = subIndex;
        }

        private boolean getOperation() {
            return operation;
        }

        private void setOperation(boolean operation) {
            this.operation = operation;
        }

        private BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
            return bluetoothGattCharacteristic;
        }

        private void setBluetoothGattCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
        }

        private String getFeedBackValue() {
            return FeedBackValue;
        }

        private void setFeedBackValue(String FeedBackValue) {
            this.FeedBackValue = FeedBackValue;
        }
    }

    private void TurnAppModeOn(String values) {
        String temp = values;
        if (!temp.equals("null")) {
            WriteQueue.clear();
            WriteQueueIndex = -1;
            if (temp.substring(7, 8).equals("1")) {//AutoLock on
                WriteQueue.add(new WriteModel(6, "65"));//turn on
                WriteQueueIndex++;
            }
            if (temp.substring(6, 7).equals("1")) {//Security on
                WriteQueue.add(new WriteModel(6, "66"));//turn on
                WriteQueueIndex++;
            }
            if (temp.substring(3, 4).equals("1")) {//ProxLU on
                WriteQueue.add(new WriteModel(6, "69"));//turn on
                WriteQueueIndex++;
            }
            if (temp.substring(2, 3).equals("1")) {//ProxSS on
                WriteQueue.add(new WriteModel(6, "70"));//turn on
                WriteQueueIndex++;
            }
            if (temp.substring(5, 6).equals("1")) {//PushStart Car
                WriteQueue.add(new WriteModel(6, "77"));//Push Retain on
                WriteQueueIndex++;
            }
            if (WriteQueueIndex > -1) {
                MultipleWrite = true;
                m_w();
            } else {

            }
        }
    }

    private void TurnRemoteModeOn(String value) {
        String temp = value;
        if (!temp.equals("null")) {
            WriteQueue.clear();
            WriteQueueIndex = -1;
            if (temp.substring(7, 8).equals("1")) {//AutoLock on
                WriteQueue.add(new WriteModel(6, "75"));//turn off
                WriteQueueIndex++;
            }
            if (temp.substring(6, 7).equals("1")) {//Security on
                WriteQueue.add(new WriteModel(6, "76"));//turn off
                WriteQueueIndex++;
            }
            if (temp.substring(3, 4).equals("1")) {//ProxLU on
                WriteQueue.add(new WriteModel(6, "79"));//turn off
                WriteQueueIndex++;
            }
            if (temp.substring(2, 3).equals("1")) {//ProxSS on
                WriteQueue.add(new WriteModel(6, "80"));//turn off
                WriteQueueIndex++;
            }
            if (temp.substring(4, 5).equals("1")) {//PushStart Car
                WriteQueue.add(new WriteModel(6, "67"));//Push Retain on
                WriteQueueIndex++;
                if (characteristicsModels.get(4).getFeedBackValue().substring(1, 2).equals("6") ||
                        characteristicsModels.get(4).getFeedBackValue().substring(1, 2).equals("8")) {//Acc off
                    WriteQueue.add(new WriteModel(4, "10"));//turn Acc on
                    WriteQueueIndex++;
                }
            } else {
                if (characteristicsModels.get(4).getFeedBackValue().substring(1, 2).equals("9")
                        || characteristicsModels.get(4).getFeedBackValue().substring(1, 2).equals("7")) {//Start
                    WriteQueue.add(new WriteModel(4, "25"));//Stop
                    WriteQueueIndex++;
                }
            }
            temp = characteristicsModels.get(3).getFeedBackValue();
            if (temp.substring(1, 2).equals("3")) {//Locked
                WriteQueue.add(new WriteModel(3, "2"));//unlock it
                WriteQueueIndex++;
            }
            if (temp.substring(0, 1).equals("3")) {//Engine Kill on
                WriteQueue.add(new WriteModel(3, "20"));//turn EK off
                WriteQueueIndex++;
            }
            if (WriteQueueIndex > -1) {
                MultipleWrite = true;
                m_w();
            } else {

            }
        }
    }

    ///PUBLIC METHODS////
    public MKDevice(Context context) {
        this.context = context;
        isDisconnected = false;
        if (!isDisconnected) {
            init();
        }
    }

    public boolean ConnectWithMK(BluetoothDevice device, boolean autoconnect, String devicename) {

        return _c_(device, autoconnect, devicename);
    }

    public void MKStatus() {
        _e_();
        return;
    }

    public boolean CheckRegularKey(String regularKey) {
        return _d_(regularKey);
    }

    public boolean ChangeRegularKey(String newRegularKey) {
        return _f_(newRegularKey);
    }

    public boolean ChangeMasterKey(String newMasterKey) {
        return _g_(newMasterKey);
    }

    public void MKLock() {
        isLockUnlockOperation = true;
        _h_();
    }

    public void MKUnlock() {
        isLockUnlockOperation = true;
        _i_();
    }

    public void MKAccOn() {
        AccPowerStartOper(1);
    }

    public void MKAccOff() {
        AccPowerStartOper(2);
    }

    public void MKPowerOn() {
        AccPowerStartOper(3);
    }

    public void MKStart() {
        AccPowerStartOper(5);
    }

    public void MKStop() {
        AccPowerStartOper(4);
    }

    public void MKProxStop() {
        if (isSelfStart)
            AccPowerStartOper(6);
        else
            AccPowerStartOper(8);
    }

    public void MKProxStopDisable() {
        if (isSelfStart)
            AccPowerStartOper(7);
        else
            AccPowerStartOper(9);
    }

    public void MKPushPowerOn() {
        AccPowerStartOper(3);
    }

    public void MKPushPowerOff() {
        AccPowerStartOper(4);
    }

    public void MKEngineDeadOn() {
        FunctionalityOper(0);
    }

    public void MKEngineDeadOff() {
        FunctionalityOper(1);
    }

    public void MKAutoLockOn() {
        FunctionalityOper(2);
    }

    public void MKAutoLockOff() {
        FunctionalityOper(3);
    }

    public void MKAutoEngineDeadOn() {
        FunctionalityOper(4);
    }

    public void MKAutoEngineDeadOff() {
        FunctionalityOper(5);
    }

    public void MKCarTypePush() {
        FunctionalityOper(6);
    }

    public void MKCarTypeSelf() {
        FunctionalityOper(7);
    }

    public void MKLockingMode(int i) {
        if (i == 4) {//Single Wire Unlock Feedback
            FunctionalityOper(15);
        } else if (i == 3) {//Single Wire Lock Feedback
            FunctionalityOper(16);
        } else if (i == 2) {//Double Wire
            FunctionalityOper(17);
        }
    }

    public  void  MKPair(){
        pairingCharac = true;
        bluetoothGatt.readCharacteristic(characteristicsModels.get(2).bluetoothGattCharacteristic);
    }

    public  void MKAuthenticate(){
         setNotificationCharacter();
    }

    public void MKWriteProxPushPowerOff() {
        WritePushStartOFF = true;
        MKPushPowerOff();

    }

    public void MKSelfStartTimer(int timer) {
        if (timer < 11 || timer > 0) {
            timer_1 = timer;
            FunctionalityOper(8);
        }
    }

    public void MKEngineKillTimer(int timer) {
        if (timer < 11 || timer > 0) {
            timer_2 = timer;
            FunctionalityOper(9);
        }
    }

    public void MKCheckMasterKeyForKeyChange(String masterKey, String keyChangeCode) {
        MK1 = masterKey.substring(0, 2);
        MK2 = masterKey.substring(2, 4);
        if (keyChangeCode.equals("1")) {//Regular
            p_m_("2");
        } else if (keyChangeCode.equals("2")) {
            p_m_("3");
        }
    }

    public void MKCheckMasterKeyForConnection(String masterKey) {
        MK1 = masterKey.substring(0, 2);
        MK2 = masterKey.substring(2, 4);
        p_m_("4");
    }

    public void MKTrunk() {
        FunctionalityOper(14);
    }

    public void MKProximityLUOn() {
        FunctionalityOper(10);
    }

    public void MKProximityLUOff() {
        FunctionalityOper(11);
    }

    public void MKProximitySSOn() {
        FunctionalityOper(12);
    }

    public void MKProximitySSOff() {
        FunctionalityOper(13);
    }

    public void MKProximitySecurity(int a, int b, int c, int d) {
        m_w_q(a, b, c, d);
    }

    public boolean getIsEngineKill() {
        return isEngineKill;
    }

    public void MKGetLock() {
//        isLockUnlockOperation = true;
//        OperationFeedback = true;
//        _p_r_(3);
        isLockUnlockOperation = true;
        _p_w_(3, "1");
    }

    public void MKGetUnlock() {
//        isLockUnlockOperation = true;
//        OperationFeedback = true;
//        _p_r_(3);

        isLockUnlockOperation = true;
        _p_w_(3, "2");
    }

    public boolean MKGetAutoLock() {
        if (characteristicsModels.get(6).getFeedBackValue().substring(7, 8).equals("1")) {
            return true;
        }
        return false;
    }

    public boolean MKGetSecurity() {
        if (characteristicsModels.get(6).getFeedBackValue().substring(6, 7).equals("1")) {
            return true;
        }
        return false;
    }

    public boolean MKGetProximityLU() {
        if (characteristicsModels.get(6).getFeedBackValue().substring(3, 4).equals("1")) {
            return true;
        }
        return false;
    }

    public boolean MKGetProximitySS() {
        if (characteristicsModels.get(6).getFeedBackValue().substring(2, 3).equals("1")) {
            return true;
        }
        return false;
    }

    public boolean isLockingMode() {
        return isLockingMode;
    }

    public String getFirmware() {
        return firmware;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getDeviceModel() {
        String str[] = serialNumber.split("-");
        if (str.length > 0) {
            return str[2];
        }
        return serialNumber;
    }

    public void MKDisconnect() {
        if (!isDisconnected) {
            //this.reconnect = false;
            bluetoothGatt.disconnect();


        }
    }

    public void MKDisconnectAndClose() {
        if (!isDisconnected) {
           // this.reconnect = reconnect;
            bluetoothGatt.disconnect();
            bleConnectionStateListener.ConnectionResponse(0, bluetoothGatt.getDevice().getAddress());
            bluetoothGatt.close();
            bluetoothGatt=null;


        }
    }

    public boolean isSelfStart(){
        return isSelfStart;
    }

    public void setConnectionStatusValid(boolean status) {
        if (!status) {
            closeConnection = true;
            reconnect = false;
            this.isConnectionStatusValid = status;
        } else {
            closeConnection = false;
            reconnect = true;
            this.isConnectionStatusValid = status;
        }
    }

    public String MKGetTimer(int index) {
        if (index == 1) {
            return ProvideTimer(index);
        } else if (index == 2) {
            return ProvideTimer(index);
        }
        return "";
    }

    public boolean MKGetCarType() {
        if (characteristicsModels != null)
            if (characteristicsModels.get(6).FeedBackValue.substring(4, 5).equals("0"))
                return true;
        return false;
    }

    public void initListner(Context context, ConnectionResponseListener connectionResponseListener, RssiResponseListner rssiResponseListner, OperationsResponseListener operationsResponseListener) {
        this.context = context;
        this.bleConnectionStateListener = connectionResponseListener;
        this.rssiResponseListner = rssiResponseListner;
        this.operationsResponseListener = operationsResponseListener;
    }

    public void DestroyListenersObj() {
        bleConnectionStateListener = null;
        operationsResponseListener = null;
        rssiResponseListner = null;
    }

    public String getMKSettings() {
        return characteristicsModels.get(6).getFeedBackValue();
    }

    public void MKSwitchMode(int mode, String values) {
        if (mode == 1) {
            TurnAppModeOn(values);
        } else if (mode == 0) {
            TurnRemoteModeOn(values);
        }
    }

    public void RegisterRssi() {
        ReadRssi = true;
        scheduleTask4Rsii = Executors.newScheduledThreadPool(1);
        scheduleTask4Rsii.scheduleAtFixedRate(new Runnable() {
            public void run() {
                bluetoothGatt.readRemoteRssi();
            }
        }, 0, 3, TimeUnit.MILLISECONDS);
    }

    private BluetoothDevice ProvideConnectedDevice() {
        return bluetoothGatt.getDevice();
    }

    private class WriteModel {
        int index;
        String value;

        public WriteModel(int index, String value) {
            this.index = index;
            this.value = value;
        }

        public int getIndex() {
            return index;
        }

        public String getValue() {
            return value;
        }

    }

}

