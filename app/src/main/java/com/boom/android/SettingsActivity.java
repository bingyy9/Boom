package com.boom.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.boom.android.log.Dogger;
import com.boom.android.ui.dialog.InputDialog;
import com.boom.android.util.PrefsUtil;
import com.boom.android.viewmodel.SettingsViewModel;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
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

    SettingsViewModel settingsViewModel;

    public static void start(Context context) {
        if(context == null){
            return;
        }

        Intent intent = new Intent(context, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);
        settingsViewModel = new ViewModelProvider(SettingsActivity.this, new ViewModelProvider.AndroidViewModelFactory((this).getApplication()))
                .get(SettingsViewModel.class);
        initView();
    }

    private void initView() {
        layoutTimeDelay.setOnClickListener(this);
        layoutFileNameFormat.setOnClickListener(this);
        settingsViewModel.getTimeDelayBeforeRecording().observe(this, (Boolean b)-> {
            tvDelayRecording.setText(this.getResources().getString(R.string.current_time,
                    PrefsUtil.getTimeDelayBeforeRecording(this)));
        });
    }

    private void updateView(){
        tvDelayRecording.setText(this.getResources().getString(R.string.current_time,
                PrefsUtil.getTimeDelayBeforeRecording(this)));
        tvFileFormat.setText(this.getResources().getString(R.string.file_name_format_value
                , PrefsUtil.getFileNameFormat(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dogger.i(Dogger.BOOM, "", "SettingsActivity", "onResume");
        updateView();
    }

    @Override
    public void onClick(View view) {
        if(view == null){
            return;
        }
        switch (view.getId()){
            case R.id.layout_time_delay:
                showInputDialog();
                break;
            case R.id.layout_file_format:
                break;
        }
    }

    private void showInputDialog(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        DialogFragment prev = (DialogFragment)fragmentManager.findFragmentByTag(InputDialog.TAG);
        if (prev != null) {
            prev.dismiss();
        }
        InputDialog.newInstance(InputDialog.TYPE_TIME_DELAY_BEFORE_RECORD).show(fragmentManager.beginTransaction(), InputDialog.TAG);
    }
}