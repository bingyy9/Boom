package com.boom.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.boom.android.log.Dogger;
import com.boom.android.service.RecordingForegroundService;
import com.boom.android.ui.adapter.repo.RecordParams;
import com.boom.android.ui.adapter.repo.Resolution;
import com.boom.android.ui.videos.MyVideosFragment;
import com.boom.android.ui.videos.RecentVideosFragment;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.LogToFileUtils;
import com.boom.android.util.NotificationUtils;
import com.boom.android.util.PrefsUtil;
import com.boom.android.util.RecordHelper;
import com.boom.android.viewmodel.RecordViewModel;
import com.boom.model.interf.IRecordModel;
import com.boom.model.repo.RecordEvent;
import com.boom.utils.StringUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.tabs.TabLayout;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IRecordModel.RecordEvtListener {

    private static final int RECORD_REQUEST_CODE  = 101;
    private static final int OVERLAY_REQUEST_CODE  = 102;

    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private RecordViewModel recordViewModel;

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
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        initPermission();
        initView();
        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent(){
        Intent intent = getIntent();
        if(intent != null){
            String action = intent.getStringExtra(RecordingForegroundService.NOTIFICATION_ACTION);
            Dogger.i(Dogger.BOOM, "action: " + action, "MainActivity", "handleIntent");
            if(StringUtils.contentEquals(action, RecordingForegroundService.STOP)){
                stopRecording();
            }
        }
    }

    private void initView(){
        recordViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory((this).getApplication()))
                .get(RecordViewModel.class);
        tabLayout = this.findViewById(R.id.tab_layout);
        viewPager = this.findViewById(R.id.view_pager);
        floatingMenu = this.findViewById(R.id.floating_menu);
        recordScreenOnly = this.findViewById(R.id.record_screen);
        recordScreenWithCamera = this.findViewById(R.id.record_screen_with_camera);
        stopRecord = this.findViewById(R.id.fab_stop_record);

        tabs = new String[]{this.getResources().getString(R.string.my_videos)};
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

    private void startRecording(){
        if (RecordHelper.isRecording() || RecordHelper.isCountDowning()) {
            NotificationUtils.showToast(this, getResources().getString(R.string.recording_in_progress));
            Dogger.w(Dogger.BOOM, "recording is in progress, ignore!", "MainActivity", "startRecording");
            return;
        }

        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
    }

    private void stopRecording(){
        if(BoomApplication.getInstance().getMediaRecordService() != null){
            BoomApplication.getInstance().getMediaRecordService().stopRecord();
        }
        RecordHelper.setRecordingStop(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecordHelper.registerRecordEventListner(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRecordingView();
    }

    @Override
    protected void onPause() {
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onPause");
        super.onPause();
        if(floatingMenu != null) {
            floatingMenu.close(false);
        }
    }

    @Override
    protected void onStop() {
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onStop");
        super.onStop();
        RecordHelper.unregisterRecordEventListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(this.isFinishing()) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            configMediaRecordService(mediaProjection);
            showCounterFloatingWindow();
        } else if(requestCode == OVERLAY_REQUEST_CODE){
            showCounterFloatingWindow();
        }
    }

    private void configMediaRecordService(MediaProjection mediaProjection){
        if(BoomApplication.getInstance().getMediaRecordService() != null){
            BoomApplication.getInstance().getMediaRecordService().setMediaProject(mediaProjection);
            BoomApplication.getInstance().getMediaRecordService().setConfig(getRecordParams());
        }
    }

    private RecordParams getRecordParams(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Dogger.i(Dogger.BOOM, "metrics.widthPixels: " + metrics.widthPixels + " metrics.heightPixels: " + metrics.heightPixels, "MainActivity", "configMediaRecordService");
        Resolution resolution = PrefsUtil.getResolution(this);
        if(resolution != null){
            if(resolution.getWidth() == 1) {
                return new RecordParams(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi, PrefsUtil.getVideoBitrate(this), PrefsUtil.getVideoFrameRate(this));
            } else {
                return new RecordParams(resolution.getWidth()
                        , resolution.getHeight()
                        , metrics.densityDpi
                        , PrefsUtil.getVideoBitrate(this)
                        , PrefsUtil.getVideoFrameRate(this));
            }
        }
        return null;
    }

    private void showCounterFloatingWindow(){
        if (!BoomHelper.ensureDrawOverlayPermission(this)) {
            NotificationUtils.showToast(this, getString(R.string.display_over_other_apps_fail_tip));
        } else {
            if(BoomApplication.getInstance().getMediaRecordService() != null){
                BoomApplication.getInstance().getMediaRecordService().showCounterFloatingWindow();
            } else {
                Dogger.i(Dogger.BOOM, "mediaRecordService is null.", "MainActivity", "showCounterFloatingWindow");
            }
        }
    }

    public native String stringFromJNI();

    private View.OnClickListener clickListener = v -> {
        switch (v.getId()) {
            case R.id.record_screen:
                SoulPermission.getInstance().checkAndRequestPermissions(
                        Permissions.build(Manifest.permission.CAMERA
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.RECORD_AUDIO),
                        new CheckRequestPermissionsListener() {
                            @Override
                            public void onAllPermissionOk(Permission[] allPermissions) {
                                ensureLogToFileInit();
                                onClickRecordScreenOnly();
                            }

                            @Override
                            public void onPermissionDenied(Permission[] refusedPermissions) {
                                NotificationUtils.showToast(MainActivity.this, getResources().getString(R.string.no_permission_to_record));
                            }
                        });
                break;
            case R.id.record_screen_with_camera:
                SoulPermission.getInstance().checkAndRequestPermissions(
                        Permissions.build(Manifest.permission.CAMERA
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.RECORD_AUDIO),
                        new CheckRequestPermissionsListener() {
                            @Override
                            public void onAllPermissionOk(Permission[] allPermissions) {
                                ensureLogToFileInit();
                                onClickRecordScreenWithCamera();
                            }

                            @Override
                            public void onPermissionDenied(Permission[] refusedPermissions) {
                                NotificationUtils.showToast(MainActivity.this, getResources().getString(R.string.no_permission_to_record));
                            }
                        });
                break;
            case R.id.fab_stop_record:
                onClickStopRecord();
                break;
        }
    };

    private void onClickRecordScreenOnly(){
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onClickRecordScreenOnly");
        RecordHelper.setRecordCamera(false);
        startRecording();
//        NotificationUtils.startRecordingNotification(this);
    }

    private void onClickRecordScreenWithCamera(){
        Dogger.i(Dogger.BOOM, "", "MainActivity", "onClickRecordScreenWithCamera");
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
                case RecordEvent.RECORD_READY_TO_RECORD:
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

    private void initPermission(){
        SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    //if you want do noting or no need all the callbacks you may use SimplePermissionsAdapter instead
                new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {
                        ensureLogToFileInit();
                    }

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                AboutActivity.start(this);
                break;
            case R.id.settings:
                SettingsActivity.start(this);
                break;
        }
        return true;
    }

    private void ensureLogToFileInit(){
        if(!LogToFileUtils.isInit){
            LogToFileUtils.init(this);
        }
    }
}