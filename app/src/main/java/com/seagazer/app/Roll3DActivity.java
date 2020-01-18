package com.seagazer.app;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Roll3DActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Roll3DView view = new Roll3DView(this);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setBackgroundColor(Color.LTGRAY);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(640, 360);
        params.gravity = Gravity.CENTER;
        frameLayout.addView(view, params);
        setContentView(frameLayout);


        view.addImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.a1));
        view.addImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.a2));
        view.addImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.a3));
        view.setRollMode(Roll3DView.RollMode.Whole3D);
        view.setPartNumber(5);
        view.setRollDirection(1);
        view.setRollDuration(1000);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.toNext();
            }
        });
    }
}
