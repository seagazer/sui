package com.seagazer.lib.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.IntRange;

/**
 * 提供虚化图功能
 */
public class RenderScriptBlur {

    /**
     * 虚化图片
     *
     * @param context 上下文
     * @param bitmap  原始图片
     * @param radius  虚化力度(0, 25]
     * @return 虚化后的图片
     */
    public static Bitmap blur(Context context, Bitmap bitmap, @IntRange(from = 0, to = 25) int radius) {
        //Let's create an empty bitmap with the same size of the bitmap we want to fastBlur
        int width = bitmap.getWidth();
        int height = bitmap.getWidth() / 16 * 9;
        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(context.getApplicationContext());
        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        //Set the radius of the fastBlur: 0 < radius <= 25
        blurScript.setRadius(radius);
        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);
        //recycle the original bitmap
        //bitmap.recycle();
        //After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return outBitmap;

    }
}
