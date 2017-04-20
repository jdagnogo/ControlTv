package com.jeff.controltvbyjeff.activity;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.long1.spacetablayout.SpaceTabLayout;

import static com.jeff.controltvbyjeff.utils.MyToolBarUtils.disableAppBarDrag;
import static com.jeff.controltvbyjeff.utils.MyToolBarUtils.hideFab;
import static com.jeff.controltvbyjeff.utils.MyToolBarUtils.setupToolbar;
import static com.jeff.controltvbyjeff.utils.MyToolBarUtils.setupToolbarOnclickListener;

public class TabsActivity extends AppCompatActivity {
    SpaceTabLayout tabLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.layout_app_bar)
    AppBarLayout appBarLayout;

    @Bind(R.id.fab_photo)
    FloatingActionButton floatingActionButton;


    MorphyToolbar morphyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        ButterKnife.bind(this);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
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
        morphyToolbar = setupToolbar(this,toolbar);
        setupToolbarOnclickListener(morphyToolbar,floatingActionButton);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}