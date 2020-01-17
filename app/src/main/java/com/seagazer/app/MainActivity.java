package com.seagazer.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.focus_lamp_helper:
                startActivity(new Intent(this, FocusLampHelperActivity.class));
                break;
            case R.id.wallpaper:
                startActivity(new Intent(this, WallpaperActivity.class));
                break;
            case R.id.camera:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.list_view:
                startActivity(new Intent(this, RecyclerViewActivity.class));
                break;
        }
    }
}
