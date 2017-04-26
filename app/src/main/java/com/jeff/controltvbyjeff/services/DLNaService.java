package com.jeff.controltvbyjeff.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.ToastControl;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.sessions.LaunchSession;
import com.jeff.controltvbyjeff.R;
import com.tapadoo.alerter.Alerter;

import java.util.List;

/**
 * Created by Jeff on 25/04/2017.
 */

public class DLNaService {

    static ConnectableDevice mTV;
    static DevicePicker dp;
    static AlertDialog pairingAlertDialog;
     AlertDialog dialog;

    private VolumeControl volumeControl;
    private ToastControl toastControl;
    static AlertDialog pairingCodeDialog;
    private Launcher launcher;
    boolean isAlreadyConnected = false;
    private static Activity activity;
    private final static String TAG = DLNaService.class.getSimpleName();

    private ConnectionObserver observer;

    private Launcher getLauncher() {
        return launcher;
    }

    private void initDiscoverManager() {
        DiscoveryManager.getInstance().registerDefaultDeviceTypes();
        DiscoveryManager.getInstance().setPairingLevel(DiscoveryManager.PairingLevel.ON);
        DiscoveryManager.getInstance().start();
    }

    private void createToastForConnexionEvent(){
        if (isAlreadyConnected){
            Alerter.create(activity)
                    .setTitle(activity.getString(R.string.connexion_status))
                    .setText("Connexion ok ...")
                    .setBackgroundColor(R.color.colorPrimary)
                    .show();
        }else {
            Alerter.create(activity)
                    .setTitle(activity.getString(R.string.connexion_status))
                    .setText("Connexion lost...")
                    .setBackgroundColor(R.color.colorDanger)
                    .show();
        }

    }
    private void setupPicker() {
        dp = new DevicePicker(activity);
        dialog = dp.getPickerDialog("Device List", new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                mTV = (ConnectableDevice) arg0.getItemAtPosition(arg2);
                mTV.addListener(deviceListener);
                mTV.setPairingType(null);
                mTV.connect();

                dp.pickDevice(mTV);
            }
        });


        pairingAlertDialog = new AlertDialog.Builder(activity)
                .setTitle("Pairing with TV")
                .setMessage("Please confirm the connection on your TV")
                .setPositiveButton("Okay", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dp.cancelPicker();
                    }
                })
                .create();

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        pairingCodeDialog = new AlertDialog.Builder(activity)
                .setTitle("Enter Pairing Code on TV")
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (mTV != null) {
                            String value = input.getText().toString().trim();
                            mTV.sendPairingKey(value);
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dp.cancelPicker();
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }
                })
                .create();
    }

    private ConnectableDeviceListener deviceListener = new ConnectableDeviceListener() {

        @Override
        public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {
            switch (pairingType) {
                case FIRST_SCREEN:
                    Log.d(TAG, "First Screen");
                    pairingAlertDialog.show();
                    break;

                case PIN_CODE:
                case MIXED:
                    Log.d(TAG, "Pin Code");
                    pairingCodeDialog.show();
                    break;

                case NONE:
                default:
                    break;
            }
        }

        void connectFailed(ConnectableDevice device) {
            if (device != null)
                Log.d(TAG, "Failed to connect to " + device.getIpAddress());

            if (mTV != null) {
                mTV.removeListener(deviceListener);
                mTV.disconnect();
                observer.onDisconnection();
                createToastForConnexionEvent();
                mTV = null;
            }
        }

        void connectEnded(ConnectableDevice device) {
            if (pairingAlertDialog.isShowing()) {
                pairingAlertDialog.dismiss();
            }
            if (pairingCodeDialog.isShowing()) {
                pairingCodeDialog.dismiss();
            }
            mTV.removeListener(deviceListener);
            mTV = null;
            observer.onDisconnection();
            createToastForConnexionEvent();
        }


        @Override
        public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
            Log.d(TAG, "onConnectFailed " + error.toString());
            connectFailed(mTV);
            isAlreadyConnected = false;
            createToastForConnexionEvent();
        }

        @Override
        public void onDeviceReady(ConnectableDevice device) {
            Log.d(TAG, "onPairingSuccess");
            if (pairingAlertDialog.isShowing()) {
                pairingAlertDialog.dismiss();
            }
            if (pairingCodeDialog.isShowing()) {
                pairingCodeDialog.dismiss();
            }
            initLauncher();
            observer.onConnection();

        }

        private void initLauncher() {
            isAlreadyConnected = true;
            createToastForConnexionEvent();
            launcher = mTV.getCapability(Launcher.class);
            toastControl = mTV.getCapability(ToastControl.class);
            volumeControl = mTV.getCapability(VolumeControl.class);
        }

        @Override
        public void onDeviceDisconnected(ConnectableDevice device) {
            Log.d(TAG, "Device Disconnected");
            createToastForConnexionEvent();
            connectEnded(mTV);
           observer.onDisconnection();

        }

        @Override
        public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {

        }
    };


    /////////////////////////////////////////////////////// public methodes /////////////////////////::::

    public void launchBrowser(String url) {
        getLauncher().launchBrowser(url, new Launcher.AppLaunchListener() {

            public void onSuccess(LaunchSession session) {
            }

            public void onError(ServiceCommandError error) {
            }
        });
    }

    public DLNaService(Activity activity, ConnectionObserver observable) {
        this.activity = activity;
        this.observer = observable;

    }

    public void initService() {
        setupPicker();
        initDiscoverManager();
        initDiscoverManager();
    }

    public ToastControl getToastControl() {
        return toastControl;
    }


    public VolumeControl getVolumeControl() {
        return volumeControl;
    }

    public boolean isAlreadyConnected() {
        return isAlreadyConnected;
    }

    public void createDialog(){
        dialog.show();
    }

}
