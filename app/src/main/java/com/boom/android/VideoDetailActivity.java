package com.boom.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.boom.android.log.Dogger;
import com.boom.android.ui.videos.VideoDetailFragment;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.DataUtils;
import com.boom.android.util.cache.BitmapCacheUtils;
import com.boom.model.interf.IRecordModel;
import com.boom.model.interf.impl.ModelBuilderManager;
import com.boom.utils.StringUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String FILENAME = "FILE_NAME";
    private IRecordModel recordModel;

    View root;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_last_modified_time)
    TextView tvLastModified;
    @BindView(R.id.share_video)
    TextView shareVideo;

    String fileName;

    public static void start(Context context, String filename) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(VideoDetailFragment.FILENAME, filename);
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
        fileName = getIntent() == null? null: getIntent().getStringExtra(FILENAME);
        if(StringUtils.isEmpty(fileName)){
            Dogger.i(Dogger.BOOM, "file name is null", "VideoDetailActivity", "initView");
            finish();
            return;
        }
        File file = new File(BoomHelper.getRecordDirectory() + fileName);
        if(file == null){
            Dogger.i(Dogger.BOOM, "file is not exist", "VideoDetailActivity", "initView");
            finish();
            return;
        }
        tvName.setText(file.getName());
        tvLastModified.setText(DataUtils.formatDate(file.lastModified()));
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
                break;
            case R.id.delete:
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

    }
}