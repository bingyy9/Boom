package com.boom.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
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
import com.boom.android.ui.videotab.MyVideoFragment;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.NotificationUtil;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int OVERLAY_REQUEST_CODE  = 102;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private MediaRecordService recordService;
    private FloatingCameraService floatingCameraService;

    Button startBtn;
    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionMenu floatingMenu;
    FloatingActionButton recordScreenOnly;
    FloatingActionButton recordScreenWithCamera;
    FloatingActionButton stopRecord;

    private String[] tabs = {"My Videos", "Recent Videos"};
    private List<MyVideoFragment> tabFragmentList = new ArrayList<>();

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionManager.requestAllPermission(this);
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        initView();
        initService();
    }

    private void initView(){
        startBtn = this.findViewById(R.id.start_record);
        tabLayout = this.findViewById(R.id.tab_layout);
        viewPager = this.findViewById(R.id.view_pager);
        floatingMenu = this.findViewById(R.id.floating_menu);
        recordScreenOnly = this.findViewById(R.id.record_screen);
        recordScreenWithCamera = this.findViewById(R.id.record_screen_with_camera);
        stopRecord = this.findViewById(R.id.fab_stop_record);

        //        startBtn.setText(stringFromJNI());
        startBtn.setText(getResources().getString(R.string.start_record));
        startBtn.setEnabled(false);
        startBtn.setOnClickListener(clickListener);

        for (int i = 0; i < tabs.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabs[i]));
            tabFragmentList.add(MyVideoFragment.newInstance(tabs[i]));
        }

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return tabFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return tabFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }
        });

        tabLayout.setupWithViewPager(viewPager, false);

        floatingMenu.setClosedOnTouchOutside(true);
        recordScreenOnly.setOnClickListener(clickListener);
        recordScreenWithCamera.setOnClickListener(clickListener);
        recordScreenWithCamera.setOnClickListener(clickListener);
    }

    private void initService(){
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

    private View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.record_screen:
                onClickRecordScreenOnly();
                break;
            case R.id.record_screen_with_camera:
                onClickRecordScreenWithCamera();
                break;
            case R.id.start_record:
                onClickStartRecordingBtn();
                break;
            case R.id.fab_stop_record:
                onClickStopRecord();
                break;
        }
    };

    private void onClickStartRecordingBtn(){
        if (recordService != null && recordService.isRunning()) {
            startBtn.setText(R.string.start_record);
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void onClickRecordScreenOnly(){
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onClickRecordScreenOnly");
        if (recordService != null && recordService.isRunning()) {
            Dogger.w(Dogger.BOOM, "recording is in progress, ignore!", "MainActivity", "onClickRecordScreenOnly");
            return;
        } else {
            startRecording();
        }
    }

    private void onClickRecordScreenWithCamera(){
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onClickRecordScreenWithCamera");
        if (recordService != null && recordService.isRunning()) {
            Dogger.w(Dogger.BOOM, "recording is in progress, ignore!", "MainActivity", "onClickRecordScreenOnly");
            return;
        } else {
            startRecording();
        }
    }

    private void onClickStopRecord(){
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onClickStopRecord");

    }
}