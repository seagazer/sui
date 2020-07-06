package com.seagazer.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.seagazer.ui.widget.AnimLogoView;

public class AnimLogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_logo);
        AnimLogoView animLogoView = findViewById(R.id.anim_logo);
        animLogoView.setShowGradient(true);
        animLogoView.setGradientColor(getResources().getColor(R.color.colorAccent));
    }
}
