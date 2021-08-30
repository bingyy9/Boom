package com.boom.android.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boom.android.BoomApplication;
import com.boom.android.R;

public class NotificationUtils {
    private static Toast mToast = null;

    public static void showToast(Context context, String msg) {
        if(mToast != null) {
            mToast.cancel();
        }
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.share_message_toast, null);
        TextView contentView = (TextView) layout.findViewById(R.id.tv_toast_message);
        contentView.setText(msg);
        mToast = new Toast(BoomApplication.getInstance());
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(layout);
        mToast.setGravity(Gravity.BOTTOM,0,WindowUtils.dip2pixels(context,225F));
        mToast.show();
    }
}
