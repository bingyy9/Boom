package com.boom.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boom.android.util.AndroidVersionManager;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.DataUtils;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {
    private static final String EMAIL_ADDRESS_GOOGLE = "bingyy9@gmail.com";
    private static final String EMAIL_ADDRESS_126 = "bingyy9@126.com";

    View root;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_copy_rights)
    TextView tvCopyRights;
    @BindView(R.id.tv_contact_us)
    View tvContactUs;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvVersion.setText(getResources().getString(R.string.version, AndroidVersionManager.getVersion()));
        tvCopyRights.setText(getResources().getString(R.string.copy_rights, DataUtils.getYear()));
        tvContactUs.setOnClickListener((v)->{
            contactUs();
        });
    }

    private void contactUs() {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("text/plain");
        String[] to;
        if(BoomHelper.enableGoogleService()){
            to = new String[]{EMAIL_ADDRESS_GOOGLE};
        } else {
            to = new String[]{EMAIL_ADDRESS_126};
        }
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        startActivity(intent);
    }
}