package com.jeff.controltvbyjeff;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.skyfishjy.library.RippleBackground;


public class ConnexionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        final Button connexion = (Button)findViewById(R.id.connexion);
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                connexion.setText("Connexion en cours ...");
                connexion.setTextColor(Color.WHITE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ConnexionActivity.this,MainActivity.class);
                        startActivity(intent);
                        rippleBackground.stopRippleAnimation();
                        connexion.setText(getString(R.string.Connexion));
                        connexion.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                }, 2000);

            }
        });
    }
}
