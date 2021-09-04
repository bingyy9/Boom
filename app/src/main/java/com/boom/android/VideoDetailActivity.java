package com.boom.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.boom.android.log.Dogger;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.DataUtils;
import com.boom.android.util.KeybordUtils;
import com.boom.android.util.NotificationUtils;
import com.boom.android.util.cache.BitmapCacheUtils;
import com.boom.model.interf.IRecordModel;
import com.boom.model.interf.impl.ModelBuilderManager;
import com.boom.utils.StringUtils;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.File;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoDetailActivity extends AppCompatActivity implements UniversalVideoView.VideoViewCallback {
    private static final String TAG = "VideoDetailActivity";
    private static final String FILENAME = "FILE_NAME";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";

    private static final int SHREA_REQUEST_CODE = 1;
    private IRecordModel recordModel;

    View root;
    @BindView(R.id.ed_name)
    EditText editName;
    @BindView(R.id.tv_last_modified_time)
    TextView tvLastModified;
    @BindView(R.id.share_video)
    TextView shareVideo;
    @BindView(R.id.video_layout)
    View mVideoLayout;
    @BindView(R.id.videoView)
    UniversalVideoView mVideoView;
    @BindView(R.id.media_controller)
    UniversalMediaController mMediaController;

    private String fileName;
    private String filePath;
    private File file;
    private int mSeekPosition;
    private int cachedHeight;
    private boolean isFullscreen;
    private Drawable editTextBackground;


    public static void start(Context context, String filename) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(FILENAME, filename);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_details);
        ButterKnife.bind(this);
        recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        initView();
    }

    private void initView() {
        fileName = getIntent() == null ? null : getIntent().getStringExtra(FILENAME);
        if (StringUtils.isEmpty(fileName)) {
            Dogger.i(Dogger.BOOM, "file name is null", "VideoDetailActivity", "initView");
            finish();
            return;
        }
        file = new File(BoomHelper.getRecordDirectory() + fileName);
        if (file == null) {
            Dogger.i(Dogger.BOOM, "file is not exist", "VideoDetailActivity", "initView");
            finish();
            return;
        }
        editName.setText(file.getName().replace(BoomHelper.filePostfix, ""));
        editTextBackground = editName.getBackground();
        updateEditTextEnable(false);
        editName.setOnEditorActionListener((v, actionId, event) -> {
            Dogger.i(Dogger.BOOM, "actionid" + actionId, "VideoDetailActivity", "initView");
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                File newFile = new File(BoomHelper.getRecordDirectory() + editName.getText() + BoomHelper.filePostfix);
                if(file != null && newFile != null){
                    file.renameTo(newFile);
                }
                updateEditTextEnable(false);
            }
            return true;
        });

        tvLastModified.setText(DataUtils.formatDate(file.lastModified()));
        filePath = file.getAbsolutePath();

        mVideoView.setMediaController(mMediaController);
        setVideoAreaSize();
        mVideoView.setVideoViewCallback(this);

        shareVideo.setOnClickListener((v)->{
            shareFile();
        });
    }

    private void updateEditTextEnable(boolean enable){
        if(editName == null){
            return;
        }
        if(enable){
            editName.setFocusable(enable);
            editName.setFocusableInTouchMode(enable);
            editName.setLongClickable(enable);
            editName.setBackground(editTextBackground);
            editName.setInputType(enable? InputType.TYPE_CLASS_TEXT:InputType.TYPE_NULL);
            editName.requestFocus();
            editName.selectAll();
            KeybordUtils.toggleSoftInput(editName);
        } else {
            editName.setFocusable(enable);
            editName.setFocusableInTouchMode(enable);
            editName.setLongClickable(enable);
            editName.setBackground(null);
            editName.setInputType(enable? InputType.TYPE_CLASS_TEXT:InputType.TYPE_NULL);
            KeybordUtils.hideKeyboard(editName);
        }
    }

    private void setVideoAreaSize() {
        mVideoLayout.post(() -> {
            int width = mVideoLayout.getWidth();
            cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
            ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
            videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            videoLayoutParams.height = cachedHeight;
            mVideoLayout.setLayoutParams(videoLayoutParams);
            mVideoView.setVideoPath(filePath);
            mVideoView.requestFocus();
            mVideoView.seekTo(100);
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.video_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_title:
                Dogger.i(Dogger.BOOM, "edit title", "VideoDetailActivity", "onOptionsItemSelected");
                updateEditTextEnable(true);
                break;
            case R.id.delete:
                if(file != null) {
                    file.delete();
                }
                BitmapCacheUtils.getInstance().removeCache(filePath);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ");
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            Log.d(TAG, "onPause mSeekPosition=" + mSeekPosition);
            mVideoView.pause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState Position=" + mVideoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        Log.d(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
    }


    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
        }

        updateFullScreenView();
    }

    private void updateFullScreenView(){
        ActionBar supportActionBar = getSupportActionBar();
        if(isFullscreen){
            editName.setVisibility(View.GONE);
            tvLastModified.setVisibility(View.GONE);
            shareVideo.setVisibility(View.GONE);

            if(supportActionBar != null){
                supportActionBar.hide();
            }
        } else {
            editName.setVisibility(View.VISIBLE);
            tvLastModified.setVisibility(View.VISIBLE);
            shareVideo.setVisibility(View.VISIBLE);
            if(supportActionBar != null){
                supportActionBar.show();
            }
        }
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPause UniversalVideoView callback");
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingEnd UniversalVideoView callback");
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

    private void shareFile(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(BoomApplication.getInstance().getApplicationContext()
                    , BuildConfig.APPLICATION_ID + ".fileProvider", file);

            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));
            intent.putExtra(Intent.EXTRA_SUBJECT, "MyApp File Share: " + file.getName());
            intent.putExtra(Intent.EXTRA_TEXT, "MyApp File Share: " + file.getName());
            intent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
        } else {
            intent.setDataAndType(Uri.fromFile(file), "video/mp4");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        this.startActivityForResult(Intent.createChooser(intent, file.getName()), SHREA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHREA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
//                NotificationUtils.showToast(this, getResources().getString(R.string.share_success));
            } else  {
//                NotificationUtils.showToast(this, getResources().getString(R.string.share_fail));
            }
        }
    }
}