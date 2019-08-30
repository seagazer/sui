package com.seagazer.ui.anim;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

public class FocusHelper {
    private Context mContext;
    private FrameLayout mParent;
    private View mFocusFrame;
    private int mOffset = 2;

    public FocusHelper(Context context, FrameLayout parent) {
        mContext = context.getApplicationContext();
        mParent = parent;
        init();
    }

    private void init() {
        mFocusFrame = new View(mContext);
        mFocusFrame.setBackgroundColor(Color.parseColor("#aa00ffff"));
        mParent.addView(mFocusFrame, new FrameLayout.LayoutParams(1, 1));
        mFocusFrame.setAlpha(0);
    }

    public void focusChange(View focused) {
        if (mFocusFrame != null && focused != null) {
            int x = (int) (focused.getX() - mOffset);
            int y = (int) (focused.getY() - mOffset);
            ViewParent p = focused.getParent();
            if (p != mParent) {
                while (p instanceof View && p != mParent) {
                    x += ((View) p).getX() - ((View) p).getScrollX();
                    y += ((View) p).getY() - ((View) p).getScrollY();
                    p = p.getParent();
                }
            }
            int w = focused.getWidth() + mOffset * 2;
            int h = focused.getHeight() + mOffset * 2;
            if (mFocusFrame.getAlpha() == 0) {
                mFocusFrame.setX(x);
                mFocusFrame.setY(y);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
                mFocusFrame.setLayoutParams(lp);
                mFocusFrame.setAlpha(1);
            } else {
                float scaleX = w * 1.0f / mFocusFrame.getWidth();
                float scaleY = h * 1.0f / mFocusFrame.getHeight();
                mFocusFrame.animate().translationX(x + (scaleX - 1) * mFocusFrame.getWidth() / 2)
                        .translationY(y + (scaleY - 1) * mFocusFrame.getHeight() / 2)
                        .scaleX(scaleX)
                        .scaleY(scaleY)
                        .start();
            }
        }
    }

    public void hide() {
        mFocusFrame.setAlpha(0);
    }

}
