package com.seagazer.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.seagazer.ui.anim.FocusHelper;

public class FocusFrameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_frame);
        FrameLayout parent = findViewById(R.id.parent);
        View frame = new View(this);
        frame.setBackgroundColor(Color.parseColor("#aa00ffff"));
        final FocusHelper focusHelper = new FocusHelper(parent, frame);
        parent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if (oldFocus != null) {
                    oldFocus.animate().scaleX(1).scaleY(1).start();
                }
                if (newFocus != null) {
                    newFocus.animate().scaleX(1.2f).scaleY(1.2f).start();
                }
                focusHelper.focusChange(newFocus, 1.2f, 1.2f);
            }
        });
    }
}
