package com.seagazer.sui.anim;

import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * 焦点框动效辅助类
 * <p>
 * 一般可以通过Parent的{@link View#getViewTreeObserver()}并且在{@link ViewTreeObserver#addOnGlobalFocusChangeListener(ViewTreeObserver.OnGlobalFocusChangeListener)}中监听焦点的变化
 * 调用{@link #focusChange(View)} 或者 {@link #focusChange(View, float, float)}通知焦点改变，自动执行动效
 * 调用{@link #setOffset(int)}可以调整焦点框与内容区域的间隙
 * 调用{@link #hide()}可以隐藏焦点框，适用于焦点移出当前parent区域
 */
public class FocusHelper {
    private FrameLayout mParent;
    private View mFocusFrame;
    private int mOffset = 2;

    public FocusHelper(FrameLayout parent, View focusFrame) {
        mFocusFrame = focusFrame;
        mParent = parent;
        init();
    }

    private void init() {
        mParent.addView(mFocusFrame, new FrameLayout.LayoutParams(1, 1));
        mFocusFrame.setAlpha(0);
    }

    /**
     * 通知焦点变更，自动执行焦点框动效
     *
     * @param focused 新的焦点
     */
    public void focusChange(View focused) {
        focusChange(focused, 1, 1);
    }

    /**
     * 通知焦点变更，自动执行焦点框动效
     *
     * @param focused       新的焦点
     * @param focusedScaleX 新的焦点x缩放比例
     * @param focusedScaleY 新的焦点y缩放比例
     */
    public void focusChange(View focused, float focusedScaleX, float focusedScaleY) {
        if (mFocusFrame != null && focused != null) {
            float focusedScaleWidth = (focusedScaleX - 1) * focused.getWidth();
            float focusedScaleHeight = (focusedScaleY - 1) * focused.getHeight();
            int newX = (int) (focused.getX() - mOffset - focusedScaleWidth / 2 - mParent.getPaddingLeft());
            int newY = (int) (focused.getY() - mOffset - focusedScaleHeight / 2 - mParent.getPaddingTop());
            ViewParent p = focused.getParent();
            // not the direct child view, may contains scrollView or others
            if (p != mParent) {
                while (p instanceof View && p != mParent) {
                    newX += ((View) p).getX() - ((View) p).getScrollX();
                    newY += ((View) p).getY() - ((View) p).getScrollY();
                    p = p.getParent();
                }
            }
            // the width and height after the focused scale
            int reallyWidth = (int) (focused.getWidth() + mOffset * 2 + focusedScaleWidth);
            int reallyHeight = (int) (focused.getHeight() + mOffset * 2 + focusedScaleHeight);
            // first init, not animate
            if (mFocusFrame.getAlpha() == 0) {
                mFocusFrame.setX(newX);
                mFocusFrame.setY(newY);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(reallyWidth, reallyHeight);
                mFocusFrame.setLayoutParams(lp);
                mFocusFrame.setAlpha(1);
            } else {// do animate
                float scaleX = reallyWidth * 1.0f / mFocusFrame.getWidth();
                float scaleY = reallyHeight * 1.0f / mFocusFrame.getHeight();
                mFocusFrame.animate()
                        .translationX(newX + (scaleX - 1) * mFocusFrame.getWidth() / 2)
                        .translationY(newY + (scaleY - 1) * mFocusFrame.getHeight() / 2)
                        .scaleX(scaleX)
                        .scaleY(scaleY)
                        .start();
            }
        }
    }

    /**
     * 隐藏焦点框
     */
    public void hide() {
        mFocusFrame.setAlpha(0);
    }

    /**
     * 设置焦点框四周偏移量，适用于控制光晕效果的焦点框
     * 默认2px
     *
     * @param offset 焦点框四周偏移量
     */
    public void setOffset(int offset) {
        mOffset = offset;
    }

}
