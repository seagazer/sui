package com.seagazer.app;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.seagazer.lib.widget.AlignMode;
import com.seagazer.lib.widget.WallpaperHelper;

public class WallpaperActivity extends AppCompatActivity {

    private int clickCount;
    private WallpaperHelper wallpaperHelper1;
    private WallpaperHelper wallpaperHelper2;
    int[] wallpapers = {R.drawable.a1, R.drawable.a2, R.drawable.a3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        wallpaperHelper1 = new WallpaperHelper();
        wallpaperHelper1.setAlignMode(AlignMode.CENTER);
        wallpaperHelper1.attach(this, null);
        wallpaperHelper1.setCrossFadeDuration(1000);

        FrameLayout preview = findViewById(R.id.preview);
        wallpaperHelper2 = new WallpaperHelper();
        wallpaperHelper2.setAlignMode(AlignMode.CENTER);
        wallpaperHelper2.attach(preview, null);
        wallpaperHelper2.setColorMask(0x803E8394);
        wallpaperHelper2.setCrossFadeDuration(1000);

    }

    public void onClick(View view) {
        int wallpaper = wallpapers[clickCount % wallpapers.length];
        wallpaperHelper1.setWallpaper(wallpaper);
        wallpaperHelper2.setWallpaper(wallpaper);
        clickCount++;
    }
}
