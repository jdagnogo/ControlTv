package com.jeff.controltvbyjeff.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeff.controltvbyjeff.R;
import com.jeff.controltvbyjeff.customlibs.CircularSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jeff.controltvbyjeff.activity.TabsActivity.getDlNaService;

/**
 * Created by Jeff on 20/04/2017.
 */

public class MainFragment extends Fragment {

    private final static String TAG = MainFragment.class.getSimpleName();
    private CircularSeekBar seekbar;
    @Bind(R.id.edit_toast)
    EditText editToast;

    @Bind(R.id.send_toast)
    ImageButton sendToast;

    @Bind(R.id.volume_status)
    TextView volumeStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initView(container);

        View view= inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, container);
        return view;
    }

    @OnClick(R.id.send_toast)
    public void setSendToast() {
        getDlNaService().getToastControl().showToast(editToast.getText().toString(), null, null, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(ViewGroup viewGroup) {
        seekbar = (CircularSeekBar) viewGroup.findViewById(R.id.circularSeekBar1);
        seekbar.getProgress();
        seekbar.setProgress(50);
        seekbar.setOnSeekBarChangeListener(new CircleSeekBarListener());
    }


    public class CircleSeekBarListener implements CircularSeekBar.OnCircularSeekBarChangeListener {
        @Override
        public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
            // TODO Insert your code herz
            volumeStatus.setText(Integer.toString(progress));

        }

        @Override
        public void onStopTrackingTouch(CircularSeekBar seekBar) {
            Log.d(TAG, "volume change : " + seekBar.getProgress());
            getDlNaService().getVolumeControl().setVolume((float) seekBar.getProgress() / 100.0f, null);
        }

        @Override
        public void onStartTrackingTouch(CircularSeekBar seekBar) {

        }
    }
}
