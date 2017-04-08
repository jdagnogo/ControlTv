package com.jeff.controltvbyjeff;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jeff.controltvbyjeff.customlibs.CircularSeekBar;
import com.jeff.controltvbyjeff.services.NFCService;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static android.view.MenuItem.SHOW_AS_ACTION_NEVER;

public class MainActivity extends AppCompatActivity {

    ConnectableDevice mTV;
    private final static String TAG = MainActivity.class.getSimpleName();
    DevicePicker dp;
    private VolumeControl volumeControl;
    AlertDialog pairingAlertDialog;
    AlertDialog dialog;
    private ToastControl toastControl;
    AlertDialog pairingCodeDialog;
    private Launcher launcher;
    boolean isStartedFromNfc = false;
    private CircularSeekBar seekbar;
    @Bind(R.id.youtubeWithBrowser)
    Button youtubeWithBrowser;
    private Toolbar myToolbar;

    @Bind(R.id.edit_toast)
    EditText editToast;

    @Bind(R.id.send_toast)
    ImageButton sendToast;

    private TextView volumeStatus;

    private NFCService nfcService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setupPicker();
        nfcService = new NFCService();
        initDiscoverManager();
        initView();

    }
    private Toolbar getMyToolbar(){
        return myToolbar;
    }

    private void initView() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        volumeStatus = (TextView) findViewById(R.id.volume_status);
        //handle when it is not connected with TV
        seekbar = (CircularSeekBar) findViewById(R.id.circularSeekBar1);
        seekbar.getProgress();
        seekbar.setProgress(50);
        seekbar.setOnSeekBarChangeListener(new CircleSeekBarListener());


    }

    @OnClick(R.id.youtubeWithBrowser)
    public void setYoutubeWithBrowserOnClick() {
        launchBrowser("https://www.youtube.com/watch?v=26WBT1ZdLdc&list=PLLK3b9Lk333IbQWdda9r7JNLCS0uuwUlt&index=2");
    }

    @OnClick(R.id.send_toast)
    public void setSendToast() {
        getToastControl().showToast(editToast.getText().toString(), null, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        isStartedFromNfc = true;
        final String tagId = removeSpace(nfcService.getTagID(tag));
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            String content = nfcService.read(intent);
            Toast.makeText(ControlTvApplication.getAppContext(), content, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_connexion:
                dialog.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private String removeSpace(String string) {
        String str = string.replaceAll("\\s+", "");
        Log.e(TAG, "without space : " + str);
        return str;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void launchBrowser(String url) {
        getLauncher().launchBrowser(url, new Launcher.AppLaunchListener() {

            public void onSuccess(LaunchSession session) {
            }

            public void onError(ServiceCommandError error) {
            }
        });
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
        }


        @Override
        public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
            Log.d(TAG, "onConnectFailed " + error.toString());
            connectFailed(mTV);
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
            getMyToolbar().getMenu().findItem(R.id.action_connexion).setShowAsAction(SHOW_AS_ACTION_NEVER);

        }

        private void initLauncher() {
            launcher = mTV.getCapability(Launcher.class);
            toastControl = mTV.getCapability(ToastControl.class);
            volumeControl = mTV.getCapability(VolumeControl.class);
            Toast.makeText(getApplicationContext(), "Connexion ok", Toast.LENGTH_SHORT).show();
            if (isStartedFromNfc) {
                setYoutubeWithBrowserOnClick();
            }
        }

        @Override
        public void onDeviceDisconnected(ConnectableDevice device) {
            Log.d(TAG, "Device Disconnected");
            connectEnded(mTV);
            getMyToolbar().getMenu().findItem(R.id.action_connexion).setShowAsAction(SHOW_AS_ACTION_ALWAYS);
            Toast.makeText(getApplicationContext(), "Device Disconnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {

        }
    };

    public ToastControl getToastControl() {
        return toastControl;
    }

    private void initDiscoverManager() {
        DiscoveryManager.getInstance().registerDefaultDeviceTypes();
        DiscoveryManager.getInstance().setPairingLevel(DiscoveryManager.PairingLevel.ON);
        DiscoveryManager.getInstance().start();
    }

    private void setupPicker() {
        dp = new DevicePicker(this);
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

        pairingAlertDialog = new AlertDialog.Builder(this)
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

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        pairingCodeDialog = new AlertDialog.Builder(this)
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

    public Launcher getLauncher() {
        return launcher;
    }

    public VolumeControl getVolumeControl() {
        return volumeControl;
    }

    public class CircleSeekBarListener implements CircularSeekBar.OnCircularSeekBarChangeListener {
        @Override
        public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
            // TODO Insert your code here
            volumeStatus.setText(Integer.toString(progress));

        }

        @Override
        public void onStopTrackingTouch(CircularSeekBar seekBar) {
            Log.d(TAG, "volume change : " + seekBar.getProgress());
            getVolumeControl().setVolume((float) seekBar.getProgress() / 100.0f, null);
        }

        @Override
        public void onStartTrackingTouch(CircularSeekBar seekBar) {

        }
    }

}
