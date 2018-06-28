package com.sorashiro.cloudcat.tool;

import android.content.Context;

public class DPUtil {

    /**
     * 设备像素(dip,dp)转屏幕像素(px)
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
