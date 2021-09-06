package com.boom.android.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.boom.android.BoomApplication;
import com.boom.android.MainActivity;
import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.service.RecordingForegroundService;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationUtils {
    private static Toast mToast = null;

    public static void showToast(Context context, String msg) {
        if (mToast != null) {
            mToast.cancel();
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.share_message_toast, null);
        TextView contentView = (TextView) layout.findViewById(R.id.tv_toast_message);
        contentView.setText(msg);
        mToast = new Toast(BoomApplication.getInstance());
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(layout);
        mToast.setGravity(Gravity.BOTTOM, 0, WindowUtils.dip2pixels(context, 225F));
        mToast.show();
    }

    //1）activity： Context.startForegroundService()
    //2）Service：startForeground(int id, Notification notification)（id must not be 0）
    public static void startRecordingNotification(Context context) {
        if (context == null) {
            return;
        }
        Dogger.i(Dogger.BOOM, "", "NotificationUtils", "startRecordingNotification");
        Intent i = new Intent(context, RecordingForegroundService.class);
        ContextCompat.startForegroundService(context, i);
    }

    public static void removeRecordingNotification(Context context) {
        if (context == null) {
            return;
        }
        Dogger.i(Dogger.BOOM, "", "NotificationUtils", "removeRecordingNotification");
        Intent i = new Intent(context, RecordingForegroundService.class);
        context.stopService(i);
    }
}
