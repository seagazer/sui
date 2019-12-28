package com.seagazer.app;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.seagazer.lib.widget.RatioDrawableWrapper;
import com.seagazer.lib.widget.WallpaperHelper;

public class WallpaperActivity extends AppCompatActivity {

    private int clickCount;
    private WallpaperHelper wallpaperHelper;
    int[] wallpapers = {R.drawable.a1, R.drawable.a2, R.drawable.a3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        wallpaperHelper = new WallpaperHelper();
        wallpaperHelper.setAlignMode(RatioDrawableWrapper.AlignMode.BOTTOM);
        wallpaperHelper.setupActivity(this, null);
        wallpaperHelper.addColorFilter(0x800A0A0A);
        wallpaperHelper.setTransitionDuration(1200);
    }

    public void onClick(View view) {
        wallpaperHelper.setWallpaper(wallpapers[clickCount % wallpapers.length]);
        clickCount++;
    }
}
