package com.jeff.controltvbyjeff.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import com.github.badoualy.morphytoolbar.MorphyToolbar;
import com.jeff.controltvbyjeff.ControlTvApplication;
import com.jeff.controltvbyjeff.R;

/**
 * Created by Jeff on 20/04/2017.
 */

public class MyToolBarUtils {

    static final int primary = ControlTvApplication.getAppContext().getResources().getColor(R.color.colorPrimary);
    static final int primaryDark= ControlTvApplication.getAppContext().getResources().getColor(R.color.colorPrimaryDark);


    public static void disableAppBarDrag(AppBarLayout appBarLayout) {
        // see http://stackoverflow.com/questions/34108501/how-to-disable-scrolling-of-appbarlayout-in-coordinatorlayout
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        params.setBehavior(behavior);
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
    }


    /** To hide fab, you need to remove its anchor */
    public static void hideFab( FloatingActionButton fabPhoto) {
        // Ugly bug makes the view go to bottom|center of screen before hiding, seems like you need to implement your own fab behavior...
        fabPhoto.setVisibility(View.GONE);
        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fabPhoto.getLayoutParams();
        layoutParams.setAnchorId(View.NO_ID);
        fabPhoto.requestLayout();
        fabPhoto.hide();
    }

    public static void showFab( FloatingActionButton fabPhoto) {
        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fabPhoto.getLayoutParams();
        layoutParams.setAnchorId(R.id.layout_app_bar);
        layoutParams.anchorGravity = Gravity.RIGHT | Gravity.END | Gravity.BOTTOM;
        fabPhoto.requestLayout();
        fabPhoto.show();
    }

    public static MorphyToolbar setupToolbar(Activity activity, Toolbar toolbar){
       MorphyToolbar morphyToolbar = MorphyToolbar.builder((AppCompatActivity) activity, toolbar)
                .withToolbarAsSupportActionBar()
                .withTitle("Control Tv")
                .withSubtitle("       By Jeff")
               .withTitleColor(primaryDark)
               .withContentExpandedMarginStart(200)
               .withContentMarginStart(250)
                .withHidePictureWhenCollapsed(false)
                .build();
        return morphyToolbar;
    }

    public static void setupToolbarOnclickListener( final MorphyToolbar morphyToolbar,final FloatingActionButton floatingActionButton){



        morphyToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (morphyToolbar.isCollapsed()) {
                    morphyToolbar.expand(primary, primaryDark, new MorphyToolbar.OnMorphyToolbarExpandedListener() {
                        @Override
                        public void onMorphyToolbarExpanded() {
                            showFab(floatingActionButton);
                        }
                    });
                } else {
                    hideFab(floatingActionButton);
                    morphyToolbar.collapse();
                }
            }
        });
    }
}
