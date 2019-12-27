package com.seagazer.ui.widget;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Abstract class to provide a focusDrawable to highLight the focus view
 */
public abstract class FocusDrawable {

    /**
     * You can draw something to highLight the focus here
     *
     * @param canvas   The size of this canvas is the root contentView most of time,
     *                 for example like {@link FocusFrameContainer} or DecorView
     * @param drawRect The drawing rect of current focus view
     */
    abstract void drawFocusFrame(Canvas canvas, Rect drawRect);

}
