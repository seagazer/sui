package com.seagazer.app;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.seagazer.lib.util.DensityConverter;
import com.seagazer.lib.widget.FocusLampDrawable;
import com.seagazer.lib.widget.FocusLampHelper;
import com.seagazer.lib.widget.FocusRoundRectDrawable;

public class FocusLampHelperActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focuslamp);
        FocusLampHelper helper = new FocusLampHelper();
        helper.setTarget(this, new Rect(0, 0, DensityConverter.dp2px(this, 516), DensityConverter.dp2px(this, 292)));
        helper.addFocusDrawable(AppCompatButton.class, new CustomRoundRectDrawable());
        helper.addFocusDrawable(ImageView.class, new FocusRoundRectDrawable(0, 4, Color.RED));
        // helper.addDefaultFocusDrawable(new ImageFocusDrawable(getResources().getDrawable(R.mipmap.ic_launcher), 150, 150));
    }

    class CustomRoundRectDrawable implements FocusLampDrawable {
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6);
            paint.setColor(Color.GREEN);
        }

        private Path path = new Path();
        private RectF rectF = new RectF();

        @Override
        public void drawFocusLamp(Canvas canvas, Rect focusRect) {
            rectF.set(focusRect);
            path.reset();
            int radius = 50;
            path.addRoundRect(rectF, new float[]{radius, radius, 0, 0, radius, radius, 0, 0}, Path.Direction.CCW);
            canvas.drawPath(path, paint);
        }
    }
}

