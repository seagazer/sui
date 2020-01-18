package com.seagazer.lib.widget;

/**
 * Align mode
 */
public enum AlignMode {
    /**
     * If the screenOrientation is landscape, it will clip the drawable from top to bottom,
     * or clip from left to right if the orientation is portrait
     */
    START,
    /**
     * If the screenOrientation is landscape, it will clip the drawable from bottom to top,
     * or clip from right to left if the orientation is portrait
     */
    END,
    /**
     * Whatever the screenOrientation is landscape or portrait, it will clip from center of drawable
     */
    CENTER
}
