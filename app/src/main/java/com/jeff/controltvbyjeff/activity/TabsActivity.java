package com.jeff.controltvbyjeff.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.badoualy.morphytoolbar.MorphyToolbar;
import com.jeff.controltvbyjeff.R;
import com.jeff.controltvbyjeff.fragment.MainFragment;
import com.jeff.controltvbyjeff.services.DLNaService;
import com.jeff.controltvbyjeff.services.NFCService;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.long1.spacetablayout.SpaceTabLayout;

import static com.jeff.controltvbyjeff.utils.Constants.YOUTBE_URL;
import static com.jeff.controltvbyjeff.utils.MyToolBarUtils.disableAppBarDrag;
import static com.jeff.controltvbyjeff.utils.MyToolBarUtils.hideFab;
import static com.jeff.controltvbyjeff.utils.MyToolBarUtils.setupToolbar;
import static com.jeff.controltvbyjeff.utils.MyToolBarUtils.setupToolbarOnclickListener;

public class TabsActivity extends AppCompatActivity {
    SpaceTabLayout tabLayout;

    Toolbar toolbar;

    AppBarLayout appBarLayout;

    @Bind(R.id.fab_photo)
    FloatingActionButton floatingActionButton;

    MorphyToolbar morphyToolbar;
    boolean isStartedFromNfc = false;

    private static DLNaService dlNaService;

    PendingIntent mPendingIntent;
    private NFCService nfcService;
    private NfcAdapter mAdapter;


    public void setYoutubeWithBrowserOnClick() {
        getDlNaService().launchBrowser(YOUTBE_URL);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getDlNaService().isAlreadyConnected()) {
            setYoutubeWithBrowserOnClick();
        } else {
            isStartedFromNfc = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        appBarLayout = (AppBarLayout)findViewById(R.id.layout_app_bar);
        nfcService = new NFCService();
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        dlNaService = new DLNaService(this);
        dlNaService.initService();
        dlNaService.createDialog();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initToolbar();
        //add the fragments you want to display in a List
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MainFragment());
        fragmentList.add(new MainFragment());
        fragmentList.add(new MainFragment());

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main_tabs);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (SpaceTabLayout) findViewById(R.id.spaceTabLayout);

        tabLayout.initialize(viewPager, getSupportFragmentManager(), fragmentList, savedInstanceState);

        tabLayout.setTabOneOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Welcome to SpaceTabLayout", Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        });

        tabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "" + tabLayout.getCurrentPosition(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        tabLayout.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (!morphyToolbar.isCollapsed()) {
            hideFab(floatingActionButton);
            morphyToolbar.collapse();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        disableAppBarDrag(appBarLayout);
        hideFab(floatingActionButton);
        morphyToolbar = setupToolbar(this, toolbar);
        setupToolbarOnclickListener(morphyToolbar, floatingActionButton);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static DLNaService getDlNaService() {
        return dlNaService;
    }
}