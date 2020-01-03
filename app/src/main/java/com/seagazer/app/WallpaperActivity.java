package com.seagazer.app;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.seagazer.lib.widget.RatioDrawableWrapper;
import com.seagazer.lib.widget.WallpaperHelper;

public class WallpaperActivity extends AppCompatActivity {

    private ImageView image;
    private int clickCount;
    private WallpaperHelper wallpaperHelper1;
    private WallpaperHelper wallpaperHelper2;
    int[] wallpapers = {R.drawable.a1, R.drawable.a2, R.drawable.a3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        image = findViewById(R.id.image);
        wallpaperHelper1 = new WallpaperHelper();
        wallpaperHelper1.setAlignMode(RatioDrawableWrapper.AlignMode.CENTER);
        wallpaperHelper1.setTarget(this, null);
        wallpaperHelper1.setColorMask(0x800A0A0A);
        wallpaperHelper1.setTransitionDuration(1000);

        FrameLayout preview = findViewById(R.id.preview);
        wallpaperHelper2 = new WallpaperHelper();
        wallpaperHelper2.setAlignMode(RatioDrawableWrapper.AlignMode.CENTER);
        wallpaperHelper2.setTarget(preview, null);
        wallpaperHelper2.setColorMask(0x803E8394);
        wallpaperHelper2.setTransitionDuration(1000);

    }

    public void onClick(View view) {
        int wallpaper = wallpapers[clickCount % wallpapers.length];
        image.setImageResource(wallpaper);
        wallpaperHelper1.setWallpaper(wallpaper);
        wallpaperHelper2.setWallpaper(wallpaper);
        clickCount++;
    }
}
