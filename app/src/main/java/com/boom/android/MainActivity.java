package com.boom.android;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.boom.android.log.Dogger;
import com.boom.android.permission.PermissionManager;
import com.boom.android.service.FloatingCameraService;
import com.boom.android.service.MediaRecordService;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.NotificationUtil;

public class MainActivity extends AppCompatActivity {

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int OVERLAY_REQUEST_CODE  = 102;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private MediaRecordService recordService;
    private FloatingCameraService floatingCameraService;

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
                if (recordService != null && recordService.isRunning()) {
                    startBtn.setText(R.string.start_record);
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });


        PermissionManager.requestAllPermission(this);

        Intent intent = new Intent(this, MediaRecordService.class);
        bindService(intent, recordServiceConnection, BIND_AUTO_CREATE);
    }

    private void startRecording(){
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
    }

    private void stopRecording(){
        if(recordService != null){
            recordService.stopRecord();
        }
        stopFloatingCameraService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(recordServiceConnection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            recordService.setMediaProject(mediaProjection);
            recordService.startRecord();
            startBtn.setText(R.string.stop_record);

            startFloatingCameraService();
        } else if(requestCode == OVERLAY_REQUEST_CODE){
            if (BoomHelper.ensureDrawOverlayPermission(this)) {
                NotificationUtil.showToast(this, getString(R.string.display_over_other_apps_fail_tip));
            } else {
//                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(MainActivity.this, FloatingCameraService.class));
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

    private ServiceConnection recordServiceConnection = new ServiceConnection() {
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

    private ServiceConnection floatWindowServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Dogger.i(Dogger.BOOM, "", "MainActivity", "onServiceConnected");
            floatingCameraService = ((FloatingCameraService.MsgBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Dogger.i(Dogger.BOOM, "", "MainActivity", "onServiceDisconnected");
            floatingCameraService = null;
        }
    };

    public native String stringFromJNI();

    private void startFloatingCameraService() {
        if (floatingCameraService != null && floatingCameraService.isStarted) {
            Dogger.i(Dogger.BOOM, "ignore", "MainActivity", "startFloatingCameraService");
            return;
        }
        if (!BoomHelper.ensureDrawOverlayPermission(this)) {
            Dogger.i(Dogger.BOOM, "ask overlay permission", "MainActivity", "startFloatingCameraService");
            NotificationUtil.showToast(this, getString(R.string.display_over_other_apps_request_tip));
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), OVERLAY_REQUEST_CODE);
        } else {
            Dogger.i(Dogger.BOOM, "start overlay service", "MainActivity", "startFloatingCameraService");
            Intent intent = new Intent(this, FloatingCameraService.class);
            bindService(intent, floatWindowServiceConnection, BIND_AUTO_CREATE);
        }
    }

    private void stopFloatingCameraService(){
        Dogger.i(Dogger.BOOM, "", "MainActivity", "stopFloatingCameraService");
        unbindService(floatWindowServiceConnection);
        floatingCameraService = null;
    }

}