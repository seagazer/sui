package com.seagazer.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.seagazer.ui.widget.FocusFrameHelper;
import com.seagazer.ui.widget.ImageFocusDrawable;
import com.seagazer.ui.widget.RoundRectFocusDrawable;

public class FocusFrameHelperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FocusFrameHelper helper = new FocusFrameHelper();
        helper.setupActivity(this, null);
        helper.addDefaultFocusDrawable(new RoundRectFocusDrawable(0, 4, Color.RED));
//        helper.addDefaultFocusDrawable(new ImageFocusDrawable(getResources().getDrawable(R.mipmap.ic_launcher), 150, 150));
    }
}
