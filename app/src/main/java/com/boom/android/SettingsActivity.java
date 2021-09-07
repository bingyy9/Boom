package com.boom.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boom.android.util.AndroidVersionManager;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.DataUtils;

import java.text.DateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    View root;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_copy_rights)
    TextView tvCopyRights;
    @BindView(R.id.tv_contact_us)
    View tvContactUs;

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
    }

}