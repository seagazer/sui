package com.seagazer.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.seagazer.ui.widget.FocusLampDrawable;
import com.seagazer.ui.widget.FocusLampHelper;
import com.seagazer.ui.widget.FocusRoundRectDrawable;

public class FocusFrameHelperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FocusLampHelper helper = new FocusLampHelper();
        helper.setupActivity(this, null);
        helper.addFocusDrawable(AppCompatButton.class, new FocusRoundRectDrawable(0, 4, Color.RED));
        helper.addFocusDrawable(ImageView.class, new CustomRoundRectDrawable());
//        helper.addDefaultFocusDrawable(new ImageFocusDrawable(getResources().getDrawable(R.mipmap.ic_launcher), 150, 150));
    }


    class CustomRoundRectDrawable implements FocusLampDrawable {
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6);
            paint.setColor(Color.RED);
        }

        private Path path = new Path();
        private RectF rectF = new RectF();
        private int radius = 50;

        @Override
        public void drawFocusFrame(Canvas canvas, Rect focusRect) {
            rectF.set(focusRect);
            path.reset();
            path.addRoundRect(rectF, new float[]{radius, radius, 0, 0, radius, radius, 0, 0}, Path.Direction.CCW);
            canvas.drawPath(path, paint);
        }
    }
}

