package com.seagazer.sui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.seagazer.sui.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A vertical bannerView only show texts.
 * <p>
 * Call {@link #flippedToPre()} or {@link #flippedToNext()} to show the pre or next banner.<p>
 * Call {@link #setTextColor(int)}  and {@link #setTextSize(float)}to set textColor and textSize.<p>
 * Call {@link #setFlippedDuration(int)} to set the flipped duration.
 */
public class VerticalFlippedView extends View {

    private List<TextBanner> banners;
    private Paint paint;
    private int width, height;
    private ValueAnimator animator;
    private int currentIndex = 0;
    private int duration = 500;
    private float textSize = 20;
    private String[] texts;

    public VerticalFlippedView(Context context) {
        this(context, null);
    }

    public VerticalFlippedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalFlippedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        banners = new ArrayList<>();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        fillBanners();
    }

    private void fillBanners() {
        // setup layouts
        for (int i = 0; i < texts.length; i++) {
            String str = texts[i];
            TextBanner banner = new TextBanner(str, i);
            banner.setX(width / 2f);
            banner.setY((i + 1) * height);
            banners.add(banner);
            Logger.d("i= [" + str + ", " + width / 2f + ", " + i * height + "]");
        }
        // move the last to top out of visible rect
        TextBanner last = banners.get(banners.size() - 1);
        last.setY(0);
        banners.remove(last);
        banners.add(0, last);
    }

    private void startFlipped(boolean isFlippedNext) {
        if (animator != null && animator.isRunning()) {
            animator.end();
        }
        animator = ValueAnimator.ofFloat(0, isFlippedNext ? -height : height);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                for (int i = 0; i < banners.size(); i++) {
                    TextBanner banner = banners.get(i);
                    banner.setX(width / 2f);
                    banner.setY(i * height + value);
                }
                invalidate();
            }
        });
        animator.setDuration(duration);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isFlippedNext) {
                    final int first = 0;
                    TextBanner firstBanner = banners.get(first);
                    firstBanner.setY(0);
                    banners.remove(first);
                    banners.add(firstBanner);
                    currentIndex++;
                    if (currentIndex > banners.size() - 1) {
                        currentIndex = first;
                    }
                } else {
                    final int last = banners.size() - 1;
                    TextBanner lastBanner = banners.get(last);
                    lastBanner.setY(0);
                    banners.remove(last);
                    banners.add(0, lastBanner);
                    currentIndex--;
                    if (currentIndex < 0) {
                        currentIndex = last;
                    }
                }
                Logger.d("currentIndex =" + currentIndex);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < banners.size(); i++) {
            TextBanner banner = banners.get(i);
            float y = calculateBaseLine(banner.getY());
            if (y >= 0 && y <= height + textSize) {
                String text = banner.getText();
                float x = banner.getX();
                canvas.drawText(text, x, y, paint);
            }
        }
    }

    private float calculateBaseLine(float h) {
        return h - this.height / 2f + paint.getTextSize() / 2;
    }

    class TextBanner {
        private int id;
        private float[] location = new float[2];
        private String text;

        TextBanner(String text, int id) {
            this.text = text;
            this.id = id;
        }

        void setX(float x) {
            location[0] = x;
        }

        void setY(float y) {
            location[1] = y;
        }

        String getText() {
            return text;
        }

        float getX() {
            return location[0];
        }

        float getY() {
            return location[1];
        }
    }

    public void initTexts(String... text) {
        if (texts == null) {
            texts = text;
        } else {
            texts = text;
            banners.clear();
            fillBanners();
        }
    }

    public void flippedToPre() {
        startFlipped(false);
    }

    public void flippedToNext() {
        startFlipped(true);
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        paint.setTextSize(textSize);
    }

    public void setTextColor(int textColor) {
        paint.setColor(textColor);
    }

    public void setFlippedDuration(int duration) {
        this.duration = duration;
    }
}
