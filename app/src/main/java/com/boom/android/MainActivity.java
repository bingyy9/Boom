package com.boom.android;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.boom.android.permission.PermissionManager;
import com.boom.android.service.FloatingVideoService;
import com.boom.android.service.MediaRecordService;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.NotificationUtil;
import com.boom.android.util.WindowUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int OVERLAY_REQUEST_CODE  = 102;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private MediaRecordService recordService;

    @BindView(R.id.start_record)
    Button startBtn;

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        startBtn = findViewById(R.id.start_record);
        startBtn.setText(stringFromJNI());
        startBtn.setEnabled(false);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordService.isRunning()) {
                    recordService.stopRecord();
                    startBtn.setText(R.string.start_record);
                } else {
                    Intent captureIntent = projectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
                }
            }
        });


        PermissionManager.requestAllPermission(this);

        Intent intent = new Intent(this, MediaRecordService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            recordService.setMediaProject(mediaProjection);
            recordService.startRecord();
            startBtn.setText(R.string.stop_record);

            startFloatingVideoService();
        } else if(requestCode == OVERLAY_REQUEST_CODE){
            if (BoomHelper.ensureDrawOverlayPermission(this)) {
                NotificationUtil.showToast(this, getString(R.string.display_over_other_apps_fail_tip));
            } else {
//                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, FloatingVideoService.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionManager.STORAGE_REQUEST_CODE || requestCode == PermissionManager.AUDIO_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            MediaRecordService.RecordBinder binder = (MediaRecordService.RecordBinder) service;
            recordService = binder.getRecordService();
            recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            startBtn.setEnabled(true);
            startBtn.setText(recordService.isRunning() ? R.string.stop_record : R.string.start_record);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    public native String stringFromJNI();

    private void startFloatingVideoService() {
        if (FloatingVideoService.isStarted) {
            return;
        }
        if (!BoomHelper.ensureDrawOverlayPermission(this)) {
            NotificationUtil.showToast(this, getString(R.string.display_over_other_apps_request_tip));
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), OVERLAY_REQUEST_CODE);
        } else {
            startService(new Intent(MainActivity.this, FloatingVideoService.class));
        }
    }
}