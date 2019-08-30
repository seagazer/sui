package com.seagazer.ui;

import android.os.Bundle;
import android.os.Handler;
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
        final FocusHelper helper = new FocusHelper(this, parent);
        final Handler handler = new Handler();
        parent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
//                if (oldFocus != null) {
//                    oldFocus.animate().scaleX(1).scaleY(1).start();
//                }
//                if (newFocus != null) {
//                    newFocus.animate().scaleX(1.2f).scaleY(1.2f).start();
//                }
                helper.focusChange(newFocus);
            }
        });
    }
}
