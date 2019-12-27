package com.seagazer.ui.widget;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Interface to provide a focusDrawable to highLight the focus view.
 * <p>
 * We provider some default focusDrawable like {@link FocusRoundRectDrawable}, {@link FocusImageDrawable} or {@link FocusCircleDrawable}.
 * If you want to custom the focusDrawable, you should implement this interface.
 */
public interface FocusLampDrawable {

    /**
     * You can draw something to highLight the focus here.
     *
     * @param canvas    The canvas to draw focusDrawable, the size of canvas is always the same as {@link FocusLampContainer},
     *                  or DecorView if you use {@link FocusLampHelper}
     * @param focusRect The rect of current focus view, you should draw the drawable in this rect
     */
    void drawFocusFrame(Canvas canvas, Rect focusRect);

}
