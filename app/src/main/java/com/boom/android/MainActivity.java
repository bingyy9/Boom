package com.boom.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.animation.OvershootInterpolator;

import com.boom.android.log.Dogger;
import com.boom.android.permission.PermissionManager;
import com.boom.android.service.FloatingCameraService;
import com.boom.android.service.MediaRecordService;
import com.boom.android.ui.videos.MyVideosFragment;
import com.boom.android.ui.videos.RecentVideosFragment;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.NotificationUtil;
import com.boom.android.util.RecordHelper;
import com.boom.model.interf.IRecordModel;
import com.boom.model.repo.RecordEvent;
import com.boom.utils.StringUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IRecordModel.RecordEvtListener {

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int OVERLAY_REQUEST_CODE  = 102;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private MediaRecordService recordService;
    private FloatingCameraService floatingCameraService;

    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionMenu floatingMenu;
    FloatingActionButton recordScreenOnly;
    FloatingActionButton recordScreenWithCamera;
    FloatingActionButton stopRecord;

    private String[] tabs;
    private List<Fragment> tabFragmentList = new ArrayList<>();

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
        tabLayout = this.findViewById(R.id.tab_layout);
        viewPager = this.findViewById(R.id.view_pager);
        floatingMenu = this.findViewById(R.id.floating_menu);
        recordScreenOnly = this.findViewById(R.id.record_screen);
        recordScreenWithCamera = this.findViewById(R.id.record_screen_with_camera);
        stopRecord = this.findViewById(R.id.fab_stop_record);

        //        startBtn.setText(stringFromJNI());

        tabs = new String[]{this.getResources().getString(R.string.my_videos), this.getResources().getString(R.string.recent_videos)};
        for (int i = 0; i < tabs.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabs[i]));
            if(StringUtils.contentEquals(tabs[i], getString(R.string.my_videos))){
                tabFragmentList.add(MyVideosFragment.newInstance(tabs[i]));
            } else if(StringUtils.contentEquals(tabs[i], this.getResources().getString(R.string.recent_videos))){
                tabFragmentList.add(RecentVideosFragment.newInstance(tabs[i]));
            }
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
        stopRecord.setOnClickListener(clickListener);
        createCustomAnimation4FloatingMenu();
    }

    private void createCustomAnimation4FloatingMenu() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(floatingMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(floatingMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(floatingMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(floatingMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                floatingMenu.getMenuIconView().setImageResource(floatingMenu.isOpened()
                        ? R.drawable.ic_x : R.drawable.ic_record);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));
        floatingMenu.setIconToggleAnimatorSet(set);
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
        if(RecordHelper.isRecordCamera()) {
            stopFloatingCameraService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecordHelper.registerRecordEventListner(this);
        updateRecordingView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecordHelper.unregisterRecordEventListener(this);
        if(floatingMenu != null) {
            floatingMenu.close(false);
        }
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
            startFloatingCameraService();
        } else if(requestCode == OVERLAY_REQUEST_CODE){
            if (BoomHelper.ensureDrawOverlayPermission(this)) {
                NotificationUtil.showToast(this, getString(R.string.display_over_other_apps_fail_tip));
            } else {
                startFloatingCameraService();
//                startService(new Intent(MainActivity.this, FloatingCameraService.class));
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
            updateRecordingView();
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
        if(!RecordHelper.isRecordCamera()){
            return;
        }
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
            case R.id.fab_stop_record:
                onClickStopRecord();
                break;
        }
    };

    private void onClickStartRecordingBtn(){
        if (RecordHelper.isRecording()) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void onClickRecordScreenOnly(){
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onClickRecordScreenOnly");
        if (RecordHelper.isRecording()) {
            Dogger.w(Dogger.BOOM, "recording is in progress, ignore!", "MainActivity", "onClickRecordScreenOnly");
            return;
        }

        RecordHelper.setRecordCamera(false);
        startRecording();
    }

    private void onClickRecordScreenWithCamera(){
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onClickRecordScreenWithCamera");
        if (RecordHelper.isRecording()) {
            Dogger.w(Dogger.BOOM, "recording is in progress, ignore!", "MainActivity", "onClickRecordScreenOnly");
            return;
        }

        RecordHelper.setRecordCamera(true);
        startRecording();
    }

    private void onClickStopRecord(){
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onClickStopRecord");
        stopRecording();
    }

    @Override
    public void onRecordEvt(RecordEvent evt) {
        if(evt == null){
            return;
        }

        runOnUiThread(()->{
            switch (evt.getType()) {
                case RecordEvent.RECORD_STATUS_UPDATE:
                    updateRecordingView();
                    break;
            }
        });
    }

    private void updateRecordingView(){
        floatingMenu.close(false);
        if(RecordHelper.isRecording()){
            stopRecord.setVisibility(View.VISIBLE);
            floatingMenu.setVisibility(View.GONE);
        } else {
            stopRecord.setVisibility(View.GONE);
            floatingMenu.setVisibility(View.VISIBLE);
        }
    }
}