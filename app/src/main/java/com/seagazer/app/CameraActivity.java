package com.seagazer.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.seagazer.ui.widget.CameraStudyView;

public class CameraActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar translateX, translateY, translateZ, rotateX, rotateY, rotateZ;
    private TextView titleTx, titleTy, titleTz, titleRx, titleRy, titleRz;
    private int tx, ty, tz, rx, ry, rz;
    private CameraStudyView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.camera_view);
        cameraView.setDrawableRes(R.drawable.a3);
        translateX = findViewById(R.id.translate_x);
        translateY = findViewById(R.id.translate_y);
        translateZ = findViewById(R.id.translate_z);
        rotateX = findViewById(R.id.rotate_x);
        rotateY = findViewById(R.id.rotate_y);
        rotateZ = findViewById(R.id.rotate_z);

        titleTx = findViewById(R.id.title_translate_x);
        titleTy = findViewById(R.id.title_translate_y);
        titleTz = findViewById(R.id.title_translate_z);
        titleRx = findViewById(R.id.title_rotate_x);
        titleRy = findViewById(R.id.title_rotate_y);
        titleRz = findViewById(R.id.title_rotate_z);


        translateX.setOnSeekBarChangeListener(this);
        translateY.setOnSeekBarChangeListener(this);
        translateZ.setOnSeekBarChangeListener(this);
        rotateX.setOnSeekBarChangeListener(this);
        rotateY.setOnSeekBarChangeListener(this);
        rotateZ.setOnSeekBarChangeListener(this);
        translateX.setProgress(50);
        translateY.setProgress(50);
        translateZ.setProgress(50);
        rotateX.setProgress(50);
        rotateY.setProgress(50);
        rotateZ.setProgress(50);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            // set negative to positive
            progress = (int) ((progress - 50) * 3.6);
            switch (seekBar.getId()) {
                case R.id.translate_x:
                    tx = progress;
                    cameraView.translate(tx, ty, tz);
                    break;
                case R.id.translate_y:
                    ty = progress;
                    cameraView.translate(tx, ty, tz);
                    break;
                case R.id.translate_z:
                    tz = progress;
                    cameraView.translate(tx, ty, tz);
                    break;
                case R.id.rotate_x:
                    rx = progress;
                    cameraView.rotate(rx, ry, rz);
                    break;
                case R.id.rotate_y:
                    ry = progress;
                    cameraView.rotate(rx, ry, rz);
                    break;
                case R.id.rotate_z:
                    rz = progress;
                    cameraView.rotate(rx, ry, rz);
                    break;
            }
            titleTx.setText("translateX: " + tx);
            titleTy.setText("translateY: " + ty);
            titleTz.setText("translateZ: " + tz);
            titleRx.setText("rotateX: " + rx);
            titleRy.setText("rotateY: " + ry);
            titleRz.setText("rotateZ: " + rz);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
