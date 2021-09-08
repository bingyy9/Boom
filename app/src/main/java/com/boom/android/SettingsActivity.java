package com.boom.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boom.android.util.AndroidVersionManager;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.DataUtils;
import com.boom.android.util.Prefs;

import java.text.DateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.layout_time_delay)
    View layoutTimeDelay;
    @BindView(R.id.tv_current_delay_recording)
    TextView tvDelayRecording;
    @BindView(R.id.layout_file_format)
    View layoutFileNameFormat;
    @BindView(R.id.tv_file_name_format_value)
    TextView tvFileFormat;

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvDelayRecording.setText(this.getResources().getString(R.string.current_time,
                String.valueOf(Prefs.with(this).readInt(Prefs.TIME_DELAY_BEFORE_RECORDING
                        , Prefs.DEFAULT_TIME_DELAY_BEFORE_RECORDING))));
        tvFileFormat.setText(this.getResources().getString(R.string.file_name_format_value
                , Prefs.with(this).read(Prefs.FILE_NAME_FORMAT, Prefs.DEFAULT_FILE_NAME_FORMAT)));
    }

    @Override
    public void onClick(View view) {
        if(view == null){
            return;
        }
        switch (view.getId()){
            case R.id.layout_time_delay:
                break;
            case R.id.layout_file_format:
                break;
        }
    }
}