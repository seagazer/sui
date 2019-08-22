package com.seagazer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.seagazer.ui.widget.AnimLogoView;
import com.seagazer.ui.widget.BackgroundView;

public class BackgroundActivity extends AppCompatActivity {

    private BackgroundView mBackgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        mBackgroundView = findViewById(R.id.background_view);
    }


    public void onClick(View view) {
        mBackgroundView.setBgImage(getResources().getDrawable(R.drawable.ic_launcher_background, null));
        mBackgroundView.startRipple();
        AnimLogoView logo = findViewById(R.id.logo_view);
        logo.playAnimation();
    }
}
