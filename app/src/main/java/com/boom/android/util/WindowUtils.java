package com.boom.android.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class WindowUtils {
    public static int getScreenHeight(final Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager wndMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wndMgr == null)
            return 0;

        Display display = wndMgr.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);

        return outSize.y;
    }

    public static int getScreenWidth(final Context context) {
        if (context == null) {
            return 0;
        }

        WindowManager wndMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wndMgr == null)
            return 0;

        Display display = wndMgr.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);

        return outSize.x;
    }
}
